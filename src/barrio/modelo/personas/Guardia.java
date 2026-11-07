package barrio.modelo.personas;

import java.time.LocalDate;

public class Guardia extends Personal {
    private static final long serialVersionUID = 1L;

    private String turno;
    private PuestoGuardia puesto;

    public Guardia(int legajo, String nombre, LocalDate fechaIngreso, String turno, PuestoGuardia puesto) {
        super(legajo, nombre, fechaIngreso);
        this.turno = turno;
        this.puesto = puesto;
    }

    @Override
    public String obtenerResponsabilidad() {
        return "Guardia de seguridad - turno " + turno + " - puesto " + puesto.name().toLowerCase();
    }

    public String getTurno() { return turno; }
    public PuestoGuardia getPuesto() { return puesto; }
}
