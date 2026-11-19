package servicio;

import modelo.Accesorio;
import modelo.Administrador;
import modelo.Booster;
import modelo.Carta;
import modelo.Inventario;
import modelo.Producto;
import modelo.ProductoSellado;
import modelo.Rareza;
import repositorio.AlmacenDatos;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Logica del inventario: registro y consulta de cartas, sellados, accesorios y boosters.
 */
public class InventarioService {

    private final AlmacenDatos almacen;
    private final Inventario inventario;

    public InventarioService(AlmacenDatos almacen) {
        this.almacen = almacen;
        this.inventario = almacen.getInventario();
    }

    public synchronized Carta crearCarta(Administrador admin, String nombre, double precioUSD,
                                         String rarezaTexto) {
        Carta carta = new Carta(UUID.randomUUID().toString(), nombre, precioUSD,
                interpretarRareza(rarezaTexto));
        inventario.agregarProducto(admin.crearProducto(carta));
        almacen.guardar();
        return carta;
    }

    public synchronized ProductoSellado crearProductoSellado(Administrador admin, String nombre,
                                                             double precioUSD, String descripcion) {
        ProductoSellado sellado = new ProductoSellado(UUID.randomUUID().toString(), nombre,
                precioUSD, descripcion);
        inventario.agregarProducto(admin.crearProducto(sellado));
        almacen.guardar();
        return sellado;
    }

    public synchronized Accesorio crearAccesorio(Administrador admin, String nombre,
                                                 double precioUSD, String tipo) {
        Accesorio accesorio = new Accesorio(UUID.randomUUID().toString(), nombre, precioUSD, tipo);
        inventario.agregarProducto(admin.crearProducto(accesorio));
        almacen.guardar();
        return accesorio;
    }

    /** Agrega un producto ya construido (por ejemplo un booster) al inventario. */
    public synchronized void agregar(Producto producto) {
        inventario.agregarProducto(producto);
        almacen.guardar();
    }

    /** Todo lo que esta disponible para la venta (boosters vendidos no aparecen). */
    public synchronized List<Producto> listarDisponibles() {
        List<Producto> resultado = new ArrayList<>();
        for (Producto producto : inventario.getProductos()) {
            if (inventario.hayDisponible(producto)) {
                resultado.add(producto);
            }
        }
        return resultado;
    }

    public synchronized <T extends Producto> List<T> listarPorTipo(Class<T> tipo) {
        List<T> resultado = new ArrayList<>();
        for (Producto producto : inventario.getProductos()) {
            if (tipo.isInstance(producto) && inventario.hayDisponible(producto)) {
                resultado.add(tipo.cast(producto));
            }
        }
        return resultado;
    }

    public synchronized List<Carta> cartasRegistradas() {
        List<Carta> cartas = new ArrayList<>();
        for (Producto producto : inventario.getProductos()) {
            if (producto instanceof Carta carta) {
                cartas.add(carta);
            }
        }
        return cartas;
    }

    public synchronized Producto buscar(String id) {
        for (Producto producto : inventario.getProductos()) {
            if (producto.getId().equals(id)) {
                return producto;
            }
        }
        throw new NoSuchElementException("Producto no encontrado");
    }

    public synchronized boolean hayDisponible(Producto producto) {
        return inventario.hayDisponible(producto);
    }

    public synchronized void eliminar(String id) {
        Producto producto = buscar(id);
        if (producto instanceof Booster booster && booster.estaVendido()) {
            throw new IllegalStateException("No se puede eliminar un booster que ya fue vendido");
        }
        inventario.quitarProducto(producto);
        almacen.guardar();
    }

    private Rareza interpretarRareza(String texto) {
        for (Rareza rareza : Rareza.values()) {
            if (rareza.name().equalsIgnoreCase(texto) || rareza.getCodigo().equalsIgnoreCase(texto)) {
                return rareza;
            }
        }
        throw new IllegalArgumentException("Rareza invalida. Use N, UC, R, SR o UR");
    }
}