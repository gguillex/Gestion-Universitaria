<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Crear cuenta · Gestión Universitaria</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="auth">

    <main class="auth-hoja">

        <section class="auth-marca">
            <h1 class="auth-titulo">Gestión<br>Universitaria</h1>
            <dl class="placa-datos">
                <dt>Cuenta</dt> <dd>Nueva</dd>
                <dt>Rol</dt>    <dd>Usuario</dd>
            </dl>
        </section>

        <section class="auth-panel" aria-labelledby="titulo-registro">
            <h2 id="titulo-registro">Crear cuenta</h2>

            <div class="auth-cuerpo">
                <c:if test="${not empty errorRegistro}">
                    <p class="error" role="alert"><c:out value="${errorRegistro}"/></p>
                </c:if>

                <c:if test="${not empty param.caducado}">
                    <p class="error" role="alert">La página había caducado. Vuelve a intentarlo.</p>
                </c:if>

                <form action="${pageContext.request.contextPath}/control" method="post">
                    <input type="hidden" name="idAccion" value="registro">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <label>Nombre de usuario
                        <input type="text" name="nombre" value="<c:out value='${nombrePrevio}'/>" autocomplete="username" required autofocus>
                        <span class="campo-error">Escribe un nombre de usuario.</span>
                    </label>
                    <label>Contraseña
                        <input type="password" name="password" autocomplete="new-password" required>
                        <span class="campo-error">Escribe una contraseña.</span>
                    </label>
                    <label>Confirmar contraseña
                        <input type="password" name="confirmar" autocomplete="new-password" required>
                        <span class="campo-error">Repite la contraseña.</span>
                    </label>
                    <button type="submit">Registrarse <span class="flecha" aria-hidden="true"></span></button>
                </form>

                <p class="auth-footer">
                    ¿Ya tienes cuenta?
                    <a href="${pageContext.request.contextPath}/control?idAccion=mostrarLogin">Iniciar sesión</a>
                </p>
            </div>
        </section>

    </main>

</body>
</html>
