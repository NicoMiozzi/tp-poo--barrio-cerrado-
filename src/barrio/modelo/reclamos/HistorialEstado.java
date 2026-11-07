package barrio.modelo.reclamos;

import java.io.Serializable;
import java.time.LocalDateTime;

public class HistorialEstado implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDateTime fecha;
    private EstadoReclamo estado;
    private String observacion;

    public HistorialEstado(LocalDateTime fecha, EstadoReclamo estado, String observacion) {
        this.fecha = fecha;
        this.estado = estado;
        this.observacion = observacion;
    }

    public LocalDateTime getFecha() { return fecha; }
    public EstadoReclamo getEstado() { return estado; }
    public String getObservacion() { return observacion; }

    @Override
    public String toString() {
        return fecha + " - " + estado + " - " + observacion;
    }
}
