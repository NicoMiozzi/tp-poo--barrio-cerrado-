package barrio.persistencia;

import barrio.modelo.equipos.Equipo;
import barrio.modelo.equipos.OrdenMantenimiento;
import barrio.modelo.expensas.AsientoAjuste;
import barrio.modelo.expensas.Pago;
import barrio.modelo.lotes.Lote;
import barrio.modelo.personas.Personal;
import barrio.modelo.reclamos.Reclamo;
import barrio.modelo.visitas.Autorizacion;
import barrio.modelo.visitas.RegistroIngreso;
import barrio.servicios.Notificacion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class DatosBarrio implements Serializable {
    private static final long serialVersionUID = 1L;

    Map<Integer, Lote> lotes = new TreeMap<>();
    List<Personal> personal = new ArrayList<>();
    List<Reclamo> reclamos = new ArrayList<>();
    List<Pago> pagos = new ArrayList<>();
    List<AsientoAjuste> ajustes = new ArrayList<>();
    Map<String, Equipo> equipos = new TreeMap<>();
    List<OrdenMantenimiento> ordenes = new ArrayList<>();
    List<Autorizacion> autorizaciones = new ArrayList<>();
    List<RegistroIngreso> ingresos = new ArrayList<>();
    List<Notificacion> notificaciones = new ArrayList<>();
    Map<String, Double> presupuestos = new HashMap<>();
    Map<String, Integer> contadores = new HashMap<>();
}
