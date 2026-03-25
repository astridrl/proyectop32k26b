package Modelo;
// Angoly Camila Araujo Mayen 9959-24-17623
import Controlador.clsAsignacionAplicacionUsuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsignacionAplicacionUsuarioDAO {

    //  INSERTAR
    public int ingresaAsignacion(clsAsignacionAplicacionUsuario asignacion) {
        int resultado = 0;

        String sql = "INSERT INTO asignacionaplicacionusuarios "
                + "(Aplcodigo, UsuId, APLUins, APLUsel, APLUupd, APLUdel, APLUrep) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, asignacion.getAplcodigo());
            stmt.setInt(2, asignacion.getUsuId());
            stmt.setString(3, asignacion.getAPLUins());
            stmt.setString(4, asignacion.getAPLUsel());
            stmt.setString(5, asignacion.getAPLUupd());
            stmt.setString(6, asignacion.getAPLUdel());
            stmt.setString(7, asignacion.getAPLUrep());

            resultado = stmt.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }

        return resultado;
    }

    // CONSULTAR TODOS
    public List<clsAsignacionAplicacionUsuario> consultaAsignaciones() {
        List<clsAsignacionAplicacionUsuario> lista = new ArrayList<>();

        String sql = "SELECT * FROM asignacionaplicacionusuarios";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clsAsignacionAplicacionUsuario asignacion = new clsAsignacionAplicacionUsuario();

                asignacion.setAplcodigo(rs.getInt("Aplcodigo"));
                asignacion.setUsuId(rs.getInt("UsuId"));
                asignacion.setAPLUins(rs.getString("APLUins"));
                asignacion.setAPLUsel(rs.getString("APLUsel"));
                asignacion.setAPLUupd(rs.getString("APLUupd"));
                asignacion.setAPLUdel(rs.getString("APLUdel"));
                asignacion.setAPLUrep(rs.getString("APLUrep"));

                lista.add(asignacion);
            }

        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }

        return lista;
    }

    // CONSULTAR POR ID
    public clsAsignacionAplicacionUsuario consultaAsignacionPorId(clsAsignacionAplicacionUsuario asignacion) {

        String sql = "SELECT * FROM asignacionaplicacionusuarios WHERE Aplcodigo=? AND UsuId=?";
        clsAsignacionAplicacionUsuario resultado = null;

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, asignacion.getAplcodigo());
            stmt.setInt(2, asignacion.getUsuId());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                resultado = new clsAsignacionAplicacionUsuario();

                resultado.setAplcodigo(rs.getInt("Aplcodigo"));
                resultado.setUsuId(rs.getInt("UsuId"));
                resultado.setAPLUins(rs.getString("APLUins"));
                resultado.setAPLUsel(rs.getString("APLUsel"));
                resultado.setAPLUupd(rs.getString("APLUupd"));
                resultado.setAPLUdel(rs.getString("APLUdel"));
                resultado.setAPLUrep(rs.getString("APLUrep"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }

        return resultado;
    }

    // ACTUALIZAR
    public int actualizaAsignacion(clsAsignacionAplicacionUsuario asignacion) {
        int resultado = 0;

        String sql = "UPDATE asignacionaplicacionusuarios SET "
                + "APLUins=?, APLUsel=?, APLUupd=?, APLUdel=?, APLUrep=? "
                + "WHERE Aplcodigo=? AND UsuId=?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, asignacion.getAPLUins());
            stmt.setString(2, asignacion.getAPLUsel());
            stmt.setString(3, asignacion.getAPLUupd());
            stmt.setString(4, asignacion.getAPLUdel());
            stmt.setString(5, asignacion.getAPLUrep());
            stmt.setInt(6, asignacion.getAplcodigo());
            stmt.setInt(7, asignacion.getUsuId());

            resultado = stmt.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }

        return resultado;
    }

    // 🔹 ELIMINAR
    public int borrarAsignacion(clsAsignacionAplicacionUsuario asignacion) {
        int resultado = 0;

        String sql = "DELETE FROM asignacionaplicacionusuarios WHERE Aplcodigo=? AND UsuId=?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, asignacion.getAplcodigo());
            stmt.setInt(2, asignacion.getUsuId());

            resultado = stmt.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }

        return resultado;
    }
}