package gestion.bean;

/**
 * Bean que representa a un alumno del sistema universitario.
 * Contiene los datos personales del alumno y no tiene lógica de negocio.
 */
public class Alumno {

    private int    id;
    private String nombre;
    private String email;
    private String dni;

    public Alumno() {}

    public Alumno(int id, String nombre, String email, String dni) {
        this.id     = id;
        this.nombre = nombre;
        this.email  = email;
        this.dni    = dni;
    }

    public int getId()                  { return id; }
    public void setId(int id)           { this.id = id; }
    public String getNombre()           { return nombre; }
    public void setNombre(String n)     { this.nombre = n; }
    public String getEmail()            { return email; }
    public void setEmail(String e)      { this.email = e; }
    public String getDni()              { return dni; }
    public void setDni(String d)        { this.dni = d; }
}
