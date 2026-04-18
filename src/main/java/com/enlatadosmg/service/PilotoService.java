package com.enlatadosmg.service;

import com.enlatadosmg.model.Piloto;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PilotoService {

    private final DataStore dataStore;

    public PilotoService(DataStore dataStore) { this.dataStore = dataStore; }

    /** Registra piloto. Rechaza CUI duplicado con mensaje claro. */
    public Piloto registrar(Piloto p) {
        if (p.getCui() == null || p.getCui().isBlank())
            throw new RuntimeException("El CUI del piloto es obligatorio.");
        if (p.getNombre() == null || p.getNombre().isBlank())
            throw new RuntimeException("El nombre del piloto es obligatorio.");

        if (dataStore.getColadePilotos().buscarPorCui(p.getCui()) != null)
            throw new RuntimeException("Ya existe un piloto registrado con el CUI '"
                    + p.getCui() + "'. Verifica el numero o registra un CUI diferente.");

        if (p.getEstado() == null || p.getEstado().isBlank()) p.setEstado("LIBRE");
        dataStore.getColadePilotos().registrar(p);
        return p;
    }

    /** Edita datos de un piloto */
    public Piloto editar(String cui, String nombre, String apellidos,
                         String licencia, String telefono) {
        Piloto p = dataStore.getColadePilotos().buscarPorCui(cui);
        if (p == null)
            throw new RuntimeException("No existe ningun piloto con el CUI: " + cui);
        if (nombre    != null && !nombre.isBlank())    p.setNombre(nombre.trim());
        if (apellidos != null)                          p.setApellidos(apellidos.trim());
        if (licencia  != null && !licencia.isBlank())  p.setLicencia(licencia);
        if (telefono  != null && !telefono.isBlank())  p.setTelefono(telefono);
        return p;
    }

    public Piloto asignar() {
        if (dataStore.getColadePilotos().contarLibres() == 0)
            throw new RuntimeException("No hay pilotos LIBRES disponibles. Todos estan ocupados o de vacaciones.");
        return dataStore.getColadePilotos().asignarDisponible();
    }

    public void liberar(Piloto p) { dataStore.getColadePilotos().liberarPiloto(p); }

    /**
     * Cambia el estado de un piloto.
     * CORREGIDO: Solo bloquea si el piloto tiene un pedido PENDIENTE o EN_CAMINO real.
     * Evita pilotos huerfanos bloqueados sin pedido activo.
     */
    public boolean cambiarEstado(String cui, String estado) {
        Piloto p = dataStore.getColadePilotos().buscarPorCui(cui);
        if (p == null)
            throw new RuntimeException("No existe ningun piloto con el CUI: " + cui);

        if ("OCUPADO".equals(p.getEstado()) && !"OCUPADO".equals(estado)) {
            boolean tienePedidoActivo = dataStore.getListaDePedidos().obtenerTodos()
                    .stream()
                    .anyMatch(ped -> ped.getPiloto() != null
                            && ped.getPiloto().getCui().equals(cui)
                            && ("PENDIENTE".equals(ped.getEstado()) || "EN_CAMINO".equals(ped.getEstado())));

            if (tienePedidoActivo)
                throw new RuntimeException("El piloto '" + p.obtenerNombreCompleto()
                        + "' tiene un pedido activo. Completa o cancela el pedido primero.");
        }

        return dataStore.getColadePilotos().cambiarEstado(cui, estado);
    }

    /**
     * Elimina un piloto del sistema.
     * No permite eliminar si tiene un pedido activo real.
     */
    public void eliminar(String cui) {
        Piloto p = dataStore.getColadePilotos().buscarPorCui(cui);
        if (p == null)
            throw new RuntimeException("No existe ningun piloto con el CUI: " + cui);

        boolean tienePedidoActivo = dataStore.getListaDePedidos().obtenerTodos()
                .stream()
                .anyMatch(ped -> ped.getPiloto() != null
                        && ped.getPiloto().getCui().equals(cui)
                        && ("PENDIENTE".equals(ped.getEstado()) || "EN_CAMINO".equals(ped.getEstado())));

        if (tienePedidoActivo)
            throw new RuntimeException("El piloto '" + p.obtenerNombreCompleto()
                    + "' tiene un pedido activo. Completa o cancela el pedido antes de eliminarlo.");

        dataStore.getColadePilotos().eliminar(cui);
    }

    public Piloto buscarPorCui(String cui)  { return dataStore.getColadePilotos().buscarPorCui(cui); }
    public List<Piloto> obtenerTodos()      { return dataStore.getColadePilotos().obtenerTodos(); }
    public int getCantidad()                { return dataStore.getColadePilotos().getTotalPilotos(); }
    public int getCantidadLibres()          { return dataStore.getColadePilotos().contarLibres(); }
}