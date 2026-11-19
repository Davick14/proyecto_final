package servicio;

import modelo.Administrador;
import modelo.Cliente;
import modelo.Torneo;
import repositorio.AlmacenDatos;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Logica de torneos: creacion, consulta e inscripcion.
 */
public class TorneoService {

    private final List<Torneo> torneos;

    public TorneoService(AlmacenDatos almacen) {
        this.torneos = almacen.getTorneos();
    }

    public synchronized Torneo crearTorneo(Administrador admin, String nombre, String fechaTexto,
                                           int cupoMaximo) {
        LocalDateTime fecha;
        try {
            fecha = LocalDateTime.parse(fechaTexto);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Fecha invalida. Use el formato 2026-12-31T18:00:00");
        }
        Torneo torneo = admin.crearTorneo(UUID.randomUUID().toString(), nombre, fecha, cupoMaximo);
        torneos.add(torneo);
        return torneo;
    }

    public synchronized List<Torneo> listar() {
        return new ArrayList<>(torneos);
    }

    public synchronized Torneo buscar(String id) {
        for (Torneo torneo : torneos) {
            if (torneo.getId().equals(id)) {
                return torneo;
            }
        }
        throw new NoSuchElementException("Torneo no encontrado");
    }

    /** Inscribe al cliente: 404 si no existe, 409 si ya esta inscrito o no hay cupo. */
    public synchronized Torneo inscribir(Cliente cliente, String torneoId) {
        Torneo torneo = buscar(torneoId);
        if (torneo.estaInscrito(cliente)) {
            throw new IllegalStateException("El cliente ya esta inscrito en este torneo");
        }
        if (!torneo.hayCupo()) {
            throw new IllegalStateException("El torneo no tiene cupos disponibles");
        }
        cliente.inscribirEnTorneo(torneo);
        return torneo;
    }
}
