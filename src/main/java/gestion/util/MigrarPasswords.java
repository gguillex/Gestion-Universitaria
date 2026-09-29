package gestion.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Script de migración (se ejecuta una vez, fuera de Tomcat): convierte a hash
 * PBKDF2 todas las contraseñas de la tabla {@code usuarios} que sigan en texto plano.
 *
 * <p>Es idempotente: las filas que ya tienen hash no se tocan, así que se puede
 * relanzar sin riesgo. Todo se hace en una transacción: o migran todas o ninguna.
 *
 * <p>Uso (desde la raíz del proyecto, tras {@code mvn package}):
 * <pre>
 * java -cp "target/classes;RUTA/mysql-connector-j-X.X.X.jar" gestion.util.MigrarPasswords \
 *      "jdbc:mysql://localhost:3306/NOMBRE_BD?useSSL=false&amp;serverTimezone=UTC" root ""
 * </pre>
 * (En Linux/macOS el separador del classpath es {@code :} en lugar de {@code ;}.)
 *
 * <p>Sin este script las contraseñas en claro también se migran solas, pero solo
 * cuando cada usuario vuelve a iniciar sesión; las de cuentas inactivas seguirían
 * en claro indefinidamente.
 */
public final class MigrarPasswords {

    private MigrarPasswords() {}

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println("Uso: MigrarPasswords <urlJdbc> <usuarioBD> <passwordBD>");
            System.exit(1);
        }

        try (Connection con = DriverManager.getConnection(args[0], args[1], args[2])) {
            con.setAutoCommit(false);
            int migradas = 0;
            int yaConHash = 0;

            try (PreparedStatement select = con.prepareStatement(
                         "SELECT id, password FROM usuarios FOR UPDATE");
                 PreparedStatement update = con.prepareStatement(
                         "UPDATE usuarios SET password = ? WHERE id = ?");
                 ResultSet rs = select.executeQuery()) {

                while (rs.next()) {
                    String almacenada = rs.getString("password");
                    if (almacenada == null) continue;
                    if (!Passwords.esLegado(almacenada)) {
                        yaConHash++;
                        continue;
                    }
                    update.setString(1, Passwords.hashear(almacenada));
                    update.setInt(2, rs.getInt("id"));
                    update.addBatch();
                    migradas++;
                }
                update.executeBatch();
                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e;
            }

            System.out.println("Contraseñas migradas a hash: " + migradas);
            System.out.println("Ya tenían hash (sin cambios): " + yaConHash);
        }
    }
}
