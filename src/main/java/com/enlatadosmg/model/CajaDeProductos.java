package com.enlatadosmg.model;

import java.time.LocalDateTime;

/**
 * ============================================================
 * CLASE: CajaDeProductos
 * ============================================================
 * Representa un LOTE de cajas de un mismo producto en el almacen.
 * Un lote puede contener N cajas (cantidadCajas).
 *
 * codigoCaja      → se genera AUTOMATICAMENTE (CAJA-0001, etc.)
 * codigoProducto  → referencia al CatalogoDeProductos
 * cantidadCajas   → numero de cajas en este lote (puede ser 500+)
 * pesoKgPorCaja   → tomado del catalogo al momento del ingreso
 *
 * Almacenado en: PilaDeAlmacen (LIFO por lote)
 * ============================================================
 */
public class CajaDeProductos {

    /** ID correlativo unico generado automaticamente */
    private int id;

    /** Codigo del lote generado automaticamente. Ej: CAJA-0001 */
    private String codigoCaja;

    /** Codigo del producto (referencia al catalogo). Ej: PROD-001 */
    private String codigoProducto;

    /** Nombre del producto (tomado del catalogo al ingresar) */
    private String producto;

    /** Numero de cajas en este lote */
    private int cantidadCajas;

    /** Peso por caja en kg (tomado del catalogo) */
    private double pesoKgPorCaja;

    /** Fecha y hora de ingreso al almacen */
    private LocalDateTime fechaIngreso;

    /** Estado: DISPONIBLE, AGOTADO, RETIRADA */
    private String estado;

    // Ubicacion fisica en el almacen
    private String zona;
    private int    pasillo;
    private int    estante;
    private int    nivel;

    public CajaDeProductos() {
        this.fechaIngreso = LocalDateTime.now();
        this.estado       = "DISPONIBLE";
    }

    public CajaDeProductos(int id, String codigoProducto, String codigoCaja,
                            String producto, int cantidadCajas, double pesoKgPorCaja,
                            String zona, int pasillo, int estante, int nivel) {
        this.id             = id;
        this.codigoProducto = codigoProducto;
        this.codigoCaja     = codigoCaja;
        this.producto       = producto;
        this.cantidadCajas  = cantidadCajas;
        this.pesoKgPorCaja  = pesoKgPorCaja;
        this.zona           = zona;
        this.pasillo        = pasillo;
        this.estante        = estante;
        this.nivel          = nivel;
        this.fechaIngreso   = LocalDateTime.now();
        this.estado         = "DISPONIBLE";
    }

    public String obtenerUbicacion() {
        return "Zona " + zona + " - Pasillo " + pasillo
               + " - Estante " + estante + " - Nivel " + nivel;
    }

    // ── Getters y Setters ────────────────────────
    public int getId()                                      { return id; }
    public void setId(int id)                               { this.id = id; }

    public String getCodigoCaja()                           { return codigoCaja; }
    public void setCodigoCaja(String c)                     { this.codigoCaja = c; }

    public String getCodigoProducto()                       { return codigoProducto; }
    public void setCodigoProducto(String c)                 { this.codigoProducto = c; }

    public String getProducto()                             { return producto; }
    public void setProducto(String producto)                { this.producto = producto; }

    public int getCantidadCajas()                           { return cantidadCajas; }
    public void setCantidadCajas(int c)                     { this.cantidadCajas = c; }

    public double getPesoKgPorCaja()                        { return pesoKgPorCaja; }
    public void setPesoKgPorCaja(double p)                  { this.pesoKgPorCaja = p; }

    public LocalDateTime getFechaIngreso()                  { return fechaIngreso; }
    public void setFechaIngreso(LocalDateTime f)            { this.fechaIngreso = f; }

    public String getEstado()                               { return estado; }
    public void setEstado(String estado)                    { this.estado = estado; }

    public String getZona()                                 { return zona; }
    public void setZona(String zona)                        { this.zona = zona; }

    public int getPasillo()                                 { return pasillo; }
    public void setPasillo(int p)                           { this.pasillo = p; }

    public int getEstante()                                 { return estante; }
    public void setEstante(int e)                           { this.estante = e; }

    public int getNivel()                                   { return nivel; }
    public void setNivel(int n)                             { this.nivel = n; }

    @Override
    public String toString() {
        return "CajaDeProductos{id=" + id + ", codigo='" + codigoCaja
               + "', producto='" + codigoProducto + "', cajas=" + cantidadCajas
               + ", ubicacion='" + obtenerUbicacion() + "', estado='" + estado + "'}";
    }
}
