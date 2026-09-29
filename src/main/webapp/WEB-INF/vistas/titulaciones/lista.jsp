<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Titulaciones</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>

    <h2>Titulaciones</h2>

    <p class="toolbar">
        <a href="${pageContext.request.contextPath}/control?idAccion=formTitulacion">Nueva titulación</a>
        <a href="${pageContext.request.contextPath}/control?idAccion=listarTitulaciones">Refrescar</a>
    </p>

    <c:if test="${not empty mensaje}">
        <p class="ok"><c:out value="${mensaje}"/></p>
    </c:if>
    <c:if test="${not empty error}">
        <p class="error"><c:out value="${error}"/></p>
    </c:if>

    <table>
        <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Descripción</th>
            <th>Acciones</th>
        </tr>
        <c:forEach var="t" items="${titulaciones}">
            <tr>
                <td>${t.id}</td>
                <td><c:out value="${t.nombre}"/></td>
                <td><c:out value="${t.descripcion}"/></td>
                <td>
                    <a href="${pageContext.request.contextPath}/control?idAccion=formTitulacion&id=${t.id}">Editar</a>
                    <form class="form-borrar" action="${pageContext.request.contextPath}/control" method="post"
                          onsubmit="return confirm('¿Eliminar titulación?')">
                        <input type="hidden" name="idAccion" value="eliminarTitulacion">
                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                        <input type="hidden" name="id" value="${t.id}">
                        <button type="submit">Eliminar</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty titulaciones}">
            <tr><td colspan="4"><em>No hay titulaciones registradas</em></td></tr>
        </c:if>
    </table>

</body>
</html>
