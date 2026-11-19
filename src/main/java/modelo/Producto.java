package modelo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Clase base abstracta de todo lo que se vende en Cards4Play.
 *
 * Al construirse, el producto consulta la tasa USD -> COP en una API externa
 * (timeout de 3 segundos). Si la API falla, usa la tasa por defecto de la variable
 * de entorno TASA_DEFECTO. El precio en COP queda fijado en ese momento.
 */
public abstract class Producto {

    private String id;
    private String nombre;
    private double precioUSD;
    private double precioCOP;
    private String fuenteTasa;

    protected Producto(String id, String nombre, double precioUSD) {
        if (id == null || id.isBlank() || nombre == null || nombre.isBlank() || precioUSD < 0) {
            throw new IllegalArgumentException("Datos de producto invalidos o incompletos");
        }
        this.id = id;
        this.nombre = nombre;
        this.precioUSD = precioUSD;

        Double tasaApi = consultarTasaApi();
        double tasa;
        if (tasaApi != null) {
            tasa = tasaApi;
            this.fuenteTasa = "API";
        } else {
            tasa = leerTasaDefecto();
            this.fuenteTasa = "DEFECTO";
            System.out.println("La API no respondió, por lo que se toma un valor por defecto de " + tasa);
        }
        this.precioCOP = precioUSD * tasa;
    }

    /** Precio en COP fijado al crear el producto. */
    public double calcularPrecioCOP() {
        return precioCOP;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecioUSD() {
        return precioUSD;
    }

    public double getPrecioCOP() {
        return precioCOP;
    }

    /** "API" o "DEFECTO": de donde salio la tasa con que se fijo el precio. */
    public String getFuenteTasa() {
        return fuenteTasa;
    }

    // ---------- Apoyo interno ----------

    /** Retorna la tasa USD -> COP de la API, o null si fallo o tardo mas del timeout. */
    private Double consultarTasaApi() {
        final int timeoutSegundos = 3;
        try {
            HttpClient cliente = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(timeoutSegundos))
                    .build();
            HttpRequest peticion = HttpRequest.newBuilder()
                    .uri(URI.create("https://open.er-api.com/v6/latest/USD"))
                    .timeout(Duration.ofSeconds(timeoutSegundos))
                    .GET()
                    .build();
            HttpResponse<String> respuesta =
                    cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() != 200) {
                return null;
            }
            JsonObject json = new Gson().fromJson(respuesta.body(), JsonObject.class);
            double valor = json.getAsJsonObject("rates").get("COP").getAsDouble();
            if (valor <= 0) {
                return null;
            }
            return valor;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (Exception e) {
            return null;
        }
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
