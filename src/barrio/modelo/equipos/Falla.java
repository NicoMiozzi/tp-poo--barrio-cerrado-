package barrio.modelo.equipos;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Falla implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDateTime fecha;
    private String descripcion;
    private String reportadaPor;

    public Falla(LocalDateTime fecha, String descripcion, String reportadaPor) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La falla necesita una descripcion.");
        }
        this.fecha = fecha;
        this.descripcion = descripcion.trim();
        this.reportadaPor = reportadaPor;
    }

    public LocalDateTime getFecha() { return fecha; }
    public String getDescripcion() { return descripcion; }
    public String getReportadaPor() { return reportadaPor; }

    @Override
    public String toString() {
        return fecha + " - " + descripcion + " (reporto: " + reportadaPor + ")";
    }
}
