package barrio.modelo.equipos;

import java.io.Serializable;
import java.util.List;

public class PrioridadCamara implements EstrategiaPrioridadOrden, Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public PrioridadOrden calcular(Equipo equipo, List<Equipo> equiposDelBarrio) {
        return PrioridadOrden.ALTA;
    }
}
