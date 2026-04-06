package com.enlatadosmg.model;

/**
 * ============================================================
 * CLASE: Piloto
 * ============================================================
 * HERENCIA:   extends Persona  → reutiliza nombre, apellidos, telefono.
 * INTERFACE:  implements Asignable → garantiza estaDisponible().
 * OPERADOR:   super() en constructor → inicializa Persona sin repetir.
 *
 * Estado: LIBRE | OCUPADO | DE_VACACIONES
 * Nunca se elimina — solo cambia estado.
 * ============================================================
 */
public class Piloto extends Persona implements Asignable {

    private String cui;
    private String licencia; // A, B o C
    private String estado;

    /** Constructor vacio — Spring lo usa al deserializar JSON.
     *  Inicializa estado LIBRE para evitar null. */
    public Piloto() {
        this.estado = "LIBRE";
    }

    /**
     * Constructor completo con super() — reutiliza Persona.
     */
    public Piloto(String cui, String nombre, String apellidos,
                  String licencia, String telefono) {
        super(nombre, apellidos, telefono); // reutiliza Persona via super
        this.cui      = cui;
        this.licencia = licencia;
        this.estado   = "LIBRE";
    }

    /** POLIMORFISMO via Identificable */
    @Override
    public String obtenerIdentificacion() {
        return "CUI: " + cui + " | Licencia: " + licencia;
    }

    /** INTERFACE Asignable */
    @Override
    public boolean estaDisponible() { return "LIBRE".equals(this.estado); }

    // ── Getters y Setters ────────────────────────
    public String getCui()                  { return cui; }
    public void setCui(String c)            { this.cui = c; }
    public String getLicencia()             { return licencia; }
    public void setLicencia(String l)       { this.licencia = l; }
    public String getEstado()               { return estado != null ? estado : "LIBRE"; }
    public void setEstado(String e)         { this.estado = e; }

    @Override
    public String toString() {
        return "Piloto{cui='" + cui + "', nombre='" + obtenerNombreCompleto()
               + "', licencia='" + licencia + "', estado='" + getEstado() + "'}";
    }
}
