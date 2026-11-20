package excepcion;

/**
 * Se lanza cuando la peticion no trae credenciales validas.
 */
public class NoAutenticadoException extends RuntimeException {

    public NoAutenticadoException(String mensaje) {
        super(mensaje);
    }
}
