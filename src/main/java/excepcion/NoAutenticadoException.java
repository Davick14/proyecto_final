package excepcion;

/**
 * Se lanza cuando la peticion no trae credenciales validas (HTTP 401).
 */
public class NoAutenticadoException extends RuntimeException {

    public NoAutenticadoException(String mensaje) {
        super(mensaje);
    }
}
