package gestion.modelo;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Utilidad para obtener conexiones a la base de datos a través del pool JNDI
 * configurado en {@code META-INF/context.xml}. El pool es gestionado por Tomcat (DBCP2),
 * por lo que nunca se crean conexiones directas desde la aplicación.
 */
public class ConexionBD {

    /**
     * Obtiene una conexión del pool de Tomcat.
     * El llamador es responsable de cerrarla (idealmente con try-with-resources).
     */
    public static Connection getConexion() throws Exception {
        Context ctx = new InitialContext();
        DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/gestion");
        return ds.getConnection();
    }
}
