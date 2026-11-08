package barrio.modelo.visitas;

import barrio.excepciones.OperacionInvalidaException;

import java.io.Serializable;
import java.time.LocalDateTime;

public class RegistroIngreso implements Serializable {
    private static final long serialVersionUID = 1L;

    private Autorizacion autorizacion;
    private LocalDateTime ingreso;
    private LocalDateTime egreso;

    public RegistroIngreso(Autorizacion autorizacion, LocalDateTime ingreso) {
        if (autorizacion == null || ingreso == null) {
            throw new IllegalArgumentException("El ingreso necesita autorizacion y fecha.");
        }
        this.autorizacion = autorizacion;
        this.ingreso = ingreso;
    }

    public void registrarEgreso(LocalDateTime fechaHora) throws OperacionInvalidaException {
        if (!estaAbierto()) {
            throw new OperacionInvalidaException("El egreso ya fue registrado.");
        }
        if (fechaHora.isBefore(ingreso)) {
            throw new OperacionInvalidaException("El egreso no puede ser anterior al ingreso.");
        }
        this.egreso = fechaHora;
    }

    public boolean estaAbierto() {
        return egreso == null;
    }

    public String getDocumento() {
        return autorizacion.getDocumento();
    }

    public Autorizacion getAutorizacion() { return autorizacion; }
    public LocalDateTime getIngreso() { return ingreso; }
    public LocalDateTime getEgreso() { return egreso; }

    @Override
    public String toString() {
        return autorizacion.getNombreVisitante() + " (DNI " + getDocumento() + ") - lote "
                + autorizacion.getLote().getNumero() + " - ingreso " + ingreso
                + (estaAbierto() ? " - DENTRO DEL BARRIO" : " - egreso " + egreso);
    }
}
