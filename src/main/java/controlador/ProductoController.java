package controlador;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import modelo.Accesorio;
import modelo.Administrador;
import modelo.Carta;
import modelo.Producto;
import modelo.ProductoSellado;
import servicio.InventarioService;
import servicio.TasaCambio;
import servicio.TasaCambioService;
import servicio.UsuarioService;
import spark.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Gestion y consulta del inventario: cartas, productos sellados y accesorios.
 */
public class ProductoController extends ControladorBase {

    private final InventarioService inventarioService;
    private final TasaCambioService tasaCambioService;
    private final Vistas vistas;

    public ProductoController(Gson gson, UsuarioService usuarioService,
                              InventarioService inventarioService,
                              TasaCambioService tasaCambioService, Vistas vistas) {
        super(gson, usuarioService);
        this.inventarioService = inventarioService;
        this.tasaCambioService = tasaCambioService;
        this.vistas = vistas;
    }

    @Override
    public void registrarRutas(Service http) {

        // ---------- Registro (solo administrador) -> 201 ----------

        http.post("/cartas", (req, res) -> {
            Administrador admin = exigirAdmin(req);
            JsonObject cuerpo = leerCuerpo(req);
            Carta carta = inventarioService.crearCarta(admin,
                    textoObligatorio(cuerpo, "nombre"),
                    numeroObligatorio(cuerpo, "precioUSD"),
                    textoObligatorio(cuerpo, "rareza"));
            return responder(res, 201, vistas.producto(carta, tasaCambioService.obtenerTasa()));
        });

        http.post("/productos-sellados", (req, res) -> {
            Administrador admin = exigirAdmin(req);
            JsonObject cuerpo = leerCuerpo(req);
            ProductoSellado sellado = inventarioService.crearProductoSellado(admin,
                    textoObligatorio(cuerpo, "nombre"),
                    numeroObligatorio(cuerpo, "precioUSD"),
                    textoObligatorio(cuerpo, "descripcion"));
            return responder(res, 201, vistas.producto(sellado, tasaCambioService.obtenerTasa()));
        });

        http.post("/accesorios", (req, res) -> {
            Administrador admin = exigirAdmin(req);
            JsonObject cuerpo = leerCuerpo(req);
            Accesorio accesorio = inventarioService.crearAccesorio(admin,
                    textoObligatorio(cuerpo, "nombre"),
                    numeroObligatorio(cuerpo, "precioUSD"),
                    textoObligatorio(cuerpo, "tipo"));
            return responder(res, 201, vistas.producto(accesorio, tasaCambioService.obtenerTasa()));
        });

        // ---------- Consulta (cualquier usuario autenticado) -> 200 ----------

        http.get("/productos", (req, res) -> {
            autenticar(req);
            TasaCambio tasa = tasaCambioService.obtenerTasa();
            return responder(res, 200, vistas.productos(inventarioService.listarDisponibles(), tasa));
        });

        http.get("/cartas", (req, res) -> {
            autenticar(req);
            TasaCambio tasa = tasaCambioService.obtenerTasa();
            return responder(res, 200, vistas.productos(inventarioService.listarPorTipo(Carta.class), tasa));
        });

        http.get("/productos-sellados", (req, res) -> {
            autenticar(req);
            TasaCambio tasa = tasaCambioService.obtenerTasa();
            return responder(res, 200,
                    vistas.productos(inventarioService.listarPorTipo(ProductoSellado.class), tasa));
        });

        http.get("/accesorios", (req, res) -> {
            autenticar(req);
            TasaCambio tasa = tasaCambioService.obtenerTasa();
            return responder(res, 200,
                    vistas.productos(inventarioService.listarPorTipo(Accesorio.class), tasa));
        });

        http.get("/productos/:id", (req, res) -> {
            autenticar(req);
            Producto producto = inventarioService.buscar(req.params(":id"));
            return responder(res, 200, vistas.producto(producto, tasaCambioService.obtenerTasa()));
        });

        // ---------- Eliminacion (solo administrador) ----------

        http.delete("/productos/:id", (req, res) -> {
            exigirAdmin(req);
            inventarioService.eliminar(req.params(":id"));
            Map<String, Object> cuerpo = new LinkedHashMap<>();
            cuerpo.put("mensaje", "Producto eliminado");
            return responder(res, 200, cuerpo);
        });
    }
}
