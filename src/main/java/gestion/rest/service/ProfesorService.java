package gestion.rest.service;

import gestion.bean.Profesor;
import gestion.modelo.ProfesorDAO;
import gestion.rest.exception.ApiException;
import gestion.rest.exception.BadRequestException;
import gestion.rest.exception.NotFoundException;
import java.util.List;

/** Lógica de negocio de profesores para el servicio REST (mismo {@link ProfesorDAO} que el MVC). */
public class ProfesorService {

    private final ProfesorDAO dao = new ProfesorDAO();

    public List<Profesor> listar() throws ApiException {
        try {
            return dao.listar();
        } catch (Exception e) {
            throw ApiException.interno("Error al listar los profesores", e);
        }
    }

    public Profesor obtenerPorId(int id) throws ApiException {
        Profesor p;
        try {
            p = dao.buscarPorId(id);
        } catch (Exception e) {
            throw ApiException.interno("Error al obtener el profesor", e);
        }
        if (p == null) throw new NotFoundException("No existe profesor con id " + id);
        return p;
    }

    public Profesor alta(Profesor p) throws ApiException {
        validar(p);
        try {
            dao.insertar(p);            // el DAO rellena el id generado
            return p;
        } catch (Exception e) {
            throw ApiException.interno("Error al dar de alta el profesor", e);
        }
    }

    public Profesor modificar(Profesor p) throws ApiException {
        validar(p);
        obtenerPorId(p.getId());        // 404 si no existe
        try {
            dao.actualizar(p);
            return p;
        } catch (Exception e) {
            throw ApiException.interno("Error al modificar el profesor", e);
        }
    }

    public void eliminar(int id) throws ApiException {
        obtenerPorId(id);               // 404 si no existe
        try {
            // El DAO desvincula sus asignaturas y lo borra en una sola transacción
            dao.eliminar(id);
        } catch (Exception e) {
            throw ApiException.interno("Error al eliminar el profesor", e);
        }
    }

    private void validar(Profesor p) throws BadRequestException {
        Validacion.obligatorio(p.getNombre(), "El nombre del profesor");
        Validacion.maximo(p.getEmail(), "El email");
    }
}
