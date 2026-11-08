package barrio.modelo.personas;

public class Residente extends Persona {
    private static final long serialVersionUID = 1L;

    private TipoResidente tipo;
    private boolean autorizadoReclamos;

    public Residente(String nombre, String apellido, String documento, String telefono, String correo,
                     TipoResidente tipo) {
        super(nombre, apellido, documento, telefono, correo);
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de residente es obligatorio.");
        }
        this.tipo = tipo;
        this.autorizadoReclamos = false;
    }

    @Override
    public String getRol() {
        return "Residente (" + tipo.name().toLowerCase() + ")";
    }

    @Override
    public boolean puedePresentarReclamos() {
        return autorizadoReclamos;
    }

    public TipoResidente getTipo() { return tipo; }
    public boolean isAutorizadoReclamos() { return autorizadoReclamos; }

    protected void setAutorizadoReclamos(boolean autorizado) {
        this.autorizadoReclamos = autorizado;
    }
}
