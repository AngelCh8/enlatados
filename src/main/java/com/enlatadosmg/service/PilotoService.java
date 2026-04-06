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

        // Garantizar estado inicial LIBRE aunque venga null del JSON
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

    public void liberar(Piloto p)                       { dataStore.getColadePilotos().liberarPiloto(p); }

    public boolean cambiarEstado(String cui, String estado) {
        Piloto p = dataStore.getColadePilotos().buscarPorCui(cui);
        if (p == null)
            throw new RuntimeException("No existe ningun piloto con el CUI: " + cui);
        if (p.getEstado().equals("OCUPADO") && !"OCUPADO".equals(estado))
            throw new RuntimeException("El piloto '" + p.obtenerNombreCompleto()
                + "' esta OCUPADO con un pedido activo. Completa o cancela el pedido primero.");
        return dataStore.getColadePilotos().cambiarEstado(cui, estado);
    }

    public Piloto buscarPorCui(String cui)  { return dataStore.getColadePilotos().buscarPorCui(cui); }
    public List<Piloto> obtenerTodos()      { return dataStore.getColadePilotos().obtenerTodos(); }
    public int getCantidad()                { return dataStore.getColadePilotos().getTotalPilotos(); }
    public int getCantidadLibres()          { return dataStore.getColadePilotos().contarLibres(); }
}
