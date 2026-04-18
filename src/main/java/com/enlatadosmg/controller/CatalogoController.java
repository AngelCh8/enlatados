package com.enlatadosmg.controller;

import com.enlatadosmg.model.CatalogoDeProductos;
import com.enlatadosmg.service.CatalogoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/catalogo")
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Map<String, Object> body) {
        try {
            CatalogoDeProductos prod = catalogoService.registrarProducto(
                    body.get("codigoProducto").toString(),
                    body.get("nombreProducto").toString(),
                    body.getOrDefault("descripcion", "").toString(),
                    Double.parseDouble(body.get("pesoKgPorCaja").toString()),
                    Integer.parseInt(body.get("unidadesPorCaja").toString()),
                    Double.parseDouble(body.getOrDefault("precioUnitario", "0").toString())
            );
            return ResponseEntity.ok(prod);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<CatalogoDeProductos>> listar() {
        return ResponseEntity.ok(catalogoService.obtenerTodos());
    }

    @GetMapping("/con-stock")
    public ResponseEntity<List<CatalogoDeProductos>> listarConStock() {
        return ResponseEntity.ok(catalogoService.obtenerConStock());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<CatalogoDeProductos>> buscar(@RequestParam String q) {
        return ResponseEntity.ok(catalogoService.buscarPorTexto(q));
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<?> obtener(@PathVariable String codigo) {
        CatalogoDeProductos p = catalogoService.buscarPorCodigo(codigo);
        if (p == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(p);
    }

    /**
     * Elimina un producto del catalogo.
     * No se puede eliminar si tiene cajas en stock.
     */
    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> eliminar(@PathVariable String codigo) {
        try {
            catalogoService.eliminar(codigo);
            return ResponseEntity.ok("Producto " + codigo + " eliminado del catalogo.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}