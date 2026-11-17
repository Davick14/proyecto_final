package modelo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Usuario con permisos para gestionar clientes, productos, boosters y torneos.
 */
public class Administrador extends Usuario {

    public Administrador(String id, String usuario, String contrasena) {
        super(id, usuario, contrasena, RolUsuario.ADMIN);
    }

    public Cliente registrarCliente(String id, String usuario, String contrasena) {
        return new Cliente(id, usuario, contrasena);
    }

    /** Crea un booster y le asigna sus diez cartas aleatorias. */
    public Booster crearBooster(String id, String nombre, double precioUSD,
                                List<Carta> cartasRegistradas) {
        Booster booster = new Booster(id, nombre, precioUSD);
        booster.asignarCartasAleatorias(cartasRegistradas);
        return booster;
    }

    /** Registra un producto (Carta, ProductoSellado o Accesorio). */
    public Producto crearProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        return producto;
    }

    public Torneo crearTorneo(String id, String nombre, LocalDateTime fechaProgramada,
                              int cupoMaximo) {
        return new Torneo(id, nombre, fechaProgramada, cupoMaximo);
    }
}
