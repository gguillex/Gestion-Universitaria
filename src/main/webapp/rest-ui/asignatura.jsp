<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="app-base" content="${pageContext.request.contextPath}">
    <meta name="csrf-token" content="<c:out value='${sessionScope.csrfToken}'/>">
    <title>Asignaturas (REST) · Gestión Universitaria</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/css/favicon.svg">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>
    <main id="contenido" tabindex="-1">

    <h1>Asignaturas (REST)</h1>

    <p class="toolbar">
        <a href="${pageContext.request.contextPath}/rest-ui/index.jsp">Menú REST</a>
    </p>

    <div id="mensaje"></div>

    <form id="formulario" novalidate>
        <p class="form-titulo" id="tituloForm">Nueva asignatura</p>
        <input type="hidden" id="id">
        <label>Nombre
            <input type="text" id="nombre" maxlength="200" required>
        </label>
        <label>Capacidad máxima
            <input type="number" id="capacidad" min="1" value="30" required>
        </label>
        <label>Titulación
            <select id="titulacion" required></select>
        </label>
        <label>Profesor
            <select id="profesor"></select>
        </label>
        <div class="acciones-form">
            <button type="submit">Guardar</button>
            <a href="#" id="limpiar">Limpiar</a>
        </div>
    </form>

    <table class="tabla-placas" role="table">
        <thead>
            <tr role="row">
                <th role="columnheader" scope="col">ID</th>
                <th role="columnheader" scope="col">Nombre</th>
                <th role="columnheader" scope="col">Capacidad</th>
                <th role="columnheader" scope="col">Titulación</th>
                <th role="columnheader" scope="col">Profesor</th>
                <th role="columnheader" scope="col">Acciones</th>
            </tr>
        </thead>
        <tbody id="cuerpoTabla"></tbody>
    </table>

    </main>

    <script src="${pageContext.request.contextPath}/rest-ui/rest.js"></script>
    <script>
        (function () {
            var R = window.Rest;
            var campoId = document.getElementById('id');
            var campoNombre = document.getElementById('nombre');
            var campoCapacidad = document.getElementById('capacidad');
            var selTitulacion = document.getElementById('titulacion');
            var selProfesor = document.getElementById('profesor');

            /** Rellena un <select> con las opciones del servidor; el profesor admite "sin asignar". */
            function llenarSelect(select, lista, admiteVacio) {
                var opciones = [];
                if (admiteVacio) opciones.push(R.el('option', { 'value': '' }, '— Sin asignar —'));
                lista.forEach(function (x) { opciones.push(R.el('option', { 'value': x.id }, x.nombre)); });
                select.replaceChildren.apply(select, opciones);
            }

            function cargarDesplegables() {
                return Promise.all([R.llamar('GET', '/titulacion/listado'), R.llamar('GET', '/profesor/listado')])
                    .then(function (rs) {
                        if (!rs[0].ok || !rs[1].ok) {
                            R.mensaje('No se pudieron cargar las titulaciones o los profesores.', 'error');
                        }
                        llenarSelect(selTitulacion, rs[0].datos.titulaciones || [], false);
                        llenarSelect(selProfesor, rs[1].datos.profesores || [], true);
                    });
            }

            function cargarLista() {
                R.llamar('GET', '/asignatura/listado').then(function (r) {
                    if (!r.ok) { R.mensaje(r.datos.resultado || 'No se pudo cargar la lista', 'error'); return; }
                    var filas = (r.datos.asignaturas || []).map(function (a) {
                        var tr = R.el('tr', { 'role': 'row' });
                        tr.appendChild(R.celdaId(a.id));
                        tr.appendChild(R.celda(a.nombre, 'Nombre', 'celda-principal'));
                        tr.appendChild(R.celda(a.capacidadMaxima, 'Capacidad'));
                        tr.appendChild(R.celda(a.nombreTitulacion, 'Titulación'));
                        tr.appendChild(R.celda(a.nombreProfesor, 'Profesor'));
                        tr.appendChild(R.celdaAcciones('asignatura ' + a.nombre,
                            function () { editar(a.id); }, function () { eliminar(a.id); }));
                        return tr;
                    });
                    R.pintarFilas(document.getElementById('cuerpoTabla'), filas, 6, 'Sin asignaturas');
                });
            }

            function guardar(ev) {
                ev.preventDefault();
                R.limpiar();
                var esEdicion = campoId.value !== '';
                if (selTitulacion.value === '') {
                    R.mensaje('Elige una titulación (si no hay ninguna, créala antes).', 'error');
                    return;
                }
                if (campoCapacidad.value === '') {
                    R.mensaje('Indica la capacidad máxima.', 'error');
                    return;
                }
                var cuerpo = {
                    nombre: campoNombre.value,
                    capacidadMaxima: parseInt(campoCapacidad.value, 10),
                    idTitulacion: parseInt(selTitulacion.value, 10),
                    idProfesor: selProfesor.value === '' ? null : parseInt(selProfesor.value, 10)
                };
                if (esEdicion) cuerpo.id = parseInt(campoId.value, 10);

                R.llamar(esEdicion ? 'PUT' : 'POST', '/asignatura', cuerpo).then(function (r) {
                    if (r.ok && r.datos.asignatura) {
                        R.mensaje(esEdicion ? 'Asignatura ' + r.datos.asignatura.id + ' modificada'
                                            : 'Asignatura creada con id ' + r.datos.asignatura.id, 'ok');
                        limpiarForm();
                        cargarLista();
                    } else {
                        R.mensaje(r.datos.resultado || 'No se pudo guardar', 'error');
                    }
                });
            }

            function editar(id) {
                R.limpiar();
                R.llamar('GET', '/asignatura/datos/' + id).then(function (r) {
                    if (!r.ok) { R.mensaje(r.datos.resultado || 'No encontrada', 'error'); return; }
                    var a = r.datos.asignatura;
                    campoId.value = a.id;
                    campoNombre.value = a.nombre || '';
                    campoCapacidad.value = a.capacidadMaxima;
                    selTitulacion.value = a.idTitulacion;
                    selProfesor.value = a.idProfesor === null ? '' : a.idProfesor;
                    document.getElementById('tituloForm').textContent = 'Editar asignatura ' + a.id;
                    campoNombre.focus();
                });
            }

            function eliminar(id) {
                R.limpiar();
                R.llamar('DELETE', '/asignatura/' + id).then(function (r) {
                    if (r.ok) { R.mensaje('Asignatura eliminada', 'ok'); cargarLista(); }
                    else      { R.mensaje(r.datos.resultado || 'No se pudo eliminar', 'error'); }
                });
            }

            function limpiarForm() {
                // reset() devuelve cada campo a su valor inicial y borra también el
                // estado «ya tocado», para que no quede marcado como inválido
                document.getElementById('formulario').reset();
                campoId.value = '';
                document.getElementById('tituloForm').textContent = 'Nueva asignatura';
            }

            document.getElementById('formulario').addEventListener('submit', guardar);
            document.getElementById('limpiar').addEventListener('click', function (ev) {
                ev.preventDefault(); R.limpiar(); limpiarForm();
            });
            cargarDesplegables().then(cargarLista);
        })();
    </script>
</body>
</html>
