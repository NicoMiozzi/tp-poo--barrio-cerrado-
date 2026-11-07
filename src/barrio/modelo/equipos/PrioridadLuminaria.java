package barrio.modelo.equipos;

import java.io.Serializable;
import java.util.List;

public class PrioridadLuminaria implements EstrategiaPrioridadOrden, Serializable {
    private static final long serialVersionUID = 1L;

    public static final int LIMITE_FALLAS_SECTOR = 3;

    @Override
    public PrioridadOrden calcular(Equipo equipo, List<Equipo> equiposDelBarrio) {
        // RN-16: cuenta las luminarias del mismo sector con falla o en reparacion (incluida esta)
        int fallando = 0;
        for (Equipo otro : equiposDelBarrio) {
            boolean esLuminariaDelSector = otro instanceof Luminaria
                    && otro.getSector().equalsIgnoreCase(equipo.getSector());
            if (esLuminariaDelSector && otro.tieneFallaActiva()) {
                fallando++;
            }
        }
        return fallando > LIMITE_FALLAS_SECTOR ? PrioridadOrden.ALTA : PrioridadOrden.NORMAL;
    }
}
