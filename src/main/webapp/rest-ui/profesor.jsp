<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="app-base" content="${pageContext.request.contextPath}">
    <meta name="csrf-token" content="<c:out value='${sessionScope.csrfToken}'/>">
    <title>Profesores (REST) · Gestión Universitaria</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/css/favicon.svg">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>
    <main id="contenido" tabindex="-1">

    <h1>Profesores (REST)</h1>

    <p class="toolbar">
        <a href="${pageContext.request.contextPath}/rest-ui/index.jsp">Menú REST</a>
    </p>

    <div id="mensaje"></div>

    <form id="formulario" novalidate>
        <p class="form-titulo" id="tituloForm">Nuevo profesor</p>
        <input type="hidden" id="id">
        <label>Nombre
            <input type="text" id="nombre" maxlength="200" required>
        </label>
        <label>Email
            <input type="email" id="email" maxlength="200">
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
                <th role="columnheader" scope="col">Email</th>
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
            var campoEmail = document.getElementById('email');

            function cargarLista() {
                R.llamar('GET', '/profesor/listado').then(function (r) {
                    if (!r.ok) { R.mensaje(r.datos.resultado || 'No se pudo cargar la lista', 'error'); return; }
                    var filas = (r.datos.profesores || []).map(function (p) {
                        var tr = R.el('tr', { 'role': 'row' });
                        tr.appendChild(R.celdaId(p.id));
                        tr.appendChild(R.celda(p.nombre, 'Nombre', 'celda-principal'));
                        tr.appendChild(R.celda(p.email, 'Email'));
                        tr.appendChild(R.celdaAcciones('profesor ' + p.nombre,
                            function () { editar(p.id); }, function () { eliminar(p.id); }));
                        return tr;
                    });
                    R.pintarFilas(document.getElementById('cuerpoTabla'), filas, 4, 'Sin profesores');
                });
            }

            function guardar(ev) {
                ev.preventDefault();
                R.limpiar();
                var esEdicion = campoId.value !== '';
                var cuerpo = { nombre: campoNombre.value, email: R.opcional(campoEmail.value) };
                if (esEdicion) cuerpo.id = parseInt(campoId.value, 10);

                R.llamar(esEdicion ? 'PUT' : 'POST', '/profesor', cuerpo).then(function (r) {
                    if (r.ok && r.datos.profesor) {
                        R.mensaje(esEdicion ? 'Profesor ' + r.datos.profesor.id + ' modificado'
                                            : 'Profesor creado con id ' + r.datos.profesor.id, 'ok');
                        limpiarForm();
                        cargarLista();
                    } else {
                        R.mensaje(r.datos.resultado || 'No se pudo guardar', 'error');
                    }
                });
            }

            function editar(id) {
                R.limpiar();
                R.llamar('GET', '/profesor/datos/' + id).then(function (r) {
                    if (!r.ok) { R.mensaje(r.datos.resultado || 'No encontrado', 'error'); return; }
                    var p = r.datos.profesor;
                    campoId.value = p.id;
                    campoNombre.value = p.nombre || '';
                    campoEmail.value = p.email || '';
                    document.getElementById('tituloForm').textContent = 'Editar profesor ' + p.id;
                    campoNombre.focus();
                });
            }

            function eliminar(id) {
                R.limpiar();
                R.llamar('DELETE', '/profesor/' + id).then(function (r) {
                    if (r.ok) { R.mensaje('Profesor eliminado', 'ok'); cargarLista(); }
                    else      { R.mensaje(r.datos.resultado || 'No se pudo eliminar', 'error'); }
                });
            }

            function limpiarForm() {
                // reset() devuelve cada campo a su valor inicial y borra también el
                // estado «ya tocado», para que no quede marcado como inválido
                document.getElementById('formulario').reset();
                campoId.value = '';
                document.getElementById('tituloForm').textContent = 'Nuevo profesor';
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
