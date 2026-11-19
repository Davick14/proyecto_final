package repositorio;

import modelo.Administrador;
import modelo.Cliente;
import modelo.Compra;
import modelo.Inventario;
import modelo.Torneo;

import java.util.ArrayList;
import java.util.List;

/**
 * Unica clase donde se crean y guardan todas las colecciones del sistema:
 * clientes, torneos, compras e inventario (que a su vez guarda los productos).
 * Los servicios no crean listas propias: reciben este almacen y trabajan sobre el.
 */
public class AlmacenDatos {

    private final Administrador administrador;
    private final List<Cliente> clientes;
    private final List<Torneo> torneos;
    private final List<Compra> compras;
    private final Inventario inventario;

    public AlmacenDatos() {
        this.administrador = new Administrador("admin-1", "admin", "admin123");
        this.clientes = new ArrayList<>();
        this.torneos = new ArrayList<>();
        this.compras = new ArrayList<>();
        this.inventario = new Inventario();
    }

    public Administrador getAdministrador() {
        return administrador;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public List<Torneo> getTorneos() {
        return torneos;
    }

    public List<Compra> getCompras() {
        return compras;
    }

    public Inventario getInventario() {
        return inventario;
    }
}
