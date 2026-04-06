package com.enlatadosmg.model;

/**
 * ============================================================
 * CLASE: Usuario
 * ============================================================
 * HERENCIA:  extends Persona → reutiliza nombre, apellidos.
 * OPERADOR:  super() en constructor.
 * ID se genera automaticamente por el sistema.
 * ============================================================
 */
public class Usuario extends Persona {

    private int    id;
    private String contrasena;

    public Usuario() {}

    public Usuario(int id, String nombre, String apellidos, String contrasena) {
        super(nombre, apellidos, null); // reutiliza Persona via super
        this.id         = id;
        this.contrasena = contrasena;
    }

    @Override
    public String obtenerIdentificacion() { return "ID: " + id; }

    public int    getId()                           { return id; }
    public void   setId(int id)                     { this.id = id; }
    public String getContrasena()                   { return contrasena; }
    public void   setContrasena(String c)           { this.contrasena = c; }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nombre='" + obtenerNombreCompleto() + "'}";
    }
}
