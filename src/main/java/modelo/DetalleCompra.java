package modelo;

/**
 * Linea de una compra: producto, cantidad y precios en COP.
 */
public class DetalleCompra {

    private Producto producto;
    private int cantidad;
    private double precioUnitarioCOP;
    private double subtotalCOP;

    public DetalleCompra(Producto producto, int cantidad, double precioUnitarioCOP) {
        if (producto == null || cantidad <= 0 || precioUnitarioCOP < 0) {
            throw new IllegalArgumentException("Datos de detalle de compra invalidos");
        }
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitarioCOP = precioUnitarioCOP;
        calcularSubtotal();
    }

    public double calcularSubtotal() {
        this.subtotalCOP = precioUnitarioCOP * cantidad;
        return subtotalCOP;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioUnitarioCOP() {
        return precioUnitarioCOP;
    }

    public double getSubtotalCOP() {
        return subtotalCOP;
    }
}
