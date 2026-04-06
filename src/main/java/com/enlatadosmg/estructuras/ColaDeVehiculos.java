package com.enlatadosmg.estructuras;

import com.enlatadosmg.model.Vehiculo;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * ESTRUCTURA: ColaDeVehiculos
 * ============================================================
 * Lista enlazada que almacena TODOS los vehiculos registrados.
 * Los vehiculos NUNCA se eliminan — solo cambian de estado:
 *   LIBRE | OCUPADO | DESCOMPUESTO
 *
 * Operaciones:
 *   registrar()         → agrega vehiculo al final
 *   asignarDisponible() → busca el primer vehiculo LIBRE y lo marca OCUPADO
 *   liberarVehiculo()   → marca el vehiculo como LIBRE al completar pedido
 *   cambiarEstado()     → cambia estado manualmente (ej: DESCOMPUESTO)
 *   buscarPorPlaca()    → busca un vehiculo por su placa
 * ============================================================
 */
public class ColaDeVehiculos {

    /**
     * TurnoDeVehiculo: nodo interno de la lista.
     * Representa la posicion de un vehiculo en la lista.
     */
    private static class TurnoDeVehiculo {
        Vehiculo        vehiculo;
        TurnoDeVehiculo siguienteTurno;

        TurnoDeVehiculo(Vehiculo vehiculo) {
            this.vehiculo       = vehiculo;
            this.siguienteTurno = null;
        }
    }

    private TurnoDeVehiculo primero;
    private TurnoDeVehiculo ultimo;
    private int totalVehiculos;

    public ColaDeVehiculos() {
        this.primero        = null;
        this.ultimo         = null;
        this.totalVehiculos = 0;
    }

    /**
     * Registrar: agrega un vehiculo al sistema.
     * Todo vehiculo ingresa con estado LIBRE.
     */
    public void registrar(Vehiculo vehiculo) {
        TurnoDeVehiculo nuevoTurno = new TurnoDeVehiculo(vehiculo);
        if (primero == null) {
            primero = nuevoTurno;
            ultimo  = nuevoTurno;
        } else {
            ultimo.siguienteTurno = nuevoTurno;
            ultimo                = nuevoTurno;
        }
        totalVehiculos++;
    }

    /**
     * Asignar disponible: busca el primer vehiculo con estado LIBRE
     * y lo marca OCUPADO. El vehiculo NO se elimina de la lista.
     * Reutiliza estaDisponible() de Vehiculo.
     */
    public Vehiculo asignarDisponible() {
        TurnoDeVehiculo actual = primero;
        while (actual != null) {
            if (actual.vehiculo.estaDisponible()) {   // reutiliza metodo de Vehiculo
                actual.vehiculo.setEstado("OCUPADO");
                return actual.vehiculo;
            }
            actual = actual.siguienteTurno;
        }
        throw new RuntimeException("No hay vehiculos LIBRES disponibles en este momento");
    }

    /**
     * Liberar vehiculo: marca el vehiculo como LIBRE al completar o cancelar pedido.
     * Reutiliza buscarPorPlaca() internamente.
     */
    public void liberarVehiculo(Vehiculo vehiculo) {
        Vehiculo encontrado = buscarPorPlaca(vehiculo.getPlaca());
        if (encontrado != null) {
            encontrado.setEstado("LIBRE");
        }
    }

    /**
     * Cambiar estado manualmente.
     * @param placa  Placa del vehiculo
     * @param estado nuevo estado
     */
    public boolean cambiarEstado(String placa, String estado) {
        Vehiculo v = buscarPorPlaca(placa);
        if (v == null) return false;
        v.setEstado(estado);
        return true;
    }

    /** Busca un vehiculo por su placa recorriendo la lista */
    public Vehiculo buscarPorPlaca(String placa) {
        TurnoDeVehiculo actual = primero;
        while (actual != null) {
            if (placa.equals(actual.vehiculo.getPlaca())) return actual.vehiculo;
            actual = actual.siguienteTurno;
        }
        return null;
    }

    /** Retorna todos los vehiculos (sin importar estado) */
    public List<Vehiculo> obtenerTodos() {
        List<Vehiculo> resultado = new ArrayList<>();
        TurnoDeVehiculo actual   = primero;
        while (actual != null) {
            resultado.add(actual.vehiculo);
            actual = actual.siguienteTurno;
        }
        return resultado;
    }

    public int contarLibres() {
        int count = 0;
        TurnoDeVehiculo actual = primero;
        while (actual != null) {
            if (actual.vehiculo.estaDisponible()) count++;
            actual = actual.siguienteTurno;
        }
        return count;
    }

    public int getTotalVehiculos()  { return totalVehiculos; }
    public boolean estaVacia()      { return primero == null; }
}
