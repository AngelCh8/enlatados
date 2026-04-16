package com.enlatadosmg.reporte;

import com.enlatadosmg.model.*;
import com.enlatadosmg.service.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ============================================================
 * SERVICIO: GraphvizService
 * ============================================================
 * Genera codigo DOT (Graphviz) para visualizar cada estructura
 * de datos del sistema como un grafico.
 *
 * Referencia: https://graphviz.org
 *
 * Cada metodo retorna un String con el codigo DOT listo para
 * renderizarse en el frontend via API de Graphviz online.
 * ============================================================
 */
@Service
public class GraphvizService {

    private final UsuarioService  usuarioService;
    private final ClienteService  clienteService;
    private final PilotoService   pilotoService;
    private final VehiculoService vehiculoService;
    private final AlmacenService  almacenService;
    private final PedidoService   pedidoService;

    public GraphvizService(UsuarioService usuarioService, ClienteService clienteService,
                           PilotoService pilotoService, VehiculoService vehiculoService,
                           AlmacenService almacenService, PedidoService pedidoService) {
        this.usuarioService  = usuarioService;
        this.clienteService  = clienteService;
        this.pilotoService   = pilotoService;
        this.vehiculoService = vehiculoService;
        this.almacenService  = almacenService;
        this.pedidoService   = pedidoService;
    }

    /**
     * Genera grafico DOT de la Lista Enlazada de Usuarios.
     * Visualiza cada nodo con su ID y nombre, conectados en cadena.
     */
    public String dotListaUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        StringBuilder dot = new StringBuilder();
        dot.append("digraph ListaDeUsuarios {\n");
        dot.append("  rankdir=LR;\n");
        dot.append("  node [shape=record, style=filled, fillcolor=\"#E3F2FD\", fontname=\"Arial\"];\n");
        dot.append("  edge [color=\"#1A237E\"];\n");

        if (usuarios.isEmpty()) {
            dot.append("  vacio [label=\"Lista vacia\", shape=ellipse, fillcolor=\"#FFEBEE\"];\n");
        } else {
            for (int i = 0; i < usuarios.size(); i++) {
                Usuario u = usuarios.get(i);
                dot.append("  u").append(i)
                   .append(" [label=\"{ID: ").append(u.getId())
                   .append(" | ").append(escapar(u.getNombre()))
                   .append(" ").append(escapar(u.getApellidos() != null ? u.getApellidos() : ""))
                   .append(" | <next> siguiente}\"];\n");
            }
            for (int i = 0; i < usuarios.size() - 1; i++) {
                dot.append("  u").append(i).append(":next -> u").append(i + 1).append(";\n");
            }
            dot.append("  u").append(usuarios.size() - 1).append(":next -> null0;\n");
            dot.append("  null0 [label=\"NULL\", shape=ellipse, fillcolor=\"#FFCDD2\"];\n");
        }
        dot.append("}\n");
        return dot.toString();
    }

    /**
     * Genera grafico DOT de la Pila del Almacen (LIFO).
     * Visualiza las cajas apiladas de tope a base.
     */
    public String dotPilaAlmacen() {
        List<CajaDeProductos> cajas = almacenService.obtenerInventario();
        StringBuilder dot = new StringBuilder();
        dot.append("digraph PilaDeAlmacen {\n");
        dot.append("  rankdir=TB;\n");
        dot.append("  node [shape=record, style=filled, fontname=\"Arial\"];\n");
        dot.append("  edge [color=\"#1A237E\"];\n");
        dot.append("  label=\"Almacen (LIFO - tope arriba)\";\n");
        dot.append("  labelloc=t;\n");

        if (cajas.isEmpty()) {
            dot.append("  vacio [label=\"Almacen vacio\", shape=ellipse, fillcolor=\"#FFEBEE\"];\n");
        } else {
            dot.append("  tope [label=\"TOPE\", shape=ellipse, fillcolor=\"#1A237E\", fontcolor=white];\n");
            for (int i = 0; i < cajas.size(); i++) {
                CajaDeProductos c = cajas.get(i);
                String color = estadoColor(c.getEstado());
                dot.append("  c").append(i)
                        .append(" [label=\"{").append(escapar(c.getCodigoCaja()))
                        .append(" | ").append(escapar(c.getProducto()))
                        .append(" | Cajas: ").append(c.getCantidadCajas())
                        .append(" | ").append(escapar(c.obtenerUbicacion()))
                        .append(" | Estado: ").append(c.getEstado())
                        .append("}\", fillcolor=\"").append(color).append("\"];\n");
            }
            dot.append("  tope -> c0;\n");
            for (int i = 0; i < cajas.size() - 1; i++) {
                dot.append("  c").append(i).append(" -> c").append(i + 1).append(";\n");
            }
            dot.append("  c").append(cajas.size()-1).append(" -> base;\n");
            dot.append("  base [label=\"BASE\", shape=ellipse, fillcolor=\"#424242\", fontcolor=white];\n");
        }
        dot.append("}\n");
        return dot.toString();
    }

    /**
     * Genera grafico DOT del Arbol AVL de Clientes.
     * Visualiza la estructura jerarquica del arbol.
     */
    public String dotArbolClientes() {
        List<Cliente> clientes = clienteService.obtenerTodos();
        StringBuilder dot = new StringBuilder();
        dot.append("digraph ArbolDeClientes {\n");
        dot.append("  node [shape=record, style=filled, fillcolor=\"#E8F5E9\", fontname=\"Arial\"];\n");
        dot.append("  edge [color=\"#2E7D32\"];\n");
        dot.append("  label=\"Arbol AVL de Clientes (ordenado por CUI)\";\n");
        dot.append("  labelloc=t;\n");

        if (clientes.isEmpty()) {
            dot.append("  vacio [label=\"Arbol vacio\", shape=ellipse, fillcolor=\"#FFEBEE\"];\n");
        } else {
            // Construir arbol AVL para DOT
            generarNodosArbol(clientes, 0, clientes.size() - 1, dot, null);
        }
        dot.append("}\n");
        return dot.toString();
    }

    private void generarNodosArbol(List<Cliente> clientes, int inicio, int fin,
                                    StringBuilder dot, String padre) {
        if (inicio > fin) return;
        int medio = (inicio + fin) / 2;
        Cliente c = clientes.get(medio);
        String nodeId = "c" + medio;
        dot.append("  ").append(nodeId)
           .append(" [label=\"{").append(escapar(c.getCui()))
           .append(" | ").append(escapar(c.getNombre()))
           .append(" ").append(escapar(c.getApellidos() != null ? c.getApellidos() : ""))
           .append("}\"];\n");
        if (padre != null) dot.append("  ").append(padre).append(" -> ").append(nodeId).append(";\n");
        generarNodosArbol(clientes, inicio, medio - 1, dot, nodeId);
        generarNodosArbol(clientes, medio + 1, fin,    dot, nodeId);
    }

    /**
     * Genera grafico DOT de la Cola de Pilotos (FIFO).
     */
    public String dotColaPilotos() {
        List<Piloto> pilotos = pilotoService.obtenerTodos();
        StringBuilder dot = new StringBuilder();
        dot.append("digraph ColadePilotos {\n");
        dot.append("  rankdir=LR;\n");
        dot.append("  node [shape=record, style=filled, fillcolor=\"#FFF3E0\", fontname=\"Arial\"];\n");
        dot.append("  edge [color=\"#E65100\"];\n");
        dot.append("  label=\"Cola de Pilotos (FIFO)\";\n");

        if (pilotos.isEmpty()) {
            dot.append("  vacio [label=\"Cola vacia\", shape=ellipse, fillcolor=\"#FFEBEE\"];\n");
        } else {
            dot.append("  frente [label=\"FRENTE\", shape=ellipse, fillcolor=\"#E65100\", fontcolor=white];\n");
            for (int i = 0; i < pilotos.size(); i++) {
                Piloto p = pilotos.get(i);
                dot.append("  p").append(i)
                   .append(" [label=\"{").append(escapar(p.getCui()))
                   .append(" | ").append(escapar(p.getNombre()))
                   .append(" | Lic: ").append(p.getLicencia()).append("}\"];\n");
            }
            dot.append("  frente -> p0;\n");
            for (int i = 0; i < pilotos.size() - 1; i++) {
                dot.append("  p").append(i).append(" -> p").append(i + 1).append(";\n");
            }
            dot.append("  p").append(pilotos.size()-1).append(" -> final_;\n");
            dot.append("  final_ [label=\"FINAL\", shape=ellipse, fillcolor=\"#E65100\", fontcolor=white];\n");
        }
        dot.append("}\n");
        return dot.toString();
    }

    /**
     * Genera grafico DOT de la Cola de Vehiculos (FIFO).
     */
    public String dotColaVehiculos() {
        List<Vehiculo> vehiculos = vehiculoService.obtenerTodos();
        StringBuilder dot = new StringBuilder();
        dot.append("digraph ColaDeVehiculos {\n");
        dot.append("  rankdir=LR;\n");
        dot.append("  node [shape=record, style=filled, fillcolor=\"#F3E5F5\", fontname=\"Arial\"];\n");
        dot.append("  edge [color=\"#6A1B9A\"];\n");
        dot.append("  label=\"Cola de Vehiculos (FIFO)\";\n");

        if (vehiculos.isEmpty()) {
            dot.append("  vacio [label=\"Cola vacia\", shape=ellipse, fillcolor=\"#FFEBEE\"];\n");
        } else {
            dot.append("  frente [label=\"FRENTE\", shape=ellipse, fillcolor=\"#6A1B9A\", fontcolor=white];\n");
            for (int i = 0; i < vehiculos.size(); i++) {
                Vehiculo v = vehiculos.get(i);
                dot.append("  v").append(i)
                   .append(" [label=\"{").append(escapar(v.getPlaca()))
                   .append(" | ").append(escapar(v.getMarca()))
                   .append(" ").append(escapar(v.getModelo())).append("}\"];\n");
            }
            dot.append("  frente -> v0;\n");
            for (int i = 0; i < vehiculos.size() - 1; i++) {
                dot.append("  v").append(i).append(" -> v").append(i + 1).append(";\n");
            }
            dot.append("  v").append(vehiculos.size()-1).append(" -> final_;\n");
            dot.append("  final_ [label=\"FINAL\", shape=ellipse, fillcolor=\"#6A1B9A\", fontcolor=white];\n");
        }
        dot.append("}\n");
        return dot.toString();
    }

    /**
     * Genera grafico DOT de la Lista de Pedidos.
     */
    public String dotListaPedidos() {
        List<PedidoDeEntrega> pedidos = pedidoService.obtenerTodos();
        StringBuilder dot = new StringBuilder();
        dot.append("digraph ListaDePedidos {\n");
        dot.append("  rankdir=LR;\n");
        dot.append("  node [shape=record, style=filled, fontname=\"Arial\"];\n");
        dot.append("  edge [color=\"#1A237E\"];\n");

        if (pedidos.isEmpty()) {
            dot.append("  vacio [label=\"Lista vacia\", shape=ellipse, fillcolor=\"#FFEBEE\"];\n");
        } else {
            for (int i = 0; i < pedidos.size(); i++) {
                PedidoDeEntrega p = pedidos.get(i);
                String color = pedidoColor(p.getEstado());
                dot.append("  p").append(i)
                        .append(" [label=\"{Pedido ").append(p.getNumeroPedido())
                        .append(" | ").append(escapar(p.getDepartamentoOrigen()))
                        .append(" a ").append(escapar(p.getDepartamentoDestino()))
                        .append(" | ").append(p.getNumeroCajas()).append(" cajas")
                        .append(" | ").append(p.getEstado())
                        .append(" | <next> sig}\", fillcolor=\"").append(color).append("\"];\n");
            }
            for (int i = 0; i < pedidos.size() - 1; i++) {
                dot.append("  p").append(i).append(":next -> p").append(i + 1).append(";\n");
            }
            dot.append("  p").append(pedidos.size()-1).append(":next -> null0;\n");
            dot.append("  null0 [label=\"NULL\", shape=ellipse, fillcolor=\"#FFCDD2\"];\n");
        }
        dot.append("}\n");
        return dot.toString();
    }

    // ── Utilidades ───────────────────────────────

    private String escapar(String texto) {
        if (texto == null) return "";
        return texto.replace("\"", "'").replace("<", "").replace(">", "").replace("{", "").replace("}", "").replace("|", "-");
    }

    private String estadoColor(String estado) {
        switch (estado != null ? estado : "") {
            case "DISPONIBLE": return "#C8E6C9";
            case "DANADA":     return "#FFCDD2";
            case "VENDIDA":    return "#FFF9C4";
            default:           return "#E3F2FD";
        }
    }

    private String pedidoColor(String estado) {
        switch (estado != null ? estado : "") {
            case "PENDIENTE":  return "#FFF9C4";
            case "EN_CAMINO":  return "#B3E5FC";
            case "COMPLETADO": return "#C8E6C9";
            case "CANCELADO":  return "#FFCDD2";
            default:           return "#E3F2FD";
        }
    }
}
