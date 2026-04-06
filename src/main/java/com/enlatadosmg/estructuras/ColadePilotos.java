package com.enlatadosmg.estructuras;

import com.enlatadosmg.model.Piloto;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * ESTRUCTURA: ColadePilotos
 * ============================================================
 * Lista enlazada que almacena TODOS los pilotos registrados.
 * Los pilotos NUNCA se eliminan — solo cambian de estado:
 *   LIBRE | OCUPADO | DE_VACACIONES
 *
 * Operaciones:
 *   registrar()        → agrega piloto al final
 *   asignarDisponible()→ busca el primer piloto LIBRE y lo marca OCUPADO
 *   liberarPiloto()    → marca el piloto como LIBRE al completar pedido
 *   cambiarEstado()    → cambia estado manualmente (ej: DE_VACACIONES)
 *   buscarPorCui()     → busca un piloto por su CUI
 * ============================================================
 */
public class ColadePilotos {

    /**
     * TurnoDePiloto: nodo interno de la lista.
     * Representa la posicion de un piloto en la lista de pilotos.
     */
    private static class TurnoDePiloto {
        Piloto        piloto;
        TurnoDePiloto siguienteTurno;

        TurnoDePiloto(Piloto piloto) {
            this.piloto         = piloto;
            this.siguienteTurno = null;
        }
    }

    private TurnoDePiloto primero;
    private TurnoDePiloto ultimo;
    private int totalPilotos;

    public ColadePilotos() {
        this.primero      = null;
        this.ultimo       = null;
        this.totalPilotos = 0;
    }

    /**
     * Registrar: agrega un nuevo piloto al sistema.
     * Todo piloto ingresa con estado LIBRE.
     */
    public void registrar(Piloto piloto) {
        TurnoDePiloto nuevoTurno = new TurnoDePiloto(piloto);
        if (primero == null) {
            primero = nuevoTurno;
            ultimo  = nuevoTurno;
        } else {
            ultimo.siguienteTurno = nuevoTurno;
            ultimo                = nuevoTurno;
        }
        totalPilotos++;
    }

    /**
     * Asignar disponible: busca el primer piloto con estado LIBRE
     * y lo marca OCUPADO. El piloto NO se elimina de la lista.
     * Reutiliza estaDisponible() de Piloto para la validacion.
     *
     * @return Piloto asignado, ahora en estado OCUPADO
     */
    public Piloto asignarDisponible() {
        TurnoDePiloto actual = primero;
        while (actual != null) {
            if (actual.piloto.estaDisponible()) {   // reutiliza metodo de Piloto
                actual.piloto.setEstado("OCUPADO");
                return actual.piloto;
            }
            actual = actual.siguienteTurno;
        }
        throw new RuntimeException("No hay pilotos LIBRES disponibles en este momento");
    }

    /**
     * Liberar piloto: marca el piloto como LIBRE al completar o cancelar pedido.
     * Busca al piloto por CUI para localizarlo en la lista.
     * Reutiliza buscarPorCui() internamente.
     */
    public void liberarPiloto(Piloto piloto) {
        Piloto encontrado = buscarPorCui(piloto.getCui());
        if (encontrado != null) {
            encontrado.setEstado("LIBRE");
        }
    }

    /**
     * Cambiar estado manualmente (ej: poner DE_VACACIONES, LIBRE, etc.)
     * @param cui    CUI del piloto
     * @param estado nuevo estado
     * @return true si se encontro y cambio, false si no existe
     */
    public boolean cambiarEstado(String cui, String estado) {
        Piloto p = buscarPorCui(cui);
        if (p == null) return false;
        p.setEstado(estado);
        return true;
    }

    /** Busca un piloto por su CUI recorriendo la lista */
    public Piloto buscarPorCui(String cui) {
        TurnoDePiloto actual = primero;
        while (actual != null) {
            if (cui.equals(actual.piloto.getCui())) return actual.piloto;
            actual = actual.siguienteTurno;
        }
        return null;
    }

    /** Retorna todos los pilotos (sin importar estado) */
    public List<Piloto> obtenerTodos() {
        List<Piloto> resultado = new ArrayList<>();
        TurnoDePiloto actual   = primero;
        while (actual != null) {
            resultado.add(actual.piloto);
            actual = actual.siguienteTurno;
        }
        return resultado;
    }

    /** Retorna cuantos pilotos estan en estado LIBRE */
    public int contarLibres() {
        int count = 0;
        TurnoDePiloto actual = primero;
        while (actual != null) {
            if (actual.piloto.estaDisponible()) count++;
            actual = actual.siguienteTurno;
        }
        return count;
    }

    public int getTotalPilotos()    { return totalPilotos; }
    public boolean estaVacia()      { return primero == null; }
}
