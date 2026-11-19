package controlador;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import modelo.Accesorio;
import modelo.Administrador;
import modelo.Carta;
import modelo.Producto;
import modelo.ProductoSellado;
import servicio.InventarioService;
import servicio.UsuarioService;
import spark.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Gestion y consulta del inventario: cartas, productos sellados y accesorios.
 */
public class ProductoController extends ControladorBase {

    private final InventarioService inventarioService;
    private final Vistas vistas;

    public ProductoController(Gson gson, UsuarioService usuarioService,
                              InventarioService inventarioService, Vistas vistas) {
        super(gson, usuarioService);
        this.inventarioService = inventarioService;
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
            return responder(res, 201, vistas.producto(carta));
        });

        http.post("/productos-sellados", (req, res) -> {
            Administrador admin = exigirAdmin(req);
            JsonObject cuerpo = leerCuerpo(req);
            ProductoSellado sellado = inventarioService.crearProductoSellado(admin,
                    textoObligatorio(cuerpo, "nombre"),
                    numeroObligatorio(cuerpo, "precioUSD"),
                    textoObligatorio(cuerpo, "descripcion"));
            return responder(res, 201, vistas.producto(sellado));
        });

        http.post("/accesorios", (req, res) -> {
            Administrador admin = exigirAdmin(req);
            JsonObject cuerpo = leerCuerpo(req);
            Accesorio accesorio = inventarioService.crearAccesorio(admin,
                    textoObligatorio(cuerpo, "nombre"),
                    numeroObligatorio(cuerpo, "precioUSD"),
                    textoObligatorio(cuerpo, "tipo"));
            return responder(res, 201, vistas.producto(accesorio));
        });

        // ---------- Consulta (cualquier usuario autenticado) -> 200 ----------

        http.get("/productos", (req, res) -> {
            autenticar(req);
            return responder(res, 200, vistas.productos(inventarioService.listarDisponibles()));
        });

        http.get("/cartas", (req, res) -> {
            autenticar(req);
            return responder(res, 200, vistas.productos(inventarioService.listarPorTipo(Carta.class)));
        });

        http.get("/productos-sellados", (req, res) -> {
            autenticar(req);
            return responder(res, 200,
                    vistas.productos(inventarioService.listarPorTipo(ProductoSellado.class)));
        });

        http.get("/accesorios", (req, res) -> {
            autenticar(req);
            return responder(res, 200,
                    vistas.productos(inventarioService.listarPorTipo(Accesorio.class)));
        });

        http.get("/productos/:id", (req, res) -> {
            autenticar(req);
            Producto producto = inventarioService.buscar(req.params(":id"));
            return responder(res, 200, vistas.producto(producto));
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
