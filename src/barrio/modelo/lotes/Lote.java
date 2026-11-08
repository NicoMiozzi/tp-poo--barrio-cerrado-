package barrio.modelo.lotes;

import barrio.excepciones.OperacionInvalidaException;
import barrio.modelo.expensas.Expensa;
import barrio.modelo.personas.Persona;
import barrio.modelo.personas.Residente;
import barrio.modelo.personas.Titular;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lote implements Serializable, Comparable<Lote> {
    private static final long serialVersionUID = 1L;

    public static final int PERIODOS_PARA_MOROSIDAD = 3;

    private int numero;
    private double superficie;
    private Titular titular;
    private List<Residente> residentes;
    private List<Expensa> expensas;
    private boolean moroso;

    public Lote(int numero, double superficie, Titular titular) {
        if (numero <= 0) {
            throw new IllegalArgumentException("El numero de lote debe ser positivo.");
        }
        if (superficie <= 0) {
            throw new IllegalArgumentException("La superficie debe ser mayor que cero.");
        }
        if (titular == null) {
            throw new IllegalArgumentException("El lote debe tener un titular.");
        }
        this.numero = numero;
        this.superficie = superficie;
        this.titular = titular;
        this.residentes = new ArrayList<>();
        this.expensas = new ArrayList<>();
        this.moroso = false;
    }

    public void agregarResidente(Residente residente) {
        if (residente != null && !residentes.contains(residente)) {
            residentes.add(residente);
        }
    }

    public boolean esTitularOAutorizado(Persona persona) {
        if (persona == null) {
            return false;
        }
        if (persona == titular) {
            return true;
        }
        return residentes.contains(persona) && persona.puedePresentarReclamos();
    }

    public Persona buscarHabitante(String documento) {
        if (titular.tieneDocumento(documento)) {
            return titular;
        }
        for (Residente residente : residentes) {
            if (residente.tieneDocumento(documento)) {
                return residente;
            }
        }
        return null;
    }

    public Expensa crearExpensa(YearMonth periodo, double presupuesto, double superficieTotal)
            throws OperacionInvalidaException {
        if (buscarExpensa(periodo) != null) {
            throw new OperacionInvalidaException("El lote " + numero + " ya tiene expensa del periodo " + periodo + ".");
        }
        double importe = presupuesto * calcularCoeficiente(superficieTotal);
        Expensa expensa = new Expensa(numero, periodo, importe);
        expensas.add(expensa);
        return expensa;
    }

    public double calcularCoeficiente(double superficieTotal) {
        if (superficieTotal <= 0) {
            throw new IllegalArgumentException("La superficie total debe ser mayor que cero.");
        }
        return superficie / superficieTotal;
    }

    public Expensa buscarExpensa(YearMonth periodo) {
        for (Expensa expensa : expensas) {
            if (expensa.getPeriodo().equals(periodo)) {
                return expensa;
            }
        }
        return null;
    }

    public List<Expensa> expensasImpagasOrdenadas() {
        List<Expensa> impagas = new ArrayList<>();
        for (Expensa expensa : expensas) {
            if (!expensa.estaPagada()) {
                impagas.add(expensa);
            }
        }
        Collections.sort(impagas);
        return impagas;
    }

    public int contarPeriodosImpagos(LocalDate fecha) {
        int cantidad = 0;
        for (Expensa expensa : expensas) {
            if (!expensa.estaPagada() && expensa.estaVencida(fecha)) {
                cantidad++;
            }
        }
        return cantidad;
    }

    public double calcularDeudaTotal(LocalDate fecha) {
        double total = 0;
        for (Expensa expensa : expensas) {
            total += expensa.calcularDeuda(fecha);
        }
        return Expensa.redondear(total);
    }

    public boolean actualizarMorosidad(LocalDate fecha) {
        boolean eraMoroso = moroso;
        moroso = contarPeriodosImpagos(fecha) > PERIODOS_PARA_MOROSIDAD;
        return moroso && !eraMoroso;
    }

    public int getNumero() { return numero; }
    public double getSuperficie() { return superficie; }
    public Titular getTitular() { return titular; }
    public boolean isMoroso() { return moroso; }

    public List<Residente> getResidentes() {
        return new ArrayList<>(residentes);
    }

    public List<Expensa> getExpensas() {
        return new ArrayList<>(expensas);
    }

    @Override
    public int compareTo(Lote otro) {
        return Integer.compare(numero, otro.numero);
    }

    @Override
    public String toString() {
        return "Lote " + numero + " (" + superficie + " m2) - titular " + titular.obtenerNombreCompleto()
                + (moroso ? " [MOROSO]" : "");
    }
}
