<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Iniciar sesión</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="auth">

    <h2>Iniciar sesión</h2>

    <c:if test="${not empty errorLogin}">
        <p class="error"><c:out value="${errorLogin}"/></p>
    </c:if>

    <c:if test="${not empty param.registroOk}">
        <p class="ok">Cuenta creada correctamente. Ya puedes iniciar sesión.</p>
    </c:if>

    <form action="${pageContext.request.contextPath}/control" method="post">
        <input type="hidden" name="idAccion" value="login">
        <label>Usuario:
            <input type="text" name="nombre" required autofocus>
        </label>
        <label>Contraseña:
            <input type="password" name="password" required>
        </label>
        <button type="submit">Entrar</button>
    </form>

    <p class="auth-footer">
        ¿No tienes cuenta?
        <a href="${pageContext.request.contextPath}/control?idAccion=mostrarRegistro">Regístrate</a>
    </p>

</body>
</html>
