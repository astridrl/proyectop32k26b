/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo.modeloCuentasCorrientes;

import Controlador.controladorCuentasCorrientes.clsFacturaCompraAcreedor;
import Modelo.Conexion;
 
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author astri
 */
public class facturacompraAcreedorDAO {
    
    private static final String SQL_INSERT =
        "INSERT INTO facturascompras " +
        "(Faccomnumero, Faccomfecha, Procodigo, Faccomsubtotal, Faccomiva, Faccomtotal, Faccomestado, Acrecodigo) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
 
    private static final String SQL_SELECT_ALL =
        "SELECT Faccomid, Faccomnumero, Faccomfecha, Procodigo, " +
        "       Faccomsubtotal, Faccomiva, Faccomtotal, Faccomestado, Acrecodigo " +
        "FROM facturascompras " +
        "WHERE Acrecodigo IS NOT NULL " +
        "ORDER BY Faccomfecha DESC";
 
    private static final String SQL_SELECT_POR_ID =
        "SELECT Faccomid, Faccomnumero, Faccomfecha, Procodigo, " +
        "       Faccomsubtotal, Faccomiva, Faccomtotal, Faccomestado, Acrecodigo " +
        "FROM facturascompras WHERE Faccomid = ?";
 
    private static final String SQL_SELECT_POR_NUMERO =
        "SELECT Faccomid, Faccomnumero, Faccomfecha, Procodigo, " +
        "       Faccomsubtotal, Faccomiva, Faccomtotal, Faccomestado, Acrecodigo " +
        "FROM facturascompras WHERE Faccomnumero = ?";
 
    private static final String SQL_SELECT_POR_ACREEDOR =
        "SELECT Faccomid, Faccomnumero, Faccomfecha, Procodigo, " +
        "       Faccomsubtotal, Faccomiva, Faccomtotal, Faccomestado, Acrecodigo " +
        "FROM facturascompras " +
        "WHERE Acrecodigo = ? ORDER BY Faccomfecha DESC";
 
    private static final String SQL_SELECT_POR_ESTADO =
        "SELECT Faccomid, Faccomnumero, Faccomfecha, Procodigo, " +
        "       Faccomsubtotal, Faccomiva, Faccomtotal, Faccomestado, Acrecodigo " +
        "FROM facturascompras " +
        "WHERE Acrecodigo IS NOT NULL AND Faccomestado = ? ORDER BY Faccomfecha DESC";
 
    private static final String SQL_UPDATE =
        "UPDATE facturascompras SET " +
        "Faccomnumero = ?, Faccomfecha = ?, Faccomsubtotal = ?, " +
        "Faccomiva = ?, Faccomtotal = ?, Faccomestado = ?, Acrecodigo = ? " +
        "WHERE Faccomid = ?";
 
    private static final String SQL_ANULAR =
        "UPDATE facturascompras SET Faccomestado = 'Anulada' WHERE Faccomid = ?";
 
    private static final String SQL_DELETE =
        "DELETE FROM facturascompras WHERE Faccomid = ?";
 
    //INSERT
    public int ingresaFacturaCompra(clsFacturaCompraAcreedor factura) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rsKeys = null;
        int nuevoId = -1;
        
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, factura.getFacComNumero());
            stmt.setString(2, factura.getFacComFecha());
            stmt.setInt(3, factura.getProCodigo());
            stmt.setDouble(4, factura.getFacComSubtotal());
            stmt.setDouble(5, factura.getFacComIva());
            stmt.setDouble(6, factura.getFacComTotal());
            stmt.setString(7, factura.getFacComEstado() != null
                    ? factura.getFacComEstado() : "Vigente");
            stmt.setInt(8, factura.getAcreCodigo());
 
            System.out.println("Ejecutando INSERT: " + SQL_INSERT);
            stmt.executeUpdate();
 
            rsKeys = stmt.getGeneratedKeys();
            if (rsKeys.next()) {
                nuevoId = rsKeys.getInt(1);
                System.out.println("Faccomid generado: " + nuevoId);
            }
 
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rsKeys);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return nuevoId;
    }
 
    //SELECT
    public List<clsFacturaCompraAcreedor> consultaFacturasCompra() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<clsFacturaCompraAcreedor> lista = new ArrayList<>();
 
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_SELECT_ALL);
            System.out.println("Ejecutando query: " + SQL_SELECT_ALL);
            rs = stmt.executeQuery();
            while (rs.next()) { lista.add(mapear(rs)); }
            System.out.println("Registros encontrados: " + lista.size());
 
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return lista;
    }
 
    // SELECT POR ID
    public clsFacturaCompraAcreedor consultaFacturaPorId(clsFacturaCompraAcreedor factura) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        clsFacturaCompraAcreedor resultado = null;
 
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_SELECT_POR_ID);
            stmt.setInt(1, factura.getFacComId());
            System.out.println("Ejecutando query: " + SQL_SELECT_POR_ID);
            rs = stmt.executeQuery();
            if (rs.next()) { resultado = mapear(rs); }
 
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return resultado;
    }
 
    // SELECT POR NUM
    public clsFacturaCompraAcreedor consultaFacturaPorNumero(clsFacturaCompraAcreedor factura) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        clsFacturaCompraAcreedor resultado = null;
 
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_SELECT_POR_NUMERO);
            stmt.setString(1, factura.getFacComNumero());
            System.out.println("Ejecutando query: " + SQL_SELECT_POR_NUMERO);
            rs = stmt.executeQuery();
            if (rs.next()) { resultado = mapear(rs); }
 
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return resultado;
    }
 
    // SELECT POR ACREEDOR
    public List<clsFacturaCompraAcreedor> consultaFacturasPorAcreedor(clsFacturaCompraAcreedor factura) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<clsFacturaCompraAcreedor> lista = new ArrayList<>();
 
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_SELECT_POR_ACREEDOR);
            stmt.setInt(1, factura.getAcreCodigo());
            System.out.println("Ejecutando query: " + SQL_SELECT_POR_ACREEDOR);
            rs = stmt.executeQuery();
            while (rs.next()) { lista.add(mapear(rs)); }
            System.out.println("Registros encontrados: " + lista.size());
 
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return lista;
    }
 
    // SELECT POR ESTADO
    public List<clsFacturaCompraAcreedor> consultaFacturasPorEstado(clsFacturaCompraAcreedor factura) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<clsFacturaCompraAcreedor> lista = new ArrayList<>();
 
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_SELECT_POR_ESTADO);
            stmt.setString(1, factura.getFacComEstado());
            System.out.println("Ejecutando query: " + SQL_SELECT_POR_ESTADO);
            rs = stmt.executeQuery();
            while (rs.next()) { lista.add(mapear(rs)); }
            System.out.println("Registros encontrados: " + lista.size());
 
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return lista;
    }
 
    // UPDATE
    public int actualizaFacturaCompra(clsFacturaCompraAcreedor factura) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
 
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE);
            stmt.setString(1, factura.getFacComNumero());
            stmt.setString(2, factura.getFacComFecha());
            stmt.setDouble (3, factura.getFacComSubtotal());
            stmt.setDouble(4, factura.getFacComIva());
            stmt.setDouble(5, factura.getFacComTotal());
            stmt.setString(6, factura.getFacComEstado());
            stmt.setInt(7, factura.getAcreCodigo());
            stmt.setInt(8, factura.getFacComId());
 
            System.out.println("Ejecutando UPDATE: " + SQL_UPDATE);
            rows = stmt.executeUpdate();
            System.out.println("Registros afectados: " + rows);
 
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return rows;
    }
 
    // ANULAR  (UPDATE estado → 'Anulada', conserva historial)
    
    public int anulaFacturaCompra(clsFacturaCompraAcreedor factura) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
 
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_ANULAR);
            stmt.setInt(1, factura.getFacComId());
 
            System.out.println("Ejecutando anulación: " + SQL_ANULAR);
            rows = stmt.executeUpdate();
            System.out.println("Registros afectados: " + rows);
 
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return rows;
    }
 
    // DELETE
    public int borraFacturaCompra(clsFacturaCompraAcreedor factura) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
 
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_DELETE);
            stmt.setInt(1, factura.getFacComId());
 
            System.out.println("Ejecutando DELETE: " + SQL_DELETE);
            rows = stmt.executeUpdate();
            System.out.println("Registros afectados: " + rows);
 
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return rows;
    }
 // Mapeo (para menos codigo)
    private clsFacturaCompraAcreedor mapear(ResultSet rs) throws SQLException {
        int acreCodigo = rs.getInt("Acrecodigo");

    if (rs.wasNull()) {
        acreCodigo = 0;
    }

    return new clsFacturaCompraAcreedor(
        rs.getInt("Faccomid"),
        rs.getString("Faccomnumero"),
        rs.getString("Faccomfecha"),
        rs.getInt("Procodigo"),
        rs.getDouble("Faccomsubtotal"),
        rs.getDouble("Faccomiva"),
        rs.getDouble("Faccomtotal"),
        rs.getString("Faccomestado"),
        acreCodigo
    );
    }
}
