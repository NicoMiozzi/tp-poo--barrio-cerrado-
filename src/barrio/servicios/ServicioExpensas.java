package barrio.servicios;

import barrio.excepciones.OperacionInvalidaException;
import barrio.excepciones.PagoInvalidoException;
import barrio.modelo.expensas.AplicacionPago;
import barrio.modelo.expensas.AsientoAjuste;
import barrio.modelo.expensas.Expensa;
import barrio.modelo.expensas.MedioPago;
import barrio.modelo.expensas.Pago;
import barrio.modelo.lotes.Lote;
import barrio.persistencia.RepositorioDatos;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class ServicioExpensas {
    private final RepositorioDatos repositorio;
    private final Notificador notificador;

    public ServicioExpensas(RepositorioDatos repositorio, Notificador notificador) {
        this.repositorio = repositorio;
        this.notificador = notificador;
    }

    public List<Expensa> emitirExpensas(YearMonth periodo, double presupuesto) throws OperacionInvalidaException {
        if (presupuesto <= 0) {
            throw new IllegalArgumentException("El presupuesto debe ser mayor que cero.");
        }
        if (repositorio.buscarPresupuesto(periodo) != null) {
            throw new OperacionInvalidaException("Las expensas de " + periodo + " ya fueron emitidas.");
        }
        double superficieTotal = calcularSuperficieTotal();
        List<Expensa> emitidas = new ArrayList<>();
        for (Lote lote : repositorio.getLotes()) {
            emitidas.add(lote.crearExpensa(periodo, presupuesto, superficieTotal));
        }
        repositorio.guardarPresupuesto(periodo, presupuesto);
        return emitidas;
    }

    public double calcularSuperficieTotal() {
        double total = 0;
        for (Lote lote : repositorio.getLotes()) {
            total += lote.getSuperficie();
        }
        return total;
    }

    public Pago imputarPago(Lote lote, LocalDate fecha, double importe, MedioPago medio) throws PagoInvalidoException {
        Pago pago = new Pago(repositorio.siguienteNumero("pago"), lote.getNumero(), fecha, importe, medio);
        if (!pago.validarImporte()) {
            throw new PagoInvalidoException("El importe del pago debe ser mayor que cero.");
        }
        double deuda = lote.calcularDeudaTotal(fecha);
        if (pago.getImporte() > deuda) {
            throw new PagoInvalidoException("El pago ($" + pago.getImporte() + ") supera la deuda del lote ($"
                    + deuda + "). No se admiten saldos a favor.");
        }

        // RN-11: primero se cancelan los intereses de todos los periodos y despues el capital,
        // siempre del periodo mas antiguo al mas nuevo
        List<Expensa> impagas = lote.expensasImpagasOrdenadas();
        double[] interesPorExpensa = new double[impagas.size()];
        double[] capitalPorExpensa = new double[impagas.size()];
        double restante = pago.getImporte();

        for (int i = 0; i < impagas.size() && restante > 0; i++) {
            interesPorExpensa[i] = Math.min(restante, impagas.get(i).calcularInteres(fecha));
            restante = Expensa.redondear(restante - interesPorExpensa[i]);
        }

        for (int i = 0; i < impagas.size() && restante > 0; i++) {
            capitalPorExpensa[i] = Math.min(restante, impagas.get(i).getSaldoCapital());
            restante = Expensa.redondear(restante - capitalPorExpensa[i]);
        }
        for (int i = 0; i < impagas.size(); i++) {
            if (interesPorExpensa[i] + capitalPorExpensa[i] > 0) {
                AplicacionPago aplicacion = new AplicacionPago(impagas.get(i), interesPorExpensa[i], capitalPorExpensa[i]);
                aplicacion.imputar(fecha);
                pago.agregarAplicacion(aplicacion);
            }
        }
        repositorio.guardar(pago);
        lote.actualizarMorosidad(fecha);
        return pago;
    }

    public AsientoAjuste registrarAjuste(int numeroPago, String motivo, LocalDate fecha)
            throws OperacionInvalidaException {
        Pago pago = repositorio.buscarPago(numeroPago);
        if (pago == null) {
            throw new OperacionInvalidaException("No existe el pago " + numeroPago + ".");
        }
        if (pago.isAjustado()) {
            throw new OperacionInvalidaException("El pago " + numeroPago + " ya tiene un asiento de ajuste.");
        }
        AsientoAjuste ajuste = new AsientoAjuste(repositorio.siguienteNumero("ajuste"), fecha, pago, motivo);
        ajuste.aplicar();
        repositorio.guardar(ajuste);
        actualizarMorosidad(fecha);
        return ajuste;
    }

    public List<Lote> actualizarMorosidad(LocalDate fecha) {
        List<Lote> nuevosMorosos = new ArrayList<>();
        for (Lote lote : repositorio.getLotes()) {
            if (lote.actualizarMorosidad(fecha)) {
                nuevosMorosos.add(lote);
                notificador.notificar(fecha.atStartOfDay(), lote.getTitular().obtenerNombreCompleto()
                        + " (" + lote.getTitular().getCorreo() + ")",
                        "Notificacion formal: el lote " + lote.getNumero() + " tiene "
                                + lote.contarPeriodosImpagos(fecha) + " periodos impagos y fue marcado como moroso."
                                + " Deuda: $" + lote.calcularDeudaTotal(fecha));
            }
        }
        return nuevosMorosos;
    }
}
