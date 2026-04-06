package com.enlatadosmg.service;

import com.enlatadosmg.model.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class PedidoService {

    private final DataStore       dataStore;
    private final AlmacenService  almacenService;
    private final PilotoService   pilotoService;
    private final VehiculoService vehiculoService;
    private final ClienteService  clienteService;
    private final CatalogoService catalogoService;

    public PedidoService(DataStore dataStore, AlmacenService almacenService,
                         PilotoService pilotoService, VehiculoService vehiculoService,
                         ClienteService clienteService, CatalogoService catalogoService) {
        this.dataStore       = dataStore;
        this.almacenService  = almacenService;
        this.pilotoService   = pilotoService;
        this.vehiculoService = vehiculoService;
        this.clienteService  = clienteService;
        this.catalogoService = catalogoService;
    }

    /**
     * Crea un pedido con MULTIPLES productos.
     * lineas: [ { "codigoProducto": "PROD-001", "cantidadCajas": 10 }, ... ]
     *
     * Logica de asignacion de camion:
     * 1. Busca un camion OCUPADO con el mismo destino que todavia tenga espacio.
     * 2. Si existe, se une al camion (mismo piloto/vehiculo).
     * 3. Si no, asigna un camion LIBRE nuevo.
     * 4. Si el pedido supera la capacidad, lo divide en multiples pedidos.
     *
     * @return Lista de pedidos creados (puede ser mas de uno si se divide)
     */
    public List<PedidoDeEntrega> crearPedidoMultiProducto(String cuiCliente,
                                                           String deptoOrigen,
                                                           String deptoDestino,
                                                           List<Map<String, Object>> lineas) {
        // Validar cliente
        Cliente cliente = clienteService.buscarPorCui(cuiCliente);
        if (cliente == null)
            throw new RuntimeException("Cliente no encontrado con CUI: " + cuiCliente);

        // Construir lineas validadas y calcular total de cajas
        List<LineaDePedido> lineasValidadas = new ArrayList<>();
        int totalCajasSolicitadas = 0;

        for (Map<String, Object> item : lineas) {
            String codigoProd   = item.get("codigoProducto").toString();
            int    cantCajas    = Integer.parseInt(item.get("cantidadCajas").toString());

            CatalogoDeProductos catalogo = catalogoService.buscarPorCodigo(codigoProd);
            if (catalogo == null)
                throw new RuntimeException("Producto no encontrado en catalogo: " + codigoProd);
            if (catalogo.getCajasEnStock() < cantCajas)
                throw new RuntimeException("Stock insuficiente de " + catalogo.getNombreProducto()
                    + ". Disponibles: " + catalogo.getCajasEnStock() + ", solicitadas: " + cantCajas);

            lineasValidadas.add(new LineaDePedido(
                codigoProd, catalogo.getNombreProducto(),
                cantCajas, catalogo.getPrecioUnitario()
            ));
            totalCajasSolicitadas += cantCajas;
        }

        List<PedidoDeEntrega> pedidosCreados = new ArrayList<>();
        int cajasRestantes = totalCajasSolicitadas;
        int lineaIdx = 0;

        // Distribuir en camiones
        while (cajasRestantes > 0) {
            // Buscar camion ocupado con mismo destino que tenga espacio
            Vehiculo vehiculo = buscarCamionCompartible(deptoDestino);
            Piloto   piloto;

            if (vehiculo != null) {
                // Compartir camion existente — buscar su piloto en el pedido activo
                piloto = buscarPilotoDelCamion(vehiculo);
            } else {
                // Asignar camion nuevo
                vehiculo = vehiculoService.asignar();
                piloto   = pilotoService.asignar();
            }

            int espacioDisponible = vehiculo.getEspacioDisponible();
            int cajasEnEstePedido = Math.min(cajasRestantes, espacioDisponible);

            PedidoDeEntrega pedido = new PedidoDeEntrega(
                dataStore.siguienteNumeroPedido(), deptoOrigen, deptoDestino, cliente, piloto, vehiculo
            );

            // Asignar lineas proporcionales a este pedido
            int cajasAsignadasEnPedido = 0;
            while (cajasAsignadasEnPedido < cajasEnEstePedido && lineaIdx < lineasValidadas.size()) {
                LineaDePedido linea     = lineasValidadas.get(lineaIdx);
                int disponibleEnPedido  = cajasEnEstePedido - cajasAsignadasEnPedido;
                int aAsignar            = Math.min(linea.getCantidadCajas(), disponibleEnPedido);

                // Retirar cajas fisicas del almacen
                List<CajaDeProductos> cajasRetiradas =
                    almacenService.retirarCajasPorProducto(linea.getCodigoProducto(), aAsignar);
                pedido.agregarLinea(new LineaDePedido(
                    linea.getCodigoProducto(), linea.getNombreProducto(),
                    aAsignar, linea.getPrecioUnitario()
                ));
                pedido.agregarCajas(cajasRetiradas);
                cajasAsignadasEnPedido += aAsignar;

                if (aAsignar == linea.getCantidadCajas()) {
                    lineaIdx++;
                } else {
                    // linea parcialmente asignada — actualizar cantidad restante
                    lineasValidadas.set(lineaIdx, new LineaDePedido(
                        linea.getCodigoProducto(), linea.getNombreProducto(),
                        linea.getCantidadCajas() - aAsignar, linea.getPrecioUnitario()
                    ));
                }
            }

            // Actualizar ocupacion del vehiculo
            vehiculo.ocuparEspacio(cajasAsignadasEnPedido);
            if (vehiculo.getEspacioDisponible() == 0) vehiculo.setEstado("OCUPADO");
            else vehiculo.setEstado("OCUPADO"); // sigue ocupado aunque tenga espacio

            dataStore.getListaDePedidos().agregar(pedido);
            pedidosCreados.add(pedido);
            cajasRestantes -= cajasAsignadasEnPedido;
        }

        return pedidosCreados;
    }

    /**
     * Busca un vehiculo OCUPADO que vaya al mismo destino y tenga espacio libre.
     * Permite "llenar el camion" con pedidos al mismo destino.
     */
    private Vehiculo buscarCamionCompartible(String destino) {
        List<PedidoDeEntrega> pedidos = dataStore.getListaDePedidos().obtenerTodos();
        for (PedidoDeEntrega p : pedidos) {
            if ("PENDIENTE".equals(p.getEstado()) || "EN_CAMINO".equals(p.getEstado())) {
                if (destino.equalsIgnoreCase(p.getDepartamentoDestino())) {
                    Vehiculo v = p.getVehiculo();
                    if (v != null && v.getEspacioDisponible() > 0) return v;
                }
            }
        }
        return null;
    }

    /** Busca el piloto asociado a un vehiculo en pedidos activos */
    private Piloto buscarPilotoDelCamion(Vehiculo vehiculo) {
        List<PedidoDeEntrega> pedidos = dataStore.getListaDePedidos().obtenerTodos();
        for (PedidoDeEntrega p : pedidos) {
            if (p.getVehiculo() != null && p.getVehiculo().getPlaca().equals(vehiculo.getPlaca())) {
                return p.getPiloto();
            }
        }
        return pilotoService.asignar();
    }

    /**
     * Cambia el estado de un pedido.
     * Al COMPLETAR o CANCELAR: libera piloto y reduce cajas ocupadas del vehiculo.
     * Si el vehiculo ya no tiene cajas, queda LIBRE.
     */
    public PedidoDeEntrega cambiarEstado(int numeroPedido, String nuevoEstado, String observaciones) {
        PedidoDeEntrega pedido = dataStore.getListaDePedidos()
            .buscar(p -> p.getNumeroPedido() == numeroPedido);
        if (pedido == null)
            throw new RuntimeException("Pedido no encontrado: " + numeroPedido);

        String actual = pedido.getEstado();
        if ("COMPLETADO".equals(actual) || "CANCELADO".equals(actual))
            throw new RuntimeException("El pedido ya fue " + actual.toLowerCase() + " y no puede modificarse");

        pedido.setEstado(nuevoEstado);
        if (observaciones != null && !observaciones.isBlank())
            pedido.setObservaciones(observaciones);

        if ("COMPLETADO".equals(nuevoEstado) || "CANCELADO".equals(nuevoEstado)) {
            pilotoService.liberar(pedido.getPiloto());
            // Liberar espacio del vehiculo
            Vehiculo v = pedido.getVehiculo();
            if (v != null) {
                v.liberarEspacio(pedido.getTotalCajas());
                if (v.getCajasOcupadas() == 0) v.setEstado("LIBRE");
            }
        }
        return pedido;
    }

    public List<PedidoDeEntrega> obtenerTodos()         { return dataStore.getListaDePedidos().obtenerTodos(); }
    public PedidoDeEntrega buscarPorNumero(int numero)  { return dataStore.getListaDePedidos().buscar(p -> p.getNumeroPedido() == numero); }
}
