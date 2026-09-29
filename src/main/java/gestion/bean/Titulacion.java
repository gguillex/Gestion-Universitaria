package gestion.bean;

/** Bean que representa una titulación universitaria. */
public class Titulacion {

    private int    id;
    private String nombre;
    private String descripcion;

    public Titulacion() {}

    public Titulacion(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getId()                     { return id; }
    public void setId(int id)              { this.id = id; }
    public String getNombre()              { return nombre; }
    public void setNombre(String nombre)   { this.nombre = nombre; }
    public String getDescripcion()         { return descripcion; }
    public void setDescripcion(String d)   { this.descripcion = d; }
}
