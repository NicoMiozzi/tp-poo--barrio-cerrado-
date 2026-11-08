package barrio.controladores;

import barrio.excepciones.LimiteReclamosException;
import barrio.excepciones.OperacionInvalidaException;
import barrio.excepciones.PersonaNoAutorizadaException;
import barrio.excepciones.TransicionInvalidaException;
import barrio.modelo.lotes.Lote;
import barrio.modelo.personas.Persona;
import barrio.modelo.personas.Personal;
import barrio.modelo.reclamos.EstadoReclamo;
import barrio.modelo.reclamos.Reclamo;
import barrio.modelo.reclamos.TipoReclamo;
import barrio.persistencia.RepositorioDatos;
import barrio.servicios.GestorReclamos;

import java.time.LocalDateTime;

public class ReclamoController {
    private final RepositorioDatos repositorio;
    private final GestorReclamos gestor;

    public ReclamoController(RepositorioDatos repositorio, GestorReclamos gestor) {
        this.repositorio = repositorio;
        this.gestor = gestor;
    }

    public Reclamo presentarReclamo(Lote lote, Persona persona, TipoReclamo tipo, String descripcion,
                                    LocalDateTime fecha) throws PersonaNoAutorizadaException {
        if (!lote.esTitularOAutorizado(persona)) {
            throw new PersonaNoAutorizadaException("Solo el titular o una persona autorizada puede presentar reclamos.");
        }
        Reclamo reclamo = new Reclamo(repositorio.siguienteNumero("reclamo"), tipo, descripcion, lote, persona, fecha);
        repositorio.guardar(reclamo);
        return reclamo;
    }

    public void asignarReclamo(int numero, Personal empleado, LocalDateTime fecha)
            throws OperacionInvalidaException, TransicionInvalidaException {
        gestor.asignarReclamo(numero, empleado, fecha);
    }

    public void cambiarEstado(int numero, EstadoReclamo nuevo, LocalDateTime fecha, String observacion)
            throws OperacionInvalidaException, TransicionInvalidaException, LimiteReclamosException {
        gestor.cambiarEstado(numero, nuevo, fecha, observacion);
    }

    public void reabrirReclamo(int numero, LocalDateTime fecha, String observacion)
            throws OperacionInvalidaException, TransicionInvalidaException {
        gestor.reabrir(numero, fecha, observacion);
    }

    public void comentar(int numero, String autor, String texto, LocalDateTime fecha) throws OperacionInvalidaException {
        gestor.buscarReclamo(numero).agregarComentario(fecha, autor, texto);
    }

    public Reclamo consultar(int numero) throws OperacionInvalidaException {
        return gestor.buscarReclamo(numero);
    }
}
