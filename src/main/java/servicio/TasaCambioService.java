package servicio;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Consulta la tasa USD -> COP en una API externa con timeout.
 * Si falla, usa la tasa por defecto definida en la variable de entorno TASA_DEFECTO.
 */
public class TasaCambioService {

    private final Gson gson;
    private final HttpClient clienteHttp;
    private final String urlApi;
    private final int timeoutSegundos;
    private final double tasaDefecto;

    public TasaCambioService(Gson gson) {
        this.gson = gson;
        this.urlApi = "https://open.er-api.com/v6/latest/USD";
        this.timeoutSegundos = 3;
        this.tasaDefecto = leerTasaDefecto();
        this.clienteHttp = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(timeoutSegundos))
                .build();
    }

    public TasaCambio obtenerTasa() {
        try {
            HttpRequest peticion = HttpRequest.newBuilder()
                    .uri(URI.create(urlApi))
                    .timeout(Duration.ofSeconds(timeoutSegundos))
                    .GET()
                    .build();
            HttpResponse<String> respuesta =
                    clienteHttp.send(peticion, HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() != 200) {
                throw new IllegalStateException("Estado HTTP " + respuesta.statusCode());
            }
            JsonObject json = gson.fromJson(respuesta.body(), JsonObject.class);
            double valor = json.getAsJsonObject("rates").get("COP").getAsDouble();
            if (valor <= 0) {
                throw new IllegalStateException("Tasa invalida");
            }
            return new TasaCambio(valor, "API");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return usarDefecto();
        } catch (Exception e) {
            return usarDefecto();
        }
    }

    public double getTasaDefecto() {
        return tasaDefecto;
    }

    private TasaCambio usarDefecto() {
        System.out.println("La API no respondió, por lo que se toma un valor por defecto de " + tasaDefecto);
        return new TasaCambio(tasaDefecto, "DEFECTO");
    }

    private double leerTasaDefecto() {
        String valor = System.getenv("TASA_DEFECTO");
        if (valor == null || valor.isBlank()) {
            return 4000.0;
        }
        try {
            double tasa = Double.parseDouble(valor.trim());
            return tasa > 0 ? tasa : 4000.0;
        } catch (NumberFormatException e) {
            return 4000.0;
        }
    }
}
