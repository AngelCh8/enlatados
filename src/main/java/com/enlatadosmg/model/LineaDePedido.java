package com.enlatadosmg.model;

/**
 * ============================================================
 * CLASE: LineaDePedido
 * ============================================================
 * Representa un item dentro de un pedido:
 * un producto especifico con su cantidad y precio.
 * Un pedido puede tener multiples LineaDePedido.
 * ============================================================
 */
public class LineaDePedido {

    private String codigoProducto;
    private String nombreProducto;
    private int    cantidadCajas;
    private double precioUnitario;   // precio por caja al momento del pedido
    private double subtotal;         // cantidadCajas * precioUnitario

    public LineaDePedido() {}

    public LineaDePedido(String codigoProducto, String nombreProducto,
                          int cantidadCajas, double precioUnitario) {
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.cantidadCajas  = cantidadCajas;
        this.precioUnitario = precioUnitario;
        this.subtotal       = cantidadCajas * precioUnitario;
    }

    public String getCodigoProducto()                   { return codigoProducto; }
    public void setCodigoProducto(String c)             { this.codigoProducto = c; }
    public String getNombreProducto()                   { return nombreProducto; }
    public void setNombreProducto(String n)             { this.nombreProducto = n; }
    public int getCantidadCajas()                       { return cantidadCajas; }
    public void setCantidadCajas(int c)                 { this.cantidadCajas = c; recalcular(); }
    public double getPrecioUnitario()                   { return precioUnitario; }
    public void setPrecioUnitario(double p)             { this.precioUnitario = p; recalcular(); }
    public double getSubtotal()                         { return subtotal; }

    private void recalcular()                           { this.subtotal = cantidadCajas * precioUnitario; }

    @Override
    public String toString() {
        return "LineaDePedido{producto='" + codigoProducto + "', cajas=" + cantidadCajas
               + ", precio=" + precioUnitario + ", subtotal=" + subtotal + "}";
    }
}
