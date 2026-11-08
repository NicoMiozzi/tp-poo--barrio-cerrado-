package barrio.servicios;

import barrio.Escenario;
import barrio.excepciones.OperacionInvalidaException;
import barrio.excepciones.PagoInvalidoException;
import barrio.modelo.expensas.Expensa;
import barrio.modelo.expensas.MedioPago;
import barrio.modelo.expensas.Pago;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

class ServicioExpensasTest {
    private Escenario e;
    private Expensa septiembre;
    private Expensa octubre;

    @BeforeEach
    void preparar() throws Exception {
        e = new Escenario();

        e.expensas.emitirExpensas(YearMonth.of(2026, 9), 400000);
        e.expensas.emitirExpensas(YearMonth.of(2026, 10), 400000);
        septiembre = e.lote1.buscarExpensa(YearMonth.of(2026, 9));
        octubre = e.lote1.buscarExpensa(YearMonth.of(2026, 10));
    }

    @Test
    void emiteUnaExpensaPorLoteConSuCoeficiente() {
        assertEquals(100000, septiembre.getImporte(), 0.001);
        assertEquals(300000, e.lote2.buscarExpensa(YearMonth.of(2026, 9)).getImporte(), 0.001);
    }

    @Test
    void noSeEmiteDosVecesElMismoMes() {
        try {
            e.expensas.emitirExpensas(YearMonth.of(2026, 9), 1);
            fail("Tenia que lanzar OperacionInvalidaException");
        } catch (OperacionInvalidaException ex) {
            assertNotNull(ex.getMessage());
        }
    }

    @Test
    void pagoParcialVaPrimeroAInteresesYDespuesAlCapitalMasViejo() throws Exception {
        LocalDate fecha = LocalDate.of(2026, 10, 15);
        assertEquals(209000, e.lote1.calcularDeudaTotal(fecha), 0.001);

        Pago pago = e.expensas.imputarPago(e.lote1, fecha, 50000, MedioPago.EFECTIVO);

        assertEquals(6000, septiembre.getInteresPagado(), 0.001);
        assertEquals(3000, octubre.getInteresPagado(), 0.001);
        assertEquals(41000, septiembre.getCapitalPagado(), 0.001);
        assertEquals(0, octubre.getCapitalPagado(), 0.001);
        assertEquals(2, pago.getAplicaciones().size());

        LocalDate mesSiguiente = LocalDate.of(2026, 11, 15);
        assertEquals(1770, septiembre.calcularInteres(mesSiguiente), 0.001);
        assertEquals(3000, octubre.calcularInteres(mesSiguiente), 0.001);
    }

    @Test
    void rechazaPagoMayorALaDeuda() {
        try {
            e.expensas.imputarPago(e.lote1, LocalDate.of(2026, 9, 1), 1000000, MedioPago.TARJETA);
            fail("Tenia que lanzar PagoInvalidoException");
        } catch (PagoInvalidoException ex) {
            assertNotNull(ex.getMessage());
        }
    }

    @Test
    void rechazaPagoEnCero() {
        try {
            e.expensas.imputarPago(e.lote1, LocalDate.of(2026, 9, 1), 0, MedioPago.TARJETA);
            fail("Tenia que lanzar PagoInvalidoException");
        } catch (PagoInvalidoException ex) {
            assertNotNull(ex.getMessage());
        }
    }

    @Test
    void elAjusteRevierteElPagoSinBorrarlo() throws Exception {
        LocalDate fecha = LocalDate.of(2026, 9, 5);
        Pago pago = e.expensas.imputarPago(e.lote1, fecha, 100000, MedioPago.TRANSFERENCIA);
        assertTrue(septiembre.estaPagada());

        e.expensas.registrarAjuste(pago.getNumero(), "Transferencia rechazada por el banco", fecha);

        assertFalse(septiembre.estaPagada());
        assertTrue(pago.isAjustado());
        assertEquals(1, e.repo.getPagos().size());
        assertEquals(1, e.repo.getAjustes().size());
        assertEquals(-100000, e.repo.getAjustes().get(0).getImporte(), 0.001);
        try {
            e.expensas.registrarAjuste(pago.getNumero(), "otra vez", fecha);
            fail("Tenia que lanzar OperacionInvalidaException");
        } catch (OperacionInvalidaException ex) {
            assertNotNull(ex.getMessage());
        }
    }

    @Test
    void marcaMorosoNotificaYSeQuitaAlRegularizar() throws Exception {
        e.expensas.emitirExpensas(YearMonth.of(2026, 11), 400000);
        e.expensas.emitirExpensas(YearMonth.of(2026, 12), 400000);
        LocalDate fecha = LocalDate.of(2026, 12, 11);

        assertEquals(2, e.expensas.actualizarMorosidad(fecha).size());
        assertTrue(e.lote1.isMoroso());
        assertEquals(2, e.repo.getNotificaciones().size());

        e.expensas.imputarPago(e.lote1, fecha, e.lote1.calcularDeudaTotal(fecha), MedioPago.EFECTIVO);
        assertFalse(e.lote1.isMoroso());
        assertEquals(0, e.lote1.calcularDeudaTotal(fecha), 0.001);
    }
}
