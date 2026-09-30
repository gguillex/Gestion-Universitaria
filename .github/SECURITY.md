# Política de seguridad

## Versiones con soporte

| Versión | Soporte |
|---|---|
| 1.1.x | ✅ Recibe correcciones de seguridad |
| 1.0.x | ❌ Actualiza a la 1.1.x |

## Cómo informar de una vulnerabilidad

**No abras un issue público.** Usa el aviso privado de GitHub:

1. Ve a la pestaña **Security** del repositorio.
2. Pulsa **Report a vulnerability** (o entra directamente en [este enlace](https://github.com/gguillex/Gestion-Universitaria/security/advisories/new)).
3. Describe el problema e incluye, si puedes:
   - la versión afectada y el entorno (Tomcat, JDK, base de datos);
   - los pasos para reproducirlo;
   - el impacto que crees que tiene.

## Qué puedes esperar

Es un proyecto mantenido por una sola persona, sin plazos garantizados. El objetivo es acusar recibo en una semana, confirmar si se trata de una vulnerabilidad y publicar la corrección en una nueva versión. Si lo deseas, se te menciona como autor del hallazgo.

## Alcance

**Dentro del alcance:** autenticación y sesión, control de roles, protección CSRF, inyección SQL, XSS, el almacenamiento de contraseñas y la API REST (`/rest/*`).

**Fuera del alcance:**

- Las credenciales de ejemplo (`admin` / `admin`) y el usuario `root` sin contraseña de `context.xml`: están documentadas como solo para desarrollo local.
- Despliegues sin HTTPS o con la configuración de ejemplo sin cambiar.
- Avisos genéricos sobre dependencias sin un vector de ataque concreto en este proyecto.

Las medidas de seguridad que ya incluye la aplicación están resumidas en la sección *Seguridad* del [README](../README.md#seguridad).
