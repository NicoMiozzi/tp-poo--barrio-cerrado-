package barrio.modelo.visitas;

import barrio.modelo.lotes.Lote;
import barrio.modelo.personas.Persona;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class AutorizacionRecurrente extends Autorizacion {
    private static final long serialVersionUID = 1L;

    public static final int MAXIMO_DIAS = 30;

    private String empresa;
    private LocalDate fechaHasta;

    public AutorizacionRecurrente(int numero, String nombreVisitante, String documento, String empresa, Lote lote,
                                  Persona autorizadaPor, LocalDate fechaDesde, LocalDate fechaHasta,
                                  LocalTime horaDesde, LocalTime horaHasta) {
        super(numero, nombreVisitante, documento, lote, autorizadaPor, fechaDesde, horaDesde, horaHasta);
        if (fechaHasta == null || fechaHasta.isBefore(fechaDesde)) {
            throw new IllegalArgumentException("La fecha final no puede ser anterior a la inicial.");
        }
        this.empresa = empresa;
        this.fechaHasta = fechaHasta;
    }

    public static long calcularDias(LocalDate desde, LocalDate hasta) {
        return ChronoUnit.DAYS.between(desde, hasta) + 1;
    }

    @Override
    public boolean esVigente(LocalDateTime fechaHora) {
        LocalDate dia = fechaHora.toLocalDate();
        boolean dentroDelPeriodo = !dia.isBefore(getFecha()) && !dia.isAfter(fechaHasta);
        return dentroDelPeriodo && estaEnFranja(fechaHora.toLocalTime());
    }

    @Override
    public String describirVigencia() {
        return "del " + getFecha() + " al " + fechaHasta + ", de " + getHoraDesde() + " a " + getHoraHasta()
                + " (proveedor " + empresa + ")";
    }

    public String getEmpresa() { return empresa; }
    public LocalDate getFechaHasta() { return fechaHasta; }
}
