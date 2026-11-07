package barrio.modelo.expensas;

import java.io.Serializable;
import java.time.LocalDate;

public class AsientoAjuste implements Serializable {
    private static final long serialVersionUID = 1L;

    private int numero;
    private LocalDate fecha;
    private Pago pagoAjustado;
    private double importe;
    private String motivo;

    public AsientoAjuste(int numero, LocalDate fecha, Pago pagoAjustado, String motivo) {
        if (pagoAjustado == null) {
            throw new IllegalArgumentException("El asiento debe referirse a un pago.");
        }
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("El asiento de ajuste necesita un motivo.");
        }
        this.numero = numero;
        this.fecha = fecha;
        this.pagoAjustado = pagoAjustado;
        this.importe = -pagoAjustado.getImporte();
        this.motivo = motivo;
    }

    public void aplicar() {
        for (AplicacionPago aplicacion : pagoAjustado.getAplicaciones()) {
            aplicacion.revertir();
        }
        pagoAjustado.marcarAjustado();
    }

    public int getNumero() { return numero; }
    public LocalDate getFecha() { return fecha; }
    public Pago getPagoAjustado() { return pagoAjustado; }
    public double getImporte() { return importe; }
    public String getMotivo() { return motivo; }

    @Override
    public String toString() {
        return "Ajuste " + numero + " - " + fecha + " - $" + importe + " sobre pago " + pagoAjustado.getNumero()
                + " - motivo: " + motivo;
    }
}
