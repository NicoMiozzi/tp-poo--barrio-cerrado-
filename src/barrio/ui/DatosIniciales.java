package barrio.ui;

import barrio.excepciones.BarrioException;
import barrio.modelo.equipos.Camara;
import barrio.modelo.equipos.Luminaria;
import barrio.modelo.expensas.Expensa;
import barrio.modelo.expensas.MedioPago;
import barrio.modelo.lotes.Lote;
import barrio.modelo.personas.Administrador;
import barrio.modelo.personas.EmpleadoMantenimiento;
import barrio.modelo.personas.Especialidad;
import barrio.modelo.personas.Guardia;
import barrio.modelo.personas.PuestoGuardia;
import barrio.modelo.personas.Residente;
import barrio.modelo.personas.TipoResidente;
import barrio.modelo.personas.Titular;
import barrio.persistencia.RepositorioDatos;
import barrio.modelo.reclamos.TipoReclamo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;

public class DatosIniciales {
    public static final int CANTIDAD_LOTES = 150;
    public static final double PRESUPUESTO_MENSUAL = 15000000;

    private DatosIniciales() {
    }

    public static void cargar(SistemaBarrio sistema) throws BarrioException {
        RepositorioDatos repo = sistema.getRepositorio();
        LocalDateTime ahora = sistema.ahora();
        LocalDate hoy = ahora.toLocalDate();

        Titular[] titulares = {
                new Titular("Ana", "Gomez", "20111111", "1150001111", "ana.gomez@mail.com"),
                new Titular("Bruno", "Diaz", "20222222", "1150002222", "bruno.diaz@mail.com"),
                new Titular("Carla", "Perez", "20333333", "1150003333", "carla.perez@mail.com"),
                new Titular("Diego", "Lopez", "20444444", "1150004444", "diego.lopez@mail.com"),
                new Titular("Elena", "Ruiz", "20555555", "1150005555", "elena.ruiz@mail.com"),
                new Titular("Federico", "Sosa", "20666666", "1150006666", "fede.sosa@mail.com"),
                new Titular("Gabriela", "Torres", "20777777", "1150007777", "gaby.torres@mail.com"),
                new Titular("Hernan", "Vega", "20888888", "1150008888", "hernan.vega@mail.com"),
                new Titular("Ines", "Molina", "20999999", "1150009999", "ines.molina@mail.com"),
                new Titular("Julian", "Castro", "21000000", "1150000000", "julian.castro@mail.com")
        };
        for (int numero = 1; numero <= CANTIDAD_LOTES; numero++) {
            double superficie = 600 + (numero % 5) * 100;
            repo.agregarLote(new Lote(numero, superficie, titulares[(numero - 1) % titulares.length]));
        }

        Lote lote1 = repo.buscarLote(1);
        Residente hija = new Residente("Lucia", "Gomez", "40111111", "1160001111", "lucia@mail.com", TipoResidente.FAMILIAR);
        Residente inquilino = new Residente("Mario", "Rios", "35111111", "1160002222", "mario@mail.com", TipoResidente.INQUILINO);
        lote1.agregarResidente(hija);
        lote1.agregarResidente(inquilino);
        titulares[0].autorizarResidente(lote1, hija);

        repo.agregarPersonal(new Administrador(1, "Laura Fernandez", LocalDate.of(2020, 3, 1)));
        repo.agregarPersonal(new Guardia(10, "Ramon Acosta", LocalDate.of(2021, 5, 10), "manana", PuestoGuardia.PORTERIA));
        repo.agregarPersonal(new Guardia(11, "Sergio Benitez", LocalDate.of(2022, 8, 1), "noche", PuestoGuardia.MONITOREO));
        repo.agregarPersonal(new EmpleadoMantenimiento(20, "Tomas Herrera", LocalDate.of(2019, 2, 15), Especialidad.ELECTRICIDAD));
        repo.agregarPersonal(new EmpleadoMantenimiento(21, "Ulises Medina", LocalDate.of(2023, 1, 9), Especialidad.PLOMERIA));
        repo.agregarPersonal(new EmpleadoMantenimiento(22, "Valeria Ortiz", LocalDate.of(2024, 4, 22), Especialidad.JARDINERIA));

        repo.agregarEquipo(new Camara("CAM-01", "Acceso principal", LocalDate.of(2023, 1, 10), "1080p", 30));
        repo.agregarEquipo(new Camara("CAM-02", "Acceso de servicio", LocalDate.of(2023, 1, 10), "1080p", 30));
        repo.agregarEquipo(new Camara("CAM-03", "Espacios comunes", LocalDate.of(2024, 6, 1), "4K", 15));
        for (int i = 1; i <= 5; i++) {
            repo.agregarEquipo(new Luminaria("LUM-0" + i, "Calle 1", LocalDate.of(2022, 9, 1), "LED", 100));
        }
        repo.agregarEquipo(new Luminaria("LUM-06", "Calle 2", LocalDate.of(2022, 9, 1), "LED", 100));

        sistema.getMantenimiento().registrarFalla("CAM-02", "No transmite imagen", "Guardia Sergio Benitez", ahora.minusDays(3));

        for (int i = 1; i <= 3; i++) {
            sistema.getMantenimiento().registrarFalla("LUM-0" + i, "No enciende", "Vecino lote " + i, ahora.minusDays(1));
        }

        for (int mesesAtras = 4; mesesAtras >= 1; mesesAtras--) {
            YearMonth periodo = YearMonth.from(hoy).minusMonths(mesesAtras);
            sistema.getExpensas().emitirExpensas(periodo, PRESUPUESTO_MENSUAL);
            for (Lote lote : repo.getLotes()) {
                boolean debeTodo = lote.getNumero() == 7;
                boolean debeDos = lote.getNumero() == 12 && mesesAtras <= 2;
                if (!debeTodo && !debeDos) {
                    Expensa expensa = lote.buscarExpensa(periodo);
                    sistema.getExpensas().imputarPago(lote, expensa.getVencimiento(), expensa.getImporte(), MedioPago.TRANSFERENCIA);
                }
            }
        }
        sistema.getExpensas().actualizarMorosidad(hoy);

        sistema.getReclamos().presentarReclamo(lote1, titulares[0], TipoReclamo.SEGURIDAD,
                "Se vio una persona saltando el cerco del fondo", ahora.minusDays(2));
        sistema.getReclamos().presentarReclamo(repo.buscarLote(2), titulares[1], TipoReclamo.MANTENIMIENTO,
                "Perdida de agua en la vereda", ahora.minusHours(5));

        sistema.getAcceso().autorizarVisita(lote1, titulares[0], "Juan Visitante", "30111222", hoy,
                LocalTime.of(0, 0), LocalTime.of(23, 59));
        sistema.getAcceso().autorizarProveedor(lote1, titulares[0], "Pedro Jardines", "28999000", "Jardineria Verde",
                hoy, hoy.plusDays(29), LocalTime.of(8, 0), LocalTime.of(18, 0));
    }
}
