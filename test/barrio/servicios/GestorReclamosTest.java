package barrio.servicios;

import barrio.Escenario;
import barrio.excepciones.LimiteReclamosException;
import barrio.excepciones.PersonaNoAutorizadaException;
import barrio.modelo.reclamos.EstadoReclamo;
import barrio.modelo.reclamos.Reclamo;
import barrio.modelo.reclamos.TipoReclamo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class GestorReclamosTest {
    private final Escenario e = new Escenario();
    private final LocalDateTime t0 = Escenario.AHORA;

    @Test
    void presentarReclamoComoTitular() throws Exception {
        Reclamo reclamo = e.reclamos.presentarReclamo(e.lote1, e.titular, TipoReclamo.SEGURIDAD, "Intruso", t0);
        assertEquals(1, reclamo.getNumero());
        assertEquals(EstadoReclamo.ABIERTO, reclamo.getEstado());
        assertSame(reclamo, e.repo.buscarReclamo(1));
    }

    @Test
    void rechazaReclamoDePersonaNoAutorizada() {
        try {
            e.reclamos.presentarReclamo(e.lote1, e.inquilino, TipoReclamo.CONVIVENCIA, "Ruidos", t0);
            fail("Tenia que lanzar PersonaNoAutorizadaException");
        } catch (PersonaNoAutorizadaException ex) {
            assertNotNull(ex.getMessage());
        }
        try {
            e.reclamos.presentarReclamo(e.lote1, null, TipoReclamo.CONVIVENCIA, "Ruidos", t0);
            fail("Tenia que lanzar PersonaNoAutorizadaException");
        } catch (PersonaNoAutorizadaException ex) {
            assertNotNull(ex.getMessage());
        }
        assertTrue(e.repo.getReclamos().isEmpty());
    }

    @Test
    void ningunEmpleadoTieneMasDe5ReclamosEnCurso() throws Exception {
        for (int i = 1; i <= 6; i++) {
            e.reclamos.presentarReclamo(e.lote1, e.titular, TipoReclamo.MANTENIMIENTO, "Arreglo " + i, t0);
            e.reclamos.asignarReclamo(i, e.electricista, t0);
        }
        for (int i = 1; i <= 5; i++) {
            e.reclamos.cambiarEstado(i, EstadoReclamo.EN_CURSO, t0, "trabajando");
        }
        try {
            e.reclamos.cambiarEstado(6, EstadoReclamo.EN_CURSO, t0, "uno mas");
            fail("Tenia que lanzar LimiteReclamosException");
        } catch (LimiteReclamosException ex) {
            assertNotNull(ex.getMessage());
        }
        assertEquals(5, e.gestorReclamos.contarEnCurso(e.electricista));

        e.reclamos.cambiarEstado(1, EstadoReclamo.RESUELTO, t0, "listo");
        e.reclamos.cambiarEstado(6, EstadoReclamo.EN_CURSO, t0, "ahora si");
    }

    @Test
    void escalaUnaSolaVezYNotificaAlAdministrador() throws Exception {
        e.reclamos.presentarReclamo(e.lote1, e.titular, TipoReclamo.SEGURIDAD, "Intruso", t0);
        assertEquals(1, e.gestorReclamos.escalarReclamos(t0.plusHours(25)).size());
        assertEquals(0, e.gestorReclamos.escalarReclamos(t0.plusHours(30)).size());
        assertEquals(1, e.repo.getNotificaciones().size());
        assertEquals("Administrador", e.repo.getNotificaciones().get(0).getDestinatario());
    }

    @Test
    void cierraLosResueltosQueNoSeReabrieron() throws Exception {
        e.reclamos.presentarReclamo(e.lote1, e.titular, TipoReclamo.MANTENIMIENTO, "Luz", t0);
        e.reclamos.asignarReclamo(1, e.electricista, t0);
        e.reclamos.cambiarEstado(1, EstadoReclamo.EN_CURSO, t0, "a");
        e.reclamos.cambiarEstado(1, EstadoReclamo.RESUELTO, t0, "b");
        assertEquals(0, e.gestorReclamos.cerrarReclamosVencidos(t0.plusDays(3)));
        assertEquals(1, e.gestorReclamos.cerrarReclamosVencidos(t0.plusDays(6)));
        assertEquals(EstadoReclamo.CERRADO, e.repo.buscarReclamo(1).getEstado());
        assertTrue(e.reportes.reportarReclamos().isEmpty());
    }
}
