package modelo;

/**
 * Carta individual (single). Unidad basica del inventario.
 */
public class Carta extends Producto {

    private Rareza rareza;

    public Carta(String id, String nombre, double precioUSD, Rareza rareza) {
        super(id, nombre, precioUSD);
        if (rareza == null) {
            throw new IllegalArgumentException("La rareza es obligatoria");
        }
        this.rareza = rareza;
    }

    public Rareza getRareza() {
        return rareza;
    }
}
