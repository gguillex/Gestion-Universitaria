# Cómo contribuir

Gracias por querer ayudar. Este es un proyecto personal y las contribuciones son bienvenidas: errores, mejoras, documentación y pruebas.

## Antes de empezar

- Para un error o una idea, abre primero un [issue](https://github.com/gguillex/Gestion-Universitaria/issues/new/choose) y cuéntalo.
- Las vulnerabilidades **no** se comunican en un issue público: sigue la [política de seguridad](SECURITY.md).
- Al participar aceptas el [código de conducta](CODE_OF_CONDUCT.md).

## Entorno

Necesitas **JDK 17 o superior**, **Tomcat 11** y **MySQL o MariaDB**. Los pasos están en el [README](../README.md#puesta-en-marcha) y en la [guía de desarrollo](../docs/guia-de-desarrollo.md). No hace falta instalar Maven: el repositorio incluye el Maven Wrapper.

## Flujo de trabajo

1. Haz un *fork* y crea una rama desde `main` con un prefijo que diga de qué se trata: `feat/…`, `fix/…`, `docs/…` o `refactor/…`.
2. Haz los cambios, con pruebas si tocan una regla de negocio.
3. Comprueba que todo pasa en local: `./mvnw verify` (en Windows, `mvnw.cmd verify`).
4. Abre un *pull request* hacia `main` y rellena la plantilla. El CI ejecuta `./mvnw -B verify` con Java 17 y debe salir en verde.

## Convenciones del código

- **JSP:** solo JSTL y EL, sin scriptlets, y toda salida con `<c:out>`.
- **SQL:** solo en los DAO y siempre con `PreparedStatement`.
- **Lo que modifica datos va por `POST`** e incluye el token `csrfToken`. Un borrado es un formulario con la clase `form-borrar`, no un enlace.
- **API REST:** los controllers no llevan lógica ni SQL; las reglas van en el `Service`. Las escrituras del cliente envían el token en la cabecera `X-CSRF-Token` y el texto del servidor se inserta con `textContent`, nunca con `innerHTML`.
- **Estilos:** un único sitio, `src/main/webapp/css/style.css`, con variables CSS. Ningún JSP lleva `<style>`.
- **Accesibilidad:** una etiqueta por campo, foco visible y nombres accesibles en las acciones de cada fila.

El detalle de cada convención está en la [guía de desarrollo](../docs/guia-de-desarrollo.md) y en el [sistema de diseño](../docs/diseno.md).

## Mensajes de commit

Se usa el estilo de [Conventional Commits](https://www.conventionalcommits.org/es/v1.0.0/), en español y en minúsculas:

```
feat(rest): servicio REST con Jersey sobre los mismos datos
fix(a11y): reflujo a 320 px, salto al contenido y foco
docs: documentar el servicio REST
test: lógica de negocio de las acciones y regla de capacidad
```

Tipos habituales: `feat`, `fix`, `docs`, `refactor`, `test` y `chore`.

## Licencia

Al contribuir aceptas que tu aportación se publique bajo la licencia [MIT](../LICENSE) del proyecto.
