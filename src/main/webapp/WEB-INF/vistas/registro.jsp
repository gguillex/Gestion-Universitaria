<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Registro de usuario</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="auth">

    <h2>Crear cuenta</h2>

    <c:if test="${not empty errorRegistro}">
        <p class="error">${errorRegistro}</p>
    </c:if>

    <form action="${pageContext.request.contextPath}/control" method="post">
        <input type="hidden" name="idAccion" value="registro">

        <label>Nombre de usuario:
            <input type="text" name="nombre" value="<c:out value='${nombrePrevio}'/>" required autofocus>
        </label>
        <label>Contraseña:
            <input type="password" name="password" required>
        </label>
        <label>Confirmar contraseña:
            <input type="password" name="confirmar" required>
        </label>

        <button type="submit">Registrarse</button>
    </form>

    <p class="auth-footer">
        ¿Ya tienes cuenta?
        <a href="${pageContext.request.contextPath}/control?idAccion=mostrarLogin">Iniciar sesión</a>
    </p>

</body>
</html>
