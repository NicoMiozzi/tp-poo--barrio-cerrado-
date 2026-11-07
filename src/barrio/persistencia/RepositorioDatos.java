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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class RepositorioDatos {
    private DatosBarrio datos;
    private final String rutaArchivo;

    public RepositorioDatos() {
        this(null);
    }

    public RepositorioDatos(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
        this.datos = new DatosBarrio();
    }

    public boolean existeArchivo() {
        return rutaArchivo != null && new File(rutaArchivo).exists();
    }

    public void guardarEnArchivo() throws IOException {
        if (rutaArchivo == null) {
            return;
        }
        File archivo = new File(rutaArchivo);
        if (archivo.getParentFile() != null) {
            archivo.getParentFile().mkdirs();
        }
        ObjectOutputStream salida = null;
        try {
            salida = new ObjectOutputStream(new FileOutputStream(archivo));
            salida.writeObject(datos);
        } finally {
            if (salida != null) {
                salida.close();
            }
        }
    }

    public void cargarDesdeArchivo() throws IOException {
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(rutaArchivo))) {
            datos = (DatosBarrio) entrada.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("El archivo " + rutaArchivo + " no tiene el formato esperado.");
        } catch (ClassCastException e) {
            throw new IOException("El archivo " + rutaArchivo + " no tiene el formato esperado.");
        }
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public int siguienteNumero(String tipo) {
        int numero = 1;
        if (datos.contadores.containsKey(tipo)) {
            numero = datos.contadores.get(tipo) + 1;
        }
        datos.contadores.put(tipo, numero);
        return numero;
    }

    public boolean guardar(Reclamo reclamo) {
        return datos.reclamos.add(reclamo);
    }

    public boolean guardar(RegistroIngreso ingreso) {
        return datos.ingresos.add(ingreso);
    }

    public boolean guardar(Autorizacion autorizacion) {
        return datos.autorizaciones.add(autorizacion);
    }

    public boolean guardar(OrdenMantenimiento orden) {
        return datos.ordenes.add(orden);
    }

    public boolean guardar(Pago pago) {
        return datos.pagos.add(pago);
    }

    public boolean guardar(AsientoAjuste ajuste) {
        return datos.ajustes.add(ajuste);
    }

    public boolean guardar(Notificacion notificacion) {
        return datos.notificaciones.add(notificacion);
    }

    public void agregarLote(Lote lote) {
        if (datos.lotes.containsKey(lote.getNumero())) {
            throw new IllegalArgumentException("Ya existe el lote " + lote.getNumero());
        }
        datos.lotes.put(lote.getNumero(), lote);
    }

    public void agregarPersonal(Personal empleado) {
        if (buscarPersonal(empleado.getLegajo()) != null) {
            throw new IllegalArgumentException("Ya existe el legajo " + empleado.getLegajo());
        }
        datos.personal.add(empleado);
    }

    public void agregarEquipo(Equipo equipo) {
        if (datos.equipos.containsKey(equipo.getCodigo())) {
            throw new IllegalArgumentException("Ya existe el equipo " + equipo.getCodigo());
        }
        datos.equipos.put(equipo.getCodigo(), equipo);
    }

    public void guardarPresupuesto(YearMonth periodo, double presupuesto) {
        datos.presupuestos.put(periodo.toString(), presupuesto);
    }

    public Lote buscarLote(int numero) {
        return datos.lotes.get(numero);
    }

    public Equipo buscarEquipo(String codigo) {
        return codigo == null ? null : datos.equipos.get(codigo.trim().toUpperCase());
    }

    public Personal buscarPersonal(int legajo) {
        for (Personal empleado : datos.personal) {
            if (empleado.getLegajo() == legajo) {
                return empleado;
            }
        }
        return null;
    }

    public Reclamo buscarReclamo(int numero) {
        for (Reclamo reclamo : datos.reclamos) {
            if (reclamo.getNumero() == numero) {
                return reclamo;
            }
        }
        return null;
    }

    public OrdenMantenimiento buscarOrden(int numero) {
        for (OrdenMantenimiento orden : datos.ordenes) {
            if (orden.getNumero() == numero) {
                return orden;
            }
        }
        return null;
    }

    public Pago buscarPago(int numero) {
        for (Pago pago : datos.pagos) {
            if (pago.getNumero() == numero) {
                return pago;
            }
        }
        return null;
    }

    public Double buscarPresupuesto(YearMonth periodo) {
        return datos.presupuestos.get(periodo.toString());
    }

    public List<Lote> getLotes() {
        return new ArrayList<>(datos.lotes.values());
    }

    public List<Personal> getPersonal() {
        return new ArrayList<>(datos.personal);
    }

    public List<Reclamo> getReclamos() {
        return new ArrayList<>(datos.reclamos);
    }

    public List<Pago> getPagos() {
        return new ArrayList<>(datos.pagos);
    }

    public List<AsientoAjuste> getAjustes() {
        return new ArrayList<>(datos.ajustes);
    }

    public List<Equipo> getEquipos() {
        return new ArrayList<>(datos.equipos.values());
    }

    public List<OrdenMantenimiento> getOrdenes() {
        return new ArrayList<>(datos.ordenes);
    }

    public List<Autorizacion> getAutorizaciones() {
        return new ArrayList<>(datos.autorizaciones);
    }

    public List<RegistroIngreso> getIngresos() {
        return new ArrayList<>(datos.ingresos);
    }

    public List<Notificacion> getNotificaciones() {
        return new ArrayList<>(datos.notificaciones);
    }
}
