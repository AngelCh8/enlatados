package com.enlatadosmg;

import com.enlatadosmg.estructuras.*;
import com.enlatadosmg.model.*;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Pruebas unitarias con JUnit 4.13.1 — minimo 15 pruebas.
 * Cubre: estructuras de datos, herencia (super), estados de piloto/vehiculo,
 * catalogo de productos, ingreso por lotes y gestion de pedidos.
 */
public class EnlatadosMgTests {

    private ListaDeUsuarios  listaUsuarios;
    private PilaDeAlmacen    pilaAlmacen;
    private ColadePilotos    colaPilotos;
    private ColaDeVehiculos  colaVehiculos;
    private ArbolDeClientes  arbolClientes;
    private ListaDePedidos   listaPedidos;

    @Before
    public void setUp() {
        listaUsuarios = new ListaDeUsuarios();
        pilaAlmacen   = new PilaDeAlmacen();
        colaPilotos   = new ColadePilotos();
        colaVehiculos = new ColaDeVehiculos();
        arbolClientes = new ArbolDeClientes();
        listaPedidos  = new ListaDePedidos();
    }

    // ── 1. Usuarios ──────────────────────────────────────────────────

    @Test
    public void testUsuarioIdAutogenerado() {
        Usuario u = new Usuario(1, "Carlos", "Garcia", "1234");
        assertEquals(1, u.getId());
        // Herencia con super(): nombre y apellidos vienen de Persona
        assertEquals("Carlos Garcia", u.obtenerNombreCompleto());
    }

    @Test
    public void testListaUsuariosAgregarYBuscar() {
        listaUsuarios.agregar(new Usuario(1, "Ana",  "Lopez", "p1"));
        listaUsuarios.agregar(new Usuario(2, "Luis", "Perez", "p2"));
        assertEquals(2, listaUsuarios.getTotalUsuarios());
        Usuario u = listaUsuarios.buscar(x -> x.getId() == 2);
        assertNotNull(u);
        assertEquals("Luis", u.getNombre());
    }

    @Test
    public void testListaUsuariosEliminar() {
        listaUsuarios.agregar(new Usuario(1, "Ana", "Lopez", "pass"));
        assertTrue(listaUsuarios.eliminar(u -> u.getId() == 1));
        assertEquals(0, listaUsuarios.getTotalUsuarios());
    }

    // ── 2. Pilotos — estado y herencia ──────────────────────────────

    @Test
    public void testPilotoHerenciaConSuper() {
        // super(nombre, apellidos, telefono) en constructor de Piloto
        Piloto p = new Piloto("001", "Juan", "Morales", "A", "55551234");
        assertTrue(p instanceof Persona);           // herencia verificada
        assertEquals("Juan Morales", p.obtenerNombreCompleto()); // metodo heredado
        assertEquals("CUI: 001 | Licencia: A", p.obtenerIdentificacion());
    }

    @Test
    public void testPilotoEstadoInicialLibre() {
        Piloto p = new Piloto("002", "Pedro", "Diaz", "B", "44441234");
        assertEquals("LIBRE", p.getEstado());
        assertTrue(p.estaDisponible());
    }

    @Test
    public void testColadePilotosAsignarSinEliminar() {
        colaPilotos.registrar(new Piloto("001", "Juan",  "Morales", "A", "11111"));
        colaPilotos.registrar(new Piloto("002", "Pedro", "Diaz",    "B", "22222"));
        // asignar NO elimina — solo cambia estado a OCUPADO
        Piloto asignado = colaPilotos.asignarDisponible();
        assertEquals("OCUPADO", asignado.getEstado());
        assertEquals(2, colaPilotos.getTotalPilotos()); // siguen siendo 2
        assertEquals(1, colaPilotos.contarLibres());    // solo 1 libre
    }

    @Test
    public void testColadePilotosLiberarPiloto() {
        Piloto p = new Piloto("001", "Juan", "Morales", "A", "11111");
        colaPilotos.registrar(p);
        colaPilotos.asignarDisponible();
        assertEquals("OCUPADO", p.getEstado());
        colaPilotos.liberarPiloto(p);
        assertEquals("LIBRE", p.getEstado());   // volvio a LIBRE
    }

    @Test
    public void testColadePilotosCambiarEstadoVacaciones() {
        colaPilotos.registrar(new Piloto("001", "Juan", "Morales", "A", "11111"));
        assertTrue(colaPilotos.cambiarEstado("001", "DE_VACACIONES"));
        assertEquals("DE_VACACIONES", colaPilotos.buscarPorCui("001").getEstado());
        assertEquals(0, colaPilotos.contarLibres()); // no disponible
    }

    @Test(expected = RuntimeException.class)
    public void testColadePilotosSinLibresLanzaExcepcion() {
        // Cola vacia — debe lanzar excepcion
        colaPilotos.asignarDisponible();
    }

    // ── 3. Vehiculos — estado ────────────────────────────────────────

    @Test
    public void testVehiculoEstadoInicialLibre() {
        Vehiculo v = new Vehiculo("P-001", "Toyota", "Hilux", "Blanco", 2022, "Manual");
        assertEquals("LIBRE", v.getEstado());
        assertTrue(v.estaDisponible());
    }

    @Test
    public void testColaDeVehiculosAsignarYLiberar() {
        colaVehiculos.registrar(new Vehiculo("P-001", "Toyota",    "Hilux", "Blanco", 2022, "Manual"));
        colaVehiculos.registrar(new Vehiculo("P-002", "Chevrolet", "NHR",   "Azul",   2020, "Manual"));
        Vehiculo v = colaVehiculos.asignarDisponible();
        assertEquals("OCUPADO", v.getEstado());
        assertEquals(2, colaVehiculos.getTotalVehiculos()); // no se elimino
        colaVehiculos.liberarVehiculo(v);
        assertEquals("LIBRE", v.getEstado());
    }

    // ── 4. Almacen — lotes y suma ────────────────────────────────────

    @Test
    public void testPilaApilarLote() {
        CajaDeProductos lote = new CajaDeProductos(1,"PROD-001","CAJA-0001","Atun",100,10.0,"A",1,1,1);
        pilaAlmacen.apilarOSumar(lote);
        assertEquals(1, pilaAlmacen.getTotalRegistros());
        assertEquals(100, pilaAlmacen.contarCajasPorProducto("PROD-001"));
    }

    @Test
    public void testPilaApilarSumaMismaubicacion() {
        CajaDeProductos l1 = new CajaDeProductos(1,"PROD-001","CAJA-0001","Atun",100,10.0,"A",1,1,1);
        CajaDeProductos l2 = new CajaDeProductos(2,"PROD-001","CAJA-0002","Atun",200,10.0,"A",1,1,1);
        pilaAlmacen.apilarOSumar(l1);
        pilaAlmacen.apilarOSumar(l2); // misma ubicacion → suma
        assertEquals(1, pilaAlmacen.getTotalRegistros()); // sigue siendo 1 registro
        assertEquals(300, pilaAlmacen.contarCajasPorProducto("PROD-001")); // pero 300 cajas
    }

    @Test
    public void testPilaRetirarCajasPorProducto() {
        pilaAlmacen.apilarOSumar(new CajaDeProductos(1,"PROD-001","CAJA-0001","Atun",   100,10.0,"A",1,1,1));
        pilaAlmacen.apilarOSumar(new CajaDeProductos(2,"PROD-002","CAJA-0002","Sardina", 50, 5.0,"B",1,1,1));
        List<CajaDeProductos> retiradas = pilaAlmacen.retirarCajasPorProducto("PROD-001", 30);
        assertFalse(retiradas.isEmpty());
        assertEquals(70, pilaAlmacen.contarCajasPorProducto("PROD-001")); // 100 - 30
        assertEquals(50, pilaAlmacen.contarCajasPorProducto("PROD-002")); // sardina intacta
    }

    @Test(expected = RuntimeException.class)
    public void testPilaRetirarMasDelStock() {
        pilaAlmacen.apilarOSumar(new CajaDeProductos(1,"PROD-001","CAJA-0001","Atun",10,10.0,"A",1,1,1));
        pilaAlmacen.retirarCajasPorProducto("PROD-001", 50); // hay 10, piden 50 → excepcion
    }

    // ── 5. Catalogo de productos ─────────────────────────────────────

    @Test
    public void testCatalogoDeProductosSumarYRestar() {
        CatalogoDeProductos cat = new CatalogoDeProductos("PROD-001","Atun","Atun en aceite",10.0,24);
        assertEquals(0, cat.getCajasEnStock());
        cat.sumarCajas(100);
        assertEquals(100, cat.getCajasEnStock());
        cat.restarCajas(30);
        assertEquals(70, cat.getCajasEnStock());
    }

    @Test(expected = RuntimeException.class)
    public void testCatalogoRestarMasDelStock() {
        CatalogoDeProductos cat = new CatalogoDeProductos("PROD-001","Atun","Atun en aceite",10.0,24);
        cat.sumarCajas(10);
        cat.restarCajas(50); // stock insuficiente → excepcion
    }

    // ── 6. Clientes y arbol AVL ─────────────────────────────────────

    @Test
    public void testArbolClientesInsertarYBuscar() {
        arbolClientes.insertar(new Cliente("1111","Maria","Perez","55551234","Zona 1"));
        arbolClientes.insertar(new Cliente("3333","Carlos","Gomez","33331234","Zona 3"));
        arbolClientes.insertar(new Cliente("2222","Beto","Ruiz","22221234","Zona 2"));
        Cliente c = arbolClientes.buscarPorCui("2222");
        assertNotNull(c);
        assertEquals("Beto", c.getNombre());
        // AVL debe mantener orden in-order
        List<Cliente> todos = arbolClientes.obtenerTodos();
        assertEquals("1111", todos.get(0).getCui());
        assertEquals("2222", todos.get(1).getCui());
        assertEquals("3333", todos.get(2).getCui());
    }

    @Test
    public void testClienteHerenciaConSuper() {
        Cliente c = new Cliente("1111","Maria","Perez","55551234","Zona 1");
        assertTrue(c instanceof Persona);
        assertEquals("CUI: 1111", c.obtenerIdentificacion());
        assertEquals("Maria Perez", c.obtenerNombreCompleto());
    }

    // ── 7. Pedidos — persistencia ────────────────────────────────────

    @Test
    public void testListaDePedidosPersiste() {
        Cliente  cli = new Cliente("1111","Maria","Perez","55551234","Zona 1");
        Piloto   pil = new Piloto("001","Juan","Morales","A","11111");
        Vehiculo veh = new Vehiculo("P-001","Toyota","Hilux","Blanco",2022,"Manual");
        PedidoDeEntrega p1 = new PedidoDeEntrega(1,"Guatemala","Quetzaltenango",cli,pil,veh,null);
        PedidoDeEntrega p2 = new PedidoDeEntrega(2,"Guatemala","Peten",cli,pil,veh,null);
        listaPedidos.agregar(p1);
        listaPedidos.agregar(p2);
        assertEquals(2, listaPedidos.getTotalPedidos());
        // Los pedidos persisten en la lista
        assertNotNull(listaPedidos.buscar(p -> p.getNumeroPedido() == 1));
        assertNotNull(listaPedidos.buscar(p -> p.getNumeroPedido() == 2));
    }

    @Test
    public void testPedidoEstadoYCambiarlo() {
        Cliente  cli = new Cliente("1111","Maria","Perez","55551234","Zona 1");
        Piloto   pil = new Piloto("001","Juan","Morales","A","11111");
        Vehiculo veh = new Vehiculo("P-001","Toyota","Hilux","Blanco",2022,"Manual");
        PedidoDeEntrega p = new PedidoDeEntrega(1,"Guatemala","Quetzaltenango",cli,pil,veh,null);
        assertEquals("PENDIENTE", p.getEstado());
        p.setEstado("COMPLETADO");
        assertEquals("COMPLETADO", p.getEstado());
    }
}
