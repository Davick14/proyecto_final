package servicio;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Consulta la tasa de cambio USD -> COP en una API externa (timeout de 3 segundos).
 * Si la API falla o no responde a tiempo, usa la variable de entorno TASA_DEFECTO (4000.0).
 */
public class TasaCambioService {

    public record ResultadoTasa(double tasa, String fuente) {}

    private static final String URL_API = "https://open.er-api.com/v6/latest/USD";
    private static final int TIMEOUT_SEGUNDOS = 3;
    private static final double TASA_RESPALDO = 4000.0;

    private final HttpClient httpClient;
    private final Gson gson;

    public TasaCambioService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(TIMEOUT_SEGUNDOS))
                .build();
        this.gson = new Gson();
    }

    public ResultadoTasa obtenerTasa() {
        Double tasaApi = consultarTasaApi();
        if (tasaApi != null) {
            return new ResultadoTasa(tasaApi, "API");
        }
        double tasaDefecto = leerTasaDefecto();
        System.out.println("La API no respondio, por lo que se toma un valor por defecto de " + tasaDefecto);
        return new ResultadoTasa(tasaDefecto, "DEFECTO");
    }

    protected Double consultarTasaApi() {
        try {
            HttpRequest peticion = HttpRequest.newBuilder()
                    .uri(URI.create(URL_API))
                    .timeout(Duration.ofSeconds(TIMEOUT_SEGUNDOS))
                    .GET()
                    .build();
            HttpResponse<String> respuesta =
                    httpClient.send(peticion, HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() != 200) {
                return null;
            }
            JsonObject json = gson.fromJson(respuesta.body(), JsonObject.class);
            double valor = json.getAsJsonObject("rates").get("COP").getAsDouble();
            return valor > 0 ? valor : null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    protected double leerTasaDefecto() {
        String valor = System.getenv("TASA_DEFECTO");
        if (valor == null || valor.isBlank()) {
            return TASA_RESPALDO;
        }
        try {
            double tasa = Double.parseDouble(valor.trim());
            return tasa > 0 ? tasa : TASA_RESPALDO;
        } catch (NumberFormatException e) {
            return TASA_RESPALDO;
        }
    }
}