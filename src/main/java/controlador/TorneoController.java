package controlador;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import modelo.Administrador;
import modelo.Cliente;
import modelo.Torneo;
import servicio.TorneoService;
import servicio.UsuarioService;
import spark.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Creacion, consulta e inscripcion en torneos.
 */
public class TorneoController extends ControladorBase {

    private final TorneoService torneoService;
    private final Vistas vistas;

    public TorneoController(Gson gson, UsuarioService usuarioService,
                            TorneoService torneoService, Vistas vistas) {
        super(gson, usuarioService);
        this.torneoService = torneoService;
        this.vistas = vistas;
    }

    @Override
    public void registrarRutas(Service http) {

        // Solo el administrador crea torneos -> 201
        http.post("/torneos", (req, res) -> {
            Administrador admin = exigirAdmin(req);
            JsonObject cuerpo = leerCuerpo(req);
            Torneo torneo = torneoService.crearTorneo(admin,
                    textoObligatorio(cuerpo, "nombre"),
                    textoObligatorio(cuerpo, "fechaProgramada"),
                    enteroObligatorio(cuerpo, "cupoMaximo"));
            return responder(res, 201, vistas.torneo(torneo));
        });

        http.get("/torneos", (req, res) -> {
            autenticar(req);
            List<Map<String, Object>> lista = new ArrayList<>();
            for (Torneo torneo : torneoService.listar()) {
                lista.add(vistas.torneo(torneo));
            }
            return responder(res, 200, lista);
        });

        http.get("/torneos/:id", (req, res) -> {
            autenticar(req);
            return responder(res, 200, vistas.torneo(torneoService.buscar(req.params(":id"))));
        });

        // Solo clientes: 404 torneo inexistente, 409 sin cupo o inscripcion duplicada
        http.post("/torneos/:id/inscripciones", (req, res) -> {
            Cliente cliente = exigirCliente(req);
            Torneo torneo = torneoService.inscribir(cliente, req.params(":id"));
            Map<String, Object> cuerpo = new LinkedHashMap<>();
            cuerpo.put("mensaje", "Inscripcion exitosa");
            cuerpo.put("torneo", vistas.torneo(torneo));
            return responder(res, 201, cuerpo);
        });
    }
}
