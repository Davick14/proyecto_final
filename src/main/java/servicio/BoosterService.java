package servicio;

import modelo.Administrador;
import modelo.Booster;
import modelo.Carta;
import modelo.Cliente;
import modelo.Producto;
import repositorio.AlmacenDatos;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Logica de boosters: creacion, consulta de disponibles y apertura.
 */
public class BoosterService {

    private final AlmacenDatos almacen;
    private final InventarioService inventarioService;

    public BoosterService(AlmacenDatos almacen, InventarioService inventarioService) {
        this.almacen = almacen;
        this.inventarioService = inventarioService;
    }

    /** Crea un booster con diez cartas. Falla si no hay ninguna carta registrada. */
    public synchronized Booster crearBooster(Administrador admin, String nombre, double precioUSD) {
        List<Carta> cartas = inventarioService.cartasRegistradas();
        Booster booster = admin.crearBooster(UUID.randomUUID().toString(), nombre, precioUSD, cartas);
        inventarioService.agregar(booster);
        return booster;
    }

    public List<Booster> listarDisponibles() {
        return inventarioService.listarPorTipo(Booster.class);
    }

    public Booster buscarBooster(String id) {
        Producto producto = inventarioService.buscar(id);
        if (producto instanceof Booster booster) {
            return booster;
        }
        throw new NoSuchElementException("Booster no encontrado");
    }

    /** Abre el booster: 404 si no existe, 403 si es ajeno, 409 si ya fue abierto. */
    public synchronized List<Carta> abrir(Cliente cliente, String boosterId) {
        Booster booster = buscarBooster(boosterId);
        List<Carta> cartas = cliente.abrirBooster(booster);
        almacen.guardar();
        return cartas;
    }
}