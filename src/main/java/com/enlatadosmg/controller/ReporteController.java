package com.enlatadosmg.controller;

import com.enlatadosmg.reporte.GraphvizService;
import com.enlatadosmg.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final UsuarioService  usuarioService;
    private final ClienteService  clienteService;
    private final PilotoService   pilotoService;
    private final VehiculoService vehiculoService;
    private final AlmacenService  almacenService;
    private final PedidoService   pedidoService;
    private final GraphvizService graphvizService;

    public ReporteController(UsuarioService u, ClienteService c, PilotoService p,
                             VehiculoService v, AlmacenService a, PedidoService pe,
                             GraphvizService g) {
        this.usuarioService=u; this.clienteService=c; this.pilotoService=p;
        this.vehiculoService=v; this.almacenService=a; this.pedidoService=pe;
        this.graphvizService=g;
    }

    @GetMapping("/usuarios")  public ResponseEntity<?> rUsuarios()  { return ResponseEntity.ok(Map.of("total",usuarioService.obtenerTodos().size(),"datos",usuarioService.obtenerTodos())); }
    @GetMapping("/clientes")  public ResponseEntity<?> rClientes()  { return ResponseEntity.ok(Map.of("total",clienteService.obtenerTodos().size(),"datos",clienteService.obtenerTodos())); }
    @GetMapping("/inventario") public ResponseEntity<?> rInventario(){ return ResponseEntity.ok(Map.of("cajasDisponibles",almacenService.getClass(),"datos",almacenService.obtenerInventario())); }
    @GetMapping("/pilotos")   public ResponseEntity<?> rPilotos()   { return ResponseEntity.ok(Map.of("disponibles",pilotoService.getCantidad(),"datos",pilotoService.obtenerTodos())); }
    @GetMapping("/vehiculos") public ResponseEntity<?> rVehiculos() { return ResponseEntity.ok(Map.of("disponibles",vehiculoService.getCantidad(),"datos",vehiculoService.obtenerTodos())); }
    @GetMapping("/pedidos")   public ResponseEntity<?> rPedidos()   { return ResponseEntity.ok(Map.of("total",pedidoService.obtenerTodos().size(),"datos",pedidoService.obtenerTodos())); }

    @GetMapping("/dot/usuarios")  public ResponseEntity<?> dotU() { return ResponseEntity.ok(Map.of("dot",graphvizService.dotListaUsuarios())); }
    @GetMapping("/dot/almacen")   public ResponseEntity<?> dotA() { return ResponseEntity.ok(Map.of("dot",graphvizService.dotPilaAlmacen())); }
    @GetMapping("/dot/clientes")  public ResponseEntity<?> dotC() { return ResponseEntity.ok(Map.of("dot",graphvizService.dotArbolClientes())); }
    @GetMapping("/dot/pilotos")   public ResponseEntity<?> dotP() { return ResponseEntity.ok(Map.of("dot",graphvizService.dotColaPilotos())); }
    @GetMapping("/dot/vehiculos") public ResponseEntity<?> dotV() { return ResponseEntity.ok(Map.of("dot",graphvizService.dotColaVehiculos())); }
    @GetMapping("/dot/pedidos")   public ResponseEntity<?> dotPe(){ return ResponseEntity.ok(Map.of("dot",graphvizService.dotListaPedidos())); }
}
