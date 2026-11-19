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
    private final TasaCambioService tasaCambioService;
    private final List<Compra> compras;

    public CompraService(AlmacenDatos almacen, InventarioService inventarioService,
                         TasaCambioService tasaCambioService) {
        this.almacen = almacen;
        this.inventarioService = inventarioService;
        this.tasaCambioService = tasaCambioService;
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

        // 2. Consultar tasa vigente y registrar la compra en COP
        TasaCambioService.ResultadoTasa resultadoTasa = tasaCambioService.obtenerTasa();
        Compra compra = new Compra(UUID.randomUUID().toString(), cliente, resultadoTasa.fuente());
        for (Producto producto : productos) {
            int cantidad = items.get(producto.getId());
            if (producto instanceof Booster booster) {
                booster.comprar(cliente);
            }
            double precioUnitarioCOP = producto.calcularPrecioCOP(resultadoTasa.tasa());
            compra.agregarDetalle(new DetalleCompra(producto, cantidad, precioUnitarioCOP));
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
}