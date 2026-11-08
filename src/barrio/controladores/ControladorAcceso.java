package barrio.controladores;

import barrio.excepciones.AccesoDenegadoException;
import barrio.excepciones.IngresoAbiertoException;
import barrio.excepciones.OperacionInvalidaException;
import barrio.excepciones.PersonaNoAutorizadaException;
import barrio.modelo.lotes.Lote;
import barrio.modelo.personas.Persona;
import barrio.modelo.visitas.Autorizacion;
import barrio.modelo.visitas.AutorizacionRecurrente;
import barrio.modelo.visitas.RegistroIngreso;
import barrio.persistencia.RepositorioDatos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ControladorAcceso {
    private final RepositorioDatos repositorio;

    public ControladorAcceso(RepositorioDatos repositorio) {
        this.repositorio = repositorio;
    }

    public Autorizacion autorizarVisita(Lote lote, Persona autorizante, String nombre, String documento,
                                        LocalDate fecha, LocalTime desde, LocalTime hasta)
            throws PersonaNoAutorizadaException {
        validarAutorizante(lote, autorizante);
        Autorizacion autorizacion = new Autorizacion(repositorio.siguienteNumero("autorizacion"), nombre, documento,
                lote, autorizante, fecha, desde, hasta);
        repositorio.guardar(autorizacion);
        return autorizacion;
    }

    public Autorizacion autorizarProveedor(Lote lote, Persona autorizante, String nombre, String documento,
                                           String empresa, LocalDate desdeFecha, LocalDate hastaFecha,
                                           LocalTime desde, LocalTime hasta)
            throws PersonaNoAutorizadaException, OperacionInvalidaException {
        validarAutorizante(lote, autorizante);
        long dias = AutorizacionRecurrente.calcularDias(desdeFecha, hastaFecha);
        if (dias > AutorizacionRecurrente.MAXIMO_DIAS) {
            throw new OperacionInvalidaException("Una autorizacion recurrente puede durar hasta "
                    + AutorizacionRecurrente.MAXIMO_DIAS + " dias (se pidieron " + dias + ").");
        }
        Autorizacion autorizacion = new AutorizacionRecurrente(repositorio.siguienteNumero("autorizacion"), nombre,
                documento, empresa, lote, autorizante, desdeFecha, hastaFecha, desde, hasta);
        repositorio.guardar(autorizacion);
        return autorizacion;
    }

    private void validarAutorizante(Lote lote, Persona autorizante) throws PersonaNoAutorizadaException {
        if (lote == null || autorizante == null || lote.buscarHabitante(autorizante.getDocumento()) != autorizante) {
            throw new PersonaNoAutorizadaException("Solo el titular o un residente del lote puede autorizar visitas.");
        }
    }

    public RegistroIngreso registrarIngreso(String documento, int numeroLote, LocalDateTime fechaHora)
            throws AccesoDenegadoException, IngresoAbiertoException {
        Autorizacion autorizacion = buscarAutorizacionVigente(documento, numeroLote, fechaHora);
        if (autorizacion == null) {
            throw new AccesoDenegadoException("Acceso denegado: no hay autorizacion vigente para el documento "
                    + documento + " en el lote " + numeroLote + ".");
        }
        if (tieneIngresoAbierto(documento)) {
            throw new IngresoAbiertoException("La persona con documento " + documento + " ya tiene un ingreso sin egreso.");
        }
        RegistroIngreso ingreso = new RegistroIngreso(autorizacion, fechaHora);
        repositorio.guardar(ingreso);
        return ingreso;
    }

    public RegistroIngreso registrarEgreso(String documento, LocalDateTime fechaHora) throws OperacionInvalidaException {
        RegistroIngreso abierto = buscarIngresoAbierto(documento);
        if (abierto == null) {
            throw new OperacionInvalidaException("No hay un ingreso abierto para el documento " + documento + ".");
        }
        abierto.registrarEgreso(fechaHora);
        return abierto;
    }

    public Autorizacion buscarAutorizacionVigente(String documento, int numeroLote, LocalDateTime fechaHora) {
        for (Autorizacion autorizacion : repositorio.getAutorizaciones()) {
            if (autorizacion.corresponde(documento, numeroLote) && autorizacion.esVigente(fechaHora)) {
                return autorizacion;
            }
        }
        return null;
    }

    public boolean tieneIngresoAbierto(String documento) {
        return buscarIngresoAbierto(documento) != null;
    }

    private RegistroIngreso buscarIngresoAbierto(String documento) {
        for (RegistroIngreso ingreso : repositorio.getIngresos()) {
            if (ingreso.estaAbierto() && ingreso.getDocumento().equalsIgnoreCase(documento.trim())) {
                return ingreso;
            }
        }
        return null;
    }

    public List<RegistroIngreso> personasDentro() {
        List<RegistroIngreso> dentro = new ArrayList<>();
        for (RegistroIngreso ingreso : repositorio.getIngresos()) {
            if (ingreso.estaAbierto()) {
                dentro.add(ingreso);
            }
        }
        return dentro;
    }
}
