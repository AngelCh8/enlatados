package com.enlatadosmg.service;

import com.enlatadosmg.model.CajaDeProductos;
import com.enlatadosmg.model.CatalogoDeProductos;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Logica de negocio para el Almacen de productos.
 * Coordina con CatalogoService para validar productos
 * y actualizar el stock al ingresar o retirar cajas.
 */
@Service
public class AlmacenService {

    private final DataStore      dataStore;
    private final CatalogoService catalogoService;

    /** super() no aplica aqui (no hereda), pero se usa inyeccion de dependencias */
    public AlmacenService(DataStore dataStore, CatalogoService catalogoService) {
        this.dataStore       = dataStore;
        this.catalogoService = catalogoService;
    }

    /**
     * Ingresa un lote de cajas al almacen.
     * - Valida que el producto exista en el catalogo.
     * - El codigoCaja se genera AUTOMATICAMENTE.
     * - Si ya hay un lote del mismo producto en la misma ubicacion, SUMA la cantidad.
     * - Actualiza el stock en el catalogo.
     */
    public CajaDeProductos ingresarCajas(String codigoProducto, int cantidadCajas,
                                          String zona, int pasillo, int estante, int nivel) {
        CatalogoDeProductos catalogo = catalogoService.buscarPorCodigo(codigoProducto);
        if (catalogo == null) {
            throw new RuntimeException("Producto no encontrado en el catalogo: " + codigoProducto
                + ". Registra primero el producto en 'Registro de Producto Nuevo'.");
        }
        if (cantidadCajas <= 0) {
            throw new RuntimeException("La cantidad de cajas debe ser mayor a 0.");
        }

        // Codigo de caja generado automaticamente
        String codigoCaja = dataStore.siguienteCodigoCaja();
        int    idLote     = dataStore.siguienteCorrelativoCaja();

        CajaDeProductos lote = new CajaDeProductos(
            idLote, codigoProducto, codigoCaja,
            catalogo.getNombreProducto(), cantidadCajas,
            catalogo.getPesoKgPorCaja(),
            zona.toUpperCase(), pasillo, estante, nivel
        );

        // Si existe un lote del mismo producto en la misma ubicacion → suma; si no → apila nuevo
        dataStore.getPilaDeAlmacen().apilarOSumar(lote);

        // Actualiza el stock en el catalogo
        catalogo.sumarCajas(cantidadCajas);
        return lote;
    }

    /**
     * Retira N cajas de un producto para un pedido.
     * Descuenta del stock en el catalogo y de la pila.
     */
    public List<CajaDeProductos> retirarCajasPorProducto(String codigoProducto, int cantidad) {
        CatalogoDeProductos catalogo = catalogoService.buscarPorCodigo(codigoProducto);
        if (catalogo == null) {
            throw new RuntimeException("Producto no encontrado: " + codigoProducto);
        }
        List<CajaDeProductos> retiradas =
            dataStore.getPilaDeAlmacen().retirarCajasPorProducto(codigoProducto, cantidad);
        catalogo.restarCajas(cantidad);
        return retiradas;
    }

    /** Cambia el estado de un lote por su ID */
    public boolean cambiarEstadoLote(int id, String nuevoEstado) {
        return dataStore.getPilaDeAlmacen().cambiarEstado(id, nuevoEstado);
    }

    public List<CajaDeProductos> obtenerInventario() {
        return dataStore.getPilaDeAlmacen().obtenerTodas();
    }
}
