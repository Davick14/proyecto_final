package controlador;

import modelo.Accesorio;
import modelo.Booster;
import modelo.Carta;
import modelo.Cliente;
import modelo.Compra;
import modelo.DetalleCompra;
import modelo.Producto;
import modelo.ProductoSellado;
import modelo.Torneo;
import servicio.TasaCambioService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Convierte el modelo en estructuras simples para responder en JSON.
 * Asi se evitan referencias circulares y nunca se revela el contenido de un booster cerrado.
 */
public class Vistas {

    private final TasaCambioService tasaCambioService;

    public Vistas(TasaCambioService tasaCambioService) {
        this.tasaCambioService = tasaCambioService;
    }

    public Map<String, Object> cliente(Cliente cliente) {
        Map<String, Object> vista = new LinkedHashMap<>();
        vista.put("id", cliente.getId());
        vista.put("usuario", cliente.getUsuario());
        vista.put("rol", cliente.getRol().name());
        return vista;
    }

    /** Vista para consultas de inventario: incluye precio en COP y origen de la tasa. */
    public Map<String, Object> producto(Producto producto) {
        return productoConTasa(producto, tasaCambioService.obtenerTasa());
    }

    public List<java.util.Map<String, Object>> productos(List<? extends Producto> lista) {
        TasaCambioService.ResultadoTasa resultadoTasa = tasaCambioService.obtenerTasa();
        List<java.util.Map<String, Object>> resultado = new ArrayList<>();
        for (Producto producto : lista) {
            resultado.add(productoConTasa(producto, resultadoTasa));
        }
        return resultado;
    }

    /** Vista de un producto dentro del historial: boosters abiertos muestran sus cartas. */
    public Map<String, Object> productoComprado(Producto producto) {
        Map<String, Object> vista = base(producto);
        if (producto instanceof Booster booster) {
            vista.put("abierto", booster.estaAbierto());
            if (booster.estaAbierto()) {
                vista.put("cartas", cartas(booster.getCartasContenidas()));
            }
        }
        return vista;
    }

    public List<java.util.Map<String, Object>> cartas(List<Carta> cartas) {
        List<java.util.Map<String, Object>> resultado = new ArrayList<>();
        for (Carta carta : cartas) {
            resultado.add(base(carta));
        }
        return resultado;
    }

    public Map<String, Object> torneo(Torneo torneo) {
        Map<String, Object> vista = new LinkedHashMap<>();
        vista.put("id", torneo.getId());
        vista.put("nombre", torneo.getNombre());
        vista.put("fechaProgramada", torneo.getFechaProgramada().toString());
        vista.put("cupoMaximo", torneo.getCupoMaximo());
        vista.put("inscritos", torneo.getParticipantes().size());
        vista.put("cupoDisponible", torneo.getCupoMaximo() - torneo.getParticipantes().size());
        return vista;
    }

    public Map<String, Object> compra(Compra compra) {
        Map<String, Object> vista = new LinkedHashMap<>();
        vista.put("id", compra.getId());
        vista.put("clienteId", compra.getCliente().getId());
        vista.put("fecha", compra.getFecha().toString());
        List<java.util.Map<String, Object>> detalles = new ArrayList<>();
        for (DetalleCompra detalle : compra.getDetalles()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("producto", productoComprado(detalle.getProducto()));
            item.put("cantidad", detalle.getCantidad());
            item.put("precioUnitarioCOP", detalle.getPrecioUnitarioCOP());
            item.put("subtotalCOP", detalle.getSubtotalCOP());
            detalles.add(item);
        }
        vista.put("detalles", detalles);
        vista.put("totalPagadoCOP", compra.getTotalPagadoCOP());
        vista.put("fuenteTasa", compra.getFuenteTasa());
        return vista;
    }

    public List<java.util.Map<String, Object>> compras(List<Compra> lista) {
        List<java.util.Map<String, Object>> resultado = new ArrayList<>();
        for (Compra compra : lista) {
            resultado.add(compra(compra));
        }
        return resultado;
    }

    //Apoyo interno 

    private Map<String, Object> productoConTasa(Producto producto,
                                                TasaCambioService.ResultadoTasa resultadoTasa) {
        Map<String, Object> vista = base(producto);
        vista.put("precioCOP", producto.calcularPrecioCOP(resultadoTasa.tasa()));
        vista.put("fuenteTasa", resultadoTasa.fuente());
        if (producto instanceof Booster booster) {
            vista.put("estado", booster.estaVendido() ? "VENDIDO" : "DISPONIBLE");
        }
        return vista;
    }

    private Map<String, Object> base(Producto producto) {
        Map<String, Object> vista = new LinkedHashMap<>();
        vista.put("id", producto.getId());
        vista.put("nombre", producto.getNombre());
        vista.put("tipo", tipo(producto));
        vista.put("precioUSD", producto.getPrecioUSD());
        if (producto instanceof Carta carta) {
            vista.put("rareza", carta.getRareza().getCodigo());
        } else if (producto instanceof ProductoSellado sellado) {
            vista.put("descripcion", sellado.getDescripcion());
        } else if (producto instanceof Accesorio accesorio) {
            vista.put("tipoAccesorio", accesorio.getTipo());
        }
        return vista;
    }

    private String tipo(Producto producto) {
        if (producto instanceof Carta) {
            return "CARTA";
        }
        if (producto instanceof Booster) {
            return "BOOSTER";
        }
        if (producto instanceof ProductoSellado) {
            return "SELLADO";
        }
        return "ACCESORIO";
    }
}