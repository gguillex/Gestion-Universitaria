<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Alumnos · Gestión Universitaria</title>
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

    <c:if test="${not empty mensaje}"><p class="ok" role="status"><c:out value="${mensaje}"/></p></c:if>
    <c:if test="${not empty error}"><p class="error" role="alert"><c:out value="${error}"/></p></c:if>

    <table class="tabla-placas">
        <tr>
            <th scope="col">ID</th>
            <th scope="col">Nombre</th>
            <th scope="col">Email</th>
            <th scope="col">DNI</th>
            <th scope="col">Acciones</th>
        </tr>
        <c:forEach var="a" items="${alumnos}">
        <tr>
            <td class="celda-id" data-label="ID"><span class="placa">${a.id}</span></td>
            <td class="celda-principal" data-label="Nombre"><c:out value="${a.nombre}"/></td>
            <td data-label="Email"><c:out value="${a.email}"/></td>
            <td data-label="DNI"><c:out value="${a.dni}"/></td>
            <td class="celda-acciones">
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
            <tr class="fila-vacia">
                <td colspan="5" class="vacio">
                    <strong class="vacio-titulo">Sin alumnos</strong>
                    <p>No hay alumnos registrados</p>
                    <a href="${pageContext.request.contextPath}/control?idAccion=formAlumno">Nuevo alumno</a>
                </td>
            </tr>
        </c:if>
    </table>

</body>
</html>
