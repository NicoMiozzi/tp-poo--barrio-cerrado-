package barrio.modelo.expensas;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

public class Expensa implements Serializable, Comparable<Expensa> {
    private static final long serialVersionUID = 1L;

    public static final int DIA_VENCIMIENTO = 10;

    public static final double TASA_INTERES_MENSUAL = 0.03;

    private int numeroLote;
    private YearMonth periodo;
    private double importe;
    private LocalDate vencimiento;
    private double capitalPagado;
    private double interesPagado;

    private double interesPendienteAlCorte;
    private LocalDate fechaCorte;

    public Expensa(int numeroLote, YearMonth periodo, double importe) {
        if (periodo == null) {
            throw new IllegalArgumentException("El periodo es obligatorio.");
        }
        if (importe <= 0) {
            throw new IllegalArgumentException("El importe de la expensa debe ser mayor que cero.");
        }
        this.numeroLote = numeroLote;
        this.periodo = periodo;
        this.importe = redondear(importe);
        this.vencimiento = periodo.atDay(DIA_VENCIMIENTO);
        this.capitalPagado = 0;
        this.interesPagado = 0;
        this.interesPendienteAlCorte = 0;
        this.fechaCorte = vencimiento;
    }

    public static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    public boolean estaVencida(LocalDate fecha) {
        return fecha.isAfter(vencimiento);
    }

    public long calcularMesesDeMora(LocalDate fecha) {
        if (!estaVencida(fecha)) {
            return 0;
        }
        return ChronoUnit.MONTHS.between(vencimiento, fecha) + 1;
    }

    // Interes pendiente del ultimo pago + 3 % por cada mes (o fraccion) nuevo sobre el capital que quedo impago
    public double calcularInteres(LocalDate fecha) {
        long mesesNuevos = Math.max(0, calcularMesesDeMora(fecha) - calcularMesesDeMora(fechaCorte));
        return redondear(interesPendienteAlCorte + getSaldoCapital() * TASA_INTERES_MENSUAL * mesesNuevos);
    }

    public double calcularDeuda(LocalDate fecha) {
        return redondear(getSaldoCapital() + calcularInteres(fecha));
    }

    public double getSaldoCapital() {
        return redondear(importe - capitalPagado);
    }

    public boolean estaPagada() {
        return getSaldoCapital() <= 0;
    }

    public EstadoExpensa obtenerEstado(LocalDate fecha) {
        if (estaPagada()) {
            return EstadoExpensa.PAGADA;
        }
        if (estaVencida(fecha)) {
            return EstadoExpensa.VENCIDA;
        }
        if (capitalPagado > 0) {
            return EstadoExpensa.PARCIALMENTE_PAGADA;
        }
        return EstadoExpensa.EMITIDA;
    }

    void aplicar(LocalDate fecha, double interes, double capital) {
        // congela el interes devengado hasta la fecha del pago, despues se descuenta lo pagado
        if (fecha.isAfter(fechaCorte)) {
            interesPendienteAlCorte = calcularInteres(fecha);
            fechaCorte = fecha;
        }
        interesPendienteAlCorte = redondear(Math.max(0, interesPendienteAlCorte - interes));
        interesPagado = redondear(interesPagado + interes);
        capitalPagado = redondear(capitalPagado + capital);
    }

    void revertir(double interes, double capital) {
        interesPendienteAlCorte = redondear(interesPendienteAlCorte + interes);
        interesPagado = redondear(interesPagado - interes);
        capitalPagado = redondear(capitalPagado - capital);
    }

    public int getNumeroLote() { return numeroLote; }
    public YearMonth getPeriodo() { return periodo; }
    public double getImporte() { return importe; }
    public LocalDate getVencimiento() { return vencimiento; }
    public double getCapitalPagado() { return capitalPagado; }
    public double getInteresPagado() { return interesPagado; }

    @Override
    public int compareTo(Expensa otra) {
        return periodo.compareTo(otra.periodo);
    }

    @Override
    public String toString() {
        return "Expensa " + periodo + " lote " + numeroLote + " - importe $" + importe
                + " - saldo capital $" + getSaldoCapital() + " - vence " + vencimiento;
    }
}
