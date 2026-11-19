package servicio;

import modelo.Booster;
import modelo.Cliente;
import modelo.Compra;
import modelo.DetalleCompra;
import modelo.Producto;
import repositorio.AlmacenDatos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Logica de compras: valida disponibilidad, convierte precios a COP y registra el historial.
 */
public class CompraService {

    private final AlmacenDatos almacen;
    private final InventarioService inventarioService;
    private final List<Compra> compras;

    public CompraService(AlmacenDatos almacen, InventarioService inventarioService) {
        this.almacen = almacen;
        this.inventarioService = inventarioService;
        this.compras = almacen.getCompras();
    }

    /**
     * @param items mapa idProducto -> cantidad
     */
    public synchronized Compra comprar(Cliente cliente, Map<String, Integer> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("La compra debe incluir al menos un producto");
        }

        // 1. Validar todo antes de modificar nada
        List<Producto> productos = new ArrayList<>();
        for (Map.Entry<String, Integer> item : items.entrySet()) {
            Producto producto = inventarioService.buscar(item.getKey());
            int cantidad = item.getValue();
            if (cantidad <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
            }
            if (!inventarioService.hayDisponible(producto)) {
                throw new IllegalStateException("El producto no esta disponible: " + producto.getNombre());
            }
            if (producto instanceof Booster && cantidad != 1) {
                throw new IllegalArgumentException("Un booster solo se puede comprar en cantidad 1");
            }
            productos.add(producto);
        }

        // 2. Registrar la compra con el precio en COP que cada producto ya tiene fijado
        Compra compra = new Compra(UUID.randomUUID().toString(), cliente, fuenteTasa(productos));
        for (Producto producto : productos) {
            int cantidad = items.get(producto.getId());
            if (producto instanceof Booster booster) {
                booster.comprar(cliente);
            }
            compra.agregarDetalle(new DetalleCompra(producto, cantidad,
                    producto.calcularPrecioCOP()));
        }
        compra.calcularTotal();
        cliente.agregarCompra(compra);
        compras.add(compra);
        almacen.guardar();
        return compra;
    }

    public synchronized List<Compra> listarTodas() {
        return new ArrayList<>(compras);
    }

    public synchronized List<Compra> listarDeCliente(Cliente cliente) {
        return new ArrayList<>(cliente.getHistorialCompras());
    }

    /** Si todos los productos usaron la misma fuente de tasa la devuelve; si no, "MIXTA". */
    private String fuenteTasa(List<Producto> productos) {
        String fuente = productos.get(0).getFuenteTasa();
        for (Producto producto : productos) {
            if (!producto.getFuenteTasa().equals(fuente)) {
                return "MIXTA";
            }
        }
        return fuente;
    }
}