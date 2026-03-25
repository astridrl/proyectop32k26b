package Modelo;

import Controlador.clsPerfil;
import Controlador.clsBitacora;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PerfilDAO {

    // --- SQL Perfil ---
    private static final String SQL_SELECT_PERFIL = "SELECT perid, pernombre, perdescripcion, perestatus FROM perfil";
    private static final String SQL_INSERT_PERFIL = "INSERT INTO perfil(pernombre, perdescripcion, perestatus) VALUES(?, ?, ?)";
    private static final String SQL_UPDATE_PERFIL = "UPDATE perfil SET pernombre=?, perdescripcion=?, perestatus=? WHERE perid=?";
    private static final String SQL_DELETE_PERFIL = "DELETE FROM perfil WHERE perid=?";
    private static final String SQL_SELECT_PERFIL_NOMBRE = "SELECT perid, pernombre, perdescripcion, perestatus FROM perfil WHERE pernombre=?";
    private static final String SQL_SELECT_PERFIL_ID = "SELECT perid, pernombre, perdescripcion, perestatus FROM perfil WHERE perid=?";

    // --- SQL Bitácora ---
    private static final String SQL_INSERT_BITACORA = "INSERT INTO bitacora(usuId, perId, accion, fecha) VALUES(?, ?, ?, CURRENT_TIMESTAMP)";
    private static final String SQL_UPDATE_BITACORA = "UPDATE bitacora SET usuId=?, perId=?, accion=? WHERE bitId=?";
    private static final String SQL_DELETE_BITACORA = "DELETE FROM bitacora WHERE bitId=?";
    private static final String SQL_SELECT_BITACORA = "SELECT bitId, usuId, perId, accion, fecha FROM bitacora";
    private static final String SQL_SELECT_BITACORA_ID = "SELECT bitId, usuId, perId, accion, fecha FROM bitacora WHERE bitId=?";

    // --- Métodos CRUD Perfil ---
    public List<clsPerfil> consultaPerfiles() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<clsPerfil> perfiles = new ArrayList<>();
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_SELECT_PERFIL);
            rs = stmt.executeQuery();
            while (rs.next()) {
                clsPerfil perfil = new clsPerfil();
                perfil.setPerId(rs.getInt("perid"));
                perfil.setPerNombre(rs.getString("pernombre"));
                perfil.setPerDescripcion(rs.getString("perdescripcion"));
                perfil.setPerEstatus(rs.getString("perestatus"));
                perfiles.add(perfil);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return perfiles;
    }

    public int ingresaPerfil(clsPerfil perfil) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_INSERT_PERFIL);
            stmt.setString(1, perfil.getPerNombre());
            stmt.setString(2, perfil.getPerDescripcion());
            stmt.setString(3, perfil.getPerEstatus());
            rows = stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return rows;
    }

    public int actualizaPerfil(clsPerfil perfil) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE_PERFIL);
            stmt.setString(1, perfil.getPerNombre());
            stmt.setString(2, perfil.getPerDescripcion());
            stmt.setString(3, perfil.getPerEstatus());
            stmt.setInt(4, perfil.getPerId());
            rows = stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return rows;
    }

    public int borrarPerfil(clsPerfil perfil) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_DELETE_PERFIL);
            stmt.setInt(1, perfil.getPerId());
            rows = stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return rows;
    }

    public clsPerfil consultaPerfilPorNombre(clsPerfil perfil) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_SELECT_PERFIL_NOMBRE);
            stmt.setString(1, perfil.getPerNombre());
            rs = stmt.executeQuery();
            if (rs.next()) {
                perfil.setPerId(rs.getInt("perid"));
                perfil.setPerNombre(rs.getString("pernombre"));
                perfil.setPerDescripcion(rs.getString("perdescripcion"));
                perfil.setPerEstatus(rs.getString("perestatus"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return perfil;
    }

    public clsPerfil consultaPerfilPorId(clsPerfil perfil) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_SELECT_PERFIL_ID);
            stmt.setInt(1, perfil.getPerId());
            rs = stmt.executeQuery();
            if (rs.next()) {
                perfil.setPerId(rs.getInt("perid"));
                perfil.setPerNombre(rs.getString("pernombre"));
                perfil.setPerDescripcion(rs.getString("perdescripcion"));
                perfil.setPerEstatus(rs.getString("perestatus"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return perfil;
    }

    // --- Métodos CRUD Bitácora ---
    public int registrarBitacoraPerfil(int usuId, int perId, String accion) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_INSERT_BITACORA);
            stmt.setInt(1, usuId);
            stmt.setInt(2, perId);
            stmt.setString(3, accion);
            rows = stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return rows;
    }

    public int actualizarBitacora(clsBitacora bitacora) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE_BITACORA);
            stmt.setInt(1, bitacora.getUsuId());
            stmt.setInt(2, bitacora.getPerId());
            stmt.setString(3, bitacora.getAccion());
            stmt.setInt(4, bitacora.getBitId());
            rows = stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return rows;
    }

    public int borrarBitacora(clsBitacora bitacora) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_DELETE_BITACORA);
            stmt.setInt(1, bitacora.getBitId());
            rows = stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return rows;
    }

    public List<clsBitacora> consultarBitacoras() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<clsBitacora> bitacoras = new ArrayList<>();
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_SELECT_BITACORA);
            rs = stmt.executeQuery();
            while (rs.next()) {
                clsBitacora bitacora = new clsBitacora();
                bitacora.setBitId(rs.getInt("bitId"));
                bitacora.setUsuId(rs.getInt("usuId"));
                bitacora.setPerId(rs.getInt("perId"));
                bitacora.setAccion(rs.getString("accion"));
                bitacora.setFecha(rs.getTimestamp("fecha"));
                bitacoras.add(bitacora);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return bitacoras;
    }

    public clsBitacora consultarBitacoraPorId(int bitId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        Result