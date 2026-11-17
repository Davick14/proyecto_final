package controlador;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import modelo.Administrador;
import modelo.Cliente;
import modelo.Usuario;
import servicio.UsuarioService;
import spark.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Registro y consulta de clientes, e historial de compras.
 */
public class ClienteController extends ControladorBase {

    private final Vistas vistas;

    public ClienteController(Gson gson, UsuarioService usuarioService, Vistas vistas) {
        super(gson, usuarioService);
        this.vistas = vistas;
    }

    @Override
    public void registrarRutas(Service http) {

        // Solo el administrador registra clientes -> 201
        http.post("/clientes", (req, res) -> {
            Administrador admin = exigirAdmin(req);
            JsonObject cuerpo = leerCuerpo(req);
            String usuario = textoObligatorio(cuerpo, "usuario");
            String contrasena = textoObligatorio(cuerpo, "contrasena");
            Cliente cliente = usuarioService.registrarCliente(admin, usuario, contrasena);
            return responder(res, 201, vistas.cliente(cliente));
        });

        http.get("/clientes", (req, res) -> {
            exigirAdmin(req);
            List<Map<String, Object>> lista = new ArrayList<>();
            for (Cliente cliente : usuarioService.listarClientes()) {
                lista.add(vistas.cliente(cliente));
            }
            return responder(res, 200, lista);
        });

        http.get("/clientes/:id", (req, res) -> {
            exigirAdmin(req);
            Cliente cliente = usuarioService.buscarCliente(req.params(":id"));
            return responder(res, 200, vistas.cliente(cliente));
        });

        // Historial: lo ve el administrador o el propio cliente (otro cliente -> 403)
        http.get("/clientes/:id/historial", (req, res) -> {
            Usuario usuario = autenticar(req);
            Cliente objetivo = usuarioService.buscarCliente(req.params(":id"));
            if (usuario instanceof Cliente propio && !propio.getId().equals(objetivo.getId())) {
                throw new SecurityException("No puede consultar el historial de otro cliente");
            }
            return responder(res, 200, vistas.compras(objetivo.getHistorialCompras()));
        });
    }
}
