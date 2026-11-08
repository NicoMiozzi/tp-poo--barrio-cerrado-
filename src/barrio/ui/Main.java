package barrio.ui;

import barrio.excepciones.BarrioException;
import barrio.persistencia.RepositorioDatos;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static final String ARCHIVO_DATOS = "datos/barrio.dat";

    public static void main(String[] args) {
        RepositorioDatos repositorio = new RepositorioDatos(ARCHIVO_DATOS);
        SistemaBarrio sistema = new SistemaBarrio(repositorio);
        Scanner teclado = new Scanner(System.in);

        try {
            if (repositorio.existeArchivo()) {
                repositorio.cargarDesdeArchivo();
                System.out.println("Datos cargados de " + ARCHIVO_DATOS);
            } else {
                System.out.println("No hay datos guardados: se cargan los datos de prueba...");
                DatosIniciales.cargar(sistema);
                sistema.guardarCambios();
            }
            new MenuPrincipal(sistema, new Consola(teclado)).ejecutar();
        } catch (IOException e) {
            System.out.println("No se pudo leer " + ARCHIVO_DATOS + ": " + e.getMessage()
                    + ". Borre el archivo para empezar con los datos de prueba.");
        } catch (BarrioException e) {
            System.out.println("Error al cargar los datos de prueba: " + e.getMessage());
        } catch (FinDeEntradaException e) {
            System.out.println("Fin de la entrada de datos.");
        } finally {
            sistema.guardarCambios();
            teclado.close();
            System.out.println("Datos guardados. Hasta luego.");
        }
    }
}
