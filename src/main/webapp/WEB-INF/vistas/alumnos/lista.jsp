<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Alumnos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>

    <h2>Alumnos</h2>

    <p class="toolbar">
        <a href="${pageContext.request.contextPath}/control?idAccion=formAlumno">Nuevo alumno</a>
        <a href="${pageContext.request.contextPath}/control?idAccion=matricular">Matricular alumno</a>
        <a href="${pageContext.request.contextPath}/control?idAccion=listarAlumnos">Refrescar</a>
    </p>

    <c:if test="${not empty mensaje}"><p class="ok"><c:out value="${mensaje}"/></p></c:if>
    <c:if test="${not empty error}"><p class="error"><c:out value="${error}"/></p></c:if>

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
                <a href="${pageContext.request.contextPath}/control?idAccion=formAlumno&id=${a.id}">Editar</a>
                <form class="form-borrar" action="${pageContext.request.contextPath}/control" method="post"
                      onsubmit="return confirm('¿Eliminar alumno y todas sus matrículas?')">
                    <input type="hidden" name="idAccion" value="eliminarAlumno">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="id" value="${a.id}">
                    <button type="submit">Eliminar</button>
                </form>
            </td>
        </tr>
        </c:forEach>
        <c:if test="${empty alumnos}">
            <tr><td colspan="5"><em>No hay alumnos registrados</em></td></tr>
        </c:if>
    </table>

</body>
</html>
