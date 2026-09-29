package gestion.bean;

/**
 * Bean que representa a un usuario del sistema.
 * El campo {@code rol} determina los permisos: "admin" tiene acceso al CRUD de usuarios;
 * "usuario" accede al resto de la funcionalidad.
 */
public class Usuario {

    private int    id;
    private String nombre;
    private String password;
    private String rol;     // "admin" o "usuario"

    public Usuario() {}

    public Usuario(int id, String nombre, String password, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.password = password;
        this.rol = rol;
    }

    public int getId()                       { return id; }
    public void setId(int id)                { this.id = id; }
    public String getNombre()                { return nombre; }
    public void setNombre(String nombre)     { this.nombre = nombre; }
    public String getPassword()              { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRol()                   { return rol; }
    public void setRol(String rol)           { this.rol = rol; }
}
