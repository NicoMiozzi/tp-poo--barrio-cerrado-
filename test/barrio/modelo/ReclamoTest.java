package barrio.modelo;

import barrio.Escenario;
import barrio.excepciones.TransicionInvalidaException;
import barrio.modelo.reclamos.EstadoReclamo;
import barrio.modelo.reclamos.Prioridad;
import barrio.modelo.reclamos.Reclamo;
import barrio.modelo.reclamos.TipoReclamo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReclamoTest {
    private Escenario e;
    private Reclamo reclamo;
    private final LocalDateTime t0 = Escenario.AHORA;

    @BeforeEach
    void preparar() {
        e = new Escenario();
        reclamo = new Reclamo(1, TipoReclamo.MANTENIMIENTO, "Perdida de agua", e.lote1, e.titular, t0);
    }

    @Test
    void laPrioridadSaleDelTipo() {
        assertEquals(Prioridad.ALTA, new Reclamo(2, TipoReclamo.SEGURIDAD, "x", e.lote1, e.titular, t0).getPrioridad());
        assertEquals(Prioridad.MEDIA, reclamo.getPrioridad());
        assertEquals(Prioridad.BAJA, new Reclamo(3, TipoReclamo.CONVIVENCIA, "x", e.lote1, e.titular, t0).getPrioridad());
        assertEquals(Prioridad.BAJA, new Reclamo(4, TipoReclamo.ADMINISTRATIVO, "x", e.lote1, e.titular, t0).getPrioridad());
    }

    @Test
    void recorreLosEstadosEnOrdenYGuardaElHistorial() throws Exception {
        reclamo.asignar(e.electricista, t0.plusHours(1));
        reclamo.cambiarEstado(EstadoReclamo.EN_CURSO, t0.plusHours(2), "Arranca");
        reclamo.cambiarEstado(EstadoReclamo.RESUELTO, t0.plusHours(3), "Listo");
        assertEquals(EstadoReclamo.RESUELTO, reclamo.getEstado());
        assertEquals(4, reclamo.getHistorial().size());
        assertEquals(e.electricista, reclamo.getResponsable());
    }

    @Test
    void noSePuedeSaltearUnEstado() {
        try {
            reclamo.cambiarEstado(EstadoReclamo.EN_CURSO, t0, "salteo asignado");
            fail("Tenia que lanzar TransicionInvalidaException");
        } catch (TransicionInvalidaException ex) {
            assertNotNull(ex.getMessage());
        }
        try {
            reclamo.cambiarEstado(EstadoReclamo.RESUELTO, t0, "salteo");
            fail("Tenia que lanzar TransicionInvalidaException");
        } catch (TransicionInvalidaException ex) {
            assertNotNull(ex.getMessage());
        }
        assertEquals(EstadoReclamo.ABIERTO, reclamo.getEstado());
    }

    @Test
    void noSePuedeAsignarDosVeces() throws Exception {
        reclamo.asignar(e.electricista, t0);
        try {
            reclamo.asignar(e.electricista, t0);
            fail("Tenia que lanzar TransicionInvalidaException");
        } catch (TransicionInvalidaException ex) {
            assertNotNull(ex.getMessage());
        }
    }

    @Test
    void seReabreDentroDeLos5Dias() throws Exception {
        resolver();
        reclamo.reabrir(t0.plusDays(4), "Sigue perdiendo");
        assertEquals(EstadoReclamo.ASIGNADO, reclamo.getEstado());
    }

    @Test
    void noSeReabrePasados5Dias() throws Exception {
        resolver();
        try {
            reclamo.reabrir(t0.plusDays(6), "tarde");
            fail("Tenia que lanzar TransicionInvalidaException");
        } catch (TransicionInvalidaException ex) {
            assertNotNull(ex.getMessage());
        }
    }

    @Test
    void seCierraSoloPasadoElPlazo() throws Exception {
        resolver();
        try {
            reclamo.cerrar(t0.plusDays(2));
            fail("Tenia que lanzar TransicionInvalidaException");
        } catch (TransicionInvalidaException ex) {
            assertNotNull(ex.getMessage());
        }
        reclamo.cerrar(t0.plusDays(6));
        assertEquals(EstadoReclamo.CERRADO, reclamo.getEstado());
    }

    @Test
    void escalaReclamoDeAltaSinAtenderEn24Horas() {
        Reclamo seguridad = new Reclamo(2, TipoReclamo.SEGURIDAD, "Intruso", e.lote1, e.titular, t0);
        assertFalse(seguridad.requiereEscalamiento(t0.plusHours(23)));
        assertTrue(seguridad.requiereEscalamiento(t0.plusHours(24)));
        assertFalse(reclamo.requiereEscalamiento(t0.plusDays(3)));
    }

    @Test
    void ordenNaturalPorPrioridadYFecha() {
        Reclamo bajo = new Reclamo(2, TipoReclamo.CONVIVENCIA, "x", e.lote1, e.titular, t0.minusDays(1));
        Reclamo alto = new Reclamo(3, TipoReclamo.SEGURIDAD, "x", e.lote1, e.titular, t0.plusDays(1));
        List<Reclamo> lista = new ArrayList<>();
        lista.add(bajo);
        lista.add(reclamo);
        lista.add(alto);
        Collections.sort(lista);
        assertSame(alto, lista.get(0));
        assertSame(reclamo, lista.get(1));
        assertSame(bajo, lista.get(2));
    }

    private void resolver() throws Exception {
        reclamo.asignar(e.electricista, t0);
        reclamo.cambiarEstado(EstadoReclamo.EN_CURSO, t0, "a");
        reclamo.cambiarEstado(EstadoReclamo.RESUELTO, t0, "b");
    }
}
