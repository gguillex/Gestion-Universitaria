<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Asignar profesor · Gestión Universitaria</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>
    <main id="contenido" tabindex="-1">

    <h2>Asignar profesor a una asignatura</h2>

    <form action="${pageContext.request.contextPath}/control" method="post">
        <input type="hidden" name="idAccion" value="asignarProfesor">
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

        <label>Asignatura
            <select name="idAsignatura" required autofocus>
                <option value="">— selecciona —</option>
                <c:forEach var="a" items="${asignaturas}">
                    <option value="${a.id}">
                        <c:out value="${a.nombre}"/> (<c:out value="${a.nombreTitulacion}"/>)
                        <c:if test="${not empty a.nombreProfesor}"> — actual: <c:out value="${a.nombreProfesor}"/></c:if>
                    </option>
                </c:forEach>
            </select>
            <span class="campo-error">Elige la asignatura.</span>
        </label>
        <label>Profesor
            <select name="idProfesor">
                <option value="">— sin asignar —</option>
                <c:forEach var="p" items="${profesores}">
                    <option value="${p.id}"><c:out value="${p.nombre}"/></option>
                </c:forEach>
            </select>
        </label>

        <div class="acciones-form">
            <button type="submit">Asignar</button>
            <a href="${pageContext.request.contextPath}/control?idAccion=listarAsignaturas">Cancelar</a>
        </div>
    </form>

    </main>
</body>
</html>
