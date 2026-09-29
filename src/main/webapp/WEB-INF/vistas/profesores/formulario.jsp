<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Profesor · Gestión Universitaria</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>

    <h2><c:choose><c:when test="${profesor.id > 0}">Editar profesor</c:when><c:otherwise>Nuevo profesor</c:otherwise></c:choose></h2>

    <form action="${pageContext.request.contextPath}/control" method="post">
        <input type="hidden" name="idAccion" value="guardarProfesor">
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
        <input type="hidden" name="id"       value="${profesor.id > 0 ? profesor.id : ''}">

        <label>Nombre
            <input type="text" name="nombre" value="<c:out value='${profesor.nombre}'/>" required autofocus>
            <span class="campo-error">Escribe el nombre del profesor.</span>
        </label>
        <label>Email
            <input type="email" name="email" value="<c:out value='${profesor.email}'/>" autocomplete="off">
            <span class="campo-error">El correo no es válido. Usa el formato nombre@dominio.es.</span>
        </label>
        <div class="acciones-form">
            <button type="submit">Guardar</button>
            <a href="${pageContext.request.contextPath}/control?idAccion=listarProfesores">Cancelar</a>
        </div>
    </form>

</body>
</html>
