package barrio.servicios;

import barrio.persistencia.RepositorioDatos;

import java.time.LocalDateTime;

public class NotificadorAdministrador implements Notificador {
    private final RepositorioDatos repositorio;
    private final boolean mostrarEnConsola;

    public NotificadorAdministrador(RepositorioDatos repositorio, boolean mostrarEnConsola) {
        this.repositorio = repositorio;
        this.mostrarEnConsola = mostrarEnConsola;
    }

    @Override
    public void notificar(LocalDateTime fecha, String destinatario, String mensaje) {
        Notificacion notificacion = new Notificacion(fecha, destinatario, mensaje);
        repositorio.guardar(notificacion);
        if (mostrarEnConsola) {
            System.out.println("  [NOTIFICACION] " + notificacion);
        }
    }
}
