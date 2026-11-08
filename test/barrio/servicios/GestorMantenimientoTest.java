package barrio.servicios;

import barrio.Escenario;
import barrio.excepciones.OperacionInvalidaException;
import barrio.excepciones.OrdenIncompletaException;
import barrio.modelo.equipos.Camara;
import barrio.modelo.equipos.EstadoEquipo;
import barrio.modelo.equipos.Luminaria;
import barrio.modelo.equipos.OrdenMantenimiento;
import barrio.modelo.equipos.PrioridadOrden;
import barrio.modelo.equipos.Equipo;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class GestorMantenimientoTest {
    private Escenario e;
    private final LocalDateTime t0 = Escenario.AHORA;

    @BeforeEach
    void preparar() {
        e = new Escenario();
        e.repo.agregarEquipo(new Camara("CAM-01", "Acceso", LocalDate.of(2024, 1, 1), "1080p", 30));
        for (int i = 1; i <= 5; i++) {
            e.repo.agregarEquipo(new Luminaria("LUM-0" + i, "Calle 1", LocalDate.of(2024, 1, 1), "LED", 100));
        }
        e.repo.agregarEquipo(new Luminaria("LUM-09", "Calle 9", LocalDate.of(2024, 1, 1), "LED", 100));
    }

    @Test
    void fallaEnCamaraGeneraOrdenDePrioridadAlta() throws Exception {
        e.mantenimiento.registrarFalla("CAM-01", "Sin imagen", "Guardia", t0);
        OrdenMantenimiento orden = e.mantenimiento.generarOrden("CAM-01", e.electricista, "Revisar", t0);
        assertEquals(PrioridadOrden.ALTA, orden.getPrioridad());
        assertEquals(EstadoEquipo.EN_REPARACION, orden.getEquipo().getEstado());
    }

    @Test
    void luminariaEsNormalHastaQueFallanMasDe3EnElSector() throws Exception {
        for (int i = 1; i <= 3; i++) {
            e.mantenimiento.registrarFalla("LUM-0" + i, "No enciende", "Vecino", t0);
        }
        assertEquals(PrioridadOrden.NORMAL, e.mantenimiento.generarOrden("LUM-01", e.electricista, "x", t0).getPrioridad());
        e.mantenimiento.registrarFalla("LUM-04", "No enciende", "Vecino", t0);
        assertEquals(PrioridadOrden.ALTA, e.mantenimiento.generarOrden("LUM-04", e.electricista, "x", t0).getPrioridad());

        e.mantenimiento.registrarFalla("LUM-09", "No enciende", "Vecino", t0);
        assertEquals(PrioridadOrden.NORMAL, e.mantenimiento.generarOrden("LUM-09", e.electricista, "x", t0).getPrioridad());
    }

    @Test
    void noSeGeneraOrdenParaUnEquipoOperativo() {
        try {
            e.mantenimiento.generarOrden("CAM-01", e.electricista, "x", t0);
            fail("Tenia que lanzar OperacionInvalidaException");
        } catch (OperacionInvalidaException ex) {
            assertNotNull(ex.getMessage());
        }
        try {
            e.mantenimiento.registrarFalla("NO-EXISTE", "x", "y", t0);
            fail("Tenia que lanzar OperacionInvalidaException");
        } catch (OperacionInvalidaException ex) {
            assertNotNull(ex.getMessage());
        }
    }

    @Test
    void laOrdenNoSeCierraSinDatosCompletos() throws Exception {
        e.mantenimiento.registrarFalla("CAM-01", "Sin imagen", "Guardia", t0);
        OrdenMantenimiento orden = e.mantenimiento.generarOrden("CAM-01", e.electricista, "Revisar", t0);
        try {
            e.mantenimiento.cerrarOrden(orden.getNumero(), t0.plusHours(2), e.electricista, " ");
            fail("Tenia que lanzar OrdenIncompletaException");
        } catch (OrdenIncompletaException ex) {
            assertNotNull(ex.getMessage());
        }
        try {
            e.mantenimiento.cerrarOrden(orden.getNumero(), t0.plusHours(2), null, "Cambio de fuente");
            fail("Tenia que lanzar OrdenIncompletaException");
        } catch (OrdenIncompletaException ex) {
            assertNotNull(ex.getMessage());
        }
        assertTrue(orden.estaAbierta());

        e.mantenimiento.cerrarOrden(orden.getNumero(), t0.plusHours(2), e.electricista, "Cambio de fuente");
        assertFalse(orden.estaAbierta());
        assertEquals(EstadoEquipo.OPERATIVO, orden.getEquipo().getEstado());
        assertEquals(1, orden.getEquipo().getOrdenes().size());
    }

    @Test
    void avisaUnaVezCuandoUnaCamaraLlevaMasDe48Horas() throws Exception {
        e.mantenimiento.registrarFalla("CAM-01", "Sin imagen", "Guardia", t0);
        assertTrue(e.mantenimiento.verificarCamaras(t0.plusHours(48)).isEmpty());
        assertEquals(1, e.mantenimiento.verificarCamaras(t0.plusHours(49)).size());
        assertTrue(e.mantenimiento.verificarCamaras(t0.plusHours(60)).isEmpty());
        assertEquals(1, e.repo.getNotificaciones().size());
    }

    @Test
    void reporteDeEquiposNoOperativosOrdenadoPorSector() throws Exception {
        e.mantenimiento.registrarFalla("LUM-02", "x", "y", t0);
        e.mantenimiento.marcarFueraDeServicio("CAM-01", t0);
        List<Equipo> reporte = e.reportes.reportarEquipos();
        assertEquals(2, reporte.size());
        assertEquals("CAM-01", reporte.get(0).getCodigo());
    }
}
