package controlador;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import excepcion.NoAutenticadoException;
import spark.Response;
import spark.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Arranca Spark, registra las rutas de los controladores y traduce
 * las excepciones a codigos HTTP con un cuerpo JSON uniforme: {"error": "..."}.
 */
public class Servidor {

    private final int puerto;
    private final Gson gson;
    private final List<ControladorBase> controladores;

    public Servidor(int puerto, Gson gson, List<ControladorBase> controladores) {
        this.puerto = puerto;
        this.gson = gson;
        this.controladores = controladores;
    }

    public void iniciar() {
        Service http = Service.ignite();
        http.port(puerto);

        registrarManejoDeErrores(http);
        for (ControladorBase controlador : controladores) {
            controlador.registrarRutas(http);
        }
        http.notFound((req, res) -> {
            res.type("application/json");
            return gson.toJson(Map.of("error", "Ruta no encontrada"));
        });

        http.awaitInitialization();
    }

    private void registrarManejoDeErrores(Service http) {
        http.exception(NoAutenticadoException.class,
                (e, req, res) -> responderError(res, 401, e.getMessage()));
        http.exception(SecurityException.class,
                (e, req, res) -> responderError(res, 403, e.getMessage()));
        http.exception(NoSuchElementException.class,
                (e, req, res) -> responderError(res, 404, e.getMessage()));
        http.exception(IllegalArgumentException.class,
                (e, req, res) -> responderError(res, 400, e.getMessage()));
        http.exception(JsonParseException.class,
                (e, req, res) -> responderError(res, 400, "JSON invalido en el cuerpo de la peticion"));
        http.exception(IllegalStateException.class,
                (e, req, res) -> responderError(res, 409, e.getMessage()));
        http.exception(Exception.class, (e, req, res) -> {
            e.printStackTrace();
            responderError(res, 500, "Error interno del servidor");
        });
    }

    private void responderError(Response res, int estado, String mensaje) {
        res.status(estado);
        res.type("application/json");
        res.body(gson.toJson(Map.of("error", mensaje == null ? "Error" : mensaje)));
    }
}
