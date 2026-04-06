package com.enlatadosmg.model;

/**
 * ============================================================
 * INTERFACE: Identificable
 * ============================================================
 * Contrato que deben cumplir todas las entidades del sistema
 * que tienen un identificador unico.
 *
 * OPERADOR: interface → define el contrato sin implementacion.
 * Garantiza que Usuario, Cliente y Piloto siempre expongan
 * su identificacion de forma consistente.
 * ============================================================
 */
public interface Identificable {
    /** Retorna la identificacion unica de la entidad */
    String obtenerIdentificacion();
}
