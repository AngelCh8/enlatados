package com.enlatadosmg.service;

import com.enlatadosmg.model.CatalogoDeProductos;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatalogoService {

    private final DataStore dataStore;

    public CatalogoService(DataStore dataStore) { this.dataStore = dataStore; }

    /** Registra un nuevo producto. Rechaza codigo duplicado. */
    public CatalogoDeProductos registrarProducto(String codigoProducto, String nombreProducto,
                                                 String descripcion, double pesoKgPorCaja,
                                                 int unidadesPorCaja, double precioUnitario) {
        String codigo = codigoProducto.trim().toUpperCase();
        if (buscarPorCodigo(codigo) != null)
            throw new RuntimeException("Ya existe un producto con el codigo: " + codigo);

        CatalogoDeProductos nuevo = new CatalogoDeProductos(
                codigo, nombreProducto.trim(), descripcion,
                pesoKgPorCaja, unidadesPorCaja, precioUnitario
        );
        dataStore.getCatalogoDeProductos().add(nuevo);
        return nuevo;
    }

    public CatalogoDeProductos buscarPorCodigo(String codigo) {
        String buscar = codigo.trim().toUpperCase();
        return dataStore.getCatalogoDeProductos().stream()
                .filter(p -> p.getCodigoProducto().equals(buscar))
                .findFirst().orElse(null);
    }

    public List<CatalogoDeProductos> buscarPorTexto(String texto) {
        String q = texto.trim().toLowerCase();
        return dataStore.getCatalogoDeProductos().stream()
                .filter(p -> p.getCodigoProducto().toLowerCase().contains(q)
                        || p.getNombreProducto().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    /**
     * Elimina un producto del catalogo.
     * No permite eliminar si tiene cajas en stock activas.
     */
    public void eliminar(String codigo) {
        String buscar = codigo.trim().toUpperCase();
        CatalogoDeProductos producto = buscarPorCodigo(buscar);
        if (producto == null)
            throw new RuntimeException("No existe ningun producto con el codigo: " + buscar);
        if (producto.getCajasEnStock() > 0)
            throw new RuntimeException("El producto '" + producto.getNombreProducto()
                    + "' tiene " + producto.getCajasEnStock() + " cajas en stock. "
                    + "Retira el stock antes de eliminarlo.");
        dataStore.getCatalogoDeProductos().removeIf(p -> p.getCodigoProducto().equals(buscar));
    }

    public List<CatalogoDeProductos> obtenerTodos()    { return dataStore.getCatalogoDeProductos(); }
    public List<CatalogoDeProductos> obtenerConStock() {
        return dataStore.getCatalogoDeProductos().stream()
                .filter(p -> p.getCajasEnStock() > 0).collect(Collectors.toList());
    }
}