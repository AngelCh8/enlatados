package com.enlatadosmg.estructuras;

import com.enlatadosmg.model.Piloto;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * ESTRUCTURA: ColadePilotos
 * ============================================================
 * Lista enlazada que almacena TODOS los pilotos registrados.
 *
 * Operaciones:
 *   registrar()         → agrega piloto al final
 *   asignarDisponible() → busca el primer piloto LIBRE y lo marca OCUPADO
 *   liberarPiloto()     → marca el piloto como LIBRE al completar pedido
 *   cambiarEstado()     → cambia estado manualmente
 *   buscarPorCui()      → busca un piloto por su CUI
 *   eliminar()          → elimina fisicamente un piloto de la lista
 * ============================================================
 */
public class ColadePilotos {

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

    public Piloto asignarDisponible() {
        TurnoDePiloto actual = primero;
        while (actual != null) {
            if (actual.piloto.estaDisponible()) {
                actual.piloto.setEstado("OCUPADO");
                return actual.piloto;
            }
            actual = actual.siguienteTurno;
        }
        throw new RuntimeException("No hay pilotos LIBRES disponibles en este momento");
    }

    public void liberarPiloto(Piloto piloto) {
        Piloto encontrado = buscarPorCui(piloto.getCui());
        if (encontrado != null) {
            encontrado.setEstado("LIBRE");
        }
    }

    public boolean cambiarEstado(String cui, String estado) {
        Piloto p = buscarPorCui(cui);
        if (p == null) return false;
        p.setEstado(estado);
        return true;
    }

    /**
     * Elimina fisicamente un piloto de la lista enlazada por su CUI.
     * @return true si se encontro y elimino, false si no existe
     */
    public boolean eliminar(String cui) {
        if (primero == null) return false;

        // Caso: el primero es el que se elimina
        if (cui.equals(primero.piloto.getCui())) {
            primero = primero.siguienteTurno;
            if (primero == null) ultimo = null;
            totalPilotos--;
            return true;
        }

        // Buscar en el resto de la lista
        TurnoDePiloto actual = primero;
        while (actual.siguienteTurno != null) {
            if (cui.equals(actual.siguienteTurno.piloto.getCui())) {
                if (actual.siguienteTurno == ultimo) ultimo = actual;
                actual.siguienteTurno = actual.siguienteTurno.siguienteTurno;
                totalPilotos--;
                return true;
            }
            actual = actual.siguienteTurno;
        }
        return false;
    }

    public Piloto buscarPorCui(String cui) {
        TurnoDePiloto actual = primero;
        while (actual != null) {
            if (cui.equals(actual.piloto.getCui())) return actual.piloto;
            actual = actual.siguienteTurno;
        }
        return null;
    }

    public List<Piloto> obtenerTodos() {
        List<Piloto> resultado = new ArrayList<>();
        TurnoDePiloto actual   = primero;
        while (actual != null) {
            resultado.add(actual.piloto);
            actual = actual.siguienteTurno;
        }
        return resultado;
    }

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