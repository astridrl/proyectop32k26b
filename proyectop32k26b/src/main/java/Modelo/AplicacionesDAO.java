//Anthony Suc marzo 2026
package Modelo;

import Controlador.clsAplicaciones;
import java.sql.*;

public class AplicacionesDAO {
    
    public ResultSet listar() {
    String sql = "SELECT * FROM aplicaciones";
    try {
        Connection conn = Conexion.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        return ps.executeQuery();
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}

    // INSERT
    public void insert(clsAplicaciones app) {

        String sql = "INSERT INTO aplicaciones (Aplnombre, Aplestado) VALUES (?, ?)";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, app.getAplnombre());
            ps.setString(2, app.getAplestado());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // UPDATE
    public void update(clsAplicaciones app) {

        String sql = "UPDATE aplicaciones SET Aplnombre=?, Aplestado=? WHERE Aplcodigo=?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, app.getAplnombre());
            ps.setString(2, app.getAplestado());
            ps.setInt(3, app.getAplcodigo());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // DELETE
    public void delete(int codigo) {

        String sql = "DELETE FROM aplicaciones WHERE Aplcodigo=?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, codigo);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // QUERY POR ID
    public clsAplicaciones query(int codigo) {

        clsAplicaciones app = null;

        String sql = "SELECT * FROM aplicaciones WHERE Aplcodigo=?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, codigo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                app = new clsAplicaciones();

                app.setAplcodigo(rs.getInt("Aplcodigo"));
                app.setAplnombre(rs.getString("Aplnombre"));
                app.setAplestado(rs.getString("Aplestado"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return app;
    }
}