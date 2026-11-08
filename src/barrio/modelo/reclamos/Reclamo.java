package barrio.modelo.reclamos;

import barrio.excepciones.TransicionInvalidaException;
import barrio.modelo.lotes.Lote;
import barrio.modelo.personas.Persona;
import barrio.modelo.personas.Personal;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Reclamo implements Serializable, Comparable<Reclamo> {
    private static final long serialVersionUID = 1L;

    public static final int DIAS_PARA_REABRIR = 5;

    public static final int HORAS_PARA_ESCALAR = 24;

    private int numero;
    private LocalDateTime fecha;
    private TipoReclamo tipo;
    private String descripcion;
    private Prioridad prioridad;
    private EstadoReclamo estado;
    private Lote lote;
    private Persona presentadoPor;
    private Personal responsable;
    private LocalDateTime fechaResuelto;
    private boolean escalado;
    private List<HistorialEstado> historial;
    private List<Comentario> comentarios;

    public Reclamo(int numero, TipoReclamo tipo, String descripcion, Lote lote, Persona presentadoPor,
                   LocalDateTime fecha) {
        if (tipo == null || lote == null || presentadoPor == null || fecha == null) {
            throw new IllegalArgumentException("Tipo, lote, persona y fecha son obligatorios.");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripcion es obligatoria.");
        }
        this.numero = numero;
        this.tipo = tipo;
        this.descripcion = descripcion.trim();
        this.lote = lote;
        this.presentadoPor = presentadoPor;
        this.fecha = fecha;
        this.estado = EstadoReclamo.ABIERTO;
        this.escalado = false;
        this.historial = new ArrayList<>();
        this.comentarios = new ArrayList<>();
        asignarPrioridad();
        historial.add(new HistorialEstado(fecha, estado, "Reclamo presentado"));
    }

    private void asignarPrioridad() {
        this.prioridad = tipo.getPrioridad();
    }

    public void asignar(Personal empleado, LocalDateTime fechaCambio) throws TransicionInvalidaException {
        if (empleado == null) {
            throw new IllegalArgumentException("Hay que indicar el empleado responsable.");
        }
        if (estado != EstadoReclamo.ABIERTO) {
            throw new TransicionInvalidaException("El reclamo " + numero + " ya fue asignado (estado " + estado + ").");
        }
        this.responsable = empleado;
        registrarCambio(EstadoReclamo.ASIGNADO, fechaCambio, "Asignado a " + empleado.getNombre());
    }

    public void cambiarEstado(EstadoReclamo nuevo, LocalDateTime fechaCambio, String observacion)
            throws TransicionInvalidaException {
        if (nuevo == EstadoReclamo.ASIGNADO) {
            throw new TransicionInvalidaException("Para pasar a ASIGNADO hay que asignar un responsable.");
        }
        // el cierre no es manual: lo hace cerrar() cuando pasan 5 dias sin reapertura
        if (nuevo == EstadoReclamo.CERRADO) {
            throw new TransicionInvalidaException("El reclamo se cierra solo cuando pasan "
                    + DIAS_PARA_REABRIR + " dias desde que se resolvio.");
        }
        if (nuevo != estado.siguiente()) {
            throw new TransicionInvalidaException("No se puede pasar de " + estado + " a " + nuevo
                    + ". El siguiente estado es " + estado.siguiente() + ".");
        }
        if (nuevo == EstadoReclamo.RESUELTO) {
            fechaResuelto = fechaCambio;
        }
        registrarCambio(nuevo, fechaCambio, observacion);
    }

    public void reabrir(LocalDateTime fechaCambio, String observacion) throws TransicionInvalidaException {
        if (estado != EstadoReclamo.RESUELTO) {
            throw new TransicionInvalidaException("Solo se puede reabrir un reclamo resuelto.");
        }
        if (vencioPlazoReapertura(fechaCambio)) {
            throw new TransicionInvalidaException("Pasaron mas de " + DIAS_PARA_REABRIR
                    + " dias desde que se resolvio: ya no se puede reabrir.");
        }
        fechaResuelto = null;
        registrarCambio(EstadoReclamo.ASIGNADO, fechaCambio, "Reabierto: " + observacion);
    }

    public void cerrar(LocalDateTime fechaCambio) throws TransicionInvalidaException {
        if (estado != EstadoReclamo.RESUELTO) {
            throw new TransicionInvalidaException("Solo se cierra un reclamo resuelto.");
        }
        if (!vencioPlazoReapertura(fechaCambio)) {
            throw new TransicionInvalidaException("Todavia esta corriendo el plazo de reapertura.");
        }
        registrarCambio(EstadoReclamo.CERRADO, fechaCambio, "Cerrado automaticamente: no fue reabierto en "
                + DIAS_PARA_REABRIR + " dias");
    }

    public boolean vencioPlazoReapertura(LocalDateTime ahora) {
        return fechaResuelto != null && ahora.isAfter(fechaResuelto.plusDays(DIAS_PARA_REABRIR));
    }

    public boolean requiereEscalamiento(LocalDateTime ahora) {
        boolean sinAtender = estado == EstadoReclamo.ABIERTO || estado == EstadoReclamo.ASIGNADO;
        long horas = Duration.between(fecha, ahora).toHours();
        return prioridad == Prioridad.ALTA && sinAtender && !escalado && horas >= HORAS_PARA_ESCALAR;
    }

    public void marcarEscalado() {
        this.escalado = true;
    }

    public void agregarComentario(LocalDateTime fechaComentario, String autor, String texto) {
        comentarios.add(new Comentario(fechaComentario, autor, texto));
    }

    public boolean estaPendiente() {
        return estado != EstadoReclamo.CERRADO;
    }

    private void registrarCambio(EstadoReclamo nuevo, LocalDateTime fechaCambio, String observacion) {
        this.estado = nuevo;
        historial.add(new HistorialEstado(fechaCambio, nuevo, observacion));
    }

    public int getNumero() { return numero; }
    public LocalDateTime getFecha() { return fecha; }
    public TipoReclamo getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public Prioridad getPrioridad() { return prioridad; }
    public EstadoReclamo getEstado() { return estado; }
    public Lote getLote() { return lote; }
    public Persona getPresentadoPor() { return presentadoPor; }
    public Personal getResponsable() { return responsable; }
    public boolean isEscalado() { return escalado; }

    public List<HistorialEstado> getHistorial() {
        return new ArrayList<>(historial);
    }

    public List<Comentario> getComentarios() {
        return new ArrayList<>(comentarios);
    }

    @Override
    public int compareTo(Reclamo otro) {
        int porPrioridad = prioridad.compareTo(otro.prioridad);
        if (porPrioridad != 0) {
            return porPrioridad;
        }
        int porFecha = fecha.compareTo(otro.fecha);
        return porFecha != 0 ? porFecha : Integer.compare(numero, otro.numero);
    }

    @Override
    public String toString() {
        return "Reclamo " + numero + " [" + prioridad + "] " + tipo + " - lote " + lote.getNumero() + " - "
                + estado + (responsable != null ? " - resp. " + responsable.getNombre() : "")
                + (escalado ? " - ESCALADO" : "") + " - " + descripcion;
    }
}
