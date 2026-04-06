package com.enlatadosmg.service;

import com.enlatadosmg.model.Usuario;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * OPERADOR: @Service + @Component (DataStore) → inyeccion de dependencias.
 * Spring inyecta DataStore automaticamente — no se necesita new DataStore().
 */
@Service
public class UsuarioService {

    private final DataStore dataStore;

    public UsuarioService(DataStore dataStore) { this.dataStore = dataStore; }

    /** Registra usuario. ID asignado automaticamente. */
    public Usuario registrar(String nombre, String apellidos, String contrasena) {
        if (nombre == null || nombre.isBlank())
            throw new RuntimeException("El nombre no puede estar vacio.");
        if (contrasena == null || contrasena.length() < 4)
            throw new RuntimeException("La contrasena debe tener al menos 4 caracteres.");

        int idGenerado = dataStore.siguienteIdUsuario();
        Usuario usuario = new Usuario(idGenerado, nombre.trim(), apellidos, contrasena);
        dataStore.getListaDeUsuarios().agregar(usuario);
        return usuario;
    }

    /** Login con mensajes claros segun el error */
    public Usuario login(String nombre, String contrasena) {
        Usuario porNombre = dataStore.getListaDeUsuarios()
            .buscar(u -> u.getNombre().equalsIgnoreCase(nombre));
        if (porNombre == null)
            throw new RuntimeException("No existe ningun usuario con el nombre: '" + nombre + "'.");
        if (!porNombre.getContrasena().equals(contrasena))
            throw new RuntimeException("Contrasena incorrecta para el usuario '" + nombre + "'. Intenta de nuevo.");
        return porNombre;
    }

    /** Edita nombre, apellidos y contrasena de un usuario */
    public Usuario editar(int id, String nombre, String apellidos, String contrasena) {
        Usuario u = buscarPorId(id);
        if (u == null)
            throw new RuntimeException("No existe ningun usuario con ID " + id + ".");
        if (nombre != null && !nombre.isBlank())     u.setNombre(nombre.trim());
        if (apellidos != null)                        u.setApellidos(apellidos.trim());
        if (contrasena != null && !contrasena.isBlank()) {
            if (contrasena.length() < 4)
                throw new RuntimeException("La nueva contrasena debe tener al menos 4 caracteres.");
            u.setContrasena(contrasena);
        }
        return u;
    }

    public List<Usuario> obtenerTodos()  { return dataStore.getListaDeUsuarios().obtenerTodos(); }

    public boolean eliminar(int id) {
        if (buscarPorId(id) == null)
            throw new RuntimeException("No existe ningun usuario con ID " + id + ".");
        return dataStore.getListaDeUsuarios().eliminar(u -> u.getId() == id);
    }

    public Usuario buscarPorId(int id) {
        return dataStore.getListaDeUsuarios().buscar(u -> u.getId() == id);
    }
}
