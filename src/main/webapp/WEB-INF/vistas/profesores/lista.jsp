<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Profesores</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>

    <h2>Profesores</h2>

    <p class="toolbar">
        <a href="${pageContext.request.contextPath}/control?idAccion=formProfesor">Nuevo profesor</a>
        <a href="${pageContext.request.contextPath}/control?idAccion=listarProfesores">Refrescar</a>
        <a href="${pageContext.request.contextPath}/control?idAccion=asignarProfesor">Asignar a asignatura</a>
    </p>

    <c:if test="${not empty mensaje}">
        <p class="ok"><c:out value="${mensaje}"/></p>
    </c:if>
    <c:if test="${not empty error}">
        <p class="error"><c:out value="${error}"/></p>
    </c:if>

    <table>
        <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Email</th>
            <th>Acciones</th>
        </tr>
        <c:forEach var="p" items="${profesores}">
            <tr>
                <td>${p.id}</td>
                <td><c:out value="${p.nombre}"/></td>
                <td><c:out value="${p.email}"/></td>
                <td>
                    <a href="${pageContext.request.contextPath}/control?idAccion=formProfesor&id=${p.id}">Editar</a>
                    <form class="form-borrar" action="${pageContext.request.contextPath}/control" method="post"
                          onsubmit="return confirm('¿Eliminar profesor? Se desasignará de sus asignaturas.')">
                        <input type="hidden" name="idAccion" value="eliminarProfesor">
                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                        <input type="hidden" name="id" value="${p.id}">
                        <button type="submit">Eliminar</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty profesores}">
            <tr><td colspan="4"><em>No hay profesores registrados</em></td></tr>
        </c:if>
    </table>

</body>
</html>
