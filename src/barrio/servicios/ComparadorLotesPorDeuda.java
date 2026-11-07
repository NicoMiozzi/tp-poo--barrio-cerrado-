package barrio.servicios;

import barrio.modelo.lotes.Lote;

import java.time.LocalDate;
import java.util.Comparator;

public class ComparadorLotesPorDeuda implements Comparator<Lote> {
    private final LocalDate fecha;

    public ComparadorLotesPorDeuda(LocalDate fecha) {
        this.fecha = fecha;
    }

    @Override
    public int compare(Lote a, Lote b) {
        int porDeuda = Double.compare(b.calcularDeudaTotal(fecha), a.calcularDeudaTotal(fecha));
        if (porDeuda != 0) {
            return porDeuda;
        }
        return a.compareTo(b);
    }
}
