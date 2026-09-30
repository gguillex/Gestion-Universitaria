package gestion.rest.service;

import gestion.bean.Titulacion;
import gestion.modelo.TitulacionDAO;
import gestion.rest.exception.ApiException;
import gestion.rest.exception.BadRequestException;
import gestion.rest.exception.ConflictException;
import gestion.rest.exception.NotFoundException;
import java.util.List;

/**
 * Lógica de negocio de titulaciones para el servicio REST. Usa el mismo
 * {@link TitulacionDAO} que la interfaz MVC, así que ambas ven los mismos datos.
 * No conoce HTTP ni JSON: recibe y devuelve beans y lanza {@link ApiException}.
 */
public class TitulacionService {

    private final TitulacionDAO dao = new TitulacionDAO();

    public List<Titulacion> listar() throws ApiException {
        try {
            return dao.listar();
        } catch (Exception e) {
            throw ApiException.interno("Error al listar las titulaciones", e);
        }
    }

    public Titulacion obtenerPorId(int id) throws ApiException {
        Titulacion t;
        try {
            t = dao.buscarPorId(id);
        } catch (Exception e) {
            throw ApiException.interno("Error al obtener la titulación", e);
        }
        if (t == null) throw new NotFoundException("No existe titulación con id " + id);
        return t;
    }

    public Titulacion alta(Titulacion t) throws ApiException {
        validar(t);
        try {
            dao.insertar(t);            // el DAO rellena el id generado
            return t;
        } catch (Exception e) {
            throw ApiException.interno("Error al dar de alta la titulación", e);
        }
    }

    public Titulacion modificar(Titulacion t) throws ApiException {
        validar(t);
        obtenerPorId(t.getId());        // 404 si no existe
        try {
            dao.actualizar(t);
            return t;
        } catch (Exception e) {
            throw ApiException.interno("Error al modificar la titulación", e);
        }
    }

    public void eliminar(int id) throws ApiException {
        obtenerPorId(id);               // 404 si no existe
        try {
            if (dao.tieneAsignaturas(id))
                throw new ConflictException("No se puede eliminar la titulación: tiene asignaturas asociadas");
            dao.eliminar(id);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw ApiException.interno("Error al eliminar la titulación", e);
        }
    }

    private void validar(Titulacion t) throws BadRequestException {
        Validacion.obligatorio(t.getNombre(), "El nombre de la titulación");
        Validacion.maximo(t.getDescripcion(), "La descripción", Validacion.MAX_DESCRIPCION);
    }
}
