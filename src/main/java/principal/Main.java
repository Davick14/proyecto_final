package principal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controlador.AuthController;
import controlador.BoosterController;
import controlador.ClienteController;
import controlador.CompraController;
import controlador.ControladorBase;
import controlador.ProductoController;
import controlador.Servidor;
import controlador.TorneoController;
import controlador.Vistas;
import servicio.BoosterService;
import servicio.CompraService;
import servicio.InventarioService;
import servicio.TasaCambioService;
import servicio.TorneoService;
import servicio.UsuarioService;

import java.util.List;

/**
 * Punto de entrada. El unico metodo static es main, que Java exige para arrancar.
 */
public class Main {

    public static void main(String[] args) {
        new Main().arrancar();
    }

    private void arrancar() {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        UsuarioService usuarioService = new UsuarioService();
        InventarioService inventarioService = new InventarioService();
        BoosterService boosterService = new BoosterService(inventarioService);
        TorneoService torneoService = new TorneoService();
        TasaCambioService tasaCambioService = new TasaCambioService(gson);
        CompraService compraService = new CompraService(inventarioService, tasaCambioService);
        Vistas vistas = new Vistas();

        List<ControladorBase> controladores = List.of(
                new AuthController(gson, usuarioService),
                new ClienteController(gson, usuarioService, vistas),
                new ProductoController(gson, usuarioService, inventarioService, tasaCambioService, vistas),
                new BoosterController(gson, usuarioService, boosterService, tasaCambioService, vistas),
                new TorneoController(gson, usuarioService, torneoService, vistas),
                new CompraController(gson, usuarioService, compraService, vistas)
        );

        int puerto = leerPuerto();
        new Servidor(puerto, gson, controladores).iniciar();
        System.out.println("Cards4Play backend iniciado en el puerto " + puerto);
    }

    private int leerPuerto() {
        String valor = System.getenv("PORT");
        if (valor == null || valor.isBlank()) {
            return 4567;
        }
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            return 4567;
        }
    }
}
