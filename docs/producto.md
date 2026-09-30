# Product

## Platform

web

## Users

- **Administración:** gestiona el catálogo (titulaciones, asignaturas, profesores, alumnos), matricula y desmatricula, asigna profesores a asignaturas. El rol `admin` además gestiona las cuentas de usuario.
- **Profesorado:** consulta y mantiene datos de estudios (asignaturas, alumnos, matriculados por asignatura).

Uso repartido a partes iguales entre **escritorio** (sesiones de trabajo con listados y formularios) y **móvil** (consultas y gestiones rápidas fuera del despacho). El móvil no es secundario.

## Product Purpose

Aplicación web interna para gestionar la información académica de una universidad: titulaciones, asignaturas, profesorado, alumnado, matrículas y usuarios. El éxito es que el personal complete cada gestión rápido, sin errores y con confianza en que los datos son correctos.

## Positioning

Herramienta interna, no un producto comercial: no compite por atención. Su valor está en la fiabilidad (reglas de negocio aplicadas siempre: capacidad máxima, no borrar asignaturas con alumnos, permisos por rol) y en la claridad al operar.

## Operating Context

- Flujo: iniciar sesión → menú principal → listado de una entidad → crear / editar / borrar / matricular.
- Idioma único: español.
- Mensajes de resultado tras cada acción (éxito o error de negocio) mostrados en la misma pantalla.
- Exportación CSV del listado de matriculados por asignatura.
- Autoregistro público de cuentas con rol `usuario`; el rol `admin` solo se asigna desde la gestión de usuarios.

## Capabilities and Constraints

- Stack fijo: Java (Jakarta EE) + Tomcat 11, vistas JSP con JSTL y EL. **Sin scriptlets** en las vistas.
- Arquitectura MVC con controlador frontal: las vistas son solo presentación; acciones y DAO no se tocan por motivos de diseño.
- Todo el estilo vive en una única hoja compartida, `src/main/webapp/css/style.css`, con variables CSS; **ningún JSP lleva `<style>` propio**. Clases existentes a reutilizar: `.topbar`, `.toolbar`, `.error`, `.ok`, `.badge`, `.auth`, `.form-borrar`.
- Formularios que modifican datos llevan campo oculto `csrfToken` y van por POST (incluidos los botones de borrar, que son formularios `.form-borrar`).
- La misma información está disponible por una **API REST en JSON** (titulaciones, profesores y asignaturas), protegida con la misma sesión, roles y token CSRF que la interfaz web; sus pantallas de ejemplo siguen el mismo sistema de diseño. Ver [rest.md](rest.md).
- Se permiten fuentes o librerías externas si aportan valor.

## Brand Commitments

- Nombre: **Gestión Universitaria** (marca genérica; no hay logo, colores ni institución concreta que respetar).
- Carácter pedido por el usuario: **sobrio, fiable, moderno**.

## Evidence on Hand

- No hay logotipo, fotografías ni material de marca. No inventar escudos, nombres de universidad ni imágenes institucionales.
- Datos reales disponibles solo los de la base de datos de ejemplo.

## Product Principles

1. **La tarea primero:** cada pantalla sirve para completar una gestión; nada decorativo compite con los datos.
2. **Confianza visible:** estados de éxito/error, confirmaciones de borrado y permisos siempre claros.
3. **Igual de bien en móvil que en escritorio:** listados y formularios usables a 390 px sin perder información.
4. **Coherencia:** un único sistema de estilos compartido; mismas piezas en todas las vistas.

## Accessibility & Inclusion

Objetivo **WCAG 2.2 AA**: contraste suficiente de texto y controles, navegación completa con teclado, foco visible, objetivos táctiles adecuados en móvil y textos legibles.
