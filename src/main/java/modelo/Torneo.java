package modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Evento competitivo con cupo maximo de participantes.
 */
public class Torneo {

    private String id;
    private String nombre;
    private LocalDateTime fechaProgramada;
    private int cupoMaximo;
    private transient List<Cliente> participantes;
    private List<String> participantesIds;

    public Torneo(String id, String nombre, LocalDateTime fechaProgramada, int cupoMaximo) {
        if (id == null || id.isBlank() || nombre == null || nombre.isBlank()
                || fechaProgramada == null || cupoMaximo <= 0) {
            throw new IllegalArgumentException("Datos de torneo invalidos o incompletos");
        }
        this.id = id;
        this.nombre = nombre;
        this.fechaProgramada = fechaProgramada;
        this.cupoMaximo = cupoMaximo;
        this.participantes = new ArrayList<>();
        this.participantesIds = new ArrayList<>();
    }

    /** Retorna false si no hay cupo o si el cliente ya estaba inscrito. */
    public boolean inscribirCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo");
        }
        if (!hayCupo() || estaInscrito(cliente)) {
            return false;
        }
        getParticipantes().add(cliente);
        getParticipantesIds().add(cliente.getId());
        return true;
    }

    public boolean hayCupo() {
        return getParticipantes().size() < cupoMaximo;
    }

    public boolean estaInscrito(Cliente cliente) {
        for (Cliente participante : getParticipantes()) {
            if (participante.getId().equals(cliente.getId())) {
                return true;
            }
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public List<Cliente> getParticipantes() {
        if (participantes == null) {
            participantes = new ArrayList<>();
        }
        return participantes;
    }

    public List<String> getParticipantesIds() {
        if (participantesIds == null) {
            participantesIds = new ArrayList<>();
        }
        return participantesIds;
    }

    public void reiniciarParticipantes() {
        this.participantes = new ArrayList<>();
    }
}