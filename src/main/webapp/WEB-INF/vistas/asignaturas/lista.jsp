<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Asignaturas · Gestión Universitaria</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/css/favicon.svg">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>
    <main id="contenido" tabindex="-1">

    <h1>Asignaturas</h1>

    <p class="toolbar">
        <a href="${pageContext.request.contextPath}/control?idAccion=formAsignatura">Nueva asignatura</a>
        <a href="${pageContext.request.contextPath}/control?idAccion=listarAsignaturas">Refrescar</a>
        <a href="${pageContext.request.contextPath}/control?idAccion=asignarProfesor">Asignar profesor</a>
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
            <th role="columnheader" scope="col">Capacidad</th>
            <th role="columnheader" scope="col">Titulación</th>
            <th role="columnheader" scope="col">Profesor</th>
            <th role="columnheader" scope="col">Acciones</th>
        </tr>
        <c:forEach var="a" items="${asignaturas}">
            <tr role="row">
                <td role="cell" class="celda-id" data-label="ID"><span class="placa">${a.id}</span></td>
                <td role="cell" class="celda-principal" data-label="Nombre"><c:out value="${a.nombre}"/></td>
                <td role="cell" data-label="Capacidad">${a.capacidadMaxima}</td>
                <td role="cell" data-label="Titulación"><c:out value="${a.nombreTitulacion}"/></td>
                <td role="cell" data-label="Profesor"><c:out value="${a.nombreProfesor}" default="—"/></td>
                <td role="cell" class="celda-acciones">
                    <a href="${pageContext.request.contextPath}/control?idAccion=matriculadosAsignatura&idAsignatura=${a.id}" aria-label="Ver alumnos de <c:out value='${a.nombre}'/>">Ver alumnos</a>
                    <a href="${pageContext.request.contextPath}/control?idAccion=formAsignatura&id=${a.id}" aria-label="Editar asignatura <c:out value='${a.nombre}'/>">Editar</a>
                    <form class="form-borrar" action="${pageContext.request.contextPath}/control" method="post"
                          onsubmit="return confirm('¿Eliminar asignatura?')">
                        <input type="hidden" name="idAccion" value="eliminarAsignatura">
                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                        <input type="hidden" name="id" value="${a.id}">
                        <button type="submit" aria-label="Eliminar asignatura <c:out value='${a.nombre}'/>">Eliminar</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty asignaturas}">
            <tr role="row" class="fila-vacia">
                <td role="cell" colspan="6" class="vacio">
                    <strong class="vacio-titulo">Sin asignaturas</strong>
                    <p>No hay asignaturas registradas</p>
                    <a href="${pageContext.request.contextPath}/control?idAccion=formAsignatura">Nueva asignatura</a>
                </td>
            </tr>
        </c:if>
    </table>

    </main>
</body>
</html>
