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
                    <a href="${pageContext.request.contextPath}/control?idAccion=eliminarUsuario&id=${u.id}"
                       onclick="return confirm('¿Eliminar usuario?')">Eliminar</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty usuarios}">
            <tr><td colspan="4"><em>No hay usuarios registrados</em></td></tr>
        </c:if>
    </table>

</body>
</html>
