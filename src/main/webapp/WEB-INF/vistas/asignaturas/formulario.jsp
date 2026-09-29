<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Asignatura</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>

    <h2>
        <c:choose>
            <c:when test="${asignatura.id > 0}">Editar asignatura</c:when>
            <c:otherwise>Nueva asignatura</c:otherwise>
        </c:choose>
    </h2>

    <c:if test="${not empty error}">
        <p class="error"><c:out value="${error}"/></p>
    </c:if>

    <form action="${pageContext.request.contextPath}/control" method="post">
        <input type="hidden" name="idAccion" value="guardarAsignatura">
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
        <input type="hidden" name="id"       value="${asignatura.id > 0 ? asignatura.id : ''}">

        <label>Nombre:
            <input type="text" name="nombre" value="<c:out value='${asignatura.nombre}'/>" required>
        </label>
        <label>Capacidad máxima:
            <input type="number" name="capacidadMaxima" min="1" value="${asignatura.capacidadMaxima > 0 ? asignatura.capacidadMaxima : 30}" required>
        </label>
        <label>Titulación:
            <select name="idTitulacion" required>
                <option value="">— selecciona —</option>
                <c:forEach var="t" items="${titulaciones}">
                    <option value="${t.id}" ${asignatura.idTitulacion == t.id ? 'selected' : ''}>
                        <c:out value="${t.nombre}"/>
                    </option>
                </c:forEach>
            </select>
        </label>
        <label>Profesor (opcional):
            <select name="idProfesor">
                <option value="">— sin asignar —</option>
                <c:forEach var="p" items="${profesores}">
                    <option value="${p.id}" ${asignatura.idProfesor == p.id ? 'selected' : ''}>
                        <c:out value="${p.nombre}"/>
                    </option>
                </c:forEach>
            </select>
        </label>

        <button type="submit">Guardar</button>
        <a href="${pageContext.request.contextPath}/control?idAccion=listarAsignaturas">Cancelar</a>
    </form>

</body>
</html>
