package gestion.bean;

public class Asignatura {

    private int     id;
    private String  nombre;
    private int     capacidadMaxima;
    private int     idTitulacion;
    private Integer idProfesor;       // puede ser null
    private String  nombreTitulacion; // campo de visualización (JOIN)
    private String  nombreProfesor;   // campo de visualización (JOIN)

    public Asignatura() {}

    public Asignatura(int id, String nombre, int capacidadMaxima, int idTitulacion, Integer idProfesor) {
        this.id = id;
        this.nombre = nombre;
        this.capacidadMaxima = capacidadMaxima;
        this.idTitulacion = idTitulacion;
        this.idProfesor = idProfesor;
    }

    public int getId()                                  { return id; }
    public void setId(int id)                           { this.id = id; }
    public String getNombre()                           { return nombre; }
    public void setNombre(String nombre)                { this.nombre = nombre; }
    public int getCapacidadMaxima()                     { return capacidadMaxima; }
    public void setCapacidadMaxima(int c)               { this.capacidadMaxima = c; }
    public int getIdTitulacion()                        { return idTitulacion; }
    public void setIdTitulacion(int idTitulacion)       { this.idTitulacion = idTitulacion; }
    public Integer getIdProfesor()                      { return idProfesor; }
    public void setIdProfesor(Integer idProfesor)       { this.idProfesor = idProfesor; }
    public String getNombreTitulacion()                 { return nombreTitulacion; }
    public void setNombreTitulacion(String n)           { this.nombreTitulacion = n; }
    public String getNombreProfesor()                   { return nombreProfesor; }
    public void setNombreProfesor(String n)             { this.nombreProfesor = n; }
}
