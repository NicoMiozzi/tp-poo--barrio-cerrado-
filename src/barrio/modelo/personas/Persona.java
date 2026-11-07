package barrio.modelo.personas;

import java.io.Serializable;

public abstract class Persona implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private String apellido;
    private String documento;
    private String telefono;
    private String correo;

    protected Persona(String nombre, String apellido, String documento, String telefono, String correo) {
        if (estaVacio(nombre) || estaVacio(apellido) || estaVacio(documento)) {
            throw new IllegalArgumentException("Nombre, apellido y documento son obligatorios.");
        }
        this.nombre = nombre.trim();
        this.apellido = apellido.trim();
        this.documento = documento.trim();
        this.telefono = telefono;
        this.correo = correo;
    }

    protected static boolean estaVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    public String obtenerNombreCompleto() {
        return nombre + " " + apellido;
    }

    public abstract String getRol();

    public abstract boolean puedePresentarReclamos();

    public boolean tieneDocumento(String documento) {
        return this.documento.equalsIgnoreCase(documento == null ? "" : documento.trim());
    }

    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getDocumento() { return documento; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }

    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setCorreo(String correo) { this.correo = correo; }

    @Override
    public String toString() {
        return getRol() + ": " + obtenerNombreCompleto() + " (DNI " + documento + ")";
    }
}
