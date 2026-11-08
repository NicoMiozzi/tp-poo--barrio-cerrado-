package barrio.ui;

import barrio.modelo.expensas.AplicacionPago;
import barrio.excepciones.BarrioException;
import barrio.modelo.expensas.AsientoAjuste;
import barrio.modelo.expensas.Expensa;
import barrio.modelo.expensas.MedioPago;
import barrio.modelo.expensas.Pago;
import barrio.modelo.lotes.Lote;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MenuExpensas extends SubMenu {
    public MenuExpensas(SistemaBarrio sistema, Consola consola) {
        super(sistema, consola);
    }

    @Override
    protected String getTitulo() {
        return "EXPENSAS Y PAGOS";
    }

    @Override
    protected String[] getOpciones() {
        return new String[]{
                "Emitir expensas del mes (administrador)",
                "Registrar pago de un lote",
                "Corregir un pago con asiento de ajuste",
                "Ver estado de cuenta de un lote",
                "Ver pagos de un lote"
        };
    }

    @Override
    protected void procesar(int opcion) throws BarrioException {
        switch (opcion) {
            case 1:
                emitir();
                break;
            case 2:
                pagar();
                break;
            case 3:
                ajustar();
                break;
            case 4:
                estadoDeCuenta();
                break;
            case 5:
                verPagos();
                break;
            default:
                System.out.println("Opcion invalida.");
                break;
        }
    }

    private void emitir() throws BarrioException {
        YearMonth periodo = leerPeriodo();
        double presupuesto = consola.leerDecimal("Presupuesto total del mes: $");
        List<Expensa> emitidas = sistema.getExpensas().emitirExpensas(periodo, presupuesto);
        System.out.println("Se emitieron " + emitidas.size() + " expensas del periodo " + periodo
                + " (vencen el " + periodo.atDay(Expensa.DIA_VENCIMIENTO) + ").");
    }

    private void pagar() throws BarrioException {
        Lote lote = Busquedas.lote(sistema.getRepositorio(), consola);
        LocalDate hoy = sistema.ahora().toLocalDate();
        System.out.println("Deuda a hoy: $" + lote.calcularDeudaTotal(hoy));
        double importe = consola.leerDecimal("Importe del pago: $");
        MedioPago medio = MedioPago.values()[consola.elegirOpcion("Medio de pago:", MedioPago.values())];
        Pago pago = sistema.getExpensas().imputarPago(lote, hoy, importe, medio);
        System.out.println("Pago registrado con numero " + pago.getNumero() + ". Imputacion:");
        for (AplicacionPago aplicacion : pago.getAplicaciones()) {
            System.out.println(aplicacion);
        }
        System.out.println("Deuda restante: $" + lote.calcularDeudaTotal(hoy) + (lote.isMoroso() ? " (sigue MOROSO)" : ""));
    }

    private void ajustar() throws BarrioException {
        int numeroPago = consola.leerEntero("Numero de pago a corregir: ");
        String motivo = consola.leerTexto("Motivo del ajuste: ");
        AsientoAjuste ajuste = sistema.getExpensas().registrarAjuste(numeroPago, motivo, sistema.ahora().toLocalDate());
        System.out.println("Registrado: " + ajuste);
        System.out.println("El pago original se conserva marcado como ajustado. Si correspondia otro importe, registre el pago correcto.");
    }

    private void estadoDeCuenta() throws BarrioException {
        Lote lote = Busquedas.lote(sistema.getRepositorio(), consola);
        LocalDate hoy = sistema.ahora().toLocalDate();
        System.out.println(lote);
        for (Expensa expensa : lote.getExpensas()) {
            System.out.println("  " + expensa.getPeriodo() + " - importe $" + expensa.getImporte()
                    + " - " + expensa.obtenerEstado(hoy) + " - saldo capital $" + expensa.getSaldoCapital()
                    + " - interes a hoy $" + expensa.calcularInteres(hoy));
        }
        System.out.println("Periodos vencidos impagos: " + lote.contarPeriodosImpagos(hoy)
                + " - deuda total a hoy: $" + lote.calcularDeudaTotal(hoy));
    }

    private void verPagos() throws BarrioException {
        Lote lote = Busquedas.lote(sistema.getRepositorio(), consola);
        for (Pago pago : sistema.getRepositorio().getPagos()) {
            if (pago.getNumeroLote() == lote.getNumero()) {
                System.out.println(pago);
                for (AplicacionPago aplicacion : pago.getAplicaciones()) {
            System.out.println(aplicacion);
        }
            }
        }
    }

    private YearMonth leerPeriodo() {
        while (true) {
            try {
                return YearMonth.parse(consola.leerTexto("Periodo (aaaa-mm): "));
            } catch (DateTimeParseException e) {
                System.out.println("  Formato invalido. Ejemplo: 2026-11");
            }
        }
    }
}
