package barrio.modelo.expensas;

import java.io.Serializable;
import java.time.LocalDate;

public class AplicacionPago implements Serializable {
    private static final long serialVersionUID = 1L;

    private Expensa expensa;
    private double interesAplicado;
    private double capitalAplicado;

    public AplicacionPago(Expensa expensa, double interesAplicado, double capitalAplicado) {
        this.expensa = expensa;
        this.interesAplicado = Expensa.redondear(interesAplicado);
        this.capitalAplicado = Expensa.redondear(capitalAplicado);
    }

    public void imputar(LocalDate fecha) {
        expensa.aplicar(fecha, interesAplicado, capitalAplicado);
    }

    void revertir() {
        expensa.revertir(interesAplicado, capitalAplicado);
    }

    public double getImporteAplicado() {
        return Expensa.redondear(interesAplicado + capitalAplicado);
    }

    public Expensa getExpensa() { return expensa; }
    public double getInteresAplicado() { return interesAplicado; }
    public double getCapitalAplicado() { return capitalAplicado; }

    @Override
    public String toString() {
        return "  -> " + expensa.getPeriodo() + ": interes $" + interesAplicado + " + capital $" + capitalAplicado;
    }
}
