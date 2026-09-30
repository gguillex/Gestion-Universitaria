# Sistema de Gestión Universitaria

Aplicación web Java EE para la gestión de una universidad (titulaciones, asignaturas, profesores y usuarios), construida sobre el patrón **MVC con Front Controller**.

**Tecnologías:** Java EE · Apache Tomcat · MySQL · JSTL · JDBC · Maven

---

## Índice

1. [Introducción y objetivos](#1-introducción-y-objetivos)
2. [Tecnologías y entorno de desarrollo](#2-tecnologías-y-entorno-de-desarrollo)
3. [Arquitectura del sistema](#3-arquitectura-del-sistema)
4. [Diseño de la base de datos](#4-diseño-de-la-base-de-datos)
5. [Implementación por capas](#5-implementación-por-capas)
   - 5.1 Filtro de seguridad
   - 5.2 Controlador frontal
   - 5.3 Interfaz Accion y clases de acción
   - 5.4 Capa de modelo (DAOs y beans)
   - 5.5 Capa de vista (JSPs)
6. [Funcionalidades implementadas](#6-funcionalidades-implementadas)
7. [Seguridad y control de acceso](#7-seguridad-y-control-de-acceso)
8. [Configuración y despliegue](#8-configuración-y-despliegue)
9. [Estructura de directorios](#9-estructura-de-directorios)
10. [Conclusiones](#10-conclusiones)

---

## 1. Introducción y objetivos

Este proyecto es una aplicación web J2EE para la gestión de una universidad. Permite administrar titulaciones, asignaturas, profesores y usuarios del sistema, aplicando el patrón arquitectónico **MVC (Modelo-Vista-Controlador)** con un **Controlador Frontal único** (*Front Controller Pattern*).

Los objetivos principales son:

- Implementar el patrón MVC mediante las tecnologías propias de Java EE (Servlets, Filtros, JSP, JSTL).
- Separar claramente las responsabilidades en capas: presentación, negocio y acceso a datos.
- Gestionar la autenticación y la sesión de usuario de forma centralizada.
- Realizar operaciones CRUD completas sobre las entidades del dominio.
- Aplicar reglas de negocio y control de acceso según el rol del usuario.

---

## 2. Tecnologías y entorno de desarrollo

| Componente | Tecnología / Versión |
|---|---|
| Lenguaje | Java 17 |
| Especificación web | Jakarta EE (Servlet 5.0, JSP 3.0, JSTL 2.0) |
| Servidor de aplicaciones | Apache Tomcat 10.x |
| Base de datos | MySQL 8 (vía XAMPP) |
| Acceso a datos | JDBC con `PreparedStatement` |
| Pool de conexiones | DBCP2 (gestionado por Tomcat vía JNDI) |
| Construcción | Apache Maven |
| IDE | IntelliJ IDEA |

El proyecto sigue la estructura estándar de Maven para aplicaciones web (`src/main/java`, `src/main/webapp`). Las dependencias de Servlet, JSTL y el driver MySQL se declaran en el `pom.xml`.

---

## 3. Arquitectura del sistema

La arquitectura sigue un diseño Front Controller clásico, con seis piezas bien diferenciadas:

```
  NAVEGADOR
      │
      ▼
 SeguridadFiltro          ← intercepta TODAS las peticiones
      │  (si no hay sesión → redirige a login)
      ▼
 ControlServlet           ← único servlet, recibe idAccion
      │  (busca en HashMap<String, Accion>)
      ▼
 AccionXxx.ejecutar()     ← lógica de negocio
      │  (llama a DAOs, guarda en request/session)
      ▼
 XxxDAO                   ← SQL puro con JDBC
      │
      ▼
 MySQL (XAMPP)
      │
      ▼
 AccionXxx devuelve ruta JSP
      │
      ▼
 ControlServlet hace forward
      │
      ▼
 JSP (JSTL + EL)          ← solo presentación
      │
      ▼
  NAVEGADOR (HTML)
```

Este diseño garantiza que:

- **Toda la navegación** pasa por un único punto de entrada: `/control?idAccion=xxx`.
- **El controlador** no contiene lógica de negocio: solo busca y delega.
- **Los JSPs** no contienen código Java: solo expresiones EL y etiquetas JSTL.
- **Los DAOs** no toman decisiones: solo ejecutan SQL y devuelven objetos Java.

---

## 4. Diseño de la base de datos

La base de datos se llama `gestion_universitaria` y contiene cuatro tablas principales. El script completo se entrega en `WEB-INF/gestion_universitaria.sql`.

### Diagrama de tablas

```
usuarios
─────────────────────────
PK  id          INT AUTO_INCREMENT
    nombre      VARCHAR(100) UNIQUE NOT NULL
    password    VARCHAR(255) NOT NULL
    rol         VARCHAR(20) NOT NULL  ('admin' | 'usuario')

titulaciones
─────────────────────────
PK  id          INT AUTO_INCREMENT
    nombre      VARCHAR(200) NOT NULL
    descripcion TEXT

profesores
─────────────────────────
PK  id          INT AUTO_INCREMENT
    nombre      VARCHAR(200) NOT NULL
    email       VARCHAR(200)

asignaturas
─────────────────────────
PK  id               INT AUTO_INCREMENT
    nombre           VARCHAR(200) NOT NULL
    capacidad_maxima INT NOT NULL DEFAULT 30
FK  id_titulacion    INT NOT NULL → titulaciones(id)
FK  id_profesor      INT NULL     → profesores(id)
```

La relación entre `asignaturas` y `profesores` es **N:1** opcional (una asignatura puede no tener profesor asignado). Al eliminar un profesor, sus asignaturas quedan con `id_profesor = NULL` (se gestiona en el DAO con una transacción).

### Script SQL

```sql
DROP TABLE IF EXISTS asignaturas;
DROP TABLE IF EXISTS titulaciones;
DROP TABLE IF EXISTS profesores;
DROP TABLE IF EXISTS usuarios;

CREATE TABLE usuarios (
    id       INT          AUTO_INCREMENT PRIMARY KEY,
    nombre   VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol      VARCHAR(20)  NOT NULL DEFAULT 'usuario'
);

CREATE TABLE titulaciones (
    id          INT          AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(200) NOT NULL,
    descripcion TEXT
);

CREATE TABLE profesores (
    id      INT          AUTO_INCREMENT PRIMARY KEY,
    nombre  VARCHAR(200) NOT NULL,
    email   VARCHAR(200)
);

CREATE TABLE asignaturas (
    id               INT          AUTO_INCREMENT PRIMARY KEY,
    nombre           VARCHAR(200) NOT NULL,
    capacidad_maxima INT          NOT NULL DEFAULT 30,
    id_titulacion    INT          NOT NULL,
    id_profesor      INT,
    FOREIGN KEY (id_titulacion) REFERENCES titulaciones(id)
);

INSERT INTO usuarios (nombre, password, rol) VALUES ('admin', 'admin', 'admin');
```

---

## 5. Implementación por capas

### 5.1 Filtro de seguridad — `SeguridadFiltro.java`

El filtro intercepta todas las peticiones antes de que lleguen al servlet. Su única responsabilidad es decidir si la petición puede continuar o si debe redirigirse al login.

```java
@WebFilter("/*")
public class SeguridadFiltro implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest)  req;
        HttpServletResponse response = (HttpServletResponse) res;

        String idAccion = request.getParameter("idAccion");
        HttpSession sesion = request.getSession(false);
        boolean estaLogueado = (sesion != null && sesion.getAttribute("usuarioLogueado") != null);

        boolean esAccionPublica = "mostrarLogin".equals(idAccion) || "login".equals(idAccion);
        String uri = request.getRequestURI();
        boolean esRecursoEstatico =
                uri.endsWith(".css") || uri.endsWith(".js")  ||
                uri.endsWith(".png") || uri.endsWith(".jpg") ||
                uri.endsWith(".gif") || uri.endsWith(".ico");
        boolean esIndex = uri.endsWith("/") || uri.endsWith("/index.jsp");

        if (estaLogueado || esAccionPublica || esRecursoEstatico || esIndex) {
            chain.doFilter(req, res);
        } else {
            response.sendRedirect(request.getContextPath() + "/control?idAccion=mostrarLogin");
        }
    }
}
```

La anotación `@WebFilter("/*")` garantiza que el filtro se aplica a **todas** las peticiones. La lógica distingue tres casos que se dejan pasar sin sesión: acciones públicas (login), recursos estáticos y la raíz del contexto.

---

### 5.2 Controlador frontal — `ControlServlet.java`

Es el único Servlet de la aplicación. En su método `init()` registra todas las acciones disponibles en un `HashMap`. Al recibir una petición, busca la acción por `idAccion`, la ejecuta y hace forward al JSP que devuelve.

```java
@WebServlet("/control")
public class ControlServlet extends HttpServlet {

    private Map<String, Accion> acciones;

    @Override
    public void init() {
        acciones = new HashMap<>();
        acciones.put("mostrarLogin",        new AccionMostrarLogin());
        acciones.put("login",               new AccionLogin());
        acciones.put("logout",              new AccionLogout());
        acciones.put("listarTitulaciones",  new AccionListarTitulaciones());
        acciones.put("formTitulacion",      new AccionFormTitulacion());
        acciones.put("guardarTitulacion",   new AccionGuardarTitulacion());
        acciones.put("eliminarTitulacion",  new AccionEliminarTitulacion());
        acciones.put("listarUsuarios",      new AccionListarUsuarios());
        acciones.put("formUsuario",         new AccionFormUsuario());
        acciones.put("guardarUsuario",      new AccionGuardarUsuario());
        acciones.put("eliminarUsuario",     new AccionEliminarUsuario());
        acciones.put("listarAsignaturas",   new AccionListarAsignaturas());
        acciones.put("formAsignatura",      new AccionFormAsignatura());
        acciones.put("guardarAsignatura",   new AccionGuardarAsignatura());
        acciones.put("eliminarAsignatura",  new AccionEliminarAsignatura());
        acciones.put("listarProfesores",    new AccionListarProfesores());
        acciones.put("formProfesor",        new AccionFormProfesor());
        acciones.put("guardarProfesor",     new AccionGuardarProfesor());
        acciones.put("eliminarProfesor",    new AccionEliminarProfesor());
        acciones.put("asignarProfesor",     new AccionAsignarProfesor());
    }

    private void procesar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idAccion = request.getParameter("idAccion");
        if (idAccion == null || !acciones.containsKey(idAccion)) {
            response.sendRedirect(request.getContextPath() + "/control?idAccion=mostrarLogin");
            return;
        }
        try {
            Accion accion = acciones.get(idAccion);
            String jspDestino = accion.ejecutar(request, response);
            if (jspDestino != null) {
                request.getRequestDispatcher(jspDestino).forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/vistas/error.jsp").forward(request, response);
        }
    }
}
```

Puntos clave del diseño:
- Si `idAccion` no existe en el mapa, redirige a login (nunca lanza un error 500 por acción desconocida).
- Si la acción devuelve `null`, significa que ya hizo un `sendRedirect` internamente.
- Cualquier excepción no controlada se captura y se muestra en la vista de error.

---

### 5.3 Interfaz `Accion` y clases de acción

La interfaz define el contrato que deben cumplir todas las acciones:

```java
package gestion.accion;

public interface Accion {
    String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
```

El retorno `String` es la ruta del JSP al que hacer forward, o `null` si la acción ya realizó una redirección.

#### Acciones de autenticación

`AccionLogin` valida las credenciales contra la base de datos. Si son correctas, crea la sesión y redirige al listado de titulaciones. Si no, devuelve el JSP de login con un mensaje de error:

```java
public class AccionLogin implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String nombre   = request.getParameter("nombre");
        String password = request.getParameter("password");

        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuario = dao.validarCredenciales(nombre, password);

        if (usuario != null) {
            HttpSession sesion = request.getSession();
            sesion.setAttribute("usuarioLogueado", usuario);
            response.sendRedirect(request.getContextPath() + "/control?idAccion=listarTitulaciones");
            return null;
        } else {
            request.setAttribute("errorLogin", "Usuario o contraseña incorrectos");
            return "/WEB-INF/vistas/login.jsp";
        }
    }
}
```

`AccionLogout` invalida la sesión y redirige al login:

```java
public class AccionLogout implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession sesion = request.getSession(false);
        if (sesion != null) sesion.invalidate();
        response.sendRedirect(request.getContextPath() + "/control?idAccion=mostrarLogin");
        return null;
    }
}
```

#### Acciones CRUD — ejemplo con Titulaciones

El patrón es idéntico para las cuatro entidades. Se muestra el caso de titulaciones como referencia:

**Listar:**
```java
public class AccionListarTitulaciones implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        TitulacionDAO dao = new TitulacionDAO();
        request.setAttribute("titulaciones", dao.listar());
        return "/WEB-INF/vistas/titulaciones/lista.jsp";
    }
}
```

**Guardar (insertar o actualizar):**
```java
public class AccionGuardarTitulacion implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String idParam      = request.getParameter("id");
        String nombre       = request.getParameter("nombre");
        String descripcion  = request.getParameter("descripcion");

        Titulacion t = new Titulacion();
        t.setNombre(nombre);
        t.setDescripcion(descripcion);

        TitulacionDAO dao = new TitulacionDAO();
        if (idParam != null && !idParam.isEmpty()) {
            t.setId(Integer.parseInt(idParam));
            dao.actualizar(t);
        } else {
            dao.insertar(t);
        }

        response.sendRedirect(request.getContextPath() + "/control?idAccion=listarTitulaciones");
        return null;
    }
}
```

Una sola acción cubre tanto la creación como la edición: si el parámetro `id` viene relleno, actualiza; si no, inserta.

#### Acción de asignación profesor — lógica especial

`AccionAsignarProfesor` tiene un comportamiento doble: si no recibe parámetros muestra el formulario de selección; si los recibe, ejecuta la asignación:

```java
public class AccionAsignarProfesor implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String idAsigParam = request.getParameter("idAsignatura");
        String idProfParam = request.getParameter("idProfesor");

        if (idAsigParam == null) {
            request.setAttribute("asignaturas", new AsignaturaDAO().listar());
            request.setAttribute("profesores",  new ProfesorDAO().listar());
            return "/WEB-INF/vistas/profesores/asignar.jsp";
        }

        int idAsignatura = Integer.parseInt(idAsigParam);
        Integer idProfesor = (idProfParam != null && !idProfParam.isEmpty())
                ? Integer.parseInt(idProfParam) : null;

        new AsignaturaDAO().asignarProfesor(idAsignatura, idProfesor);
        response.sendRedirect(request.getContextPath() + "/control?idAccion=listarAsignaturas");
        return null;
    }
}
```

---

### 5.4 Capa de modelo — Beans y DAOs

#### Beans (POJOs)

Los beans son clases Java puras sin lógica. Almacenan el estado de una entidad del dominio. Ejemplo con `Asignatura`, que incluye campos de visualización calculados por JOIN en el DAO:

```java
public class Asignatura {
    private int     id;
    private String  nombre;
    private int     capacidadMaxima;
    private int     idTitulacion;
    private Integer idProfesor;          // null si no tiene profesor
    private String  nombreTitulacion;    // calculado por JOIN
    private String  nombreProfesor;      // calculado por JOIN

    // Constructor vacío y constructor completo
    public Asignatura() {}
    public Asignatura(int id, String nombre, int capacidadMaxima,
                      int idTitulacion, Integer idProfesor) { ... }

    // Getters y setters para todos los campos
}
```

El uso de `Integer` (en lugar de `int`) para `idProfesor` permite representar el valor `NULL` de la base de datos.

#### `ConexionBD.java` — pool JNDI

La conexión a la base de datos se obtiene a través del pool configurado en Tomcat vía JNDI, evitando abrir una conexión nueva por petición:

```java
public class ConexionBD {

    public static Connection getConexion() throws Exception {
        Context ctx = new InitialContext();
        DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/gestion");
        return ds.getConnection();
    }
}
```

#### DAOs — acceso a datos con JDBC

Cada DAO encapsula exclusivamente las operaciones SQL de su entidad. Se usa siempre `PreparedStatement` para prevenir inyección SQL y el patrón `try-with-resources` para garantizar el cierre de conexiones.

Ejemplo — `TitulacionDAO.listar()`:

```java
public List<Titulacion> listar() throws Exception {
    List<Titulacion> lista = new ArrayList<>();
    String sql = "SELECT id, nombre, descripcion FROM titulaciones ORDER BY nombre";
    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            lista.add(new Titulacion(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("descripcion")
            ));
        }
    }
    return lista;
}
```

Ejemplo más complejo — `AsignaturaDAO.listar()` con JOIN:

```java
public List<Asignatura> listar() throws Exception {
    List<Asignatura> lista = new ArrayList<>();
    String sql =
        "SELECT a.id, a.nombre, a.capacidad_maxima, a.id_titulacion, a.id_profesor, " +
        "       t.nombre AS nombre_titulacion, p.nombre AS nombre_profesor " +
        "FROM asignaturas a " +
        "JOIN titulaciones t ON t.id = a.id_titulacion " +
        "LEFT JOIN profesores p ON p.id = a.id_profesor " +
        "ORDER BY a.nombre";
    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            lista.add(construir(rs));
        }
    }
    return lista;
}
```

El `LEFT JOIN` con profesores permite que las asignaturas sin profesor asignado aparezcan igualmente en el listado.

Ejemplo de operación con transacción — `ProfesorDAO.eliminar()`: al eliminar un profesor hay que desvincular primero sus asignaturas para no dejar FK inconsistentes:

```java
public void eliminar(int id) throws Exception {
    String sql1 = "UPDATE asignaturas SET id_profesor = NULL WHERE id_profesor = ?";
    String sql2 = "DELETE FROM profesores WHERE id = ?";
    try (Connection con = ConexionBD.getConexion()) {
        con.setAutoCommit(false);
        try (PreparedStatement ps1 = con.prepareStatement(sql1);
             PreparedStatement ps2 = con.prepareStatement(sql2)) {
            ps1.setInt(1, id); ps1.executeUpdate();
            ps2.setInt(1, id); ps2.executeUpdate();
            con.commit();
        } catch (Exception e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }
}
```

---

### 5.5 Capa de vista — JSPs con JSTL

Los JSPs no contienen ningún scriptlet Java (`<% %>`). Toda la lógica de presentación se expresa con etiquetas JSTL y expresiones EL (`${...}`).

#### Ejemplo — lista de titulaciones

```jsp
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Titulaciones</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        table { border-collapse: collapse; width: 80%; }
        th, td { border: 1px solid #999; padding: 6px 10px; }
        th { background-color: #ddd; }
    </style>
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/menu.jsp"/>
    <h2>Titulaciones</h2>
    <p>
        <a href="${pageContext.request.contextPath}/control?idAccion=formTitulacion">Nueva titulación</a>
    </p>

    <c:if test="${not empty error}">
        <p style="color:red">${error}</p>
    </c:if>

    <table>
        <tr><th>ID</th><th>Nombre</th><th>Descripción</th><th>Acciones</th></tr>
        <c:forEach var="t" items="${titulaciones}">
        <tr>
            <td>${t.id}</td>
            <td><c:out value="${t.nombre}"/></td>
            <td><c:out value="${t.descripcion}"/></td>
            <td>
                <a href="${pageContext.request.contextPath}/control?idAccion=formTitulacion&id=${t.id}">Editar</a>
                <a href="${pageContext.request.contextPath}/control?idAccion=eliminarTitulacion&id=${t.id}"
                   onclick="return confirm('¿Eliminar?')">Eliminar</a>
            </td>
        </tr>
        </c:forEach>
    </table>
</body>
</html>
```

Los datos llegan al JSP mediante `request.getAttribute("titulaciones")`, que en EL se accede directamente como `${titulaciones}`. Los campos del bean se acceden con notación punto: `${t.nombre}` invoca `t.getNombre()`.

#### Menu compartido — `menu.jsp`

Para evitar repetir la barra de navegación en cada JSP se utiliza `<jsp:include>`:

```jsp
<nav>
    <a href="...?idAccion=listarTitulaciones">Titulaciones</a>
    <a href="...?idAccion=listarAsignaturas">Asignaturas</a>
    <a href="...?idAccion=listarProfesores">Profesores</a>
    <c:if test="${sessionScope.usuarioLogueado.rol == 'admin'}">
        <a href="...?idAccion=listarUsuarios">Usuarios</a>
    </c:if>
    <a href="...?idAccion=logout">Cerrar sesión (${sessionScope.usuarioLogueado.nombre})</a>
</nav>
```

El enlace a "Usuarios" solo aparece en el menú si el usuario logueado tiene rol `admin`, ocultándolo visualmente a los demás usuarios.

---

## 6. Funcionalidades implementadas

### 6.1 Autenticación

- Login con usuario y contraseña contra la tabla `usuarios`.
- Sesión guardada en `HttpSession` con el objeto `Usuario` bajo la clave `usuarioLogueado`.
- Logout que invalida la sesión completa.
- El filtro protege todas las rutas excepto las acciones de login.

### 6.2 CRUD de Titulaciones

| Acción | `idAccion` | Método HTTP | Descripción |
|--------|-----------|-------------|-------------|
| Listar | `listarTitulaciones` | GET | Muestra tabla con todas las titulaciones |
| Formulario nuevo | `formTitulacion` | GET | Formulario vacío |
| Formulario editar | `formTitulacion&id=X` | GET | Formulario pre-relleno |
| Guardar | `guardarTitulacion` | POST | Inserta o actualiza según `id` |
| Eliminar | `eliminarTitulacion&id=X` | GET | Elimina y redirige al listado |

### 6.3 CRUD de Usuarios (solo admin)

Mismo esquema que titulaciones. Las cuatro acciones verifican al inicio que el usuario logueado tiene rol `admin`; si no, redirigen al listado de titulaciones sin ejecutar la operación.

```java
Usuario usuarioLogueado = (Usuario) request.getSession(false).getAttribute("usuarioLogueado");
if (usuarioLogueado == null || !"admin".equals(usuarioLogueado.getRol())) {
    response.sendRedirect(request.getContextPath() + "/control?idAccion=listarTitulaciones");
    return null;
}
```

### 6.4 CRUD de Asignaturas

Además del CRUD básico, el formulario de asignatura incluye:
- Selector desplegable de titulación (cargado desde `TitulacionDAO.listar()`).
- Selector opcional de profesor (cargado desde `ProfesorDAO.listar()`).
- Campo de capacidad máxima con valor por defecto 30.

### 6.5 CRUD de Profesores y asignación

- CRUD completo: crear, editar, eliminar profesores.
- Al eliminar un profesor, sus asignaturas quedan con `id_profesor = NULL` (mediante transacción).
- Acción `asignarProfesor`: formulario dedicado que permite elegir una asignatura y un profesor y asociarlos.

---

## 7. Seguridad y control de acceso

### Autenticación

La sesión se verifica en dos niveles:

1. **Filtro (`SeguridadFiltro`):** comprobación global. Si no hay sesión activa y la acción no es pública, redirige al login antes de que el controlador llegue a ejecutarse.

2. **Acción individual:** las acciones de administración repiten la comprobación de rol para garantizar que no se puede acceder aunque se manipule la URL.

### Prevención de inyección SQL

Todo acceso a la base de datos usa `PreparedStatement` con parámetros `?`. Nunca se concatena directamente la entrada del usuario en una cadena SQL.

```java
// Correcto — parámetro enlazado
String sql = "SELECT * FROM usuarios WHERE nombre = ? AND password = ?";
ps.setString(1, nombre);
ps.setString(2, password);

// Incorrecto — vulnerable a SQL Injection (NO se usa en este proyecto)
// String sql = "SELECT * FROM usuarios WHERE nombre = '" + nombre + "'";
```

### Contraseñas

Actualmente las contraseñas se almacenan en texto plano (ver Limitaciones). En un sistema real deberían guardarse con un algoritmo de hash seguro como `bcrypt` o `Argon2`, nunca en claro.

### XSS

En los JSPs se usa `<c:out value="${variable}"/>` para mostrar datos provenientes de la base de datos, lo que escapa automáticamente caracteres como `<`, `>` y `"`, previniendo ataques XSS.

---

## 8. Configuración y despliegue

### `META-INF/context.xml` — pool de conexiones JNDI

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Context>
    <Resource name="jdbc/gestion"
              auth="Container"
              type="javax.sql.DataSource"
              maxTotal="10"
              maxIdle="5"
              maxWaitMillis="10000"
              username="root"
              password=""
              driverClassName="com.mysql.cj.jdbc.Driver"
              url="jdbc:mysql://localhost:3306/gestion_universitaria?useSSL=false
                   &amp;serverTimezone=UTC&amp;allowPublicKeyRetrieval=true"/>
</Context>
```

Este fichero le indica a Tomcat que cree un pool de conexiones DBCP2 accesible desde la aplicación bajo el nombre JNDI `java:comp/env/jdbc/gestion`.

### `WEB-INF/web.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee" version="3.1">
    <display-name>GestionUniversitaria</display-name>
    <welcome-file-list>
        <welcome-file>index.jsp</welcome-file>
    </welcome-file-list>
    <error-page>
        <error-code>404</error-code>
        <location>/WEB-INF/vistas/error.jsp</location>
    </error-page>
</web-app>
```

El servlet y el filtro se registran mediante anotaciones (`@WebServlet`, `@WebFilter`), por lo que no necesitan declaración en `web.xml`.

### `index.jsp` — punto de entrada

```jsp
<%@ page contentType="text/html;charset=UTF-8" %>
<% response.sendRedirect(request.getContextPath() + "/control?idAccion=mostrarLogin"); %>
```

Redirige automáticamente al login al acceder a la raíz de la aplicación.

### Pasos de despliegue

1. Arrancar XAMPP (Apache + MySQL).
2. Crear la base de datos `gestion_universitaria` en phpMyAdmin.
3. Ejecutar el script `WEB-INF/gestion_universitaria.sql`.
4. Construir el proyecto: `mvn clean package`.
5. Copiar el `.war` generado en `target/` al directorio `webapps/` de Tomcat.
6. Iniciar Tomcat y acceder a `http://localhost:8080/GestionUniversitaria`.

---

## 9. Estructura de directorios

```
GestionUniversitaria/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── gestion/
        │       ├── filtro/
        │       │   └── SeguridadFiltro.java
        │       ├── controlador/
        │       │   └── ControlServlet.java
        │       ├── accion/
        │       │   ├── Accion.java
        │       │   ├── AccionMostrarLogin.java
        │       │   ├── AccionLogin.java
        │       │   ├── AccionLogout.java
        │       │   ├── titulaciones/
        │       │   │   ├── AccionListarTitulaciones.java
        │       │   │   ├── AccionFormTitulacion.java
        │       │   │   ├── AccionGuardarTitulacion.java
        │       │   │   └── AccionEliminarTitulacion.java
        │       │   ├── usuarios/
        │       │   │   ├── AccionListarUsuarios.java
        │       │   │   ├── AccionFormUsuario.java
        │       │   │   ├── AccionGuardarUsuario.java
        │       │   │   └── AccionEliminarUsuario.java
        │       │   ├── asignaturas/
        │       │   │   ├── AccionListarAsignaturas.java
        │       │   │   ├── AccionFormAsignatura.java
        │       │   │   ├── AccionGuardarAsignatura.java
        │       │   │   └── AccionEliminarAsignatura.java
        │       │   └── profesores/
        │       │       ├── AccionListarProfesores.java
        │       │       ├── AccionFormProfesor.java
        │       │       ├── AccionGuardarProfesor.java
        │       │       ├── AccionEliminarProfesor.java
        │       │       └── AccionAsignarProfesor.java
        │       ├── modelo/
        │       │   ├── ConexionBD.java
        │       │   ├── TitulacionDAO.java
        │       │   ├── UsuarioDAO.java
        │       │   ├── AsignaturaDAO.java
        │       │   └── ProfesorDAO.java
        │       └── bean/
        │           ├── Titulacion.java
        │           ├── Usuario.java
        │           ├── Asignatura.java
        │           └── Profesor.java
        └── webapp/
            ├── index.jsp
            ├── META-INF/
            │   └── context.xml
            └── WEB-INF/
                ├── web.xml
                ├── gestion_universitaria.sql
                ├── lib/
                │   ├── mysql-connector-j-X.X.X.jar
                │   └── (JSTL incluido vía Maven)
                └── vistas/
                    ├── login.jsp
                    ├── menu.jsp
                    ├── error.jsp
                    ├── titulaciones/
                    │   ├── lista.jsp
                    │   └── formulario.jsp
                    ├── usuarios/
                    │   ├── lista.jsp
                    │   └── formulario.jsp
                    ├── asignaturas/
                    │   ├── lista.jsp
                    │   └── formulario.jsp
                    └── profesores/
                        ├── lista.jsp
                        ├── formulario.jsp
                        └── asignar.jsp
```

---

## 10. Conclusiones y limitaciones

El proyecto implementa de forma completa el patrón **Front Controller MVC** sobre Java EE, consiguiendo una separación limpia de responsabilidades:

- El **filtro** centraliza la seguridad de acceso sin duplicar código en cada acción.
- El **controlador** desacopla la URL de la lógica, haciendo que añadir nuevas funcionalidades sea tan simple como crear una nueva clase `AccionXxx` y registrarla en el mapa.
- Las **acciones** son la única capa con lógica de negocio: cohesionadas, pequeñas y fáciles de localizar.
- Los **DAOs** encapsulan el SQL, haciendo que un cambio en el esquema de la BD no afecte a las capas superiores.
- Los **JSPs** son plantillas HTML puras con JSTL, sin mezclar Java y presentación.

Este diseño favorece el mantenimiento y la escalabilidad: añadir una nueva entidad (por ejemplo, alumnos) implica seguir el mismo patrón — bean + DAO + acciones + JSPs — sin modificar ninguna de las piezas ya existentes.

La principal limitación identificada es el almacenamiento de contraseñas en texto plano, que en un entorno real debería sustituirse por un hash seguro. Igualmente, la validación de entrada se realiza en su mayor parte en el cliente (atributos HTML `required`, `type="number"`), debiendo complementarse con validación en servidor para mayor robustez.
