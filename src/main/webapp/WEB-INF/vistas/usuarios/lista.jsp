<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Usuarios · Gestión Universitaria</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/css/favicon.svg">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>
    <main id="contenido" tabindex="-1">

    <h1>Usuarios (zona admin)</h1>

    <p class="toolbar">
        <a href="${pageContext.request.contextPath}/control?idAccion=formUsuario">Nuevo usuario</a>
        <a href="${pageContext.request.contextPath}/control?idAccion=listarUsuarios">Refrescar</a>
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
            <th role="columnheader" scope="col">Rol</th>
            <th role="columnheader" scope="col">Acciones</th>
        </tr>
        <c:forEach var="u" items="${usuarios}">
            <tr role="row">
                <td role="cell" class="celda-id" data-label="ID"><span class="placa">${u.id}</span></td>
                <td role="cell" class="celda-principal" data-label="Nombre"><c:out value="${u.nombre}"/></td>
                <td role="cell" data-label="Rol"><span class="badge ${u.rol == 'admin' ? 'badge-admin' : ''}"><c:out value="${u.rol}"/></span></td>
                <td role="cell" class="celda-acciones">
                    <a href="${pageContext.request.contextPath}/control?idAccion=formUsuario&id=${u.id}" aria-label="Editar usuario <c:out value='${u.nombre}'/>">Editar</a>
                    <form class="form-borrar" action="${pageContext.request.contextPath}/control" method="post"
                          onsubmit="return confirm('¿Eliminar usuario?')">
                        <input type="hidden" name="idAccion" value="eliminarUsuario">
                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                        <input type="hidden" name="id" value="${u.id}">
                        <button type="submit" aria-label="Eliminar usuario <c:out value='${u.nombre}'/>">Eliminar</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty usuarios}">
            <tr role="row" class="fila-vacia">
                <td role="cell" colspan="4" class="vacio">
                    <strong class="vacio-titulo">Sin usuarios</strong>
                    <p>No hay usuarios registrados</p>
                    <a href="${pageContext.request.contextPath}/control?idAccion=formUsuario">Nuevo usuario</a>
                </td>
            </tr>
        </c:if>
    </table>

    </main>
</body>
</html>
