package barrio.modelo.equipos;

import barrio.excepciones.OperacionInvalidaException;
import barrio.excepciones.OrdenIncompletaException;
import barrio.modelo.personas.EmpleadoMantenimiento;

import java.io.Serializable;
import java.time.LocalDateTime;

public class OrdenMantenimiento implements Serializable {
    private static final long serialVersionUID = 1L;

    private int numero;
    private Equipo equipo;
    private EmpleadoMantenimiento tecnico;
    private LocalDateTime fechaApertura;
    private PrioridadOrden prioridad;
    private String descripcionTrabajo;
    private LocalDateTime fechaCierre;
    private String reparacion;

    public OrdenMantenimiento(int numero, Equipo equipo, EmpleadoMantenimiento tecnico, LocalDateTime fechaApertura,
                              PrioridadOrden prioridad, String descripcionTrabajo) {
        if (equipo == null || tecnico == null || fechaApertura == null || prioridad == null) {
            throw new IllegalArgumentException("Equipo, tecnico, fecha y prioridad son obligatorios.");
        }
        this.numero = numero;
        this.equipo = equipo;
        this.tecnico = tecnico;
        this.fechaApertura = fechaApertura;
        this.prioridad = prioridad;
        this.descripcionTrabajo = descripcionTrabajo;
    }

    public void cerrarOrden(LocalDateTime fecha, EmpleadoMantenimiento tecnicoQueReparo, String descripcionReparacion)
            throws OrdenIncompletaException, OperacionInvalidaException {
        if (!estaAbierta()) {
            throw new OperacionInvalidaException("La orden " + numero + " ya esta cerrada.");
        }
        if (fecha == null || tecnicoQueReparo == null
                || descripcionReparacion == null || descripcionReparacion.trim().isEmpty()) {
            throw new OrdenIncompletaException("Para cerrar la orden hay que registrar fecha, tecnico y "
                    + "descripcion de la reparacion.");
        }
        if (fecha.isBefore(fechaApertura)) {
            throw new OrdenIncompletaException("La fecha de cierre no puede ser anterior a la de apertura.");
        }
        this.fechaCierre = fecha;
        this.tecnico = tecnicoQueReparo;
        this.reparacion = descripcionReparacion.trim();
        equipo.marcarReparado();
    }

    public boolean estaAbierta() {
        return fechaCierre == null;
    }

    public int getNumero() { return numero; }
    public Equipo getEquipo() { return equipo; }
    public EmpleadoMantenimiento getTecnico() { return tecnico; }
    public LocalDateTime getFechaApertura() { return fechaApertura; }
    public PrioridadOrden getPrioridad() { return prioridad; }
    public String getDescripcionTrabajo() { return descripcionTrabajo; }
    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public String getReparacion() { return reparacion; }

    @Override
    public String toString() {
        String texto = "Orden " + numero + " [" + prioridad + "] " + equipo.getCodigo() + " - tecnico "
                + tecnico.getNombre() + " - abierta " + fechaApertura + " - " + descripcionTrabajo;
        if (!estaAbierta()) {
            texto += " - CERRADA " + fechaCierre + ": " + reparacion;
        }
        return texto;
    }
}
