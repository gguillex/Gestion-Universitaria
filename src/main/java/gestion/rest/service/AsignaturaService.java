package gestion.rest.service;

import gestion.bean.Asignatura;
import gestion.modelo.AlumnoDAO;
import gestion.modelo.AsignaturaDAO;
import gestion.modelo.ProfesorDAO;
import gestion.modelo.TitulacionDAO;
import gestion.rest.exception.ApiException;
import gestion.rest.exception.BadRequestException;
import gestion.rest.exception.ConflictException;
import gestion.rest.exception.NotFoundException;
import java.util.List;

/**
 * Lógica de negocio de asignaturas para el servicio REST. Aplica las mismas reglas que
 * la interfaz MVC: la titulación y el profesor deben existir, no se puede bajar la
 * capacidad por debajo de los alumnos matriculados ni borrar una asignatura con alumnos.
 */
public class AsignaturaService {

    private final AsignaturaDAO   dao           = new AsignaturaDAO();
    private final TitulacionDAO   titulacionDAO = new TitulacionDAO();
    private final ProfesorDAO     profesorDAO   = new ProfesorDAO();
    private final AlumnoDAO       alumnoDAO     = new AlumnoDAO();

    public List<Asignatura> listar() throws ApiException {
        try {
            return dao.listar();
        } catch (Exception e) {
            throw ApiException.interno("Error al listar las asignaturas", e);
        }
    }

    public Asignatura obtenerPorId(int id) throws ApiException {
        Asignatura a;
        try {
            a = dao.buscarPorId(id);
        } catch (Exception e) {
            throw ApiException.interno("Error al obtener la asignatura", e);
        }
        if (a == null) throw new NotFoundException("No existe asignatura con id " + id);
        return a;
    }

    public Asignatura alta(Asignatura a) throws ApiException {
        validar(a);
        comprobarReferencias(a.getIdTitulacion(), a.getIdProfesor());
        try {
            dao.insertar(a);            // el DAO rellena el id generado
        } catch (Exception e) {
            throw ApiException.interno("Error al dar de alta la asignatura", e);
        }
        try {
            return obtenerPorId(a.getId()); // releída, con los nombres de titulación y profesor
        } catch (ApiException e) {
            return a;   // ya está creada: no se devuelve un error que invite a reintentar el alta
        }
    }

    public Asignatura modificar(Asignatura a) throws ApiException {
        validar(a);
        obtenerPorId(a.getId());        // 404 si no existe
        comprobarReferencias(a.getIdTitulacion(), a.getIdProfesor());
        try {
            int matriculados = alumnoDAO.contarMatriculados(a.getId());
            if (a.getCapacidadMaxima() < matriculados)
                throw new ConflictException("No se puede fijar la capacidad en " + a.getCapacidadMaxima()
                        + ": la asignatura ya tiene " + matriculados + " alumno(s) matriculado(s)");
            dao.actualizar(a);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw ApiException.interno("Error al modificar la asignatura", e);
        }
        return obtenerPorId(a.getId());
    }

    public void eliminar(int id) throws ApiException {
        obtenerPorId(id);               // 404 si no existe
        try {
            int matriculados = alumnoDAO.contarMatriculados(id);
            if (matriculados > 0)
                throw new ConflictException("No se puede eliminar: la asignatura tiene " + matriculados
                        + " alumno(s) matriculado(s)");
            dao.eliminar(id);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw ApiException.interno("Error al eliminar la asignatura", e);
        }
    }

    /** Asigna un profesor a una asignatura, o lo quita si {@code idProfesor} es null. */
    public Asignatura asignarProfesor(int idAsignatura, Integer idProfesor) throws ApiException {
        obtenerPorId(idAsignatura);     // 404 si la asignatura no existe
        if (idProfesor != null) comprobarProfesor(idProfesor);
        try {
            dao.asignarProfesor(idAsignatura, idProfesor);
        } catch (Exception e) {
            throw ApiException.interno("Error al asignar el profesor", e);
        }
        return obtenerPorId(idAsignatura);
    }

    // ------------------------------------------------------------------ helpers

    private void validar(Asignatura a) throws BadRequestException {
        Validacion.obligatorio(a.getNombre(), "El nombre de la asignatura");
        if (a.getCapacidadMaxima() <= 0)
            throw new BadRequestException("La capacidad máxima debe ser mayor que 0");
        if (a.getIdTitulacion() <= 0)
            throw new BadRequestException("La asignatura debe pertenecer a una titulación");
    }

    private void comprobarReferencias(int idTitulacion, Integer idProfesor) throws ApiException {
        try {
            if (titulacionDAO.buscarPorId(idTitulacion) == null)
                throw new NotFoundException("No existe titulación con id " + idTitulacion);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw ApiException.interno("Error al comprobar la titulación", e);
        }
        if (idProfesor != null) comprobarProfesor(idProfesor);
    }

    private void comprobarProfesor(int idProfesor) throws ApiException {
        try {
            if (profesorDAO.buscarPorId(idProfesor) == null)
                throw new NotFoundException("No existe profesor con id " + idProfesor);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw ApiException.interno("Error al comprobar el profesor", e);
        }
    }
}
