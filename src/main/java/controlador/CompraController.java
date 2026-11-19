package controlador;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import modelo.Cliente;
import modelo.Compra;
import modelo.Usuario;
import servicio.CompraService;
import servicio.UsuarioService;
import spark.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registro de compras y consulta de compras realizadas.
 *
 * Cuerpo esperado para comprar:
 * { "items": [ { "productoId": "...", "cantidad": 1 } ] }
 */
public class CompraController extends ControladorBase {

    private final CompraService compraService;
    private final Vistas vistas;

    public CompraController(Gson gson, UsuarioService usuarioService,
                            CompraService compraService, Vistas vistas) {
        super(gson, usuarioService);
        this.compraService = compraService;
        this.vistas = vistas;
    }

    @Override
    public void registrarRutas(Service http) {

        // Solo clientes compran -> 201
        http.post("/compras", (req, res) -> {
            Cliente cliente = exigirCliente(req);
            Map<String, Integer> items = leerItems(leerCuerpo(req));
            Compra compra = compraService.comprar(cliente, items);
            return responder(res, 201, vistas.compra(compra));
        });

        // Cliente: sus compras. Administrador: todas las compras.
        http.get("/compras", (req, res) -> {
            Usuario usuario = autenticar(req);
            List<Compra> lista = usuario instanceof Cliente cliente
                    ? compraService.listarDeCliente(cliente)
                    : compraService.listarTodas();
            return responder(res, 200, vistas.compras(lista));
        });
    }

    private Map<String, Integer> leerItems(JsonObject cuerpo) {
        JsonElement elemento = cuerpo.get("items");
        if (elemento == null || !elemento.isJsonArray() || elemento.getAsJsonArray().isEmpty()) {
            throw new IllegalArgumentException("El campo 'items' debe ser una lista con al menos un producto");
        }
        Map<String, Integer> items = new LinkedHashMap<>();
        for (JsonElement item : elemento.getAsJsonArray()) {
            if (!item.isJsonObject()) {
                throw new IllegalArgumentException("Cada item debe ser un objeto con productoId y cantidad");
            }
            JsonObject objeto = item.getAsJsonObject();
            String productoId = textoObligatorio(objeto, "productoId");
            int cantidad = enteroObligatorio(objeto, "cantidad");
            items.merge(productoId, cantidad, Integer::sum);
        }
        return items;
    }
}
