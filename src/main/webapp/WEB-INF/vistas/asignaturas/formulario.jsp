<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Asignatura · Gestión Universitaria</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/css/favicon.svg">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>
    <main id="contenido" tabindex="-1">

    <h1><c:choose><c:when test="${asignatura.id > 0}">Editar asignatura</c:when><c:otherwise>Nueva asignatura</c:otherwise></c:choose></h1>

    <c:if test="${not empty error}">
        <p class="error" role="alert"><c:out value="${error}"/></p>
    </c:if>

    <form action="${pageContext.request.contextPath}/control" method="post">
        <input type="hidden" name="idAccion" value="guardarAsignatura">
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
        <input type="hidden" name="id"       value="${asignatura.id > 0 ? asignatura.id : ''}">

        <label>Nombre
            <input type="text" name="nombre" value="<c:out value='${asignatura.nombre}'/>" required autofocus>
            <span class="campo-error">Escribe el nombre de la asignatura.</span>
        </label>
        <label>Capacidad máxima
            <input type="number" name="capacidadMaxima" min="1" inputmode="numeric" value="${asignatura.capacidadMaxima > 0 ? asignatura.capacidadMaxima : 30}" required>
            <span class="campo-error">Indica una capacidad de 1 plaza o más.</span>
        </label>
        <label>Titulación
            <select name="idTitulacion" required>
                <option value="">— selecciona —</option>
                <c:forEach var="t" items="${titulaciones}">
                    <option value="${t.id}" ${asignatura.idTitulacion == t.id ? 'selected' : ''}>
                        <c:out value="${t.nombre}"/>
                    </option>
                </c:forEach>
            </select>
            <span class="campo-error">Elige la titulación a la que pertenece.</span>
        </label>
        <label>Profesor (opcional)
            <select name="idProfesor">
                <option value="">— sin asignar —</option>
                <c:forEach var="p" items="${profesores}">
                    <option value="${p.id}" ${asignatura.idProfesor == p.id ? 'selected' : ''}>
                        <c:out value="${p.nombre}"/>
                    </option>
                </c:forEach>
            </select>
        </label>

        <div class="acciones-form">
            <button type="submit">Guardar</button>
            <a href="${pageContext.request.contextPath}/control?idAccion=listarAsignaturas">Cancelar</a>
        </div>
    </form>

    </main>
</body>
</html>
