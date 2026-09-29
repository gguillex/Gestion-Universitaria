<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Matricular alumno</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>

    <h2>Matricular alumno en asignatura</h2>

    <c:if test="${not empty error}"><p class="error">${error}</p></c:if>

    <form action="${pageContext.request.contextPath}/control" method="post">
        <input type="hidden" name="idAccion" value="matricular">

        <label>Alumno:
            <select name="idAlumno" required>
                <option value="">— selecciona alumno —</option>
                <c:forEach var="a" items="${alumnos}">
                    <option value="${a.id}"><c:out value="${a.nombre}"/></option>
                </c:forEach>
            </select>
        </label>

        <label>Asignatura:
            <select name="idAsignatura" required>
                <option value="">— selecciona asignatura —</option>
                <c:forEach var="as" items="${asignaturas}">
                    <option value="${as.id}">
                        <c:out value="${as.nombre}"/> (cap. ${as.capacidadMaxima})
                    </option>
                </c:forEach>
            </select>
        </label>

        <button type="submit">Matricular</button>
        <a href="${pageContext.request.contextPath}/control?idAccion=listarAlumnos">Cancelar</a>
    </form>

</body>
</html>
