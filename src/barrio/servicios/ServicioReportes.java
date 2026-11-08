package barrio.servicios;

import barrio.modelo.equipos.Equipo;
import barrio.modelo.lotes.Lote;
import barrio.modelo.reclamos.Reclamo;
import barrio.persistencia.RepositorioDatos;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServicioReportes {
    private final RepositorioDatos repositorio;

    public ServicioReportes(RepositorioDatos repositorio) {
        this.repositorio = repositorio;
    }

    public List<Reclamo> reportarReclamos() {
        List<Reclamo> pendientes = new ArrayList<>();
        for (Reclamo reclamo : repositorio.getReclamos()) {
            if (reclamo.estaPendiente()) {
                pendientes.add(reclamo);
            }
        }
        Collections.sort(pendientes);
        return pendientes;
    }

    public List<Lote> reportarMorosidad(LocalDate fecha) {
        List<Lote> conDeuda = new ArrayList<>();
        for (Lote lote : repositorio.getLotes()) {
            if (lote.contarPeriodosImpagos(fecha) > 0) {
                conDeuda.add(lote);
            }
        }
        Collections.sort(conDeuda, new ComparadorLotesPorDeuda(fecha));
        return conDeuda;
    }

    public List<Equipo> reportarEquipos() {
        List<Equipo> noOperativos = new ArrayList<>();
        for (Equipo equipo : repositorio.getEquipos()) {
            if (!equipo.estaFuncionando()) {
                noOperativos.add(equipo);
            }
        }
        Collections.sort(noOperativos, new ComparadorEquiposPorSector());
        return noOperativos;
    }

    public String textoReclamos() {
        StringBuilder texto = new StringBuilder("REPORTE DE RECLAMOS PENDIENTES\n");
        List<Reclamo> reclamos = reportarReclamos();
        for (Reclamo reclamo : reclamos) {
            texto.append(reclamo).append('\n');
        }
        texto.append("Total: ").append(reclamos.size()).append('\n');
        return texto.toString();
    }

    public String textoMorosidad(LocalDate fecha) {
        StringBuilder texto = new StringBuilder("REPORTE DE MOROSIDAD POR LOTE al " + fecha + "\n");
        List<Lote> lotes = reportarMorosidad(fecha);
        for (Lote lote : lotes) {
            texto.append("Lote ").append(lote.getNumero())
                    .append(" - ").append(lote.getTitular().obtenerNombreCompleto())
                    .append(" - periodos impagos: ").append(lote.contarPeriodosImpagos(fecha))
                    .append(" - deuda: $").append(lote.calcularDeudaTotal(fecha))
                    .append(lote.isMoroso() ? " - MOROSO" : "").append('\n');
        }
        texto.append("Lotes con deuda vencida: ").append(lotes.size()).append('\n');
        return texto.toString();
    }

    public String textoEquipos(java.time.LocalDateTime ahora) {
        StringBuilder texto = new StringBuilder("REPORTE DE EQUIPOS FUERA DE SERVICIO / CON FALLA\n");
        List<Equipo> equipos = reportarEquipos();
        for (Equipo equipo : equipos) {
            texto.append(equipo).append(" - ").append(equipo.horasSinFuncionar(ahora)).append(" h sin funcionar\n");
        }
        texto.append("Total: ").append(equipos.size()).append('\n');
        return texto.toString();
    }

    public String exportar(String nombreArchivo, String contenido) throws IOException {
        File carpeta = new File("reportes");
        carpeta.mkdirs();
        File archivo = new File(carpeta, nombreArchivo);
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(archivo))) {
            escritor.write(contenido);
        }
        return archivo.getPath();
    }
}
