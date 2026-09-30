<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%-- Sección activa según la acción en curso (también cuando se llega por un guardado o un borrado) --%>
<c:set var="accion" value="${param.idAccion}"/>
<c:set var="enTitulaciones" value="${fn:contains(accion, 'Titulacion')}"/>
<c:set var="enAsignaturas"  value="${fn:contains(accion, 'Asignatura')}"/>
<c:set var="enAsignar"      value="${accion == 'asignarProfesor'}"/>
<c:set var="enProfesores"   value="${fn:contains(accion, 'Profesor') and not enAsignar}"/>
<c:set var="enAlumnos"      value="${fn:contains(accion, 'Alumno')}"/>
<c:set var="enMatricular"   value="${accion == 'matricular'}"/>
<c:set var="enUsuarios"     value="${fn:contains(accion, 'Usuario')}"/>
<c:set var="enRest"         value="${fn:startsWith(pageContext.request.servletPath, '/rest-ui/')}"/>
<a class="saltar" href="#contenido">Saltar al contenido</a>
<nav class="topbar" aria-label="Principal">
    <div class="topbar-brand">Gestión Universitaria</div>
    <div class="topbar-links">
        <a href="${pageContext.request.contextPath}/control?idAccion=listarTitulaciones" ${enTitulaciones ? 'aria-current="page"' : ''}>Titulaciones</a>
        <a href="${pageContext.request.contextPath}/control?idAccion=listarAsignaturas" ${enAsignaturas ? 'aria-current="page"' : ''}>Asignaturas</a>
        <a href="${pageContext.request.contextPath}/control?idAccion=listarProfesores" ${enProfesores ? 'aria-current="page"' : ''}>Profesores</a>
        <a href="${pageContext.request.contextPath}/control?idAccion=asignarProfesor" ${enAsignar ? 'aria-current="page"' : ''}>Asignar profesor</a>
        <a href="${pageContext.request.contextPath}/control?idAccion=listarAlumnos" ${enAlumnos ? 'aria-current="page"' : ''}>Alumnos</a>
        <a href="${pageContext.request.contextPath}/control?idAccion=matricular" ${enMatricular ? 'aria-current="page"' : ''}>Matricular</a>
        <a href="${pageContext.request.contextPath}/rest-ui/index.jsp" ${enRest ? 'aria-current="page"' : ''}>API REST</a>
        <c:if test="${sessionScope.usuarioLogueado.rol == 'admin'}">
            <a href="${pageContext.request.contextPath}/control?idAccion=listarUsuarios" ${enUsuarios ? 'aria-current="page"' : ''}>Usuarios</a>
        </c:if>
    </div>
    <div class="topbar-user">
        <span><c:out value="${sessionScope.usuarioLogueado.nombre}"/> <span class="badge"><c:out value="${sessionScope.usuarioLogueado.rol}"/></span></span>
        <a href="${pageContext.request.contextPath}/control?idAccion=logout">Cerrar sesión</a>
    </div>
</nav>
<script>
    /* En móvil los enlaces forman una tira desplazable: se centra la sección activa para que no quede oculta */
    (function () {
        var activo = document.querySelector('.topbar-links a[aria-current="page"]');
        if (!activo) return;
        var tira = activo.parentElement;
        var izq = activo.getBoundingClientRect().left - tira.getBoundingClientRect().left + tira.scrollLeft;
        tira.scrollLeft = izq - (tira.clientWidth - activo.offsetWidth) / 2;
    })();
</script>
