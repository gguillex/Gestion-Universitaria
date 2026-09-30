<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Alumnos · Gestión Universitaria</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/css/favicon.svg">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>
    <main id="contenido" tabindex="-1">

    <h1>Alumnos</h1>

    <p class="toolbar">
        <a href="${pageContext.request.contextPath}/control?idAccion=formAlumno">Nuevo alumno</a>
        <a href="${pageContext.request.contextPath}/control?idAccion=matricular">Matricular alumno</a>
        <a href="${pageContext.request.contextPath}/control?idAccion=listarAlumnos">Refrescar</a>
    </p>

    <c:if test="${not empty mensaje}"><p class="ok" role="status"><c:out value="${mensaje}"/></p></c:if>
    <c:if test="${not empty error}"><p class="error" role="alert"><c:out value="${error}"/></p></c:if>

    <table class="tabla-placas" role="table">
        <tr role="row">
            <th role="columnheader" scope="col">ID</th>
            <th role="columnheader" scope="col">Nombre</th>
            <th role="columnheader" scope="col">Email</th>
            <th role="columnheader" scope="col">DNI</th>
            <th role="columnheader" scope="col">Acciones</th>
        </tr>
        <c:forEach var="a" items="${alumnos}">
        <tr role="row">
            <td role="cell" class="celda-id" data-label="ID"><span class="placa">${a.id}</span></td>
            <td role="cell" class="celda-principal" data-label="Nombre"><c:out value="${a.nombre}"/></td>
            <td role="cell" data-label="Email"><c:out value="${a.email}"/></td>
            <td role="cell" data-label="DNI"><c:out value="${a.dni}"/></td>
            <td role="cell" class="celda-acciones">
                <a href="${pageContext.request.contextPath}/control?idAccion=formAlumno&id=${a.id}" aria-label="Editar alumno <c:out value='${a.nombre}'/>">Editar</a>
                <form class="form-borrar" action="${pageContext.request.contextPath}/control" method="post"
                      onsubmit="return confirm('¿Eliminar alumno y todas sus matrículas?')">
                    <input type="hidden" name="idAccion" value="eliminarAlumno">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="id" value="${a.id}">
                    <button type="submit" aria-label="Eliminar alumno <c:out value='${a.nombre}'/>">Eliminar</button>
                </form>
            </td>
        </tr>
        </c:forEach>
        <c:if test="${empty alumnos}">
            <tr role="row" class="fila-vacia">
                <td role="cell" colspan="5" class="vacio">
                    <strong class="vacio-titulo">Sin alumnos</strong>
                    <p>No hay alumnos registrados</p>
                    <a href="${pageContext.request.contextPath}/control?idAccion=formAlumno">Nuevo alumno</a>
                </td>
            </tr>
        </c:if>
    </table>

    </main>
</body>
</html>
