<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Matricular · Gestión Universitaria</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/css/favicon.svg">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>
    <main id="contenido" tabindex="-1">

    <h1>Matricular alumno en asignatura</h1>

    <c:if test="${not empty error}"><p class="error" role="alert"><c:out value="${error}"/></p></c:if>

    <form action="${pageContext.request.contextPath}/control" method="post">
        <input type="hidden" name="idAccion" value="matricular">
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

        <label>Alumno
            <select name="idAlumno" required autofocus>
                <option value="">— selecciona alumno —</option>
                <c:forEach var="a" items="${alumnos}">
                    <option value="${a.id}" ${param.idAlumno == a.id ? 'selected' : ''}><c:out value="${a.nombre}"/></option>
                </c:forEach>
            </select>
            <span class="campo-error">Elige el alumno que se va a matricular.</span>
        </label>
        <label>Asignatura
            <select name="idAsignatura" required>
                <option value="">— selecciona asignatura —</option>
                <c:forEach var="as" items="${asignaturas}">
                    <option value="${as.id}" ${param.idAsignatura == as.id ? 'selected' : ''}>
                        <c:out value="${as.nombre}"/> (cap. ${as.capacidadMaxima})
                    </option>
                </c:forEach>
            </select>
            <span class="campo-error">Elige la asignatura.</span>
        </label>
        <div class="acciones-form">
            <button type="submit">Matricular</button>
            <a href="${pageContext.request.contextPath}/control?idAccion=listarAlumnos">Cancelar</a>
        </div>
    </form>

    </main>
</body>
</html>
