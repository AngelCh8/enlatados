package com.enlatadosmg.service;

import com.enlatadosmg.model.Cliente;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClienteService {

    private final DataStore dataStore;

    public ClienteService(DataStore dataStore) { this.dataStore = dataStore; }

    /**
     * Agrega un cliente. Lanza error si el CUI ya existe.
     */
    public Cliente agregar(Cliente c) {
        if (dataStore.getArbolDeClientes().buscarPorCui(c.getCui()) != null)
            throw new RuntimeException("Ya existe un cliente con el CUI: " + c.getCui()
                + ". Cada cliente debe tener un CUI unico.");
        dataStore.getArbolDeClientes().insertar(c);
        return c;
    }

    public void modificar(Cliente c)        { dataStore.getArbolDeClientes().insertar(c); }
    public Cliente buscarPorCui(String cui) { return dataStore.getArbolDeClientes().buscarPorCui(cui); }
    public boolean eliminar(String cui)     { return dataStore.getArbolDeClientes().eliminar(cui); }
    public List<Cliente> obtenerTodos()     { return dataStore.getArbolDeClientes().obtenerTodos(); }
}
