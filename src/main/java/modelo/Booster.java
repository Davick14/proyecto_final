package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Sobre unico con exactamente diez cartas fijas desde su creacion.
 * Solo puede ser comprado una vez y abierto una vez por su propietario.
 */
public class Booster extends Producto {

    private List<Carta> cartasContenidas;
    private boolean abierto;
    private Cliente propietario;

    public Booster(String id, String nombre, double precioUSD) {
        super(id, nombre, precioUSD);
        this.cartasContenidas = new ArrayList<>();
        this.abierto = false;
        this.propietario = null;
    }

    /**
     * Asigna exactamente diez cartas: primero sortea la rareza segun su probabilidad
     * y luego elige al azar una carta de esa rareza (con regla de respaldo).
     * Las cartas se pueden repetir y no se retiran del inventario.
     */
    public void asignarCartasAleatorias(List<Carta> cartasRegistradas) {
        if (cartasRegistradas == null || cartasRegistradas.isEmpty()) {
            throw new IllegalStateException("No hay cartas registradas para crear el booster");
        }
        final int cantidadCartas = 10;
        Random random = new Random();
        List<Carta> seleccion = new ArrayList<>();
        for (int i = 0; i < cantidadCartas; i++) {
            Rareza rareza = sortearRareza(random);
            seleccion.add(elegirCarta(rareza, cartasRegistradas, random));
        }
        this.cartasContenidas = seleccion;
    }

    /** Marca el booster como vendido asignandolo a su comprador. */
    public void comprar(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El comprador no puede ser nulo");
        }
        if (estaVendido()) {
            throw new IllegalStateException("El booster ya fue vendido");
        }
        this.propietario = cliente;
    }

    /** Abre el booster. Solo su propietario, y una unica vez. */
    public List<Carta> abrir(Cliente solicitante) {
        if (solicitante == null || propietario == null
                || !propietario.getId().equals(solicitante.getId())) {
            throw new SecurityException("El booster no pertenece al cliente");
        }
        if (abierto) {
            throw new IllegalStateException("El booster ya fue abierto");
        }
        this.abierto = true;
        return getCartasContenidas();
    }

    public boolean estaAbierto() {
        return abierto;
    }

    public boolean estaVendido() {
        return propietario != null;
    }

    public List<Carta> getCartasContenidas() {
        return Collections.unmodifiableList(cartasContenidas);
    }

    public Cliente getPropietario() {
        return propietario;
    }

    // ---------- Apoyo interno ----------

    private Rareza sortearRareza(Random random) {
        double tirada = random.nextDouble() * 100;
        double acumulado = 0;
        for (Rareza rareza : Rareza.values()) {
            acumulado += rareza.getProbabilidad();
            if (tirada < acumulado) {
                return rareza;
            }
        }
        return Rareza.ULTRA_RARO;
    }

    private Carta elegirCarta(Rareza rareza, List<Carta> registradas, Random random) {
        List<Carta> candidatas = cartasDeRareza(rareza, registradas);

        Rareza actual = rareza.inferior();
        while (candidatas.isEmpty() && actual != null) {
            candidatas = cartasDeRareza(actual, registradas);
            actual = actual.inferior();
        }

        actual = rareza.superior();
        while (candidatas.isEmpty() && actual != null) {
            candidatas = cartasDeRareza(actual, registradas);
            actual = actual.superior();
        }

        return candidatas.get(random.nextInt(candidatas.size()));
    }

    private List<Carta> cartasDeRareza(Rareza rareza, List<Carta> registradas) {
        List<Carta> resultado = new ArrayList<>();
        for (Carta carta : registradas) {
            if (carta.getRareza() == rareza) {
                resultado.add(carta);
            }
        }
        return resultado;
    }
}
