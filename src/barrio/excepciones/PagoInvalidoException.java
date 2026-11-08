package barrio.excepciones;

public class PagoInvalidoException extends BarrioException {
    public PagoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
