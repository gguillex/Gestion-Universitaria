<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Iniciar sesión · Gestión Universitaria</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/css/favicon.svg">
</head>
<body class="auth">

    <main class="auth-hoja">

        <section class="auth-marca">
            <h1 class="auth-titulo">Gestión<br>Universitaria</h1>
            <dl class="placa-datos">
                <dt>Acceso</dt>  <dd>Administración y profesorado</dd>
                <dt>Sistema</dt> <dd>Gestión universitaria</dd>
            </dl>
        </section>

        <section class="auth-panel" aria-labelledby="titulo-acceso">
            <h2 id="titulo-acceso">Iniciar sesión</h2>

            <div class="auth-cuerpo">
                <c:if test="${not empty errorLogin}">
                    <p class="error" role="alert"><c:out value="${errorLogin}"/></p>
                </c:if>

                <c:if test="${not empty param.registroOk}">
                    <p class="ok" role="status">Cuenta creada correctamente. Ya puedes iniciar sesión.</p>
                </c:if>

                <c:if test="${not empty param.caducado}">
                    <p class="error" role="alert">La página había caducado. Vuelve a intentarlo.</p>
                </c:if>

                <form action="${pageContext.request.contextPath}/control" method="post">
                    <input type="hidden" name="idAccion" value="login">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <label>Usuario
                        <input type="text" name="nombre" value="<c:out value='${param.nombre}'/>" autocomplete="username" required autofocus>
                    </label>
                    <label>Contraseña
                        <input type="password" name="password" autocomplete="current-password" required>
                    </label>
                    <button type="submit">Entrar <span class="flecha" aria-hidden="true"></span></button>
                </form>

                <p class="auth-footer">
                    ¿No tienes cuenta?
                    <a href="${pageContext.request.contextPath}/control?idAccion=mostrarRegistro">Regístrate</a>
                </p>
            </div>
        </section>

    </main>

</body>
</html>
