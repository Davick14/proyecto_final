package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Usuario que consulta productos, compra, abre boosters e inscribe en torneos.
 */
public class Cliente extends Usuario {

    private List<Compra> historialCompras;
    private List<Torneo> torneosInscritos;

    public Cliente(String id, String usuario, String contrasena) {
        super(id, usuario, contrasena, RolUsuario.CLIENTE);
        this.historialCompras = new ArrayList<>();
        this.torneosInscritos = new ArrayList<>();
    }

    public void agregarCompra(Compra compra) {
        if (compra == null) {
            throw new IllegalArgumentException("La compra no puede ser nula");
        }
        historialCompras.add(compra);
    }

    /** Inscribe al cliente en el torneo. Retorna false si no hay cupo o ya estaba inscrito. */
    public boolean inscribirEnTorneo(Torneo torneo) {
        if (torneo == null) {
            throw new IllegalArgumentException("El torneo no puede ser nulo");
        }
        if (torneo.inscribirCliente(this)) {
            torneosInscritos.add(torneo);
            return true;
        }
        return false;
    }

    /** Abre un booster propio y retorna las cartas obtenidas. */
    public List<Carta> abrirBooster(Booster booster) {
        if (booster == null) {
            throw new IllegalArgumentException("El booster no puede ser nulo");
        }
        return booster.abrir(this);
    }

    public List<Compra> getHistorialCompras() {
        return historialCompras;
    }

    public List<Torneo> getTorneosInscritos() {
        return torneosInscritos;
    }
}
