<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Alumnos matriculados</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>

    <h2>Alumnos matriculados en: <c:out value="${asignatura.nombre}"/></h2>

    <p class="info">
        Ocupación: ${ocupacion} / ${asignatura.capacidadMaxima} plazas
    </p>

    <p class="toolbar">
        <a href="${pageContext.request.contextPath}/control?idAccion=matricular&idAsignatura=${asignatura.id}">
            Matricular alumno
        </a>
        <a href="${pageContext.request.contextPath}/control?idAccion=matriculadosAsignatura&idAsignatura=${asignatura.id}&csv=true">
            Exportar CSV
        </a>
        <a href="${pageContext.request.contextPath}/control?idAccion=listarAsignaturas">Volver a asignaturas</a>
    </p>

    <table>
        <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Email</th>
            <th>DNI</th>
            <th>Acciones</th>
        </tr>
        <c:forEach var="a" items="${alumnos}">
        <tr>
            <td>${a.id}</td>
            <td><c:out value="${a.nombre}"/></td>
            <td><c:out value="${a.email}"/></td>
            <td><c:out value="${a.dni}"/></td>
            <td>
                <form class="form-borrar" action="${pageContext.request.contextPath}/control" method="post"
                      onsubmit="return confirm('¿Desmatricular a este alumno?')">
                    <input type="hidden" name="idAccion" value="desmatricular">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="idAlumno" value="${a.id}">
                    <input type="hidden" name="idAsignatura" value="${asignatura.id}">
                    <button type="submit">Desmatricular</button>
                </form>
            </td>
        </tr>
        </c:forEach>
        <c:if test="${empty alumnos}">
            <tr><td colspan="5"><em>No hay alumnos matriculados en esta asignatura</em></td></tr>
        </c:if>
    </table>

</body>
</html>
