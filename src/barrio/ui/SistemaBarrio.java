package barrio.ui;

import barrio.controladores.ControladorAcceso;
import barrio.controladores.ReclamoController;
import barrio.persistencia.RepositorioDatos;
import barrio.servicios.GestorMantenimiento;
import barrio.servicios.GestorReclamos;
import barrio.servicios.Notificador;
import barrio.servicios.NotificadorAdministrador;
import barrio.servicios.ServicioExpensas;
import barrio.servicios.ServicioReportes;

import java.io.IOException;
import java.time.LocalDateTime;

public class SistemaBarrio {
    private final RepositorioDatos repositorio;
    private final ReclamoController reclamos;
    private final GestorReclamos gestorReclamos;
    private final ServicioExpensas expensas;
    private final GestorMantenimiento mantenimiento;
    private final ControladorAcceso acceso;
    private final ServicioReportes reportes;
    private LocalDateTime fechaSimulada;

    public SistemaBarrio(RepositorioDatos repositorio) {
        this.repositorio = repositorio;
        Notificador notificador = new NotificadorAdministrador(repositorio, true);
        this.gestorReclamos = new GestorReclamos(repositorio, notificador);
        this.reclamos = new ReclamoController(repositorio, gestorReclamos);
        this.expensas = new ServicioExpensas(repositorio, notificador);
        this.mantenimiento = new GestorMantenimiento(repositorio, notificador);
        this.acceso = new ControladorAcceso(repositorio);
        this.reportes = new ServicioReportes(repositorio);
    }

    public LocalDateTime ahora() {
        return fechaSimulada != null ? fechaSimulada : LocalDateTime.now().withSecond(0).withNano(0);
    }

    public void setFechaSimulada(LocalDateTime fecha) {
        this.fechaSimulada = fecha;
    }

    public boolean usaFechaSimulada() {
        return fechaSimulada != null;
    }

    public void guardarCambios() {
        try {
            repositorio.guardarEnArchivo();
        } catch (IOException e) {
            System.out.println("ATENCION: no se pudieron guardar los datos (" + e.getMessage() + ").");
        }
    }

    public RepositorioDatos getRepositorio() { return repositorio; }
    public ReclamoController getReclamos() { return reclamos; }
    public GestorReclamos getGestorReclamos() { return gestorReclamos; }
    public ServicioExpensas getExpensas() { return expensas; }
    public GestorMantenimiento getMantenimiento() { return mantenimiento; }
    public ControladorAcceso getAcceso() { return acceso; }
    public ServicioReportes getReportes() { return reportes; }
}
