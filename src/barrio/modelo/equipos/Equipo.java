package barrio.modelo.equipos;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Equipo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String codigo;
    private String sector;
    private LocalDate fechaInstalacion;
    private EstadoEquipo estado;
    private LocalDateTime inicioFalla;
    private boolean informadoAdministrador;
    private List<Falla> fallas;
    private List<OrdenMantenimiento> ordenes;

    protected Equipo(String codigo, String sector, LocalDate fechaInstalacion) {
        if (codigo == null || codigo.trim().isEmpty() || sector == null || sector.trim().isEmpty()) {
            throw new IllegalArgumentException("Codigo y sector son obligatorios.");
        }
        this.codigo = codigo.trim().toUpperCase();
        this.sector = sector.trim();
        this.fechaInstalacion = fechaInstalacion;
        this.estado = EstadoEquipo.OPERATIVO;
        this.fallas = new ArrayList<>();
        this.ordenes = new ArrayList<>();
    }

    public abstract PrioridadOrden calcularPrioridadFalla(List<Equipo> equiposDelBarrio);

    public abstract String getTipo();

    public void registrarFalla(Falla falla) {
        fallas.add(falla);
        if (estado == EstadoEquipo.OPERATIVO) {
            estado = EstadoEquipo.CON_FALLA;
        }
        if (inicioFalla == null) {
            inicioFalla = falla.getFecha();
        }
    }

    public void agregarOrden(OrdenMantenimiento orden) {
        ordenes.add(orden);
        estado = EstadoEquipo.EN_REPARACION;
    }

    public void marcarReparado() {
        estado = EstadoEquipo.OPERATIVO;
        inicioFalla = null;
        informadoAdministrador = false;
    }

    public void marcarFueraDeServicio(LocalDateTime fecha) {
        estado = EstadoEquipo.FUERA_DE_SERVICIO;
        if (inicioFalla == null) {
            inicioFalla = fecha;
        }
    }

    public boolean tieneFallaActiva() {
        return estado == EstadoEquipo.CON_FALLA || estado == EstadoEquipo.EN_REPARACION;
    }

    public boolean estaFuncionando() {
        return estado == EstadoEquipo.OPERATIVO;
    }

    public long horasSinFuncionar(LocalDateTime ahora) {
        if (estaFuncionando() || inicioFalla == null) {
            return 0;
        }
        return Duration.between(inicioFalla, ahora).toHours();
    }

    public void marcarInformadoAdministrador() {
        this.informadoAdministrador = true;
    }

    public String getCodigo() { return codigo; }
    public String getSector() { return sector; }
    public LocalDate getFechaInstalacion() { return fechaInstalacion; }
    public EstadoEquipo getEstado() { return estado; }
    public LocalDateTime getInicioFalla() { return inicioFalla; }
    public boolean isInformadoAdministrador() { return informadoAdministrador; }

    public List<Falla> getFallas() {
        return new ArrayList<>(fallas);
    }

    public List<OrdenMantenimiento> getOrdenes() {
        return new ArrayList<>(ordenes);
    }

    @Override
    public String toString() {
        return getTipo() + " " + codigo + " - sector " + sector + " - " + estado;
    }
}
