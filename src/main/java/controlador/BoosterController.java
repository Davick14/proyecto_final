package controlador;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import modelo.Administrador;
import modelo.Booster;
import modelo.Carta;
import modelo.Cliente;
import servicio.BoosterService;
import servicio.UsuarioService;
import spark.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Creacion, consulta y apertura de boosters. Su contenido solo se revela al abrirlos.
 */
public class BoosterController extends ControladorBase {

    private final BoosterService boosterService;
    private final Vistas vistas;

    public BoosterController(Gson gson, UsuarioService usuarioService,
                             BoosterService boosterService, Vistas vistas) {
        super(gson, usuarioService);
        this.boosterService = boosterService;
        this.vistas = vistas;
    }

    @Override
    public void registrarRutas(Service http) {

        // Solo el administrador crea boosters -> 201 (sin revelar cartas)
        http.post("/boosters", (req, res) -> {
            Administrador admin = exigirAdmin(req);
            JsonObject cuerpo = leerCuerpo(req);
            Booster booster = boosterService.crearBooster(admin,
                    textoObligatorio(cuerpo, "nombre"),
                    numeroObligatorio(cuerpo, "precioUSD"));
            return responder(res, 201, vistas.producto(booster));
        });

        // Boosters disponibles para la venta, sin contenido
        http.get("/boosters", (req, res) -> {
            autenticar(req);
            return responder(res, 200, vistas.productos(boosterService.listarDisponibles()));
        });

        http.get("/boosters/:id", (req, res) -> {
            autenticar(req);
            Booster booster = boosterService.buscarBooster(req.params(":id"));
            return responder(res, 200, vistas.producto(booster));
        });

        // Solo el dueno (cliente) puede abrirlo, una unica vez
        http.post("/boosters/:id/abrir", (req, res) -> {
            Cliente cliente = exigirCliente(req);
            String id = req.params(":id");
            List<Carta> cartas = boosterService.abrir(cliente, id);
            Map<String, Object> cuerpo = new LinkedHashMap<>();
            cuerpo.put("boosterId", id);
            cuerpo.put("cartas", vistas.cartas(cartas));
            return responder(res, 200, cuerpo);
        });
    }
}
