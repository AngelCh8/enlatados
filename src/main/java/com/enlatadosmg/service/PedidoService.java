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
     *
     * CORREGIDO: Ahora valida que haya vehiculo Y piloto disponibles ANTES
     * de marcar cualquier recurso como OCUPADO. Si falta alguno, lanza error
     * sin dejar ningun recurso huerfano.
     *
     * Logica de asignacion:
     * 1. Valida cliente, productos y stock.
     * 2. Verifica disponibilidad de vehiculo y piloto SIN asignarlos todavia.
     * 3. Solo si todo esta bien, asigna ambos y marca OCUPADO.
     */
    public List<PedidoDeEntrega> crearPedidoMultiProducto(String cuiCliente,
                                                          String deptoOrigen,
                                                          String deptoDestino,
                                                          List<Map<String, Object>> lineas) {
        // ── 1. Validar cliente ───────────────────────────────────────────────
        Cliente cliente = clienteService.buscarPorCui(cuiCliente);
        if (cliente == null)
            throw new RuntimeException("Cliente no encontrado con CUI: " + cuiCliente);

        // ── 2. Validar productos y stock ─────────────────────────────────────
        List<LineaDePedido> lineasValidadas = new ArrayList<>();
        int totalCajasSolicitadas = 0;

        for (Map<String, Object> item : lineas) {
            String codigoProd = item.get("codigoProducto").toString();
            int    cantCajas  = Integer.parseInt(item.get("cantidadCajas").toString());

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

        // ── 3. Verificar disponibilidad ANTES de asignar nada ────────────────
        // Si no hay camion compartible, debe haber al menos 1 vehiculo y 1 piloto libres.
        Vehiculo camionCompartible = buscarCamionCompartible(deptoDestino);
        if (camionCompartible == null) {
            if (vehiculoService.getCantidadLibres() == 0)
                throw new RuntimeException(
                        "No hay vehiculos LIBRES disponibles. Registra o libera un vehiculo antes de crear el pedido.");
            if (pilotoService.getCantidadLibres() == 0)
                throw new RuntimeException(
                        "No hay pilotos LIBRES disponibles. Registra o libera un piloto antes de crear el pedido.");
        }

        // ── 4. Todo validado — proceder con la asignacion ───────────────────
        List<PedidoDeEntrega> pedidosCreados = new ArrayList<>();
        int cajasRestantes = totalCajasSolicitadas;
        int lineaIdx = 0;

        while (cajasRestantes > 0) {
            Vehiculo vehiculo = buscarCamionCompartible(deptoDestino);
            Piloto   piloto;

            if (vehiculo != null) {
                // Compartir camion existente con su piloto
                piloto = buscarPilotoDelCamion(vehiculo);
            } else {
                // Verificar nuevamente en cada iteracion (por si el loop divide pedidos)
                if (vehiculoService.getCantidadLibres() == 0)
                    throw new RuntimeException(
                            "No hay mas vehiculos LIBRES para continuar distribuyendo el pedido.");
                if (pilotoService.getCantidadLibres() == 0)
                    throw new RuntimeException(
                            "No hay mas pilotos LIBRES para continuar distribuyendo el pedido.");

                // Asignar ambos recursos juntos — si uno falla, el otro no se toca
                vehiculo = vehiculoService.asignar();
                try {
                    piloto = pilotoService.asignar();
                } catch (RuntimeException e) {
                    // Revertir vehiculo si el piloto falla
                    vehiculo.setEstado("LIBRE");
                    vehiculo.setCajasOcupadas(0);
                    throw new RuntimeException(
                            "No se pudo asignar piloto. El vehiculo fue liberado. Detalle: " + e.getMessage());
                }
            }

            int espacioDisponible = vehiculo.getEspacioDisponible();
            int cajasEnEstePedido = Math.min(cajasRestantes, espacioDisponible);

            PedidoDeEntrega pedido = new PedidoDeEntrega(
                    dataStore.siguienteNumeroPedido(), deptoOrigen, deptoDestino, cliente, piloto, vehiculo
            );

            // Asignar lineas proporcionales a este pedido
            int cajasAsignadasEnPedido = 0;
            while (cajasAsignadasEnPedido < cajasEnEstePedido && lineaIdx < lineasValidadas.size()) {
                LineaDePedido linea    = lineasValidadas.get(lineaIdx);
                int disponibleEnPedido = cajasEnEstePedido - cajasAsignadasEnPedido;
                int aAsignar           = Math.min(linea.getCantidadCajas(), disponibleEnPedido);

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
                    lineasValidadas.set(lineaIdx, new LineaDePedido(
                            linea.getCodigoProducto(), linea.getNombreProducto(),
                            linea.getCantidadCajas() - aAsignar, linea.getPrecioUnitario()
                    ));
                }
            }

            // Marcar vehiculo como OCUPADO solo cuando ya tiene cajas reales
            vehiculo.ocuparEspacio(cajasAsignadasEnPedido);
            vehiculo.setEstado("OCUPADO");

            dataStore.getListaDePedidos().agregar(pedido);
            pedidosCreados.add(pedido);
            cajasRestantes -= cajasAsignadasEnPedido;
        }

        return pedidosCreados;
    }

    /**
     * Busca un vehiculo OCUPADO que vaya al mismo destino y tenga espacio libre.
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
            if (p.getVehiculo() != null
                    && p.getVehiculo().getPlaca().equals(vehiculo.getPlaca())) {
                return p.getPiloto();
            }
        }
        // fallback — no deberia llegar aqui si buscarCamionCompartible funciona bien
        return pilotoService.asignar();
    }

    /**
     * Cambia el estado de un pedido.
     * Al COMPLETAR o CANCELAR libera piloto y reduce cajas del vehiculo.
     * Si el vehiculo queda sin cajas, pasa a LIBRE automaticamente.
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
            Vehiculo v = pedido.getVehiculo();
            if (v != null) {
                v.liberarEspacio(pedido.getTotalCajas());
                if (v.getCajasOcupadas() == 0) v.setEstado("LIBRE");
            }
        }
        return pedido;
    }

    public List<PedidoDeEntrega> obtenerTodos()        { return dataStore.getListaDePedidos().obtenerTodos(); }
    public PedidoDeEntrega buscarPorNumero(int numero) { return dataStore.getListaDePedidos().buscar(p -> p.getNumeroPedido() == numero); }
}