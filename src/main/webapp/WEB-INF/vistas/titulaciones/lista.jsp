<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Titulaciones · Gestión Universitaria</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/css/favicon.svg">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>
    <main id="contenido" tabindex="-1">

    <h1>Titulaciones</h1>

    <p class="toolbar">
        <a href="${pageContext.request.contextPath}/control?idAccion=formTitulacion">Nueva titulación</a>
        <a href="${pageContext.request.contextPath}/control?idAccion=listarTitulaciones">Refrescar</a>
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
            <th role="columnheader" scope="col">Descripción</th>
            <th role="columnheader" scope="col">Acciones</th>
        </tr>
        <c:forEach var="t" items="${titulaciones}">
            <tr role="row">
                <td role="cell" class="celda-id" data-label="ID"><span class="placa">${t.id}</span></td>
                <td role="cell" class="celda-principal" data-label="Nombre"><c:out value="${t.nombre}"/></td>
                <td role="cell" data-label="Descripción"><c:out value="${t.descripcion}"/></td>
                <td role="cell" class="celda-acciones">
                    <a href="${pageContext.request.contextPath}/control?idAccion=formTitulacion&id=${t.id}" aria-label="Editar titulación <c:out value='${t.nombre}'/>">Editar</a>
                    <form class="form-borrar" action="${pageContext.request.contextPath}/control" method="post"
                          onsubmit="return confirm('¿Eliminar titulación?')">
                        <input type="hidden" name="idAccion" value="eliminarTitulacion">
                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                        <input type="hidden" name="id" value="${t.id}">
                        <button type="submit" aria-label="Eliminar titulación <c:out value='${t.nombre}'/>">Eliminar</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty titulaciones}">
            <tr role="row" class="fila-vacia">
                <td role="cell" colspan="4" class="vacio">
                    <strong class="vacio-titulo">Sin titulaciones</strong>
                    <p>No hay titulaciones registradas</p>
                    <a href="${pageContext.request.contextPath}/control?idAccion=formTitulacion">Nueva titulación</a>
                </td>
            </tr>
        </c:if>
    </table>

    </main>
</body>
</html>
