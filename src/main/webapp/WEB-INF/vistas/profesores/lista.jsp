<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Profesores · Gestión Universitaria</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/css/favicon.svg">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>
    <main id="contenido" tabindex="-1">

    <h1>Profesores</h1>

    <p class="toolbar">
        <a href="${pageContext.request.contextPath}/control?idAccion=formProfesor">Nuevo profesor</a>
        <a href="${pageContext.request.contextPath}/control?idAccion=listarProfesores">Refrescar</a>
        <a href="${pageContext.request.contextPath}/control?idAccion=asignarProfesor">Asignar a asignatura</a>
    </p>

    <c:if test="${not empty mensaje}">
        <p class="ok" role="status"><c:out value="${mensaje}"/></p>
    </c:if>
    <c:if test="${not empty error}">
        <p class="error" role="alert"><c:out value="${error}"/></p>
    </c:if>

    <table class="tabla-placas" role="table">
        <tr role="row">
            <th role="columnheader" scope="col">ID</th>
            <th role="columnheader" scope="col">Nombre</th>
            <th role="columnheader" scope="col">Email</th>
            <th role="columnheader" scope="col">Acciones</th>
        </tr>
        <c:forEach var="p" items="${profesores}">
            <tr role="row">
                <td role="cell" class="celda-id" data-label="ID"><span class="placa">${p.id}</span></td>
                <td role="cell" class="celda-principal" data-label="Nombre"><c:out value="${p.nombre}"/></td>
                <td role="cell" data-label="Email"><c:out value="${p.email}"/></td>
                <td role="cell" class="celda-acciones">
                    <a href="${pageContext.request.contextPath}/control?idAccion=formProfesor&id=${p.id}" aria-label="Editar profesor <c:out value='${p.nombre}'/>">Editar</a>
                    <form class="form-borrar" action="${pageContext.request.contextPath}/control" method="post"
                          onsubmit="return confirm('¿Eliminar profesor? Se desasignará de sus asignaturas.')">
                        <input type="hidden" name="idAccion" value="eliminarProfesor">
                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                        <input type="hidden" name="id" value="${p.id}">
                        <button type="submit" aria-label="Eliminar profesor <c:out value='${p.nombre}'/>">Eliminar</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty profesores}">
            <tr role="row" class="fila-vacia">
                <td role="cell" colspan="4" class="vacio">
                    <strong class="vacio-titulo">Sin profesores</strong>
                    <p>No hay profesores registrados</p>
                    <a href="${pageContext.request.contextPath}/control?idAccion=formProfesor">Nuevo profesor</a>
                </td>
            </tr>
        </c:if>
    </table>

    </main>
</body>
</html>
