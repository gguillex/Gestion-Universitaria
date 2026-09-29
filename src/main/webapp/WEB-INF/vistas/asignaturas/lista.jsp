<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Asignaturas</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>

    <h2>Asignaturas</h2>

    <p class="toolbar">
        <a href="${pageContext.request.contextPath}/control?idAccion=formAsignatura">Nueva asignatura</a>
        <a href="${pageContext.request.contextPath}/control?idAccion=listarAsignaturas">Refrescar</a>
        <a href="${pageContext.request.contextPath}/control?idAccion=asignarProfesor">Asignar profesor</a>
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
            <th>Capacidad</th>
            <th>Titulación</th>
            <th>Profesor</th>
            <th>Acciones</th>
        </tr>
        <c:forEach var="a" items="${asignaturas}">
            <tr>
                <td>${a.id}</td>
                <td><c:out value="${a.nombre}"/></td>
                <td>${a.capacidadMaxima}</td>
                <td><c:out value="${a.nombreTitulacion}"/></td>
                <td><c:out value="${a.nombreProfesor}" default="—"/></td>
                <td>
                    <a href="${pageContext.request.contextPath}/control?idAccion=matriculadosAsignatura&idAsignatura=${a.id}">Ver alumnos</a>
                    <a href="${pageContext.request.contextPath}/control?idAccion=formAsignatura&id=${a.id}">Editar</a>
                    <form class="form-borrar" action="${pageContext.request.contextPath}/control" method="post"
                          onsubmit="return confirm('¿Eliminar asignatura?')">
                        <input type="hidden" name="idAccion" value="eliminarAsignatura">
                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                        <input type="hidden" name="id" value="${a.id}">
                        <button type="submit">Eliminar</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty asignaturas}">
            <tr><td colspan="6"><em>No hay asignaturas registradas</em></td></tr>
        </c:if>
    </table>

</body>
</html>
