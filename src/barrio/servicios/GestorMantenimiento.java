package barrio.servicios;

import barrio.excepciones.OperacionInvalidaException;
import barrio.excepciones.OrdenIncompletaException;
import barrio.modelo.equipos.Camara;
import barrio.modelo.equipos.EstadoEquipo;
import barrio.modelo.equipos.Equipo;
import barrio.modelo.equipos.Falla;
import barrio.modelo.equipos.OrdenMantenimiento;
import barrio.modelo.equipos.PrioridadOrden;
import barrio.modelo.personas.EmpleadoMantenimiento;
import barrio.persistencia.RepositorioDatos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GestorMantenimiento {
    private final RepositorioDatos repositorio;
    private final Notificador notificador;

    public GestorMantenimiento(RepositorioDatos repositorio, Notificador notificador) {
        this.repositorio = repositorio;
        this.notificador = notificador;
    }

    public Equipo buscarEquipo(String codigo) throws OperacionInvalidaException {
        Equipo equipo = repositorio.buscarEquipo(codigo);
        if (equipo == null) {
            throw new OperacionInvalidaException("No existe el equipo " + codigo + ".");
        }
        return equipo;
    }

    public void registrarFalla(String codigo, String descripcion, String reportadaPor, LocalDateTime fecha)
            throws OperacionInvalidaException {
        Equipo equipo = buscarEquipo(codigo);
        if (equipo.getEstado() == EstadoEquipo.FUERA_DE_SERVICIO) {
            throw new OperacionInvalidaException("El equipo " + codigo + " ya esta fuera de servicio.");
        }
        equipo.registrarFalla(new Falla(fecha, descripcion, reportadaPor));
    }

    public OrdenMantenimiento generarOrden(String codigo, EmpleadoMantenimiento tecnico, String descripcion,
                                           LocalDateTime fecha) throws OperacionInvalidaException {
        Equipo equipo = buscarEquipo(codigo);
        if (equipo.estaFuncionando()) {
            throw new OperacionInvalidaException("El equipo " + codigo + " esta operativo: primero hay que registrar la falla.");
        }
        for (OrdenMantenimiento orden : equipo.getOrdenes()) {
            if (orden.estaAbierta()) {
                throw new OperacionInvalidaException("El equipo ya tiene abierta la orden " + orden.getNumero() + ".");
            }
        }
        PrioridadOrden prioridad = equipo.calcularPrioridadFalla(repositorio.getEquipos());
        OrdenMantenimiento orden = new OrdenMantenimiento(repositorio.siguienteNumero("orden"), equipo, tecnico,
                fecha, prioridad, descripcion);
        equipo.agregarOrden(orden);
        repositorio.guardar(orden);
        return orden;
    }

    public void cerrarOrden(int numero, LocalDateTime fecha, EmpleadoMantenimiento tecnico, String reparacion)
            throws OperacionInvalidaException, OrdenIncompletaException {
        OrdenMantenimiento orden = repositorio.buscarOrden(numero);
        if (orden == null) {
            throw new OperacionInvalidaException("No existe la orden " + numero + ".");
        }
        orden.cerrarOrden(fecha, tecnico, reparacion);
    }

    public void marcarFueraDeServicio(String codigo, LocalDateTime fecha) throws OperacionInvalidaException {
        buscarEquipo(codigo).marcarFueraDeServicio(fecha);
    }

    public List<Camara> verificarCamaras(LocalDateTime ahora) {
        List<Camara> informadas = new ArrayList<>();
        for (Equipo equipo : repositorio.getEquipos()) {
            if (equipo instanceof Camara) {
                Camara camara = (Camara) equipo;
                if (camara.debeInformarseAlAdministrador(ahora)) {
                    notificador.notificar(ahora, "Administrador", "La camara " + camara.getCodigo() + " ("
                            + camara.getSector() + ") lleva " + camara.horasSinFuncionar(ahora) + " horas sin funcionar.");
                    camara.marcarInformadoAdministrador();
                    informadas.add(camara);
                }
            }
        }
        return informadas;
    }
}
