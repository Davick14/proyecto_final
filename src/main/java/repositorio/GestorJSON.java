package repositorio;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GestorJSON {

    private final Gson gson;

    public GestorJSON() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonSerializer<LocalDateTime>) (f, t, c) -> new JsonPrimitive(f.toString()))
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (j, t, c) -> LocalDateTime.parse(j.getAsString()))
                .setPrettyPrinting()
                .create();
    }

    public <T> List<T> leerLista(String rutaArchivo, Class<T[]> claseArreglo) {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists() || archivo.length() == 0) {
            return new ArrayList<>();
        }
        try (Reader reader = new FileReader(archivo)) {
            T[] arreglo = gson.fromJson(reader, claseArreglo);
            if (arreglo == null) {
                return new ArrayList<>();
            }
            return new ArrayList<>(Arrays.asList(arreglo));
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo " + rutaArchivo, e);
        }
    }

    public <T> void escribirLista(String rutaArchivo, List<T> lista) {
        File archivo = new File(rutaArchivo);
        File carpetaPadre = archivo.getParentFile();
        if (carpetaPadre != null && !carpetaPadre.exists()) {
            carpetaPadre.mkdirs();
        }
        try (Writer writer = new FileWriter(archivo)) {
            gson.toJson(lista, writer);
        } catch (IOException e) {
            throw new RuntimeException("Error al escribir el archivo " + rutaArchivo, e);
        }
    }
}