<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<nav class="topbar">
    <div class="topbar-brand">Gestión Universitaria</div>
    <div class="topbar-links">
        <a href="${pageContext.request.contextPath}/control?idAccion=listarTitulaciones">Titulaciones</a>
        <a href="${pageContext.request.contextPath}/control?idAccion=listarAsignaturas">Asignaturas</a>
        <a href="${pageContext.request.contextPath}/control?idAccion=listarProfesores">Profesores</a>
        <a href="${pageContext.request.contextPath}/control?idAccion=asignarProfesor">Asignar profesor</a>
        <a href="${pageContext.request.contextPath}/control?idAccion=listarAlumnos">Alumnos</a>
        <a href="${pageContext.request.contextPath}/control?idAccion=matricular">Matricular</a>
        <c:if test="${sessionScope.usuarioLogueado.rol == 'admin'}">
            <a href="${pageContext.request.contextPath}/control?idAccion=listarUsuarios">Usuarios</a>
        </c:if>
    </div>
    <div class="topbar-user">
        <span><c:out value="${sessionScope.usuarioLogueado.nombre}"/> <span class="badge">${sessionScope.usuarioLogueado.rol}</span></span>
        <a href="${pageContext.request.contextPath}/control?idAccion=logout">Cerrar sesión</a>
    </div>
</nav>
