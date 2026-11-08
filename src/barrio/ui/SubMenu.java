package barrio.ui;

import barrio.excepciones.BarrioException;

public abstract class SubMenu {
    protected final SistemaBarrio sistema;
    protected final Consola consola;

    protected SubMenu(SistemaBarrio sistema, Consola consola) {
        this.sistema = sistema;
        this.consola = consola;
    }

    protected abstract String getTitulo();

    protected abstract String[] getOpciones();

    protected abstract void procesar(int opcion) throws BarrioException;

    protected String getTextoSalir() {
        return "Volver";
    }

    public void ejecutar() {
        int opcion;
        do {
            mostrar();
            opcion = consola.leerEntero("Opcion: ");
            if (opcion < 0 || opcion > getOpciones().length) {
                System.out.println("Opcion invalida.");
            } else if (opcion != 0) {
                try {
                    procesar(opcion);
                } catch (BarrioException e) {
                    System.out.println("NO SE PUDO: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.out.println("DATO INVALIDO: " + e.getMessage());
                } finally {
                    sistema.guardarCambios();
                }
            }
        } while (opcion != 0);
    }

    private void mostrar() {
        System.out.println();
        System.out.println("=== " + getTitulo() + " === (fecha del sistema: " + sistema.ahora()
                + (sistema.usaFechaSimulada() ? ", simulada" : "") + ")");
        String[] opciones = getOpciones();
        for (int i = 0; i < opciones.length; i++) {
            System.out.println((i + 1) + ") " + opciones[i]);
        }
        System.out.println("0) " + getTextoSalir());
    }
}
