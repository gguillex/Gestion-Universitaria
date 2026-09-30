<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="app-base" content="${pageContext.request.contextPath}">
    <meta name="csrf-token" content="<c:out value='${sessionScope.csrfToken}'/>">
    <title>Titulaciones (REST) · Gestión Universitaria</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/css/favicon.svg">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>
    <main id="contenido" tabindex="-1">

    <h1>Titulaciones (REST)</h1>

    <p class="toolbar">
        <a href="${pageContext.request.contextPath}/rest-ui/index.jsp">Menú REST</a>
    </p>

    <div id="mensaje"></div>

    <form id="formulario" novalidate>
        <p class="form-titulo" id="tituloForm">Nueva titulación</p>
        <input type="hidden" id="id">
        <label>Nombre
            <input type="text" id="nombre" maxlength="200" required>
        </label>
        <label>Descripción
            <textarea id="descripcion" rows="3"></textarea>
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
                <th role="columnheader" scope="col">Descripción</th>
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
            var campoDescripcion = document.getElementById('descripcion');

            function cargarLista() {
                R.llamar('GET', '/titulacion/listado').then(function (r) {
                    if (!r.ok) { R.mensaje(r.datos.resultado || 'No se pudo cargar la lista', 'error'); return; }
                    var filas = (r.datos.titulaciones || []).map(function (t) {
                        var tr = R.el('tr', { 'role': 'row' });
                        tr.appendChild(R.celdaId(t.id));
                        tr.appendChild(R.celda(t.nombre, 'Nombre', 'celda-principal'));
                        tr.appendChild(R.celda(t.descripcion, 'Descripción'));
                        tr.appendChild(R.celdaAcciones('titulación ' + t.nombre,
                            function () { editar(t.id); }, function () { eliminar(t.id); }));
                        return tr;
                    });
                    R.pintarFilas(document.getElementById('cuerpoTabla'), filas, 4, 'Sin titulaciones');
                });
            }

            function guardar(ev) {
                ev.preventDefault();
                R.limpiar();
                var esEdicion = campoId.value !== '';
                var cuerpo = { nombre: campoNombre.value, descripcion: R.opcional(campoDescripcion.value) };
                if (esEdicion) cuerpo.id = parseInt(campoId.value, 10);

                R.llamar(esEdicion ? 'PUT' : 'POST', '/titulacion', cuerpo).then(function (r) {
                    if (r.ok && r.datos.titulacion) {
                        R.mensaje(esEdicion ? 'Titulación ' + r.datos.titulacion.id + ' modificada'
                                            : 'Titulación creada con id ' + r.datos.titulacion.id, 'ok');
                        limpiarForm();
                        cargarLista();
                    } else {
                        R.mensaje(r.datos.resultado || 'No se pudo guardar', 'error');
                    }
                });
            }

            function editar(id) {
                R.limpiar();
                R.llamar('GET', '/titulacion/datos/' + id).then(function (r) {
                    if (!r.ok) { R.mensaje(r.datos.resultado || 'No encontrada', 'error'); return; }
                    var t = r.datos.titulacion;
                    campoId.value = t.id;
                    campoNombre.value = t.nombre || '';
                    campoDescripcion.value = t.descripcion || '';
                    document.getElementById('tituloForm').textContent = 'Editar titulación ' + t.id;
                    campoNombre.focus();
                });
            }

            function eliminar(id) {
                R.limpiar();
                R.llamar('DELETE', '/titulacion/' + id).then(function (r) {
                    if (r.ok) { R.mensaje('Titulación eliminada', 'ok'); cargarLista(); }
                    else      { R.mensaje(r.datos.resultado || 'No se pudo eliminar', 'error'); }
                });
            }

            function limpiarForm() {
                // reset() devuelve cada campo a su valor inicial y borra también el
                // estado «ya tocado», para que no quede marcado como inválido
                document.getElementById('formulario').reset();
                campoId.value = '';
                document.getElementById('tituloForm').textContent = 'Nueva titulación';
            }

            document.getElementById('formulario').addEventListener('submit', guardar);
            document.getElementById('limpiar').addEventListener('click', function (ev) {
                ev.preventDefault(); R.limpiar(); limpiarForm();
            });
            cargarLista();
        })();
    </script>
</body>
</html>
