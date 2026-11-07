package barrio.modelo.equipos;

import java.time.LocalDate;
import java.util.List;

public class Camara extends Equipo {
    private static final long serialVersionUID = 1L;

    public static final int HORAS_PARA_INFORMAR = 48;

    private String resolucion;
    private int diasGrabacion;
    private EstrategiaPrioridadOrden estrategia;

    public Camara(String codigo, String sector, LocalDate fechaInstalacion, String resolucion, int diasGrabacion) {
        super(codigo, sector, fechaInstalacion);
        if (diasGrabacion <= 0) {
            throw new IllegalArgumentException("Los dias de grabacion deben ser positivos.");
        }
        this.resolucion = resolucion;
        this.diasGrabacion = diasGrabacion;
        this.estrategia = new PrioridadCamara();
    }

    @Override
    public PrioridadOrden calcularPrioridadFalla(List<Equipo> equiposDelBarrio) {
        return estrategia.calcular(this, equiposDelBarrio);
    }

    @Override
    public String getTipo() {
        return "Camara";
    }

    public boolean debeInformarseAlAdministrador(java.time.LocalDateTime ahora) {
        return !isInformadoAdministrador() && horasSinFuncionar(ahora) > HORAS_PARA_INFORMAR;
    }

    public String getResolucion() { return resolucion; }
    public int getDiasGrabacion() { return diasGrabacion; }
}
