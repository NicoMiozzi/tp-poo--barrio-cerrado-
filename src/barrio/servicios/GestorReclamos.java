package barrio.servicios;

import barrio.excepciones.LimiteReclamosException;
import barrio.excepciones.OperacionInvalidaException;
import barrio.excepciones.TransicionInvalidaException;
import barrio.modelo.lotes.Lote;
import barrio.modelo.personas.Persona;
import barrio.modelo.personas.Personal;
import barrio.modelo.reclamos.EstadoReclamo;
import barrio.modelo.reclamos.Reclamo;
import barrio.persistencia.RepositorioDatos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GestorReclamos {
    public static final int MAXIMO_EN_CURSO = 5;

    private final RepositorioDatos repositorio;
    private final Notificador notificador;

    public GestorReclamos(RepositorioDatos repositorio, Notificador notificador) {
        this.repositorio = repositorio;
        this.notificador = notificador;
    }

    public boolean validarReclamo(Lote lote, Persona persona) {
        return lote != null && lote.esTitularOAutorizado(persona);
    }

    public Reclamo buscarReclamo(int numero) throws OperacionInvalidaException {
        Reclamo reclamo = repositorio.buscarReclamo(numero);
        if (reclamo == null) {
            throw new OperacionInvalidaException("No existe el reclamo " + numero + ".");
        }
        return reclamo;
    }

    public void asignarReclamo(int numero, Personal empleado, LocalDateTime fecha)
            throws OperacionInvalidaException, TransicionInvalidaException {
        buscarReclamo(numero).asignar(empleado, fecha);
    }

    public void cambiarEstado(int numero, EstadoReclamo nuevo, LocalDateTime fecha, String observacion)
            throws OperacionInvalidaException, TransicionInvalidaException, LimiteReclamosException {
        Reclamo reclamo = buscarReclamo(numero);
        if (nuevo == EstadoReclamo.EN_CURSO && reclamo.getResponsable() != null) {
            int enCurso = contarEnCurso(reclamo.getResponsable());
            if (enCurso >= MAXIMO_EN_CURSO) {
                throw new LimiteReclamosException(reclamo.getResponsable().getNombre() + " ya tiene "
                        + enCurso + " reclamos en curso (maximo " + MAXIMO_EN_CURSO + ").");
            }
        }
        reclamo.cambiarEstado(nuevo, fecha, observacion);
    }

    public void reabrir(int numero, LocalDateTime fecha, String observacion)
            throws OperacionInvalidaException, TransicionInvalidaException {
        buscarReclamo(numero).reabrir(fecha, observacion);
    }

    public int contarEnCurso(Personal empleado) {
        int cantidad = 0;
        for (Reclamo reclamo : repositorio.getReclamos()) {
            if (reclamo.getResponsable() == empleado && reclamo.getEstado() == EstadoReclamo.EN_CURSO) {
                cantidad++;
            }
        }
        return cantidad;
    }

    public int cerrarReclamosVencidos(LocalDateTime ahora) {
        int cerrados = 0;
        for (Reclamo reclamo : repositorio.getReclamos()) {
            if (reclamo.getEstado() == EstadoReclamo.RESUELTO && reclamo.vencioPlazoReapertura(ahora)) {
                try {
                    reclamo.cerrar(ahora);
                    cerrados++;
                } catch (TransicionInvalidaException e) {
                    System.out.println("No se pudo cerrar el reclamo " + reclamo.getNumero() + ": " + e.getMessage());
                }
            }
        }
        return cerrados;
    }

    public List<Reclamo> escalarReclamos(LocalDateTime ahora) {
        List<Reclamo> escalados = new ArrayList<>();
        for (Reclamo reclamo : repositorio.getReclamos()) {
            if (reclamo.requiereEscalamiento(ahora)) {
                reclamo.marcarEscalado();
                notificador.notificar(ahora, "Administrador", "Reclamo " + reclamo.getNumero()
                        + " de prioridad ALTA sin atender hace mas de " + Reclamo.HORAS_PARA_ESCALAR + " horas.");
                escalados.add(reclamo);
            }
        }
        return escalados;
    }
}
