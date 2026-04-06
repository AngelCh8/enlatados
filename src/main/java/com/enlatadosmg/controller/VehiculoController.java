package com.enlatadosmg.controller;

import com.enlatadosmg.model.Vehiculo;
import com.enlatadosmg.service.VehiculoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Map<String, Object> body) {
        try {
            Vehiculo v = new Vehiculo(
                body.get("placa").toString(),
                body.get("marca").toString(),
                body.get("modelo").toString(),
                body.get("color").toString(),
                Integer.parseInt(body.get("anio").toString()),
                body.get("tipoTransmision").toString(),
                Integer.parseInt(body.getOrDefault("capacidadMaxCajas", "50").toString())
            );
            return ResponseEntity.ok(vehiculoService.registrar(v));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Vehiculo>> listar() {
        return ResponseEntity.ok(vehiculoService.obtenerTodos());
    }

    @GetMapping("/disponibles")
    public ResponseEntity<Map<String, Integer>> disponibles() {
        return ResponseEntity.ok(Map.of(
            "libres", vehiculoService.getCantidadLibres(),
            "total",  vehiculoService.getCantidad()
        ));
    }

    @PutMapping("/{placa}")
    public ResponseEntity<?> editar(@PathVariable String placa,
                                     @RequestBody Map<String, Object> body) {
        try {
            Vehiculo v = vehiculoService.editar(
                placa,
                body.containsKey("marca")              ? body.get("marca").toString()             : null,
                body.containsKey("modelo")             ? body.get("modelo").toString()            : null,
                body.containsKey("color")              ? body.get("color").toString()             : null,
                body.containsKey("anio")               ? Integer.parseInt(body.get("anio").toString()) : null,
                body.containsKey("tipoTransmision")    ? body.get("tipoTransmision").toString()   : null,
                body.containsKey("capacidadMaxCajas")  ? Integer.parseInt(body.get("capacidadMaxCajas").toString()) : null
            );
            return ResponseEntity.ok(v);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{placa}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable String placa,
                                            @RequestBody Map<String, String> body) {
        try {
            vehiculoService.cambiarEstado(placa, body.get("estado"));
            return ResponseEntity.ok("Estado del vehiculo " + placa + " actualizado a: " + body.get("estado"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
