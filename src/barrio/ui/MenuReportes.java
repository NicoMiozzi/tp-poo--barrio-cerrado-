package barrio.ui;

import barrio.excepciones.BarrioException;
import barrio.modelo.equipos.Camara;
import barrio.modelo.lotes.Lote;
import barrio.modelo.reclamos.Reclamo;
import barrio.servicios.Notificacion;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class MenuReportes extends SubMenu {
    public MenuReportes(SistemaBarrio sistema, Consola consola) {
        super(sistema, consola);
    }

    @Override
    protected String getTitulo() {
        return "REPORTES Y CONTROLES (administrador)";
    }

    @Override
    protected String[] getOpciones() {
        return new String[]{
                "Reporte de reclamos pendientes",
                "Reporte de morosidad por lote",
                "Reporte de equipos fuera de servicio",
                "Exportar los tres reportes a archivos de texto",
                "Correr controles automaticos (cerrar reclamos, escalar, camaras, morosidad)",
                "Ver notificaciones"
        };
    }

    @Override
    protected void procesar(int opcion) throws BarrioException {
        LocalDate hoy = sistema.ahora().toLocalDate();
        switch (opcion) {
            case 1:
                System.out.println(sistema.getReportes().textoReclamos());
                break;
            case 2:
                System.out.println(sistema.getReportes().textoMorosidad(hoy));
                break;
            case 3:
                System.out.println(sistema.getReportes().textoEquipos(sistema.ahora()));
                break;
            case 4:
                exportar(hoy);
                break;
            case 5:
                correrControles(hoy);
                break;
            case 6:
                for (Notificacion notificacion : sistema.getRepositorio().getNotificaciones()) {
                    System.out.println(notificacion);
                }
                break;
            default:
                System.out.println("Opcion invalida.");
                break;
        }
    }

    private void exportar(LocalDate hoy) {
        try {
            System.out.println("Generado: " + sistema.getReportes().exportar("reclamos-pendientes.txt",
                    sistema.getReportes().textoReclamos()));
            System.out.println("Generado: " + sistema.getReportes().exportar("morosidad.txt",
                    sistema.getReportes().textoMorosidad(hoy)));
            System.out.println("Generado: " + sistema.getReportes().exportar("equipos-fuera-de-servicio.txt",
                    sistema.getReportes().textoEquipos(sistema.ahora())));
        } catch (IOException e) {
            System.out.println("No se pudo escribir el reporte: " + e.getMessage());
        }
    }

    private void correrControles(LocalDate hoy) {
        int cerrados = sistema.getGestorReclamos().cerrarReclamosVencidos(sistema.ahora());
        List<Reclamo> escalados = sistema.getGestorReclamos().escalarReclamos(sistema.ahora());
        List<Camara> camaras = sistema.getMantenimiento().verificarCamaras(sistema.ahora());
        List<Lote> morosos = sistema.getExpensas().actualizarMorosidad(hoy);
        System.out.println("Reclamos cerrados por vencer el plazo de reapertura: " + cerrados);
        System.out.println("Reclamos escalados al administrador: " + escalados.size());
        System.out.println("Camaras informadas por estar mas de 48 h sin funcionar: " + camaras.size());
        System.out.println("Lotes que pasaron a morosos: " + morosos.size());
    }
}
