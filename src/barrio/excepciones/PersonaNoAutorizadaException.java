package barrio.excepciones;

public class PersonaNoAutorizadaException extends BarrioException {
    public PersonaNoAutorizadaException(String mensaje) {
        super(mensaje);
    }
}
