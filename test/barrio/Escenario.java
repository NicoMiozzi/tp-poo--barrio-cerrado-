package barrio;

import barrio.controladores.ControladorAcceso;
import barrio.controladores.ReclamoController;
import barrio.modelo.lotes.Lote;
import barrio.modelo.personas.EmpleadoMantenimiento;
import barrio.modelo.personas.Especialidad;
import barrio.modelo.personas.Residente;
import barrio.modelo.personas.TipoResidente;
import barrio.modelo.personas.Titular;
import barrio.persistencia.RepositorioDatos;
import barrio.servicios.GestorMantenimiento;
import barrio.servicios.GestorReclamos;
import barrio.servicios.NotificadorAdministrador;
import barrio.servicios.ServicioExpensas;
import barrio.servicios.ServicioReportes;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Escenario {
    public static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 4, 10, 0);

    public final RepositorioDatos repo = new RepositorioDatos();
    public final NotificadorAdministrador notificador = new NotificadorAdministrador(repo, false);
    public final GestorReclamos gestorReclamos = new GestorReclamos(repo, notificador);
    public final ReclamoController reclamos = new ReclamoController(repo, gestorReclamos);
    public final ServicioExpensas expensas = new ServicioExpensas(repo, notificador);
    public final GestorMantenimiento mantenimiento = new GestorMantenimiento(repo, notificador);
    public final ControladorAcceso acceso = new ControladorAcceso(repo);
    public final ServicioReportes reportes = new ServicioReportes(repo);

    public final Titular titular = new Titular("Ana", "Gomez", "20111111", "11", "ana@mail.com");
    public final Titular otroTitular = new Titular("Bruno", "Diaz", "20222222", "11", "bruno@mail.com");
    public final Residente familiar = new Residente("Lucia", "Gomez", "40111111", "11", "l@mail.com", TipoResidente.FAMILIAR);
    public final Residente inquilino = new Residente("Mario", "Rios", "35111111", "11", "m@mail.com", TipoResidente.INQUILINO);
    public final Lote lote1;
    public final Lote lote2;
    public final EmpleadoMantenimiento electricista =
            new EmpleadoMantenimiento(20, "Tomas", LocalDate.of(2020, 1, 1), Especialidad.ELECTRICIDAD);

    public Escenario() {
        lote1 = new Lote(1, 1000, titular);
        lote2 = new Lote(2, 3000, otroTitular);
        lote1.agregarResidente(familiar);
        lote1.agregarResidente(inquilino);
        repo.agregarLote(lote1);
        repo.agregarLote(lote2);
        repo.agregarPersonal(electricista);
    }
}
