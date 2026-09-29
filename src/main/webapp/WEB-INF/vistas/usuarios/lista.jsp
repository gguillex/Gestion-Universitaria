<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Usuarios · Gestión Universitaria</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>

    <h2>Usuarios (zona admin)</h2>

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

    <table class="tabla-placas">
        <tr>
            <th scope="col">ID</th>
            <th scope="col">Nombre</th>
            <th scope="col">Rol</th>
            <th scope="col">Acciones</th>
        </tr>
        <c:forEach var="u" items="${usuarios}">
            <tr>
                <td class="celda-id" data-label="ID"><span class="placa">${u.id}</span></td>
                <td class="celda-principal" data-label="Nombre"><c:out value="${u.nombre}"/></td>
                <td data-label="Rol"><span class="badge ${u.rol == 'admin' ? 'badge-admin' : ''}"><c:out value="${u.rol}"/></span></td>
                <td class="celda-acciones">
                    <a href="${pageContext.request.contextPath}/control?idAccion=formUsuario&id=${u.id}">Editar</a>
                    <form class="form-borrar" action="${pageContext.request.contextPath}/control" method="post"
                          onsubmit="return confirm('¿Eliminar usuario?')">
                        <input type="hidden" name="idAccion" value="eliminarUsuario">
                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                        <input type="hidden" name="id" value="${u.id}">
                        <button type="submit">Eliminar</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty usuarios}">
            <tr class="fila-vacia">
                <td colspan="4" class="vacio">
                    <strong class="vacio-titulo">Sin usuarios</strong>
                    <p>No hay usuarios registrados</p>
                    <a href="${pageContext.request.contextPath}/control?idAccion=formUsuario">Nuevo usuario</a>
                </td>
            </tr>
        </c:if>
    </table>

</body>
</html>
