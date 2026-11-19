package servicio;

import modelo.Administrador;
import modelo.Cliente;
import modelo.Usuario;
import repositorio.AlmacenDatos;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Gestiona al administrador por defecto, el registro de clientes y la autenticacion.
 */
public class UsuarioService {

    private final AlmacenDatos almacen;
    private final Administrador administrador;
    private final List<Cliente> clientes;

    public UsuarioService(AlmacenDatos almacen) {
        this.almacen = almacen;
        this.administrador = almacen.getAdministrador();
        this.clientes = almacen.getClientes();
    }

    /** Retorna el usuario si las credenciales son correctas, o null si no lo son. */
    public synchronized Usuario autenticar(String usuario, String contrasena) {
        if (administrador.autenticar(usuario, contrasena)) {
            return administrador;
        }
        for (Cliente cliente : clientes) {
            if (cliente.autenticar(usuario, contrasena)) {
                return cliente;
            }
        }
        return null;
    }

    public synchronized Cliente registrarCliente(Administrador admin, String usuario, String contrasena) {
        if (existeUsuario(usuario)) {
            throw new IllegalStateException("Ya existe un usuario con ese nombre");
        }
        Cliente cliente = admin.registrarCliente(UUID.randomUUID().toString(), usuario, contrasena);
        clientes.add(cliente);
        almacen.guardar();
        return cliente;
    }

    public synchronized List<Cliente> listarClientes() {
        return new ArrayList<>(clientes);
    }

    public synchronized Cliente buscarCliente(String id) {
        for (Cliente cliente : clientes) {
            if (cliente.getId().equals(id)) {
                return cliente;
            }
        }
        throw new NoSuchElementException("Cliente no encontrado");
    }

    public Administrador getAdministrador() {
        return administrador;
    }

    private boolean existeUsuario(String usuario) {
        if (administrador.getUsuario().equals(usuario)) {
            return true;
        }
        for (Cliente cliente : clientes) {
            if (cliente.getUsuario().equals(usuario)) {
                return true;
            }
        }
        return false;
    }
}