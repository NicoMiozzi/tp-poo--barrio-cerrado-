package barrio.modelo.personas;

import java.time.LocalDate;

public class EmpleadoMantenimiento extends Personal {
    private static final long serialVersionUID = 1L;

    private Especialidad especialidad;

    public EmpleadoMantenimiento(int legajo, String nombre, LocalDate fechaIngreso, Especialidad especialidad) {
        super(legajo, nombre, fechaIngreso);
        this.especialidad = especialidad;
    }

    @Override
    public String obtenerResponsabilidad() {
        return "Mantenimiento - " + especialidad.name().toLowerCase();
    }

    public Especialidad getEspecialidad() { return especialidad; }
}
