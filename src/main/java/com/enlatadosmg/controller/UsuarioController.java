package com.enlatadosmg.controller;

import com.enlatadosmg.model.Usuario;
import com.enlatadosmg.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Map<String, String> body) {
        try {
            Usuario u = usuarioService.registrar(
                body.get("nombre"), body.get("apellidos"), body.get("contrasena")
            );
            return ResponseEntity.ok(Map.of(
                "mensaje",   "Cuenta creada exitosamente.",
                "id",        u.getId(),
                "nombre",    u.getNombre()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            Usuario u = usuarioService.login(body.get("nombre"), body.get("contrasena"));
            return ResponseEntity.ok(Map.of(
                "mensaje", "Bienvenido, " + u.obtenerNombreCompleto() + ".",
                "id",      u.getId(),
                "nombre",  u.getNombre()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable int id,
                                     @RequestBody Map<String, String> body) {
        try {
            Usuario u = usuarioService.editar(
                id, body.get("nombre"), body.get("apellidos"), body.get("contrasena")
            );
            return ResponseEntity.ok(u);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable int id) {
        try {
            usuarioService.eliminar(id);
            return ResponseEntity.ok("Usuario #" + id + " eliminado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
