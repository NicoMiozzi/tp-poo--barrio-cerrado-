package barrio.ui;

import barrio.excepciones.BarrioException;
import barrio.modelo.lotes.Lote;
import barrio.modelo.personas.Persona;
import barrio.modelo.personas.Personal;
import barrio.modelo.reclamos.Comentario;
import barrio.modelo.reclamos.EstadoReclamo;
import barrio.modelo.reclamos.HistorialEstado;
import barrio.modelo.reclamos.Reclamo;
import barrio.modelo.reclamos.TipoReclamo;

public class MenuReclamos extends SubMenu {
    public MenuReclamos(SistemaBarrio sistema, Consola consola) {
        super(sistema, consola);
    }

    @Override
    protected String getTitulo() {
        return "RECLAMOS";
    }

    @Override
    protected String[] getOpciones() {
        return new String[]{
                "Presentar reclamo (vecino)",
                "Asignar reclamo a un empleado (administrador)",
                "Avanzar estado (en curso / resuelto)",
                "Reabrir reclamo resuelto (vecino)",
                "Agregar comentario",
                "Ver historial y comentarios de un reclamo",
                "Listar todos los reclamos",
                "Ver personal del barrio"
        };
    }

    @Override
    protected void procesar(int opcion) throws BarrioException {
        switch (opcion) {
            case 1:
                presentar();
                break;
            case 2:
                asignar();
                break;
            case 3:
                avanzar();
                break;
            case 4:
                reabrir();
                break;
            case 5:
                comentar();
                break;
            case 6:
                verDetalle();
                break;
            case 7:
                for (Reclamo reclamo : sistema.getRepositorio().getReclamos()) {
                    System.out.println(reclamo);
                }
                break;
            case 8:
                for (Personal empleado : sistema.getRepositorio().getPersonal()) {
                    System.out.println(empleado);
                }
                break;
            default:
                System.out.println("Opcion invalida.");
                break;
        }
    }

    private void presentar() throws BarrioException {
        Lote lote = Busquedas.lote(sistema.getRepositorio(), consola);
        String documento = consola.leerTexto("Documento de quien presenta: ");
        Persona persona = lote.buscarHabitante(documento);
        TipoReclamo tipo = TipoReclamo.values()[consola.elegirOpcion("Tipo de reclamo:", TipoReclamo.values())];
        String descripcion = consola.leerTexto("Descripcion: ");
        Reclamo reclamo = sistema.getReclamos().presentarReclamo(lote, persona, tipo, descripcion, sistema.ahora());
        System.out.println("Reclamo creado: numero " + reclamo.getNumero() + ", prioridad " + reclamo.getPrioridad());
    }

    private void asignar() throws BarrioException {
        int numero = consola.leerEntero("Numero de reclamo: ");
        Personal empleado = Busquedas.personal(sistema.getRepositorio(), consola);
        sistema.getReclamos().asignarReclamo(numero, empleado, sistema.ahora());
        System.out.println("Reclamo " + numero + " asignado a " + empleado.getNombre() + ".");
    }

    private void avanzar() throws BarrioException {
        int numero = consola.leerEntero("Numero de reclamo: ");
        EstadoReclamo[] estados = {EstadoReclamo.EN_CURSO, EstadoReclamo.RESUELTO};
        EstadoReclamo nuevo = estados[consola.elegirOpcion("Nuevo estado:", estados)];
        String observacion = consola.leerTexto("Observacion: ");
        sistema.getReclamos().cambiarEstado(numero, nuevo, sistema.ahora(), observacion);
        System.out.println("Reclamo " + numero + " ahora esta " + nuevo + ".");
    }

    private void reabrir() throws BarrioException {
        int numero = consola.leerEntero("Numero de reclamo: ");
        String motivo = consola.leerTexto("Motivo de la reapertura: ");
        sistema.getReclamos().reabrirReclamo(numero, sistema.ahora(), motivo);
        System.out.println("Reclamo " + numero + " reabierto (vuelve a ASIGNADO).");
    }

    private void comentar() throws BarrioException {
        int numero = consola.leerEntero("Numero de reclamo: ");
        String autor = consola.leerTexto("Autor (nombre del vecino o del empleado): ");
        String texto = consola.leerTexto("Comentario: ");
        sistema.getReclamos().comentar(numero, autor, texto, sistema.ahora());
        System.out.println("Comentario agregado.");
    }

    private void verDetalle() throws BarrioException {
        Reclamo reclamo = sistema.getReclamos().consultar(consola.leerEntero("Numero de reclamo: "));
        System.out.println(reclamo);
        System.out.println("Presentado por: " + reclamo.getPresentadoPor());
        System.out.println("Historial de estados:");
        for (HistorialEstado cambio : reclamo.getHistorial()) {
            System.out.println("  " + cambio);
        }
        System.out.println("Comentarios:");
        for (Comentario comentario : reclamo.getComentarios()) {
            System.out.println("  " + comentario);
        }
    }
}
