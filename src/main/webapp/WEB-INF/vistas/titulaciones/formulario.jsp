<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Titulación</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>

    <h2>
        <c:choose>
            <c:when test="${titulacion.id > 0}">Editar titulación</c:when>
            <c:otherwise>Nueva titulación</c:otherwise>
        </c:choose>
    </h2>

    <form action="${pageContext.request.contextPath}/control" method="post">
        <input type="hidden" name="idAccion" value="guardarTitulacion">
        <input type="hidden" name="id"       value="${titulacion.id > 0 ? titulacion.id : ''}">

        <label>Nombre:
            <input type="text" name="nombre" value="<c:out value='${titulacion.nombre}'/>" required>
        </label>
        <label>Descripción:
            <textarea name="descripcion" rows="4"><c:out value='${titulacion.descripcion}'/></textarea>
        </label>

        <button type="submit">Guardar</button>
        <a href="${pageContext.request.contextPath}/control?idAccion=listarTitulaciones">Cancelar</a>
    </form>

</body>
</html>
