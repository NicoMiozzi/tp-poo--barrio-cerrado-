package barrio.ui;

import barrio.excepciones.BarrioException;

import java.time.LocalDateTime;

public class MenuPrincipal extends SubMenu {
    public MenuPrincipal(SistemaBarrio sistema, Consola consola) {
        super(sistema, consola);
    }

    @Override
    protected String getTitulo() {
        return "BARRIO LOS AROMOS - MENU PRINCIPAL";
    }

    @Override
    protected String[] getOpciones() {
        return new String[]{
                "Reclamos y solicitudes",
                "Expensas y pagos",
                "Luminarias y camaras",
                "Visitas y proveedores",
                "Reportes y controles",
                "Cambiar fecha del sistema (para probar plazos)",
                "Volver a la fecha real"
        };
    }

    @Override
    protected String getTextoSalir() {
        return "Guardar y salir";
    }

    @Override
    protected void procesar(int opcion) throws BarrioException {
        switch (opcion) {
            case 1:
                new MenuReclamos(sistema, consola).ejecutar();
                break;
            case 2:
                new MenuExpensas(sistema, consola).ejecutar();
                break;
            case 3:
                new MenuEquipos(sistema, consola).ejecutar();
                break;
            case 4:
                new MenuVisitas(sistema, consola).ejecutar();
                break;
            case 5:
                new MenuReportes(sistema, consola).ejecutar();
                break;
            case 6:
                LocalDateTime fecha = consola.leerFechaHora("Nueva fecha del sistema");
                sistema.setFechaSimulada(fecha);
                break;
            case 7:
                sistema.setFechaSimulada(null);
                break;
            default:
                System.out.println("Opcion invalida.");
                break;
        }
    }
}
