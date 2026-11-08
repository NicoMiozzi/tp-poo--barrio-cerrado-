package barrio.controladores;

import barrio.Escenario;
import barrio.excepciones.AccesoDenegadoException;
import barrio.excepciones.IngresoAbiertoException;
import barrio.excepciones.OperacionInvalidaException;
import barrio.excepciones.PersonaNoAutorizadaException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ControladorAccesoTest {
    private final Escenario e = new Escenario();
    private final LocalDate dia = LocalDate.of(2026, 11, 4);

    private void autorizarJuan() throws Exception {
        e.acceso.autorizarVisita(e.lote1, e.titular, "Juan", "30111222", dia, LocalTime.of(14, 0), LocalTime.of(20, 0));
    }

    @Test
    void ingresaConAutorizacionVigenteYDespuesSale() throws Exception {
        autorizarJuan();
        e.acceso.registrarIngreso("30111222", 1, dia.atTime(15, 0));
        assertTrue(e.acceso.tieneIngresoAbierto("30111222"));
        e.acceso.registrarEgreso("30111222", dia.atTime(18, 0));
        assertFalse(e.acceso.tieneIngresoAbierto("30111222"));
    }

    @Test
    void sinAutorizacionVigenteSeDeniegaElAcceso() throws Exception {
        autorizarJuan();
        try {
            e.acceso.registrarIngreso("99999999", 1, dia.atTime(15, 0));
            fail("Tenia que lanzar AccesoDenegadoException");
        } catch (AccesoDenegadoException ex) {
            assertNotNull(ex.getMessage());
        }
        try {
            e.acceso.registrarIngreso("30111222", 1, dia.atTime(21, 0));
            fail("Tenia que lanzar AccesoDenegadoException");
        } catch (AccesoDenegadoException ex) {
            assertNotNull(ex.getMessage());
        }
        try {
            e.acceso.registrarIngreso("30111222", 2, dia.atTime(15, 0));
            fail("Tenia que lanzar AccesoDenegadoException");
        } catch (AccesoDenegadoException ex) {
            assertNotNull(ex.getMessage());
        }
        try {
            e.acceso.registrarIngreso("30111222", 1, dia.plusDays(1).atTime(15, 0));
            fail("Tenia que lanzar AccesoDenegadoException");
        } catch (AccesoDenegadoException ex) {
            assertNotNull(ex.getMessage());
        }
    }

    @Test
    void noHaySegundoIngresoSinEgreso() throws Exception {
        autorizarJuan();
        e.acceso.registrarIngreso("30111222", 1, dia.atTime(15, 0));
        try {
            e.acceso.registrarIngreso("30111222", 1, dia.atTime(16, 0));
            fail("Tenia que lanzar IngresoAbiertoException");
        } catch (IngresoAbiertoException ex) {
            assertNotNull(ex.getMessage());
        }
        assertEquals(1, e.repo.getIngresos().size());
    }

    @Test
    void soloAutorizaAlguienQueViveEnElLote() {
        try {
            e.acceso.autorizarVisita(e.lote1, e.otroTitular, "Juan", "30111222", dia, LocalTime.of(14, 0), LocalTime.of(20, 0));
            fail("Tenia que lanzar PersonaNoAutorizadaException");
        } catch (PersonaNoAutorizadaException ex) {
            assertNotNull(ex.getMessage());
        }
    }

    @Test
    void proveedorRecurrenteHasta30Dias() throws Exception {
        e.acceso.autorizarProveedor(e.lote1, e.inquilino, "Pedro", "28999000", "Jardineria", dia, dia.plusDays(29),
                LocalTime.of(8, 0), LocalTime.of(18, 0));
        LocalDateTime dia20 = dia.plusDays(20).atTime(9, 0);
        assertNotNull(e.acceso.buscarAutorizacionVigente("28999000", 1, dia20));
        assertNull(e.acceso.buscarAutorizacionVigente("28999000", 1, dia.plusDays(30).atTime(9, 0)));

        try {

            e.acceso.autorizarProveedor(e.lote1, e.titular, "Pedro", "28999000", "Jardineria", dia, dia.plusDays(30), LocalTime.of(8, 0), LocalTime.of(18, 0));

            fail("Tenia que lanzar OperacionInvalidaException");

        } catch (OperacionInvalidaException ex) {

            assertNotNull(ex.getMessage());

        }
    }
}
