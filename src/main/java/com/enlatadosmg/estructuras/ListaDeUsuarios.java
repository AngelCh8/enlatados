package com.enlatadosmg.estructuras;

import com.enlatadosmg.model.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * ============================================================
 * ESTRUCTURA: ListaDeUsuarios
 * ============================================================
 * Lista enlazada simple que almacena los usuarios del sistema.
 * Cada enlace de la cadena es un EnlaceDeUsuario (nodo semantico).
 *
 * Operaciones disponibles:
 *   - agregar()   → inserta al final
 *   - buscar()    → busca por condicion
 *   - eliminar()  → elimina por condicion
 *   - obtenerTodos() → retorna lista completa
 * ============================================================
 */
public class ListaDeUsuarios {

    /**
     * EnlaceDeUsuario: nodo interno de la lista.
     * Se llama "Enlace" porque conecta un usuario con el siguiente.
     * Nombre semantico: deja claro que este nodo pertenece a usuarios.
     */
    private static class EnlaceDeUsuario {
        Usuario usuario;
        EnlaceDeUsuario siguiente;

        EnlaceDeUsuario(Usuario usuario) {
            this.usuario   = usuario;
            this.siguiente = null;
        }
    }

    /** Primer enlace de la cadena de usuarios */
    private EnlaceDeUsuario primero;

    /** Cantidad de usuarios en la lista */
    private int totalUsuarios;

    public ListaDeUsuarios() {
        this.primero       = null;
        this.totalUsuarios = 0;
    }

    /** Agrega un usuario al final de la lista */
    public void agregar(Usuario usuario) {
        EnlaceDeUsuario nuevoEnlace = new EnlaceDeUsuario(usuario);
        if (primero == null) {
            primero = nuevoEnlace;
        } else {
            EnlaceDeUsuario actual = primero;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevoEnlace;
        }
        totalUsuarios++;
    }

    /** Busca un usuario que cumpla la condicion dada */
    public Usuario buscar(Predicate<Usuario> condicion) {
        EnlaceDeUsuario actual = primero;
        while (actual != null) {
            if (condicion.test(actual.usuario)) return actual.usuario;
            actual = actual.siguiente;
        }
        return null;
    }

    /** Elimina el primer usuario que cumpla la condicion */
    public boolean eliminar(Predicate<Usuario> condicion) {
        if (primero == null) return false;
        if (condicion.test(primero.usuario)) {
            primero = primero.siguiente;
            totalUsuarios--;
            return true;
        }
        EnlaceDeUsuario actual = primero;
        while (actual.siguiente != null) {
            if (condicion.test(actual.siguiente.usuario)) {
                actual.siguiente = actual.siguiente.siguiente;
                totalUsuarios--;
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    /** Retorna todos los usuarios como lista Java */
    public List<Usuario> obtenerTodos() {
        List<Usuario> resultado = new ArrayList<>();
        EnlaceDeUsuario actual  = primero;
        while (actual != null) {
            resultado.add(actual.usuario);
            actual = actual.siguiente;
        }
        return resultado;
    }

    public int getTotalUsuarios() { return totalUsuarios; }
    public boolean estaVacia()    { return primero == null; }
}
