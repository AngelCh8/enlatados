package com.enlatadosmg.service;

import com.enlatadosmg.estructuras.*;
import com.enlatadosmg.model.CatalogoDeProductos;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio central Singleton de todas las estructuras de datos.
 * Spring garantiza que solo existe UNA instancia en toda la aplicacion,
 * por lo que los datos persisten en memoria durante toda la sesion.
 */
@Component
public class DataStore {

    // ── Estructuras de datos ─────────────────────────────────────────
    private final ListaDeUsuarios  listaDeUsuarios  = new ListaDeUsuarios();
    private final PilaDeAlmacen    pilaDeAlmacen    = new PilaDeAlmacen();
    private final ArbolDeClientes  arbolDeClientes  = new ArbolDeClientes();
    private final ColadePilotos    coladePilotos    = new ColadePilotos();
    private final ColaDeVehiculos  colaDeVehiculos  = new ColaDeVehiculos();
    private final ListaDePedidos   listaDePedidos   = new ListaDePedidos();

    /** Catalogo de productos registrados (codigo unico por producto) */
    private final List<CatalogoDeProductos> catalogoDeProductos = new ArrayList<>();

    // ── Contadores autoincrementales ─────────────────────────────────
    private int contadorIdUsuarios  = 1;
    private int correlativoCajas    = 1;
    private int contadorPedidos     = 1;

    // ── Getters de estructuras ───────────────────────────────────────
    public ListaDeUsuarios  getListaDeUsuarios()            { return listaDeUsuarios;  }
    public PilaDeAlmacen    getPilaDeAlmacen()              { return pilaDeAlmacen;    }
    public ArbolDeClientes  getArbolDeClientes()            { return arbolDeClientes;  }
    public ColadePilotos    getColadePilotos()              { return coladePilotos;    }
    public ColaDeVehiculos  getColaDeVehiculos()            { return colaDeVehiculos;  }
    public ListaDePedidos   getListaDePedidos()             { return listaDePedidos;   }
    public List<CatalogoDeProductos> getCatalogoDeProductos() { return catalogoDeProductos; }

    // ── Generadores de IDs ───────────────────────────────────────────
    public int siguienteIdUsuario()       { return contadorIdUsuarios++; }
    public int siguienteNumeroPedido()    { return contadorPedidos++; }

    /** Genera el codigo de caja automaticamente: CAJA-0001, CAJA-0002, ... */
    public String siguienteCodigoCaja() {
        return String.format("CAJA-%04d", correlativoCajas++);
    }

    /** Siguiente ID numerico para registros de almacen */
    public int siguienteCorrelativoCaja() { return correlativoCajas; }
}
