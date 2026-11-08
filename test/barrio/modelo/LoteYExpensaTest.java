package barrio.modelo;

import barrio.Escenario;
import barrio.excepciones.OperacionInvalidaException;
import barrio.excepciones.PersonaNoAutorizadaException;
import barrio.modelo.expensas.EstadoExpensa;
import barrio.modelo.expensas.Expensa;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

class LoteYExpensaTest {
    private final Escenario e = new Escenario();

    @Test
    void soloTitularOResidenteAutorizadoPuedenReclamar() throws Exception {
        assertTrue(e.lote1.esTitularOAutorizado(e.titular));
        assertFalse(e.lote1.esTitularOAutorizado(e.familiar));
        e.titular.autorizarResidente(e.lote1, e.familiar);
        assertTrue(e.lote1.esTitularOAutorizado(e.familiar));
        assertFalse(e.lote1.esTitularOAutorizado(e.inquilino));
        assertFalse(e.lote1.esTitularOAutorizado(e.otroTitular));
        assertFalse(e.lote1.esTitularOAutorizado(null));
    }

    @Test
    void unTitularNoAutorizaResidentesDeOtroLote() {
        try {
            e.otroTitular.autorizarResidente(e.lote1, e.familiar);
            fail("Tenia que lanzar PersonaNoAutorizadaException");
        } catch (PersonaNoAutorizadaException ex) {
            assertNotNull(ex.getMessage());
        }
    }

    @Test
    void expensaEsPresupuestoPorCoeficiente() throws Exception {
        Expensa expensa = e.lote1.crearExpensa(YearMonth.of(2026, 9), 400000, 4000);
        assertEquals(100000, expensa.getImporte(), 0.001);
        assertEquals(LocalDate.of(2026, 9, 10), expensa.getVencimiento());
    }

    @Test
    void noSeEmiteDosVecesElMismoPeriodo() throws Exception {
        e.lote1.crearExpensa(YearMonth.of(2026, 9), 400000, 4000);
        try {
            e.lote1.crearExpensa(YearMonth.of(2026, 9), 400000, 4000);
            fail("Tenia que lanzar OperacionInvalidaException");
        } catch (OperacionInvalidaException ex) {
            assertNotNull(ex.getMessage());
        }
    }

    @Test
    void interesDel3PorcientoMensualDesdeElVencimiento() {
        Expensa expensa = new Expensa(1, YearMonth.of(2026, 9), 100000);
        assertEquals(0, expensa.calcularInteres(LocalDate.of(2026, 9, 10)), 0.001);
        assertEquals(3000, expensa.calcularInteres(LocalDate.of(2026, 9, 11)), 0.001);
        assertEquals(6000, expensa.calcularInteres(LocalDate.of(2026, 10, 11)), 0.001);
        assertEquals(EstadoExpensa.VENCIDA, expensa.obtenerEstado(LocalDate.of(2026, 9, 11)));
        assertEquals(EstadoExpensa.EMITIDA, expensa.obtenerEstado(LocalDate.of(2026, 9, 5)));
    }

    @Test
    void esMorosoConMasDeTresPeriodosVencidosImpagos() throws Exception {
        for (int mes = 6; mes <= 9; mes++) {
            e.lote1.crearExpensa(YearMonth.of(2026, mes), 400000, 4000);
        }

        assertFalse(e.lote1.actualizarMorosidad(LocalDate.of(2026, 8, 11)));

        assertTrue(e.lote1.actualizarMorosidad(LocalDate.of(2026, 9, 11)));
        assertTrue(e.lote1.isMoroso());
        assertFalse(e.lote1.actualizarMorosidad(LocalDate.of(2026, 9, 12)));
    }
}
