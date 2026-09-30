# Gestión Universitaria

[![CI](https://github.com/gguillex/Gestion-Universitaria/actions/workflows/ci.yml/badge.svg)](https://github.com/gguillex/Gestion-Universitaria/actions/workflows/ci.yml)

Aplicación web para gestionar **titulaciones, asignaturas, profesorado, alumnado y matrículas**, con control de plazas, roles de usuario y exportación de listados. Está construida con Jakarta EE y JSP, siguiendo el patrón **Front Controller**, sin frameworks, y además expone una **API REST en JSON** sobre los mismos datos.

![Pantalla de acceso](docs/img/login.png)

## Qué puede hacer

- **Acceso y cuentas:** inicio de sesión, autorregistro y roles `admin` y `usuario`. Solo el administrador gestiona usuarios.
- **Catálogo académico:** alta, edición y baja de titulaciones, asignaturas, profesores y alumnos, y asignación de profesor a cada asignatura.
- **Matrículas:** matricular y desmatricular respetando la capacidad máxima de cada asignatura.
- **Listados:** alumnos matriculados por asignatura con indicador de plazas y **exportación a CSV**.
- **API REST:** las titulaciones, los profesores y las asignaturas también se gestionan por HTTP y JSON bajo `/rest/`, con pantallas de ejemplo (menú *API REST*) que la consumen con `fetch`. Exige sesión y token CSRF, y aplica las mismas reglas que la interfaz clásica. Ver [docs/rest.md](docs/rest.md).
- **Reglas de negocio:** no se puede eliminar una asignatura con alumnos matriculados ni bajar su capacidad por debajo de los ya matriculados.

![Listado de asignaturas](docs/img/asignaturas.png)

## Tecnología

| Capa | Tecnología |
|---|---|
| Lenguaje y plataforma | Java 17, Jakarta EE (Servlet 6, JSP 4, JSTL 3) |
| Servicio web | REST con Jersey (JAX-RS) 3.1 y JSON (`org.json`) |
| Servidor | Apache Tomcat 11 |
| Base de datos | MySQL 8 o MariaDB, con un pool JNDI |
| Acceso a datos | JDBC con `PreparedStatement` |
| Construcción | Maven (con Maven Wrapper) |
| Pruebas | JUnit 5 y Mockito |

## Puesta en marcha

Requisitos: **JDK 17 o superior**, **Tomcat 11** y **MySQL o MariaDB** (por ejemplo con XAMPP).

1. **Crear la base de datos** y cargar los datos de ejemplo:

   ```bash
   mysql -u root < db/gestion_universitaria.sql
   ```

2. **Revisar la conexión** en [`src/main/webapp/META-INF/context.xml`](src/main/webapp/META-INF/context.xml). Por defecto usa `root` sin contraseña, como una instalación local de XAMPP.

3. **Construir el WAR:**

   ```bash
   ./mvnw package        # en Windows: mvnw.cmd package
   ```

4. **Desplegarlo:** copiar `target/GestionUniversitaria.war` a la carpeta `webapps/` de Tomcat y abrir  
   <http://localhost:8080/GestionUniversitaria/>.

**Acceso de ejemplo:** usuario `admin`, contraseña `admin`. Cámbiala en cuanto entres (*Usuarios → Editar*).

> **Uso en producción:** este proyecto es una demostración de la arquitectura, no un producto listo para producción. Antes de exponerlo, cambia las credenciales de ejemplo, configura una contraseña para el usuario de la base de datos y sirve la aplicación por HTTPS.

## Pruebas

```bash
./mvnw test
```

67 pruebas automáticas sobre las reglas de negocio principales (login, control de acceso por rol, capacidad de las asignaturas y matrículas) y sobre el servicio REST (seguridad del filtro, servicios, errores y conversión JSON). No necesitan base de datos: las peticiones y los DAO se simulan con Mockito.

## API REST

Además de la interfaz web, los datos se pueden gestionar por HTTP y JSON bajo `/GestionUniversitaria/rest/`:

| Recurso | Ruta | Operaciones |
|---|---|---|
| Titulaciones | `/rest/titulacion` | listar, consultar, crear, modificar y eliminar |
| Profesores | `/rest/profesor` | listar, consultar, crear, modificar y eliminar |
| Asignaturas | `/rest/asignatura` | lo anterior, más asignar o quitar el profesor |

La API usa la misma sesión que la interfaz web. Las lecturas (`GET`) solo necesitan haber iniciado sesión; `POST`, `PUT` y `DELETE` envían además el token CSRF en la cabecera `X-CSRF-Token`. Las respuestas de error son siempre `{"resultado": "mensaje"}` con su código HTTP. Hay pantallas de ejemplo en el menú *API REST*, y los endpoints completos están en [docs/rest.md](docs/rest.md).

![Cliente REST de titulaciones](docs/img/api-rest.png)

## Seguridad

- Contraseñas con **PBKDF2-HMAC-SHA256** (210.000 iteraciones y sal aleatoria); las contraseñas en texto plano heredadas se convierten a hash en su primer acceso.
- **Protección CSRF** con un token por sesión en todos los formularios; las acciones que modifican datos solo aceptan `POST`.
- Cambio de identificador de sesión al iniciar sesión, **límite de intentos** de acceso (5 por usuario y 20 por dirección IP cada 15 minutos) y revalidación del usuario y su rol en cada petición.
- Consultas siempre con `PreparedStatement` y salida escapada con `<c:out>` en las vistas.
- **API REST protegida por el mismo filtro:** sin sesión responde 401; toda escritura (`POST`, `PUT`, `DELETE`) debe llevar el token CSRF en la cabecera `X-CSRF-Token`; el usuario se revalida en cada llamada; y los errores se devuelven como JSON sin detalles internos.

## Documentación

| Documento | Contenido |
|---|---|
| [Arquitectura](docs/arquitectura.md) | Flujo de una petición, paquetes, acciones, seguridad y modelo de datos |
| [Servicio REST](docs/rest.md) | Arquitectura por capas, seguridad, reglas y tabla de endpoints de la API |
| [MVC y REST](docs/mvc-y-rest.md) | Cómo conviven la interfaz JSP y la API REST: qué cambia, qué se comparte y por qué |
| [Guía de desarrollo](docs/guia-de-desarrollo.md) | Cómo ejecutar, probar y ampliar la aplicación |
| [Sistema de diseño](docs/diseno.md) | Colores, tipografía, componentes y reglas de accesibilidad |
| [Producto](docs/producto.md) | Usuarios, objetivos y principios del producto |

## Estructura

```
.
├── db/                       Script SQL: esquema y datos de ejemplo
├── docs/                     Documentación
├── src/
│   ├── main/
│   │   ├── java/gestion/     Código: filtro, controlador, acciones, modelo (DAO), utilidades y servicio REST
│   │   └── webapp/           Vistas JSP, cliente REST (rest-ui), hoja de estilos, fuentes y configuración de Tomcat
│   └── test/java/gestion/    Pruebas automáticas
├── pom.xml
└── mvnw, mvnw.cmd            Maven Wrapper: no hace falta instalar Maven
```

## Contribuir

Las contribuciones son bienvenidas: consulta la [guía de contribución](.github/CONTRIBUTING.md) y el [código de conducta](.github/CODE_OF_CONDUCT.md). Las vulnerabilidades se comunican en privado, como explica la [política de seguridad](.github/SECURITY.md).

## Licencia

Código bajo licencia [MIT](LICENSE). Las fuentes incluidas (Archivo y Big Shoulders Display) se distribuyen con su propia licencia SIL Open Font License; ver [`src/main/webapp/css/fonts/`](src/main/webapp/css/fonts/).
