package modelo;

/**
 * Clase base abstracta de todo lo que se vende en Cards4Play.
 */
public abstract class Producto {

    private String id;
    private String nombre;
    private double precioUSD;

    protected Producto(String id, String nombre, double precioUSD) {
        if (id == null || id.isBlank() || nombre == null || nombre.isBlank() || precioUSD < 0) {
            throw new IllegalArgumentException("Datos de producto invalidos o incompletos");
        }
        this.id = id;
        this.nombre = nombre;
        this.precioUSD = precioUSD;
    }

    /** Convierte el precio base en USD a COP usando la tasa recibida. */
    public double calcularPrecioCOP(double tasa) {
        return precioUSD * tasa;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecioUSD() {
        return precioUSD;
    }
}
