package barrio.ui;

import barrio.excepciones.BarrioException;
import barrio.modelo.lotes.Lote;
import barrio.modelo.personas.Persona;
import barrio.modelo.visitas.Autorizacion;
import barrio.modelo.visitas.RegistroIngreso;

import java.time.LocalDate;
import java.time.LocalTime;

public class MenuVisitas extends SubMenu {
    public MenuVisitas(SistemaBarrio sistema, Consola consola) {
        super(sistema, consola);
    }

    @Override
    protected String getTitulo() {
        return "VISITAS Y PROVEEDORES";
    }

    @Override
    protected String[] getOpciones() {
        return new String[]{
                "Autorizar visita (vecino)",
                "Autorizar proveedor habitual hasta 30 dias (vecino)",
                "Registrar ingreso (guardia)",
                "Registrar egreso (guardia)",
                "Ver personas dentro del barrio",
                "Listar autorizaciones"
        };
    }

    @Override
    protected void procesar(int opcion) throws BarrioException {
        switch (opcion) {
            case 1:
                autorizarVisita();
                break;
            case 2:
                autorizarProveedor();
                break;
            case 3:
                registrarIngreso();
                break;
            case 4:
                String documento = consola.leerTexto("Documento: ");
                RegistroIngreso registro = sistema.getAcceso().registrarEgreso(documento, sistema.ahora());
                System.out.println("Egreso registrado: " + registro);
                break;
            case 5:
                for (RegistroIngreso dentro : sistema.getAcceso().personasDentro()) {
                    System.out.println(dentro);
                }
                break;
            case 6:
                for (Autorizacion autorizacion : sistema.getRepositorio().getAutorizaciones()) {
                    System.out.println(autorizacion);
                }
                break;
            default:
                System.out.println("Opcion invalida.");
                break;
        }
    }

    private void autorizarVisita() throws BarrioException {
        Lote lote = Busquedas.lote(sistema.getRepositorio(), consola);
        Persona autorizante = Busquedas.habitante(lote, consola);
        String nombre = consola.leerTexto("Nombre del visitante: ");
        String documento = consola.leerTexto("Documento del visitante: ");
        LocalDate fecha = consola.leerFecha("Fecha de la visita");
        LocalTime desde = consola.leerHora("Desde");
        LocalTime hasta = consola.leerHora("Hasta");
        Autorizacion autorizacion = sistema.getAcceso().autorizarVisita(lote, autorizante, nombre, documento, fecha, desde, hasta);
        System.out.println("Creada: " + autorizacion);
    }

    private void autorizarProveedor() throws BarrioException {
        Lote lote = Busquedas.lote(sistema.getRepositorio(), consola);
        Persona autorizante = Busquedas.habitante(lote, consola);
        String nombre = consola.leerTexto("Nombre del proveedor: ");
        String documento = consola.leerTexto("Documento del proveedor: ");
        String empresa = consola.leerTexto("Empresa / rubro: ");
        LocalDate desdeFecha = consola.leerFecha("Desde el dia");
        LocalDate hastaFecha = consola.leerFecha("Hasta el dia");
        LocalTime desde = consola.leerHora("Hora desde");
        LocalTime hasta = consola.leerHora("Hora hasta");
        Autorizacion autorizacion = sistema.getAcceso().autorizarProveedor(lote, autorizante, nombre, documento, empresa,
                desdeFecha, hastaFecha, desde, hasta);
        System.out.println("Creada: " + autorizacion);
    }

    private void registrarIngreso() throws BarrioException {
        String documento = consola.leerTexto("Documento de la persona que llega: ");
        int numeroLote = consola.leerEntero("Lote que visita: ");
        RegistroIngreso registro = sistema.getAcceso().registrarIngreso(documento, numeroLote, sistema.ahora());
        System.out.println("Ingreso habilitado: " + registro);
    }
}
