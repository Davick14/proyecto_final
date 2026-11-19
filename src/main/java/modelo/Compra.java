package modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Registro de una compra realizada por un cliente.
 */
public class Compra {

    private String id;
    private transient Cliente cliente;
    private String clienteId;
    private List<DetalleCompra> detalles;
    private LocalDateTime fecha;
    private double totalPagadoCOP;
    private String fuenteTasa;

    public Compra(String id, Cliente cliente, String fuenteTasa) {
        if (id == null || id.isBlank() || cliente == null
                || fuenteTasa == null || fuenteTasa.isBlank()) {
            throw new IllegalArgumentException("Datos de compra invalidos o incompletos");
        }
        this.id = id;
        this.cliente = cliente;
        this.clienteId = cliente.getId();
        this.fuenteTasa = fuenteTasa;
        this.detalles = new ArrayList<>();
        this.fecha = LocalDateTime.now();
        this.totalPagadoCOP = 0;
    }

    public void agregarDetalle(DetalleCompra detalle) {
        if (detalle == null) {
            throw new IllegalArgumentException("El detalle no puede ser nulo");
        }
        getDetalles().add(detalle);
    }

    /** Suma los subtotales de los detalles y guarda el resultado en totalPagadoCOP. */
    public double calcularTotal() {
        double total = 0;
        for (DetalleCompra detalle : getDetalles()) {
            total += detalle.getSubtotalCOP();
        }
        this.totalPagadoCOP = total;
        return total;
    }

    public String getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<DetalleCompra> getDetalles() {
        if (detalles == null) {
            detalles = new ArrayList<>();
        }
        return detalles;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public double getTotalPagadoCOP() {
        return totalPagadoCOP;
    }

    public String getFuenteTasa() {
        return fuenteTasa;
    }
}