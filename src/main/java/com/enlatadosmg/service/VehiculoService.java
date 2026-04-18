package com.enlatadosmg.service;

import com.enlatadosmg.model.Vehiculo;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VehiculoService {

    private final DataStore dataStore;

    public VehiculoService(DataStore dataStore) { this.dataStore = dataStore; }

    /** Registra vehiculo. Rechaza placa duplicada con mensaje claro. */
    public Vehiculo registrar(Vehiculo v) {
        if (v.getPlaca() == null || v.getPlaca().isBlank())
            throw new RuntimeException("La placa del vehiculo es obligatoria.");
        if (v.getCapacidadMaxCajas() <= 0)
            throw new RuntimeException("La capacidad maxima debe ser mayor a 0.");

        if (dataStore.getColaDeVehiculos().buscarPorPlaca(v.getPlaca()) != null)
            throw new RuntimeException("Ya existe un vehiculo registrado con la placa '"
                    + v.getPlaca() + "'. Verifica la placa o registra una diferente.");

        if (v.getEstado() == null || v.getEstado().isBlank()) v.setEstado("LIBRE");
        dataStore.getColaDeVehiculos().registrar(v);
        return v;
    }

    /** Edita datos de un vehiculo */
    public Vehiculo editar(String placa, String marca, String modelo,
                           String color, Integer anio, String transmision,
                           Integer capacidad) {
        Vehiculo v = dataStore.getColaDeVehiculos().buscarPorPlaca(placa);
        if (v == null)
            throw new RuntimeException("No existe ningun vehiculo con la placa: " + placa);
        if (marca       != null && !marca.isBlank())       v.setMarca(marca);
        if (modelo      != null && !modelo.isBlank())      v.setModelo(modelo);
        if (color       != null && !color.isBlank())       v.setColor(color);
        if (anio        != null && anio > 1900)            v.setAnio(anio);
        if (transmision != null && !transmision.isBlank()) v.setTipoTransmision(transmision);
        if (capacidad   != null && capacidad > 0)          v.setCapacidadMaxCajas(capacidad);
        return v;
    }

    public Vehiculo asignar() {
        if (dataStore.getColaDeVehiculos().contarLibres() == 0)
            throw new RuntimeException("No hay vehiculos LIBRES disponibles. Todos estan ocupados o fuera de servicio.");
        return dataStore.getColaDeVehiculos().asignarDisponible();
    }

    public void liberar(Vehiculo v) { dataStore.getColaDeVehiculos().liberarVehiculo(v); }

    /**
     * Cambia el estado de un vehiculo.
     * CORREGIDO: Solo bloquea si el vehiculo tiene cajas fisicas activas (cajasOcupadas > 0).
     * Si cajasOcupadas == 0, permite el cambio aunque el estado sea OCUPADO
     * para evitar vehiculos huerfanos sin pedido real.
     */
    public boolean cambiarEstado(String placa, String estado) {
        Vehiculo v = dataStore.getColaDeVehiculos().buscarPorPlaca(placa);
        if (v == null)
            throw new RuntimeException("No existe ningun vehiculo con la placa: " + placa);

        if ("OCUPADO".equals(v.getEstado()) && v.getCajasOcupadas() > 0 && !"OCUPADO".equals(estado))
            throw new RuntimeException("El vehiculo " + placa
                    + " tiene " + v.getCajasOcupadas() + " cajas activas. "
                    + "Completa o cancela el pedido primero.");

        return dataStore.getColaDeVehiculos().cambiarEstado(placa, estado);
    }

    /**
     * Elimina un vehiculo del sistema.
     * No permite eliminar si tiene cajas activas (cajasOcupadas > 0).
     */
    public void eliminar(String placa) {
        Vehiculo v = dataStore.getColaDeVehiculos().buscarPorPlaca(placa);
        if (v == null)
            throw new RuntimeException("No existe ningun vehiculo con la placa: " + placa);
        if (v.getCajasOcupadas() > 0)
            throw new RuntimeException("El vehiculo " + placa
                    + " tiene cajas activas. Completa o cancela el pedido antes de eliminarlo.");
        dataStore.getColaDeVehiculos().eliminar(placa);
    }

    public Vehiculo buscarPorPlaca(String placa)  { return dataStore.getColaDeVehiculos().buscarPorPlaca(placa); }
    public List<Vehiculo> obtenerTodos()          { return dataStore.getColaDeVehiculos().obtenerTodos(); }
    public int getCantidad()                      { return dataStore.getColaDeVehiculos().getTotalVehiculos(); }
    public int getCantidadLibres()                { return dataStore.getColaDeVehiculos().contarLibres(); }
}