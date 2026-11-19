package repositorio;

import modelo.Accesorio;
import modelo.Administrador;
import modelo.Booster;
import modelo.Carta;
import modelo.Cliente;
import modelo.Compra;
import modelo.DetalleCompra;
import modelo.Inventario;
import modelo.Producto;
import modelo.ProductoSellado;
import modelo.Torneo;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unica clase donde se crean y guardan todas las colecciones del sistema:
 * clientes, torneos, compras e inventario (que a su vez guarda los productos).
 * Persiste los datos en archivos JSON dentro de DATA_DIR (por defecto "data").
 */
public class AlmacenDatos {

    private final Administrador administrador;
    private final List<Cliente> clientes;
    private final List<Torneo> torneos;
    private final List<Compra> compras;
    private final Inventario inventario;
    private final GestorJSON gestor;
    private final String dataDir;

    public AlmacenDatos() {
        this.administrador = new Administrador("admin-1", "admin", "admin123");
        this.clientes = new ArrayList<>();
        this.torneos = new ArrayList<>();
        this.compras = new ArrayList<>();
        this.inventario = new Inventario();
        this.gestor = new GestorJSON();
        this.dataDir = leerDataDir();
        cargar();
    }

    public synchronized void cargar() {
        clientes.clear();
        torneos.clear();
        compras.clear();
        inventario.getProductos().clear();

        clientes.addAll(gestor.leerLista(ruta("clientes.json"), Cliente[].class));

        for (Carta c : gestor.leerLista(ruta("cartas.json"), Carta[].class)) {
            inventario.agregarProducto(c);
        }
        for (Booster b : gestor.leerLista(ruta("boosters.json"), Booster[].class)) {
            inventario.agregarProducto(b);
        }
        for (ProductoSellado s : gestor.leerLista(ruta("sellados.json"), ProductoSellado[].class)) {
            inventario.agregarProducto(s);
        }
        for (Accesorio a : gestor.leerLista(ruta("accesorios.json"), Accesorio[].class)) {
            inventario.agregarProducto(a);
        }

        torneos.addAll(gestor.leerLista(ruta("torneos.json"), Torneo[].class));
        compras.addAll(gestor.leerLista(ruta("compras.json"), Compra[].class));

        reconstruirRelaciones();
    }

    public synchronized void guardar() {
        gestor.escribirLista(ruta("clientes.json"), clientes);
        gestor.escribirLista(ruta("torneos.json"), torneos);
        gestor.escribirLista(ruta("compras.json"), compras);

        List<Carta> cartas = new ArrayList<>();
        List<Booster> boosters = new ArrayList<>();
        List<ProductoSellado> sellados = new ArrayList<>();
        List<Accesorio> accesorios = new ArrayList<>();

        for (Producto producto : inventario.getProductos()) {
            if (producto instanceof Carta c) {
                cartas.add(c);
            } else if (producto instanceof Booster b) {
                boosters.add(b);
            } else if (producto instanceof ProductoSellado s) {
                sellados.add(s);
            } else if (producto instanceof Accesorio a) {
                accesorios.add(a);
            }
        }

        gestor.escribirLista(ruta("cartas.json"), cartas);
        gestor.escribirLista(ruta("boosters.json"), boosters);
        gestor.escribirLista(ruta("sellados.json"), sellados);
        gestor.escribirLista(ruta("accesorios.json"), accesorios);
    }

    private void reconstruirRelaciones() {
        Map<String, Cliente> clientesPorId = new HashMap<>();
        for (Cliente cliente : clientes) {
            cliente.reiniciarListas();
            clientesPorId.put(cliente.getId(), cliente);
        }

        Map<String, Producto> productosPorId = new HashMap<>();
        for (Producto producto : inventario.getProductos()) {
            productosPorId.put(producto.getId(), producto);
            if (producto instanceof Booster booster && booster.getPropietarioId() != null) {
                booster.setPropietario(clientesPorId.get(booster.getPropietarioId()));
            }
        }

        for (Torneo torneo : torneos) {
            torneo.reiniciarParticipantes();
            for (String clienteId : torneo.getParticipantesIds()) {
                Cliente cliente = clientesPorId.get(clienteId);
                if (cliente != null) {
                    torneo.getParticipantes().add(cliente);
                    cliente.getTorneosInscritos().add(torneo);
                }
            }
        }

        for (Compra compra : compras) {
            Cliente cliente = clientesPorId.get(compra.getClienteId());
            if (cliente != null) {
                compra.setCliente(cliente);
                cliente.getHistorialCompras().add(compra);
            }
            for (DetalleCompra detalle : compra.getDetalles()) {
                Producto producto = productosPorId.get(detalle.getProductoId());
                if (producto != null) {
                    detalle.setProducto(producto);
                }
            }
        }
    }

    private String ruta(String archivo) {
        return Paths.get(dataDir, archivo).toString();
    }

    private String leerDataDir() {
        String env = System.getenv("DATA_DIR");
        return (env == null || env.isBlank()) ? "data" : env.trim();
    }

    public Administrador getAdministrador() {
        return administrador;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public List<Torneo> getTorneos() {
        return torneos;
    }

    public List<Compra> getCompras() {
        return compras;
    }

    public Inventario getInventario() {
        return inventario;
    }
}