<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Titulación · Gestión Universitaria</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/css/favicon.svg">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>
    <main id="contenido" tabindex="-1">

    <h1><c:choose><c:when test="${titulacion.id > 0}">Editar titulación</c:when><c:otherwise>Nueva titulación</c:otherwise></c:choose></h1>

    <form action="${pageContext.request.contextPath}/control" method="post">
        <input type="hidden" name="idAccion" value="guardarTitulacion">
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
        <input type="hidden" name="id"       value="${titulacion.id > 0 ? titulacion.id : ''}">

        <label>Nombre
            <input type="text" name="nombre" value="<c:out value='${titulacion.nombre}'/>" required autofocus>
            <span class="campo-error">Escribe el nombre de la titulación.</span>
        </label>
        <label>Descripción
            <textarea name="descripcion" rows="4"><c:out value='${titulacion.descripcion}'/></textarea>
        </label>
        <div class="acciones-form">
            <button type="submit">Guardar</button>
            <a href="${pageContext.request.contextPath}/control?idAccion=listarTitulaciones">Cancelar</a>
        </div>
    </form>

    </main>
</body>
</html>
