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

    /** Calcula el precio en COP aplicando la tasa recibida. */
    public double calcularPrecioCOP(double tasa) {
        if (tasa <= 0) {
            throw new IllegalArgumentException("La tasa de cambio debe ser mayor a cero");
        }
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