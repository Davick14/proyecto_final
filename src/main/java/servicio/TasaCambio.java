package servicio;

/**
 * Resultado de consultar la tasa USD -> COP: el valor y de donde salio.
 */
public class TasaCambio {

    private final double valor;
    private final String fuente;

    public TasaCambio(double valor, String fuente) {
        this.valor = valor;
        this.fuente = fuente;
    }

    public double getValor() {
        return valor;
    }

    /** "API" o "DEFECTO". */
    public String getFuente() {
        return fuente;
    }
}
