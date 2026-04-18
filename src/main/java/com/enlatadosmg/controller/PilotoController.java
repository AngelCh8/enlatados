package com.enlatadosmg.controller;

import com.enlatadosmg.model.Piloto;
import com.enlatadosmg.service.PilotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/pilotos")
public class PilotoController {

    private final PilotoService pilotoService;

    public PilotoController(PilotoService pilotoService) {
        this.pilotoService = pilotoService;
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Piloto piloto) {
        try {
            return ResponseEntity.ok(pilotoService.registrar(piloto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Piloto>> listar() {
        return ResponseEntity.ok(pilotoService.obtenerTodos());
    }

    @GetMapping("/disponibles")
    public ResponseEntity<Map<String, Integer>> disponibles() {
        return ResponseEntity.ok(Map.of(
                "libres", pilotoService.getCantidadLibres(),
                "total",  pilotoService.getCantidad()
        ));
    }

    @PutMapping("/{cui}")
    public ResponseEntity<?> editar(@PathVariable String cui,
                                    @RequestBody Map<String, String> body) {
        try {
            Piloto p = pilotoService.editar(
                    cui, body.get("nombre"), body.get("apellidos"),
                    body.get("licencia"), body.get("telefono")
            );
            return ResponseEntity.ok(p);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Cambia el estado de un piloto (LIBRE, OCUPADO, DE_VACACIONES).
     * CORREGIDO: Solo bloquea si tiene pedido activo real.
     */
    @PatchMapping("/{cui}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable String cui,
                                           @RequestBody Map<String, String> body) {
        try {
            pilotoService.cambiarEstado(cui, body.get("estado"));
            return ResponseEntity.ok("Estado del piloto actualizado a: " + body.get("estado"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Elimina un piloto del sistema.
     * No se puede eliminar si tiene un pedido activo.
     */
    @DeleteMapping("/{cui}")
    public ResponseEntity<?> eliminar(@PathVariable String cui) {
        try {
            pilotoService.eliminar(cui);
            return ResponseEntity.ok("Piloto " + cui + " eliminado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}