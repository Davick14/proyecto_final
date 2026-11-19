package controlador;

import com.google.gson.Gson;
import modelo.Usuario;
import servicio.UsuarioService;
import spark.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Rutas de verificacion de credenciales y de salud del servicio.
 */
public class AuthController extends ControladorBase {

    public AuthController(Gson gson, UsuarioService usuarioService) {
        super(gson, usuarioService);
    }

    @Override
    public void registrarRutas(Service http) {

        // Sin autenticacion: sirve para comprobar que el servicio esta vivo (PaaS)
        http.get("/salud", (req, res) -> {
            Map<String, Object> cuerpo = new LinkedHashMap<>();
            cuerpo.put("estado", "OK");
            return responder(res, 200, cuerpo);
        });

        // 200 si las credenciales son validas, 401 si no
        http.get("/auth/perfil", (req, res) -> {
            Usuario usuario = autenticar(req);
            Map<String, Object> cuerpo = new LinkedHashMap<>();
            cuerpo.put("id", usuario.getId());
            cuerpo.put("usuario", usuario.getUsuario());
            cuerpo.put("rol", usuario.getRol().name());
            return responder(res, 200, cuerpo);
        });
    }
}
