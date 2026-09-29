<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Error</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="menu.jsp"/>
    <h2>Se ha producido un error</h2>
    <p class="error">
        <c:out value="${error}" default="Error desconocido"/>
    </p>
    <p><a href="${pageContext.request.contextPath}/control?idAccion=listarTitulaciones">Volver al inicio</a></p>
</body>
</html>
