package barrio.modelo.personas;

import java.io.Serializable;
import java.time.LocalDate;

public abstract class Personal implements Serializable, Comparable<Personal> {
    private static final long serialVersionUID = 1L;

    private int legajo;
    private String nombre;
    private LocalDate fechaIngreso;

    protected Personal(int legajo, String nombre, LocalDate fechaIngreso) {
        if (legajo <= 0) {
            throw new IllegalArgumentException("El legajo debe ser positivo.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        this.legajo = legajo;
        this.nombre = nombre.trim();
        this.fechaIngreso = fechaIngreso;
    }

    public abstract String obtenerResponsabilidad();

    public int getLegajo() { return legajo; }
    public String getNombre() { return nombre; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }

    @Override
    public int compareTo(Personal otro) {
        return Integer.compare(legajo, otro.legajo);
    }

    @Override
    public String toString() {
        return "Legajo " + legajo + " - " + nombre + " - " + obtenerResponsabilidad();
    }
}
