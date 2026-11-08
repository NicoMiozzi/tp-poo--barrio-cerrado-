package barrio.ui;

public class FinDeEntradaException extends RuntimeException {
    public FinDeEntradaException() {
        super("No hay mas datos de entrada.");
    }
}
