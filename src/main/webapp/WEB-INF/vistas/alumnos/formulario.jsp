<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Alumno · Gestión Universitaria</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>

    <h2><c:choose><c:when test="${alumno.id > 0}">Editar alumno</c:when><c:otherwise>Nuevo alumno</c:otherwise></c:choose></h2>

    <c:if test="${not empty error}"><p class="error" role="alert"><c:out value="${error}"/></p></c:if>

    <form action="${pageContext.request.contextPath}/control" method="post">
        <input type="hidden" name="idAccion" value="guardarAlumno">
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
        <input type="hidden" name="id" value="${alumno.id > 0 ? alumno.id : ''}">

        <label>Nombre
            <input type="text" name="nombre" value="<c:out value='${alumno.nombre}'/>" required autofocus>
            <span class="campo-error">Escribe el nombre del alumno.</span>
        </label>
        <label>Email
            <input type="email" name="email" value="<c:out value='${alumno.email}'/>" autocomplete="off">
            <span class="campo-error">El correo no es válido. Usa el formato nombre@dominio.es.</span>
        </label>
        <label>DNI
            <input type="text" name="dni" value="<c:out value='${alumno.dni}'/>" autocomplete="off">
        </label>
        <div class="acciones-form">
            <button type="submit">Guardar</button>
            <a href="${pageContext.request.contextPath}/control?idAccion=listarAlumnos">Cancelar</a>
        </div>
    </form>

</body>
</html>
