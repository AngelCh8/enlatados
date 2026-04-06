package com.enlatadosmg.model;

/**
 * ============================================================
 * CLASE: Vehiculo
 * ============================================================
 * INTERFACE: implements Asignable → misma garantia que Piloto:
 *   estaDisponible(), getEstado(), setEstado() siempre presentes.
 *
 * Estado: LIBRE | OCUPADO | FUERA_DE_SERVICIO
 * capacidadMaxCajas → tope fisico del camion.
 * cajasOcupadas     → cuantas cajas lleva actualmente.
 * ============================================================
 */
public class Vehiculo implements Asignable {

    private String placa;
    private String marca;
    private String modelo;
    private String color;
    private int    anio;
    private String tipoTransmision;
    private String estado;
    private int    capacidadMaxCajas;
    private int    cajasOcupadas;

    public Vehiculo() {
        this.estado        = "LIBRE";
        this.cajasOcupadas = 0;
    }

    public Vehiculo(String placa, String marca, String modelo,
                    String color, int anio, String tipoTransmision,
                    int capacidadMaxCajas) {
        this.placa             = placa;
        this.marca             = marca;
        this.modelo            = modelo;
        this.color             = color;
        this.anio              = anio;
        this.tipoTransmision   = tipoTransmision;
        this.capacidadMaxCajas = capacidadMaxCajas;
        this.cajasOcupadas     = 0;
        this.estado            = "LIBRE";
    }

    /** INTERFACE Asignable */
    @Override
    public boolean estaDisponible() { return "LIBRE".equals(this.estado); }

    public int  getEspacioDisponible()  { return capacidadMaxCajas - cajasOcupadas; }
    public void ocuparEspacio(int n)    { this.cajasOcupadas += n; }
    public void liberarEspacio(int n)   { this.cajasOcupadas = Math.max(0, cajasOcupadas - n); }

    // ── Getters y Setters ────────────────────────
    public String getPlaca()                    { return placa; }
    public void setPlaca(String p)              { this.placa = p; }
    public String getMarca()                    { return marca; }
    public void setMarca(String m)              { this.marca = m; }
    public String getModelo()                   { return modelo; }
    public void setModelo(String m)             { this.modelo = m; }
    public String getColor()                    { return color; }
    public void setColor(String c)              { this.color = c; }
    public int getAnio()                        { return anio; }
    public void setAnio(int a)                  { this.anio = a; }
    public String getTipoTransmision()          { return tipoTransmision; }
    public void setTipoTransmision(String t)    { this.tipoTransmision = t; }
    public String getEstado()                   { return estado != null ? estado : "LIBRE"; }
    public void setEstado(String e)             { this.estado = e; }
    public int getCapacidadMaxCajas()           { return capacidadMaxCajas; }
    public void setCapacidadMaxCajas(int c)     { this.capacidadMaxCajas = c; }
    public int getCajasOcupadas()               { return cajasOcupadas; }
    public void setCajasOcupadas(int c)         { this.cajasOcupadas = c; }

    @Override
    public String toString() {
        return "Vehiculo{placa='" + placa + "', capacidad=" + capacidadMaxCajas
               + ", ocupadas=" + cajasOcupadas + ", estado='" + getEstado() + "'}";
    }
}
