package barrio.modelo.expensas;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Pago implements Serializable {
    private static final long serialVersionUID = 1L;

    private int numero;
    private int numeroLote;
    private LocalDate fecha;
    private double importe;
    private MedioPago medio;
    private List<AplicacionPago> aplicaciones;
    private boolean ajustado;

    public Pago(int numero, int numeroLote, LocalDate fecha, double importe, MedioPago medio) {
        if (fecha == null || medio == null) {
            throw new IllegalArgumentException("La fecha y el medio de pago son obligatorios.");
        }
        this.numero = numero;
        this.numeroLote = numeroLote;
        this.fecha = fecha;
        this.importe = Expensa.redondear(importe);
        this.medio = medio;
        this.aplicaciones = new ArrayList<>();
        this.ajustado = false;
    }

    public boolean validarImporte() {
        return importe > 0;
    }

    public void agregarAplicacion(AplicacionPago aplicacion) {
        aplicaciones.add(aplicacion);
    }

    void marcarAjustado() {
        this.ajustado = true;
    }

    public int getNumero() { return numero; }
    public int getNumeroLote() { return numeroLote; }
    public LocalDate getFecha() { return fecha; }
    public double getImporte() { return importe; }
    public MedioPago getMedio() { return medio; }
    public boolean isAjustado() { return ajustado; }

    public List<AplicacionPago> getAplicaciones() {
        return new ArrayList<>(aplicaciones);
    }

    @Override
    public String toString() {
        return "Pago " + numero + " - lote " + numeroLote + " - " + fecha + " - $" + importe + " ("
                + medio.name().toLowerCase() + ")" + (ajustado ? " [ANULADO POR AJUSTE]" : "");
    }
}
