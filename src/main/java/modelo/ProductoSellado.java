package modelo;

/**
 * Producto sellado (cajas, mazos preconstruidos, etc.).
 */
public class ProductoSellado extends Producto {

    private String descripcion;

    public ProductoSellado(String id, String nombre, double precioUSD, String descripcion) {
        super(id, nombre, precioUSD);
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
