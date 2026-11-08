package barrio.ui;

import barrio.excepciones.OperacionInvalidaException;
import barrio.excepciones.PersonaNoAutorizadaException;
import barrio.modelo.lotes.Lote;
import barrio.modelo.personas.EmpleadoMantenimiento;
import barrio.modelo.personas.Persona;
import barrio.modelo.personas.Personal;
import barrio.persistencia.RepositorioDatos;

class Busquedas {
    private Busquedas() {
    }

    static Lote lote(RepositorioDatos repositorio, Consola consola) throws OperacionInvalidaException {
        int numero = consola.leerEntero("Numero de lote: ");
        Lote lote = repositorio.buscarLote(numero);
        if (lote == null) {
            throw new OperacionInvalidaException("No existe el lote " + numero + ".");
        }
        return lote;
    }

    static Persona habitante(Lote lote, Consola consola) throws PersonaNoAutorizadaException {
        String documento = consola.leerTexto("Documento de la persona: ");
        Persona persona = lote.buscarHabitante(documento);
        if (persona == null) {
            throw new PersonaNoAutorizadaException("El documento " + documento + " no corresponde a nadie del lote "
                    + lote.getNumero() + ".");
        }
        return persona;
    }

    static Personal personal(RepositorioDatos repositorio, Consola consola) throws OperacionInvalidaException {
        int legajo = consola.leerEntero("Legajo del empleado: ");
        Personal empleado = repositorio.buscarPersonal(legajo);
        if (empleado == null) {
            throw new OperacionInvalidaException("No existe el legajo " + legajo + ".");
        }
        return empleado;
    }

    static EmpleadoMantenimiento tecnico(RepositorioDatos repositorio, Consola consola) throws OperacionInvalidaException {
        Personal empleado = personal(repositorio, consola);
        if (!(empleado instanceof EmpleadoMantenimiento)) {
            throw new OperacionInvalidaException(empleado.getNombre() + " no es empleado de mantenimiento.");
        }
        return (EmpleadoMantenimiento) empleado;
    }
}
