package barrio.modelo.personas;

import barrio.excepciones.PersonaNoAutorizadaException;
import barrio.modelo.lotes.Lote;

public class Titular extends Persona {
    private static final long serialVersionUID = 1L;

    public Titular(String nombre, String apellido, String documento, String telefono, String correo) {
        super(nombre, apellido, documento, telefono, correo);
    }

    @Override
    public String getRol() {
        return "Titular";
    }

    @Override
    public boolean puedePresentarReclamos() {
        return true;
    }

    public void autorizarResidente(Lote lote, Residente residente) throws PersonaNoAutorizadaException {
        if (lote.getTitular() != this) {
            throw new PersonaNoAutorizadaException("Solo el titular del lote " + lote.getNumero()
                    + " puede autorizar residentes.");
        }
        if (!lote.getResidentes().contains(residente)) {
            throw new PersonaNoAutorizadaException(residente.obtenerNombreCompleto()
                    + " no vive en el lote " + lote.getNumero() + ".");
        }
        residente.setAutorizadoReclamos(true);
    }
}
