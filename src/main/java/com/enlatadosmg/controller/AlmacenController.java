package com.enlatadosmg.controller;

import com.enlatadosmg.model.CajaDeProductos;
import com.enlatadosmg.service.AlmacenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/almacen")
public class AlmacenController {

    private final AlmacenService almacenService;

    public AlmacenController(AlmacenService almacenService) {
        this.almacenService = almacenService;
    }

    /**
     * Ingresa un lote de cajas al almacen.
     * codigoCaja se genera automaticamente — NO se envia en el body.
     * Body: { "codigoProducto":"PROD-001", "cantidadCajas":500,
     *         "zona":"A", "pasillo":2, "estante":5, "nivel":3 }
     */
    @PostMapping("/ingresar")
    public ResponseEntity<?> ingresarCajas(@RequestBody Map<String, Object> body) {
        try {
            String codigoProducto = (String) body.get("codigoProducto");
            int    cantidadCajas  = Integer.parseInt(body.get("cantidadCajas").toString());
            String zona           = (String) body.get("zona");
            int    pasillo        = Integer.parseInt(body.get("pasillo").toString());
            int    estante        = Integer.parseInt(body.get("estante").toString());
            int    nivel          = Integer.parseInt(body.get("nivel").toString());

            CajaDeProductos lote = almacenService.ingresarCajas(
                codigoProducto, cantidadCajas, zona, pasillo, estante, nivel
            );
            return ResponseEntity.ok(Map.of(
                "mensaje",        "Cajas ingresadas exitosamente",
                "codigoCaja",     lote.getCodigoCaja(),
                "codigoProducto", lote.getCodigoProducto(),
                "producto",       lote.getProducto(),
                "cantidadCajas",  lote.getCantidadCajas(),
                "ubicacion",      lote.obtenerUbicacion()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /** Cambia el estado de un lote por ID */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable int id,
                                            @RequestBody Map<String, String> body) {
        boolean ok = almacenService.cambiarEstadoLote(id, body.get("estado"));
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Estado actualizado");
    }

    /** Lista todo el inventario (todos los lotes) */
    @GetMapping
    public ResponseEntity<List<CajaDeProductos>> listar() {
        return ResponseEntity.ok(almacenService.obtenerInventario());
    }
}
