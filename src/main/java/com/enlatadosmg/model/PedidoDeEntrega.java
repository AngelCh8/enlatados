package com.enlatadosmg.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * CLASE: PedidoDeEntrega
 * ============================================================
 * Un pedido puede contener MULTIPLES productos (lineas).
 * El total se calcula sumando subtotales de cada linea.
 * Un vehiculo puede compartirse entre pedidos al mismo destino
 * mientras tenga espacio disponible.
 *
 * Estados: PENDIENTE | EN_CAMINO | COMPLETADO | CANCELADO
 * ============================================================
 */
public class PedidoDeEntrega {

    private int                  numeroPedido;
    private String               departamentoOrigen;
    private String               departamentoDestino;
    private LocalDateTime        fechaHoraInicio;
    private Cliente              cliente;
    private Piloto               piloto;
    private Vehiculo             vehiculo;
    private List<LineaDePedido>  lineas;           // multiples productos
    private List<CajaDeProductos> cajasAsignadas;  // cajas fisicas retiradas
    private int                  totalCajas;
    private double               totalPedido;      // suma de todos los subtotales
    private String               estado;
    private String               observaciones;

    public PedidoDeEntrega() {
        this.lineas        = new ArrayList<>();
        this.cajasAsignadas = new ArrayList<>();
    }

    public PedidoDeEntrega(int numeroPedido, String departamentoOrigen,
                           String departamentoDestino, Cliente cliente,
                           Piloto piloto, Vehiculo vehiculo) {
        this.numeroPedido        = numeroPedido;
        this.departamentoOrigen  = departamentoOrigen;
        this.departamentoDestino = departamentoDestino;
        this.fechaHoraInicio     = LocalDateTime.now();
        this.cliente             = cliente;
        this.piloto              = piloto;
        this.vehiculo            = vehiculo;
        this.lineas              = new ArrayList<>();
        this.cajasAsignadas      = new ArrayList<>();
        this.totalCajas          = 0;
        this.totalPedido         = 0.0;
        this.estado              = "PENDIENTE";
        this.observaciones       = "";
    }

    /** Agrega una linea de producto al pedido y recalcula el total */
    public void agregarLinea(LineaDePedido linea) {
        this.lineas.add(linea);
        this.totalCajas  += linea.getCantidadCajas();
        this.totalPedido += linea.getSubtotal();
    }

    /** Agrega cajas fisicas retiradas del almacen */
    public void agregarCajas(List<CajaDeProductos> cajas) {
        if (cajas != null) this.cajasAsignadas.addAll(cajas);
    }

    // ── Getters y Setters ────────────────────────
    public int getNumeroPedido()                        { return numeroPedido; }
    public void setNumeroPedido(int n)                  { this.numeroPedido = n; }
    public String getDepartamentoOrigen()               { return departamentoOrigen; }
    public void setDepartamentoOrigen(String d)         { this.departamentoOrigen = d; }
    public String getDepartamentoDestino()              { return departamentoDestino; }
    public void setDepartamentoDestino(String d)        { this.departamentoDestino = d; }
    public LocalDateTime getFechaHoraInicio()           { return fechaHoraInicio; }
    public void setFechaHoraInicio(LocalDateTime f)     { this.fechaHoraInicio = f; }
    public Cliente getCliente()                         { return cliente; }
    public void setCliente(Cliente c)                   { this.cliente = c; }
    public Piloto getPiloto()                           { return piloto; }
    public void setPiloto(Piloto p)                     { this.piloto = p; }
    public Vehiculo getVehiculo()                       { return vehiculo; }
    public void setVehiculo(Vehiculo v)                 { this.vehiculo = v; }
    public List<LineaDePedido> getLineas()              { return lineas; }
    public void setLineas(List<LineaDePedido> l)        { this.lineas = l; }
    public List<CajaDeProductos> getCajasAsignadas()    { return cajasAsignadas; }
    public void setCajasAsignadas(List<CajaDeProductos> c) { this.cajasAsignadas = c; }
    public int getTotalCajas()                          { return totalCajas; }
    public void setTotalCajas(int t)                    { this.totalCajas = t; }
    public double getTotalPedido()                      { return totalPedido; }
    public void setTotalPedido(double t)                { this.totalPedido = t; }
    public String getEstado()                           { return estado; }
    public void setEstado(String e)                     { this.estado = e; }
    public String getObservaciones()                    { return observaciones; }
    public void setObservaciones(String o)              { this.observaciones = o; }

    @Override
    public String toString() {
        return "PedidoDeEntrega{num=" + numeroPedido + ", destino='" + departamentoDestino
               + "', cajas=" + totalCajas + ", total=Q" + totalPedido + ", estado='" + estado + "'}";
    }

    private int numeroCajas;

    public int getNumeroCajas() {
        return numeroCajas;
    }

    public void setNumeroCajas(int numeroCajas) {
        this.numeroCajas = numeroCajas;
    }
}
