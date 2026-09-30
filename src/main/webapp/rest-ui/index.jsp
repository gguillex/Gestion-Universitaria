<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Servicio REST · Gestión Universitaria</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/css/favicon.svg">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>
    <main id="contenido" tabindex="-1">

    <h1>Servicio REST</h1>

    <p class="info">
        Los mismos datos que la interfaz clásica, expuestos como API JSON bajo
        <code>${pageContext.request.contextPath}/rest/</code>. Estas pantallas la consumen con
        <code>fetch</code> y actualizan la tabla sin recargar la página. Requieren sesión iniciada.
    </p>

    <p class="toolbar">
        <a href="${pageContext.request.contextPath}/rest-ui/titulacion.jsp">Titulaciones</a>
        <a href="${pageContext.request.contextPath}/rest-ui/profesor.jsp">Profesores</a>
        <a href="${pageContext.request.contextPath}/rest-ui/asignatura.jsp">Asignaturas</a>
    </p>

    </main>
</body>
</html>
