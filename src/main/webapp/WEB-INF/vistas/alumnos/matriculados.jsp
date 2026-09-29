<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Matriculados · Gestión Universitaria</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>
    <main id="contenido" tabindex="-1">

    <h2>Alumnos matriculados en: <c:out value="${asignatura.nombre}"/></h2>

    <p class="info ocupacion-bloque">
        <span>Ocupación: ${ocupacion} / ${asignatura.capacidadMaxima} plazas</span>
        <progress class="ocupacion ${ocupacion >= asignatura.capacidadMaxima ? 'llena' : ''}"
                  value="${ocupacion}" max="${asignatura.capacidadMaxima}"
                  aria-label="Plazas ocupadas: ${ocupacion} de ${asignatura.capacidadMaxima}"></progress>
    </p>

    <p class="toolbar">
        <a href="${pageContext.request.contextPath}/control?idAccion=matricular&idAsignatura=${asignatura.id}">
            Matricular alumno
        </a>
        <a href="${pageContext.request.contextPath}/control?idAccion=matriculadosAsignatura&idAsignatura=${asignatura.id}&csv=true">
            Exportar CSV
        </a>
        <a href="${pageContext.request.contextPath}/control?idAccion=listarAsignaturas">Volver a asignaturas</a>
    </p>

    <table class="tabla-placas">
        <tr>
            <th scope="col">ID</th>
            <th scope="col">Nombre</th>
            <th scope="col">Email</th>
            <th scope="col">DNI</th>
            <th scope="col">Acciones</th>
        </tr>
        <c:forEach var="a" items="${alumnos}">
        <tr>
            <td class="celda-id" data-label="ID"><span class="placa">${a.id}</span></td>
            <td class="celda-principal" data-label="Nombre"><c:out value="${a.nombre}"/></td>
            <td data-label="Email"><c:out value="${a.email}"/></td>
            <td data-label="DNI"><c:out value="${a.dni}"/></td>
            <td class="celda-acciones">
                <form class="form-borrar" action="${pageContext.request.contextPath}/control" method="post"
                      onsubmit="return confirm('¿Desmatricular a este alumno?')">
                    <input type="hidden" name="idAccion" value="desmatricular">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="idAlumno" value="${a.id}">
                    <input type="hidden" name="idAsignatura" value="${asignatura.id}">
                    <button type="submit">Desmatricular</button>
                </form>
            </td>
        </tr>
        </c:forEach>
        <c:if test="${empty alumnos}">
            <tr class="fila-vacia">
                <td colspan="5" class="vacio">
                    <strong class="vacio-titulo">Sin matriculados</strong>
                    <p>No hay alumnos matriculados en esta asignatura</p>
                    <a href="${pageContext.request.contextPath}/control?idAccion=matricular&idAsignatura=${asignatura.id}">Matricular alumno</a>
                </td>
            </tr>
        </c:if>
    </table>

    </main>
</body>
</html>
