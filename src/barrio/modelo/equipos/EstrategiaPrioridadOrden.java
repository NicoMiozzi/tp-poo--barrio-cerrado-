package barrio.modelo.equipos;

import java.util.List;

public interface EstrategiaPrioridadOrden {
    PrioridadOrden calcular(Equipo equipo, List<Equipo> equiposDelBarrio);
}
