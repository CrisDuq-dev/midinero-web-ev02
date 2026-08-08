package com.midinero.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase encargada de gestionar la conexión JDBC hacia la base de datos
 * MySQL del proyecto Mi Dinero+ (motor XAMPP / MySQL Workbench).
 *
 * Estándar de nombramiento de clases: PascalCase.
 */
public class ConexionBD {

    private static final String URL = "jdbc:mysql://localhost:3306/midinero_db?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "";

    private ConexionBD() {
    }

    public static Connection obtenerConexion() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver JDBC de MySQL. Verifica el classpath.", e);
        }
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }
}
