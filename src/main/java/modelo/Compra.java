package modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Registro de una compra realizada por un cliente.
 */
public class Compra {

    private String id;
    private Cliente cliente;
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
        this.fuenteTasa = fuenteTasa;
        this.detalles = new ArrayList<>();
        this.fecha = LocalDateTime.now();
        this.totalPagadoCOP = 0;
    }

    public void agregarDetalle(DetalleCompra detalle) {
        if (detalle == null) {
            throw new IllegalArgumentException("El detalle no puede ser nulo");
        }
        detalles.add(detalle);
    }

    /** Suma los subtotales de los detalles y guarda el resultado en totalPagadoCOP. */
    public double calcularTotal() {
        double total = 0;
        for (DetalleCompra detalle : detalles) {
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

    public List<DetalleCompra> getDetalles() {
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
