package controlador;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import excepcion.NoAutenticadoException;
import modelo.Administrador;
import modelo.Cliente;
import modelo.Usuario;
import servicio.UsuarioService;
import spark.Request;
import spark.Response;
import spark.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Base de todos los controladores: autenticacion Basic, control de rol y lectura del cuerpo JSON.
 */
public abstract class ControladorBase {

    protected final Gson gson;
    protected final UsuarioService usuarioService;

    protected ControladorBase(Gson gson, UsuarioService usuarioService) {
        this.gson = gson;
        this.usuarioService = usuarioService;
    }

    /** Cada controlador registra aqui sus rutas. */
    public abstract void registrarRutas(Service http);

    /** 401 si no hay credenciales validas. */
    protected Usuario autenticar(Request req) {
        String cabecera = req.headers("Authorization");
        if (cabecera == null || !cabecera.startsWith("Basic ")) {
            throw new NoAutenticadoException("Se requiere autenticacion");
        }
        String credenciales;
        try {
            byte[] bytes = Base64.getDecoder().decode(cabecera.substring(6).trim());
            credenciales = new String(bytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new NoAutenticadoException("Credenciales mal formadas");
        }
        int separador = credenciales.indexOf(':');
        if (separador < 0) {
            throw new NoAutenticadoException("Credenciales mal formadas");
        }
        Usuario usuario = usuarioService.autenticar(
                credenciales.substring(0, separador), credenciales.substring(separador + 1));
        if (usuario == null) {
            throw new NoAutenticadoException("Credenciales invalidas");
        }
        return usuario;
    }

    /** 401 si no hay autenticacion, 403 si el usuario no es administrador. */
    protected Administrador exigirAdmin(Request req) {
        Usuario usuario = autenticar(req);
        if (usuario instanceof Administrador administrador) {
            return administrador;
        }
        throw new SecurityException("Accion reservada al administrador");
    }

    /** 401 si no hay autenticacion, 403 si el usuario no es cliente. */
    protected Cliente exigirCliente(Request req) {
        Usuario usuario = autenticar(req);
        if (usuario instanceof Cliente cliente) {
            return cliente;
        }
        throw new SecurityException("Accion reservada a los clientes");
    }

    protected JsonObject leerCuerpo(Request req) {
        String cuerpo = req.body();
        if (cuerpo == null || cuerpo.isBlank()) {
            throw new IllegalArgumentException("El cuerpo de la peticion es obligatorio");
        }
        JsonObject objeto = gson.fromJson(cuerpo, JsonObject.class);
        if (objeto == null) {
            throw new IllegalArgumentException("El cuerpo de la peticion es obligatorio");
        }
        return objeto;
    }

    protected String textoObligatorio(JsonObject objeto, String campo) {
        JsonElement elemento = objeto.get(campo);
        if (elemento == null || !elemento.isJsonPrimitive() || elemento.getAsString().isBlank()) {
            throw new IllegalArgumentException("El campo '" + campo + "' es obligatorio");
        }
        return elemento.getAsString().trim();
    }

    protected double numeroObligatorio(JsonObject objeto, String campo) {
        JsonElement elemento = objeto.get(campo);
        if (elemento == null || !elemento.isJsonPrimitive()
                || !elemento.getAsJsonPrimitive().isNumber()) {
            throw new IllegalArgumentException("El campo '" + campo + "' debe ser numerico");
        }
        return elemento.getAsDouble();
    }

    protected int enteroObligatorio(JsonObject objeto, String campo) {
        double valor = numeroObligatorio(objeto, campo);
        if (valor != Math.floor(valor)) {
            throw new IllegalArgumentException("El campo '" + campo + "' debe ser un numero entero");
        }
        return (int) valor;
    }

    protected String responder(Response res, int estado, Object cuerpo) {
        res.status(estado);
        res.type("application/json");
        return gson.toJson(cuerpo);
    }
}
