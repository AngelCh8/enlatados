package com.enlatadosmg.model;

/**
 * ============================================================
 * CLASE: Cliente
 * ============================================================
 * HERENCIA:  extends Persona → reutiliza nombre, apellidos, telefono.
 * OPERADOR:  super() en constructor.
 * Almacenado en ArbolDeClientes (AVL, llave: cui).
 * ============================================================
 */
public class Cliente extends Persona {

    private String cui;
    private String direccion;

    public Cliente() {}

    public Cliente(String cui, String nombre, String apellidos,
                   String telefono, String direccion) {
        super(nombre, apellidos, telefono); // reutiliza Persona via super
        this.cui       = cui;
        this.direccion = direccion;
    }

    @Override
    public String obtenerIdentificacion() { return "CUI: " + cui; }

    public String getCui()                      { return cui; }
    public void   setCui(String c)              { this.cui = c; }
    public String getDireccion()                { return direccion; }
    public void   setDireccion(String d)        { this.direccion = d; }

    @Override
    public String toString() {
        return "Cliente{cui='" + cui + "', nombre='" + obtenerNombreCompleto() + "'}";
    }
}
