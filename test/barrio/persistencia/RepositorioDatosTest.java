package barrio.persistencia;

import barrio.Escenario;
import barrio.modelo.reclamos.TipoReclamo;
import barrio.modelo.lotes.Lote;
import barrio.modelo.reclamos.Reclamo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

class RepositorioDatosTest {
    @TempDir
    Path carpeta;

    @Test
    void losDatosSeConservanEntreEjecuciones() throws Exception {
        String ruta = carpeta.resolve("barrio.dat").toString();
        Escenario e = new Escenario();
        e.reclamos.presentarReclamo(e.lote1, e.titular, TipoReclamo.SEGURIDAD, "Intruso", Escenario.AHORA);
        e.expensas.emitirExpensas(YearMonth.of(2026, 10), 400000);

        RepositorioDatos conArchivo = new RepositorioDatos(ruta);
        for (Lote lote : e.repo.getLotes()) {
            conArchivo.agregarLote(lote);
        }
        for (Reclamo reclamo : e.repo.getReclamos()) {
            conArchivo.guardar(reclamo);
        }
        conArchivo.siguienteNumero("reclamo");
        conArchivo.guardarEnArchivo();

        RepositorioDatos leido = new RepositorioDatos(ruta);
        assertTrue(leido.existeArchivo());
        leido.cargarDesdeArchivo();
        assertEquals(2, leido.getLotes().size());
        assertEquals("Intruso", leido.buscarReclamo(1).getDescripcion());
        assertEquals(100000, leido.buscarLote(1).buscarExpensa(YearMonth.of(2026, 10)).getImporte(), 0.001);
        assertEquals(2, leido.siguienteNumero("reclamo"));
    }

    @Test
    void archivoDanadoTiraIOException() throws Exception {
        File archivo = carpeta.resolve("roto.dat").toFile();
        try (FileWriter escritor = new FileWriter(archivo)) {
            escritor.write("esto no es un objeto serializado");
        }
        RepositorioDatos repo = new RepositorioDatos(archivo.getPath());
        try {
            repo.cargarDesdeArchivo();
            fail("Tenia que lanzar IOException");
        } catch (IOException ex) {
            assertNotNull(ex.getMessage());
        }
    }
}
