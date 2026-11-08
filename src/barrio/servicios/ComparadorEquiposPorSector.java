package barrio.servicios;

import barrio.modelo.equipos.Equipo;

import java.util.Comparator;

public class ComparadorEquiposPorSector implements Comparator<Equipo> {

    @Override
    public int compare(Equipo a, Equipo b) {
        int porSector = a.getSector().compareTo(b.getSector());
        if (porSector != 0) {
            return porSector;
        }
        return a.getCodigo().compareTo(b.getCodigo());
    }
}
