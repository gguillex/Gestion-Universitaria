# Guía de desarrollo

## Entorno

- **JDK 17 o superior.** El código se compila para Java 17 (`<release>17</release>`).
- **Tomcat 11** (Jakarta EE). Con Tomcat 9 o anterior no funciona: usan el espacio de nombres `javax.*`.
- **MySQL 8 o MariaDB.** Cualquier instalación local sirve (por ejemplo XAMPP).
- No hace falta instalar Maven: el proyecto incluye el Maven Wrapper (`mvnw` / `mvnw.cmd`). Solo necesita `JAVA_HOME` apuntando al JDK.

## Comandos habituales

| Tarea | Comando |
|---|---|
| Ejecutar las pruebas | `./mvnw test` |
| Construir el WAR | `./mvnw package` |
| Limpiar y volver a construir | `./mvnw clean package` |
| Recrear la base de datos | `mysql -u root < db/gestion_universitaria.sql` |

En Windows, usa `mvnw.cmd` en lugar de `./mvnw`.

## Trabajar con Eclipse

1. **File → Import → Existing Maven Projects** y elegir la carpeta del repositorio.
2. Añadir un servidor **Tomcat v11** y el proyecto a él.
3. Comprobar en **Properties → Deployment Assembly** que aparece `Maven Dependencies → WEB-INF/lib`. Si falta, pulsar *Add… → Java Build Path Entries → Maven Dependencies*.

### Problemas conocidos

- **`Imposible obtener recurso JAR [/WEB-INF/vistas/jakarta.tags.core]`** al abrir cualquier página: Tomcat no tiene las librerías de la aplicación (JSTL, conector MySQL). Es el paso 3 anterior. Hay que repetirlo si *Maven → Update Project* lo elimina.
- **Errores rojos `TagExtraInfo … ForEachTEI` en los `.jsp`:** son falsos positivos del validador de JSP de Eclipse con JSTL 3 (Jakarta). La aplicación funciona. Se pueden ocultar desactivando el validador *JSP Content Validator* en **Window → Preferences → Validation**.

## Cómo añadir un campo a una entidad

Ejemplo: añadir `creditos` a las asignaturas. Son siempre cuatro sitios, y conviene seguir este orden:

1. **Base de datos.** Añadir la columna en `db/gestion_universitaria.sql` (y, en una base ya creada, `ALTER TABLE asignaturas ADD creditos INT NOT NULL DEFAULT 6;`).
2. **Bean.** Campo, constructor, `get` y `set` en `gestion/bean/Asignatura.java`.
3. **DAO.** La columna en las consultas de `gestion/modelo/AsignaturaDAO.java` (`SELECT`, `INSERT` y `UPDATE`) y en el método que construye el objeto desde el `ResultSet`.
4. **Acción y vistas.** Leer y validar el parámetro en `gestion/accion/asignaturas/AccionGuardarAsignatura.java`; añadir el campo a `asignaturas/formulario.jsp` y mostrarlo en `asignaturas/lista.jsp`.

## Cómo exponer una entidad por REST

Si la entidad ya existe en el MVC, basta con:

1. Un `Controller` en `gestion/rest/controller/` con `@Path("/entidad")` y sus métodos `@GET`, `@POST`, `@PUT` y `@DELETE`.
2. Un `Service` en `gestion/rest/service/` con la validación y las reglas de negocio, que lance `ApiException` (400, 404, 409).
3. Las conversiones bean ↔ JSON en `ParserObject`.

No hay que tocar `web.xml` (Jersey escanea el paquete `gestion.rest`), ni el filtro, ni el `ErrorMapper`. Los detalles y la seguridad están en [rest.md](rest.md). Al añadir un campo a una entidad hay que actualizar también `ParserObject` y, si tiene regla de negocio, su `Service`.

## Cómo añadir una entidad nueva

1. Tabla en `db/gestion_universitaria.sql`, bean y DAO.
2. Las acciones `listar`, `form`, `guardar` y `eliminar` en un paquete nuevo de `gestion.accion`.
3. Registrarlas en `ControlServlet.init()` con su `idAccion`.
4. Dos vistas (`lista.jsp` y `formulario.jsp`) y un enlace en `menu.jsp`.
5. Pruebas de las reglas de negocio que tenga.

## Convenciones

- **Nada de lógica en los JSP.** Solo JSTL y EL, sin scriptlets, y toda salida con `<c:out>`.
- **Nada de SQL fuera de los DAO**, y siempre con `PreparedStatement`.
- **En la API REST**, los controllers no llevan lógica ni SQL, las reglas van en el `Service`, y las escrituras del cliente envían el token CSRF en la cabecera `X-CSRF-Token`. Los textos que llegan del servidor se insertan con `textContent`, nunca con `innerHTML`.
- **Lo que modifica datos va por `POST`** y su formulario lleva `<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">`. Un borrado es un formulario pequeño con la clase `form-borrar`, no un enlace.
- **Un solo sitio para el estilo:** `src/main/webapp/css/style.css`, con variables CSS. Ningún JSP lleva `<style>`. Las piezas reutilizables están descritas en [diseno.md](diseno.md).
- **Formularios accesibles:** un `<label>` por campo, un `<span class="campo-error">` con el mensaje de validación y los botones dentro de `.acciones-form`.
- **Listados:** `class="tabla-placas"` con `data-label` en cada celda para que se conviertan en tarjetas en móvil, y `aria-label` en las acciones de cada fila con el nombre del registro.

## Pruebas

Las pruebas están en `src/test/java/gestion/` y usan JUnit 5 y Mockito. Las del servicio REST cubren el filtro (sesión, token y usuario vigente), los servicios y la conversión de errores. Las acciones crean sus DAO con `new`, así que se interceptan con `mockConstruction`; para el DAO de matrículas se simula solo la conexión JDBC y el método se ejecuta de verdad. Antes de dar por buena una prueba de una regla, conviene comprobar que **falla** si se rompe la regla a propósito.

## Integración continua

Cada `push` a `main` y cada *pull request* ejecutan `./mvnw verify` con Java 17 (`.github/workflows/ci.yml`).
