package barrio.servicios;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Notificacion implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDateTime fecha;
    private String destinatario;
    private String mensaje;

    public Notificacion(LocalDateTime fecha, String destinatario, String mensaje) {
        this.fecha = fecha;
        this.destinatario = destinatario;
        this.mensaje = mensaje;
    }

    public LocalDateTime getFecha() { return fecha; }
    public String getDestinatario() { return destinatario; }
    public String getMensaje() { return mensaje; }

    @Override
    public String toString() {
        return fecha + " - para " + destinatario + ": " + mensaje;
    }
}
