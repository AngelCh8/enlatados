package com.enlatadosmg.controller;

import com.enlatadosmg.model.PedidoDeEntrega;
import com.enlatadosmg.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    /**
     * Crea un pedido con multiples productos.
     * Body:
     * {
     *   "cuiCliente": "1234567890101",
     *   "departamentoOrigen": "Guatemala",
     *   "departamentoDestino": "Quetzaltenango",
     *   "lineas": [
     *     { "codigoProducto": "PROD-001", "cantidadCajas": 10 },
     *     { "codigoProducto": "PROD-002", "cantidadCajas": 5 }
     *   ]
     * }
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> body) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> lineas = (List<Map<String, Object>>) body.get("lineas");

            List<PedidoDeEntrega> pedidos = pedidoService.crearPedidoMultiProducto(
                body.get("cuiCliente").toString(),
                body.get("departamentoOrigen").toString(),
                body.get("departamentoDestino").toString(),
                lineas
            );

            if (pedidos.size() == 1) return ResponseEntity.ok(pedidos.get(0));

            // Si se dividio en varios pedidos por capacidad
            return ResponseEntity.ok(Map.of(
                "mensaje", "Pedido dividido en " + pedidos.size() + " envios por capacidad de camion",
                "pedidos", pedidos
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<PedidoDeEntrega>> listar() {
        return ResponseEntity.ok(pedidoService.obtenerTodos());
    }

    @GetMapping("/{numero}")
    public ResponseEntity<?> obtener(@PathVariable int numero) {
        PedidoDeEntrega p = pedidoService.buscarPorNumero(numero);
        if (p == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(p);
    }

    @PatchMapping("/{numero}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable int numero,
                                            @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(pedidoService.cambiarEstado(
                numero, body.get("estado"),
                body.getOrDefault("observaciones", "")
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
