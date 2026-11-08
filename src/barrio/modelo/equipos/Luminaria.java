package barrio.modelo.equipos;

import java.time.LocalDate;
import java.util.List;

public class Luminaria extends Equipo {
    private static final long serialVersionUID = 1L;

    private String tipoLampara;
    private int potencia;
    private EstrategiaPrioridadOrden estrategia;

    public Luminaria(String codigo, String sector, LocalDate fechaInstalacion, String tipoLampara, int potencia) {
        super(codigo, sector, fechaInstalacion);
        if (potencia <= 0) {
            throw new IllegalArgumentException("La potencia debe ser positiva.");
        }
        this.tipoLampara = tipoLampara;
        this.potencia = potencia;
        this.estrategia = new PrioridadLuminaria();
    }

    @Override
    public PrioridadOrden calcularPrioridadFalla(List<Equipo> equiposDelBarrio) {
        return estrategia.calcular(this, equiposDelBarrio);
    }

    @Override
    public String getTipo() {
        return "Luminaria";
    }

    public String getTipoLampara() { return tipoLampara; }
    public int getPotencia() { return potencia; }
}
