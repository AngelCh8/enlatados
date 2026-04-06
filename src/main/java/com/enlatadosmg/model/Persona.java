package com.enlatadosmg.model;

/**
 * ============================================================
 * CLASE ABSTRACTA: Persona
 * ============================================================
 * OPERADOR: abstract → clase base con logica comun para
 *   Usuario, Cliente y Piloto.
 * OPERADOR: implements Identificable → contrato que obliga
 *   a cada subclase a definir su propia identificacion.
 *
 * Campos heredados via protected (accesibles en subclases
 * sin redeclararlos — encapsulamiento + herencia).
 * ============================================================
 */
public abstract class Persona implements Identificable {

    protected String nombre;
    protected String apellidos;
    protected String telefono;

    public Persona() {}

    /**
     * Constructor reutilizable.
     * Las subclases lo invocan con super() — evita repetir
     * la inicializacion de nombre/apellidos/telefono.
     */
    public Persona(String nombre, String apellidos, String telefono) {
        this.nombre    = nombre;
        this.apellidos = apellidos;
        this.telefono  = telefono;
    }

    /**
     * POLIMORFISMO via interface Identificable.
     * Cada subclase implementa su propia version.
     */
    @Override
    public abstract String obtenerIdentificacion();

    /** Metodo heredado — no se repite en ninguna subclase */
    public String obtenerNombreCompleto() {
        return (nombre != null ? nombre : "") + " " + (apellidos != null ? apellidos : "");
    }

    // ── Getters y Setters ────────────────────────
    public String getNombre()               { return nombre; }
    public void setNombre(String n)         { this.nombre = n; }
    public String getApellidos()            { return apellidos; }
    public void setApellidos(String a)      { this.apellidos = a; }
    public String getTelefono()             { return telefono; }
    public void setTelefono(String t)       { this.telefono = t; }
}
