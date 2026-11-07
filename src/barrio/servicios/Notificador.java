package barrio.servicios;

import java.time.LocalDateTime;

public interface Notificador {
    void notificar(LocalDateTime fecha, String destinatario, String mensaje);
}
