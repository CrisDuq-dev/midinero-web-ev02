package com.midinero.dao;

import com.midinero.conexion.ConexionBD;
import com.midinero.modelo.Transaccion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de acceso a datos (DAO) para la entidad Transaccion.
 * Implementa el CRUD completo (Create, Read, Update, Delete) utilizando JDBC.
 *
 * Estándar de nombramiento de clases: PascalCase + sufijo DAO.
 * Estándar de nombramiento de métodos: camelCase con verbo.
 */
public class TransaccionDAO {

    // ---------- CREATE ----------
    /**
     * Inserta una nueva transacción en la base de datos.
     */
    public boolean insertarTransaccion(Transaccion transaccion) {
        String sql = "INSERT INTO transacciones (tipo, monto, fecha, descripcion, categoria) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setString(1, transaccion.getTipo());
            statement.setDouble(2, transaccion.getMonto());
            statement.setDate(3, Date.valueOf(transaccion.getFecha()));
            statement.setString(4, transaccion.getDescripcion());
            statement.setString(5, transaccion.getCategoria());

            int filasAfectadas = statement.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar la transacción: " + e.getMessage());
            return false;
        }
    }

    // ---------- READ ----------
    /**
     * Consulta y retorna todas las transacciones registradas.
     */
    public List<Transaccion> consultarTransacciones() {
        List<Transaccion> listaTransacciones = new ArrayList<>();
        String sql = "SELECT * FROM transacciones ORDER BY fecha DESC";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {

            while (resultado.next()) {
                listaTransacciones.add(mapearTransaccion(resultado));
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar las transacciones: " + e.getMessage());
        }
        return listaTransacciones;
    }

    /**
     * Consulta una transacción específica por su id.
     */
    public Transaccion consultarTransaccionPorId(int id) {
        String sql = "SELECT * FROM transacciones WHERE id = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, id);
            try (ResultSet resultado = statement.executeQuery()) {
                if (resultado.next()) {
                    return mapearTransaccion(resultado);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar la transacción: " + e.getMessage());
        }
        return null;
    }

    // ---------- UPDATE ----------
    /**
     * Actualiza los datos de una transacción existente.
     */
    public boolean actualizarTransaccion(Transaccion transaccion) {
        String sql = "UPDATE transacciones SET tipo = ?, monto = ?, fecha = ?, descripcion = ?, categoria = ? WHERE id = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setString(1, transaccion.getTipo());
            statement.setDouble(2, transaccion.getMonto());
            statement.setDate(3, Date.valueOf(transaccion.getFecha()));
            statement.setString(4, transaccion.getDescripcion());
            statement.setString(5, transaccion.getCategoria());
            statement.setInt(6, transaccion.getId());

            int filasAfectadas = statement.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar la transacción: " + e.getMessage());
            return false;
        }
    }

    // ---------- DELETE ----------
    /**
     * Elimina una transacción de la base de datos según su id.
     */
    public boolean eliminarTransaccion(int id) {
        String sql = "DELETE FROM transacciones WHERE id = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, id);
            int filasAfectadas = statement.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar la transacción: " + e.getMessage());
            return false;
        }
    }

    // ---------- Método auxiliar ----------
    private Transaccion mapearTransaccion(ResultSet resultado) throws SQLException {
        return new Transaccion(
                resultado.getInt("id"),
                resultado.getString("tipo"),
                resultado.getDouble("monto"),
                resultado.getDate("fecha").toLocalDate(),
                resultado.getString("descripcion"),
                resultado.getString("categoria")
        );
    }
}
