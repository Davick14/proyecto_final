package modelo;

/**

 */
public class Accesorio extends Producto {

    private String tipo;

    public Accesorio(String id, String nombre, double precioUSD, String tipo) {
        super(id, nombre, precioUSD);
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }
}
