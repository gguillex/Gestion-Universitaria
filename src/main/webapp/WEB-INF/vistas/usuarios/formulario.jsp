<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Usuario</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>

    <h2>
        <c:choose>
            <c:when test="${usuario.id > 0}">Editar usuario</c:when>
            <c:otherwise>Nuevo usuario</c:otherwise>
        </c:choose>
    </h2>

    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>

    <form action="${pageContext.request.contextPath}/control" method="post">
        <input type="hidden" name="idAccion" value="guardarUsuario">
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
        <input type="hidden" name="id"       value="${usuario.id > 0 ? usuario.id : ''}">

        <label>Nombre:
            <input type="text" name="nombre" value="<c:out value='${usuario.nombre}'/>" required>
        </label>
        <c:choose>
            <c:when test="${usuario.id > 0}">
                <label>Nueva contraseña (vacía = no cambiarla):
                    <input type="password" name="password" autocomplete="new-password">
                </label>
            </c:when>
            <c:otherwise>
                <label>Contraseña:
                    <input type="password" name="password" autocomplete="new-password" required>
                </label>
            </c:otherwise>
        </c:choose>
        <label>Rol:
            <select name="rol">
                <option value="usuario" ${usuario.rol == 'usuario' ? 'selected' : ''}>usuario</option>
                <option value="admin"   ${usuario.rol == 'admin'   ? 'selected' : ''}>admin</option>
            </select>
        </label>

        <button type="submit">Guardar</button>
        <a href="${pageContext.request.contextPath}/control?idAccion=listarUsuarios">Cancelar</a>
    </form>

</body>
</html>
