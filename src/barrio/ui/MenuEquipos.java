package barrio.ui;

import barrio.excepciones.BarrioException;
import barrio.modelo.equipos.Equipo;
import barrio.modelo.equipos.Falla;
import barrio.modelo.equipos.OrdenMantenimiento;
import barrio.modelo.personas.EmpleadoMantenimiento;

public class MenuEquipos extends SubMenu {
    public MenuEquipos(SistemaBarrio sistema, Consola consola) {
        super(sistema, consola);
    }

    @Override
    protected String getTitulo() {
        return "LUMINARIAS Y CAMARAS";
    }

    @Override
    protected String[] getOpciones() {
        return new String[]{
                "Registrar falla de un equipo (vecino o guardia)",
                "Generar orden de mantenimiento (administrador)",
                "Cerrar orden de mantenimiento",
                "Marcar equipo fuera de servicio",
                "Consultar estado e historial de un equipo",
                "Listar equipos"
        };
    }

    @Override
    protected void procesar(int opcion) throws BarrioException {
        switch (opcion) {
            case 1:
                registrarFalla();
                break;
            case 2:
                generarOrden();
                break;
            case 3:
                cerrarOrden();
                break;
            case 4:
                String codigo = consola.leerTexto("Codigo del equipo: ");
                sistema.getMantenimiento().marcarFueraDeServicio(codigo, sistema.ahora());
                System.out.println("Equipo marcado fuera de servicio.");
                break;
            case 5:
                consultar();
                break;
            case 6:
                for (Equipo equipo : sistema.getRepositorio().getEquipos()) {
                    System.out.println(equipo);
                }
                break;
            default:
                System.out.println("Opcion invalida.");
                break;
        }
    }

    private void registrarFalla() throws BarrioException {
        String codigo = consola.leerTexto("Codigo del equipo (ej. CAM-01, LUM-03): ");
        String descripcion = consola.leerTexto("Descripcion de la falla: ");
        String quien = consola.leerTexto("Quien la reporta: ");
        sistema.getMantenimiento().registrarFalla(codigo, descripcion, quien, sistema.ahora());
        System.out.println("Falla registrada. El equipo queda CON_FALLA.");
    }

    private void generarOrden() throws BarrioException {
        String codigo = consola.leerTexto("Codigo del equipo: ");
        EmpleadoMantenimiento tecnico = Busquedas.tecnico(sistema.getRepositorio(), consola);
        String descripcion = consola.leerTexto("Trabajo a realizar: ");
        OrdenMantenimiento orden = sistema.getMantenimiento().generarOrden(codigo, tecnico, descripcion, sistema.ahora());
        System.out.println("Orden " + orden.getNumero() + " generada con prioridad " + orden.getPrioridad() + ".");
    }

    private void cerrarOrden() throws BarrioException {
        int numero = consola.leerEntero("Numero de orden: ");
        EmpleadoMantenimiento tecnico = Busquedas.tecnico(sistema.getRepositorio(), consola);
        String reparacion = consola.leerTextoOpcional("Descripcion de la reparacion: ");
        sistema.getMantenimiento().cerrarOrden(numero, sistema.ahora(), tecnico, reparacion);
        System.out.println("Orden cerrada. El equipo vuelve a estar OPERATIVO.");
    }

    private void consultar() throws BarrioException {
        Equipo equipo = sistema.getMantenimiento().buscarEquipo(consola.leerTexto("Codigo del equipo: "));
        System.out.println(equipo + " - instalado " + equipo.getFechaInstalacion());
        System.out.println("Fallas:");
        for (Falla falla : equipo.getFallas()) {
            System.out.println("  " + falla);
        }
        System.out.println("Ordenes:");
        for (OrdenMantenimiento orden : equipo.getOrdenes()) {
            System.out.println("  " + orden);
        }
    }
}
