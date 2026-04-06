package com.enlatadosmg.model;

/**
 * ============================================================
 * INTERFACE: Asignable
 * ============================================================
 * Contrato para entidades que pueden ser asignadas/liberadas
 * en pedidos: Piloto y Vehiculo.
 *
 * OPERADOR: interface → garantiza que ambos tipos siempre
 * implementen los mismos metodos de disponibilidad,
 * evitando ambiguedad en el servicio de pedidos.
 * ============================================================
 */
public interface Asignable {
    boolean estaDisponible();
    String  getEstado();
    void    setEstado(String estado);
}
