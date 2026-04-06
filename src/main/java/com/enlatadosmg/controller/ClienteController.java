package com.enlatadosmg.controller;

import com.enlatadosmg.model.Cliente;
import com.enlatadosmg.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Cliente cliente) {
        try {
            return ResponseEntity.ok(clienteService.agregar(cliente));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listar() {
        return ResponseEntity.ok(clienteService.obtenerTodos());
    }

    @GetMapping("/{cui}")
    public ResponseEntity<?> obtener(@PathVariable String cui) {
        Cliente c = clienteService.buscarPorCui(cui);
        if (c == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(c);
    }

    @PutMapping("/{cui}")
    public ResponseEntity<?> modificar(@PathVariable String cui, @RequestBody Cliente cliente) {
        if (clienteService.buscarPorCui(cui) == null) return ResponseEntity.notFound().build();
        cliente.setCui(cui);
        clienteService.modificar(cliente);
        return ResponseEntity.ok(cliente);
    }

    @DeleteMapping("/{cui}")
    public ResponseEntity<String> eliminar(@PathVariable String cui) {
        if (!clienteService.eliminar(cui)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Cliente eliminado");
    }
}
