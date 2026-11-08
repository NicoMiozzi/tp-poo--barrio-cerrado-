package barrio.modelo.reclamos;

public enum EstadoReclamo {
    ABIERTO, ASIGNADO, EN_CURSO, RESUELTO, CERRADO;

    public EstadoReclamo siguiente() {
        int posicion = ordinal() + 1;
        return posicion < values().length ? values()[posicion] : null;
    }
}
