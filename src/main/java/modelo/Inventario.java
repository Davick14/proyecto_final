package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Almacena todos los productos de Cards4Play.
 */
public class Inventario {

    private List<Producto> productos;

    public Inventario() {
        this.productos = new ArrayList<>();
    }

    public void agregarProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        if (buscarPorId(producto.getId()) != null) {
            throw new IllegalStateException("Ya existe un producto con ese id");
        }
        productos.add(producto);
    }

    public boolean quitarProducto(Producto producto) {
        return producto != null && productos.removeIf(p -> p.getId().equals(producto.getId()));
    }

    /** Un producto esta disponible si esta registrado y, de ser booster, no ha sido vendido. */
    public boolean hayDisponible(Producto producto) {
        if (producto == null) {
            return false;
        }
        Producto existente = buscarPorId(producto.getId());
        if (existente == null) {
            return false;
        }
        if (existente instanceof Booster booster) {
            return !booster.estaVendido();
        }
        return true;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    private Producto buscarPorId(String id) {
        for (Producto producto : productos) {
            if (producto.getId().equals(id)) {
                return producto;
            }
        }
        return null;
    }
}
