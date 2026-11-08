package barrio.modelo.personas;

import java.time.LocalDate;

public class Administrador extends Personal {
    private static final long serialVersionUID = 1L;

    public Administrador(int legajo, String nombre, LocalDate fechaIngreso) {
        super(legajo, nombre, fechaIngreso);
    }

    @Override
    public String obtenerResponsabilidad() {
        return "Administrador del barrio";
    }
}
