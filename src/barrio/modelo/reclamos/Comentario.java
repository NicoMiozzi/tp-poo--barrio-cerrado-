package barrio.modelo.reclamos;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Comentario implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDateTime fecha;
    private String autor;
    private String texto;

    public Comentario(LocalDateTime fecha, String autor, String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("El comentario no puede estar vacio.");
        }
        this.fecha = fecha;
        this.autor = autor;
        this.texto = texto.trim();
    }

    public LocalDateTime getFecha() { return fecha; }
    public String getAutor() { return autor; }
    public String getTexto() { return texto; }

    @Override
    public String toString() {
        return fecha + " - " + autor + ": " + texto;
    }
}
