<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Usuarios</title>
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
        <p class="ok">${mensaje}</p>
    </c:if>
    <c:if test="${not empty error}">
        <p class="error">${error}</p>
    </c:if>

    <table>
        <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Rol</th>
            <th>Acciones</th>
        </tr>
        <c:forEach var="u" items="${usuarios}">
            <tr>
                <td>${u.id}</td>
                <td><c:out value="${u.nombre}"/></td>
                <td><c:out value="${u.rol}"/></td>
                <td>
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
            <tr><td colspan="4"><em>No hay usuarios registrados</em></td></tr>
        </c:if>
    </table>

</body>
</html>
