package com.enlatadosmg.model;

/**
 * ============================================================
 * CLASE: CatalogoDeProductos
 * ============================================================
 * Fuente de verdad de cada tipo de producto.
 * codigoProducto es UNICO — validado al registrar.
 * precioUnitario → precio por caja (usado en pedidos).
 * ============================================================
 */
public class CatalogoDeProductos {

    private String codigoProducto;
    private String nombreProducto;
    private String descripcion;
    private double pesoKgPorCaja;
    private int    unidadesPorCaja;
    private double precioUnitario;   // precio por caja
    private int    cajasEnStock;

    public CatalogoDeProductos() {}

    public CatalogoDeProductos(String codigoProducto, String nombreProducto,
                                String descripcion, double pesoKgPorCaja,
                                int unidadesPorCaja, double precioUnitario) {
        this.codigoProducto  = codigoProducto;
        this.nombreProducto  = nombreProducto;
        this.descripcion     = descripcion;
        this.pesoKgPorCaja   = pesoKgPorCaja;
        this.unidadesPorCaja = unidadesPorCaja;
        this.precioUnitario  = precioUnitario;
        this.cajasEnStock    = 0;
    }

    public void sumarCajas(int cantidad)  { this.cajasEnStock += cantidad; }

    public void restarCajas(int cantidad) {
        if (cantidad > this.cajasEnStock)
            throw new RuntimeException("Stock insuficiente para " + codigoProducto
                + ". Disponibles: " + cajasEnStock + ", solicitadas: " + cantidad);
        this.cajasEnStock -= cantidad;
    }

    public String getCodigoProducto()               { return codigoProducto; }
    public void setCodigoProducto(String c)         { this.codigoProducto = c; }
    public String getNombreProducto()               { return nombreProducto; }
    public void setNombreProducto(String n)         { this.nombreProducto = n; }
    public String getDescripcion()                  { return descripcion; }
    public void setDescripcion(String d)            { this.descripcion = d; }
    public double getPesoKgPorCaja()                { return pesoKgPorCaja; }
    public void setPesoKgPorCaja(double p)          { this.pesoKgPorCaja = p; }
    public int getUnidadesPorCaja()                 { return unidadesPorCaja; }
    public void setUnidadesPorCaja(int u)           { this.unidadesPorCaja = u; }
    public double getPrecioUnitario()               { return precioUnitario; }
    public void setPrecioUnitario(double p)         { this.precioUnitario = p; }
    public int getCajasEnStock()                    { return cajasEnStock; }
    public void setCajasEnStock(int c)              { this.cajasEnStock = c; }

    @Override
    public String toString() {
        return "CatalogoDeProductos{codigo='" + codigoProducto + "', nombre='" + nombreProducto
               + "', stock=" + cajasEnStock + ", precio=" + precioUnitario + "}";
    }
}
