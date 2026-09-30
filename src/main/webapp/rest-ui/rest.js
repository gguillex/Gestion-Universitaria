/*
 * Utilidades del cliente del servicio REST. Cada página lee de aquí las llamadas HTTP
 * y el pintado de filas y mensajes; nada se inserta con innerHTML: todo el texto que llega
 * del servidor entra por textContent, así que no puede inyectar marcado.
 *
 * Las páginas declaran en <head>:
 *   <meta name="app-base"   content="/GestionUniversitaria">   (ruta de contexto)
 *   <meta name="csrf-token" content="...">                      (token de la sesión)
 */
(function () {
    'use strict';

    function meta(nombre) {
        var m = document.querySelector('meta[name="' + nombre + '"]');
        return m ? m.content : '';
    }

    var BASE = meta('app-base');
    var TOKEN = meta('csrf-token');

    /**
     * Llama al servicio y devuelve { ok, estado, datos }. Las escrituras llevan el token CSRF
     * en la cabecera X-CSRF-Token. Si la sesión ha caducado (401) vuelve al login.
     */
    function llamar(metodo, ruta, cuerpo) {
        var opciones = { method: metodo, headers: { 'Accept': 'application/json' }, credentials: 'same-origin' };
        if (metodo !== 'GET') {
            opciones.headers['X-CSRF-Token'] = TOKEN;
        }
        if (cuerpo !== undefined) {
            opciones.headers['Content-Type'] = 'application/json';
            opciones.body = JSON.stringify(cuerpo);
        }
        return fetch(BASE + '/rest' + ruta, opciones).then(function (r) {
            if (r.status === 401) {
                window.location.href = BASE + '/control?idAccion=mostrarLogin';
            }
            return r.json().catch(function () { return {}; }).then(function (datos) {
                return { ok: r.ok, estado: r.status, datos: datos };
            });
        });
    }

    var aviso = null;
    function mensaje(texto, tipo) {
        if (!aviso) aviso = document.getElementById('mensaje');
        aviso.textContent = texto || '';
        aviso.className = texto ? tipo : '';
        aviso.setAttribute('role', tipo === 'error' ? 'alert' : 'status');
    }
    function limpiar() { mensaje('', ''); }

    /** Crea un elemento con atributos y texto (siempre como texto, nunca como HTML). */
    function el(etiqueta, atributos, texto) {
        var e = document.createElement(etiqueta);
        Object.keys(atributos || {}).forEach(function (k) { e.setAttribute(k, atributos[k]); });
        if (texto !== undefined && texto !== null) e.textContent = texto;
        return e;
    }

    /** Celda de tabla con la etiqueta que usa la vista de tarjetas en móvil. */
    function celda(texto, etiqueta, clase) {
        var td = el('td', { 'role': 'cell', 'data-label': etiqueta });
        if (clase) td.className = clase;
        if (texto !== null && texto !== undefined && texto !== '') td.textContent = texto;
        return td;
    }

    function celdaId(id) {
        var td = el('td', { 'role': 'cell', 'class': 'celda-id', 'data-label': 'ID' });
        td.appendChild(el('span', { 'class': 'placa' }, id));
        return td;
    }

    /** Celda con los botones Editar y Eliminar de una fila. */
    function celdaAcciones(nombre, alEditar, alEliminar) {
        var td = el('td', { 'role': 'cell', 'class': 'celda-acciones' });

        var editar = el('a', { 'href': '#', 'aria-label': 'Editar ' + nombre }, 'Editar');
        editar.addEventListener('click', function (ev) { ev.preventDefault(); alEditar(); });
        td.appendChild(editar);

        var form = el('form', { 'class': 'form-borrar' });
        form.appendChild(el('button', { 'type': 'submit', 'aria-label': 'Eliminar ' + nombre }, 'Eliminar'));
        form.addEventListener('submit', function (ev) {
            ev.preventDefault();
            if (window.confirm('¿Eliminar ' + nombre + '?')) alEliminar();
        });
        td.appendChild(form);
        return td;
    }

    /** Sustituye las filas de la tabla; si no hay ninguna, muestra el aviso de tabla vacía. */
    function pintarFilas(cuerpo, filas, columnas, textoVacio) {
        cuerpo.replaceChildren.apply(cuerpo, filas);
        if (filas.length === 0) {
            var tr = el('tr', { 'role': 'row', 'class': 'fila-vacia' });
            var td = el('td', { 'role': 'cell', 'colspan': String(columnas), 'class': 'vacio' });
            td.appendChild(el('strong', { 'class': 'vacio-titulo' }, textoVacio));
            tr.appendChild(td);
            cuerpo.appendChild(tr);
        }
    }

    /** Convierte '' en null (campo opcional sin rellenar). */
    function opcional(valor) {
        var v = String(valor).trim();
        return v === '' ? null : v;
    }

    window.Rest = {
        llamar: llamar, mensaje: mensaje, limpiar: limpiar, el: el,
        celda: celda, celdaId: celdaId, celdaAcciones: celdaAcciones,
        pintarFilas: pintarFilas, opcional: opcional
    };
})();
