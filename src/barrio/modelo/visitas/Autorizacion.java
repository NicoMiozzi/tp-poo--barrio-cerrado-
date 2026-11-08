package barrio.modelo.visitas;

import barrio.modelo.lotes.Lote;
import barrio.modelo.personas.Persona;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Autorizacion implements Serializable {
    private static final long serialVersionUID = 1L;

    private int numero;
    private String nombreVisitante;
    private String documento;
    private Lote lote;
    private Persona autorizadaPor;
    private LocalDate fecha;
    private LocalTime horaDesde;
    private LocalTime horaHasta;

    public Autorizacion(int numero, String nombreVisitante, String documento, Lote lote, Persona autorizadaPor,
                        LocalDate fecha, LocalTime horaDesde, LocalTime horaHasta) {
        if (nombreVisitante == null || nombreVisitante.trim().isEmpty()
                || documento == null || documento.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre y documento del visitante son obligatorios.");
        }
        if (lote == null || autorizadaPor == null || fecha == null || horaDesde == null || horaHasta == null) {
            throw new IllegalArgumentException("Lote, autorizante, fecha y franja horaria son obligatorios.");
        }
        if (!horaHasta.isAfter(horaDesde)) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la de inicio.");
        }
        this.numero = numero;
        this.nombreVisitante = nombreVisitante.trim();
        this.documento = documento.trim();
        this.lote = lote;
        this.autorizadaPor = autorizadaPor;
        this.fecha = fecha;
        this.horaDesde = horaDesde;
        this.horaHasta = horaHasta;
    }

    public boolean esVigente(LocalDateTime fechaHora) {
        return fechaHora.toLocalDate().equals(fecha) && estaEnFranja(fechaHora.toLocalTime());
    }

    protected boolean estaEnFranja(LocalTime hora) {
        return !hora.isBefore(horaDesde) && !hora.isAfter(horaHasta);
    }

    public boolean corresponde(String documento, int numeroLote) {
        return this.documento.equalsIgnoreCase(documento.trim()) && lote.getNumero() == numeroLote;
    }

    public String describirVigencia() {
        return fecha + " de " + horaDesde + " a " + horaHasta;
    }

    public int getNumero() { return numero; }
    public String getNombreVisitante() { return nombreVisitante; }
    public String getDocumento() { return documento; }
    public Lote getLote() { return lote; }
    public Persona getAutorizadaPor() { return autorizadaPor; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHoraDesde() { return horaDesde; }
    public LocalTime getHoraHasta() { return horaHasta; }

    @Override
    public String toString() {
        return "Autorizacion " + numero + " - " + nombreVisitante + " (DNI " + documento + ") - lote "
                + lote.getNumero() + " - " + describirVigencia() + " - autorizo "
                + autorizadaPor.obtenerNombreCompleto();
    }
}
