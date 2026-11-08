package barrio.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Consola {
    private final Scanner teclado;

    public Consola(Scanner teclado) {
        this.teclado = teclado;
    }

    public String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = leerLinea();
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("  El dato no puede quedar vacio.");
        }
    }

    public String leerTextoOpcional(String mensaje) {
        System.out.print(mensaje);
        return leerLinea();
    }

    public int leerEntero(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(mensaje));
            } catch (NumberFormatException e) {
                System.out.println("  Tiene que ser un numero entero.");
            }
        }
    }

    public double leerDecimal(String mensaje) {
        while (true) {
            try {
                return Double.parseDouble(leerTexto(mensaje).replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("  Tiene que ser un numero (por ejemplo 1500.50).");
            }
        }
    }

    public LocalDate leerFecha(String mensaje) {
        while (true) {
            try {
                return LocalDate.parse(leerTexto(mensaje + " (aaaa-mm-dd): "));
            } catch (DateTimeParseException e) {
                System.out.println("  Formato de fecha invalido. Ejemplo: 2026-11-04");
            }
        }
    }

    public LocalTime leerHora(String mensaje) {
        while (true) {
            try {
                return LocalTime.parse(leerTexto(mensaje + " (hh:mm): "));
            } catch (DateTimeParseException e) {
                System.out.println("  Formato de hora invalido. Ejemplo: 09:30");
            }
        }
    }

    public LocalDateTime leerFechaHora(String mensaje) {
        LocalDate fecha = leerFecha(mensaje + " - fecha");
        LocalTime hora = leerHora(mensaje + " - hora");
        return LocalDateTime.of(fecha, hora);
    }

    public int elegirOpcion(String mensaje, Object[] valores) {
        System.out.println(mensaje);
        for (int i = 0; i < valores.length; i++) {
            System.out.println("  " + (i + 1) + ") " + valores[i]);
        }
        while (true) {
            int opcion = leerEntero("Opcion: ");
            if (opcion >= 1 && opcion <= valores.length) {
                return opcion - 1;
            }
            System.out.println("  Opcion fuera de rango.");
        }
    }

    private String leerLinea() {
        if (!teclado.hasNextLine()) {
            throw new FinDeEntradaException();
        }
        return teclado.nextLine().trim();
    }

}
