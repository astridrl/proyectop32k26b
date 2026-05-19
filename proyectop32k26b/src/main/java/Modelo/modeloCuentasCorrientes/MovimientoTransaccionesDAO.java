/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo.modeloCuentasCorrientes;
import Controlador.controladorCuentasCorrientes.clsMovimientoTransacciones;
import Modelo.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author WINDOWS
 */
public class MovimientoTransaccionesDAO {
    private static final String SQL_SELECT = "SELECT Mccid, Mccfecha, Mccmonto, Mcctipo, Mccconcepto, Mccestado, Mccsaldo, Cliid, Procodigo, Acrecodigo, Venid, TTid, Mccmodulo, Mccorigenid FROM movimientoscc";
    private static final String SQL_QUERY_POR_PROVEEDOR = "SELECT Mccid, Mccfecha, Mccmonto, Mcctipo, Mccconcepto, " + "Mccestado, Mccsaldo, Cliid, Procodigo, Acrecodigo, Venid, " + "TTid, Mccmodulo, Mccorigenid FROM movimientoscc " + "WHERE Procodigo = ? ORDER BY Mccfecha";
    private static final String SQL_QUERY_POR_FECHAS = "SELECT Mccid, Mccfecha, Mccmonto, Mcctipo, Mccconcepto, " + "Mccestado, Mccsaldo, Cliid, Procodigo, Acrecodigo, Venid, " + "TTid, Mccmodulo, Mccorigenid FROM movimientoscc " + "WHERE Mccfecha BETWEEN ? AND ? ORDER BY Mccfecha";
    private static final String SQL_QUERY_POR_TIPO = "SELECT Mccid, Mccfecha, Mccmonto, Mcctipo, Mccconcepto, " + "Mccestado, Mccsaldo, Cliid, Procodigo, Acrecodigo, Venid, " + "TTid, Mccmodulo, Mccorigenid FROM movimientoscc " + "WHERE Mcctipo = ?";
    private static final String SQL_SALDO_CLIENTE = "SELECT SUM(Mccmonto) AS saldo FROM movimientoscc " + "WHERE Cliid = ? AND Mccestado = 'A'";
    private static final String SQL_SALDO_PROVEEDOR = "SELECT SUM(Mccmonto) AS saldo FROM movimientoscc " + "WHERE Procodigo = ? AND Mccestado = 'A'";
    private static final String SQL_INSERT = "INSERT INTO movimientoscc (Mccmonto, Mcctipo, Mccconcepto, Mccsaldo, Cliid, Procodigo, Acrecodigo, Venid, TTid, Mccmodulo, Mccorigenid, Mccfecha, Mccestado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, 'A')";

    private static final String SQL_QUERY_POR_CODIGO = "SELECT Mccid, Mccfecha, Mccmonto, Mcctipo, Mccconcepto, Mccestado, Mccsaldo, Cliid, Procodigo, Acrecodigo, Venid, TTid, Mccmodulo, Mccorigenid FROM movimientoscc WHERE Mccid = ?";

    private static final String SQL_QUERY_POR_CLIENTE = "SELECT Mccid, Mccfecha, Mccmonto, Mcctipo, Mccconcepto, Mccestado, Mccsaldo, Cliid, Procodigo, Acrecodigo, Venid, TTid, Mccmodulo, Mccorigenid FROM movimientoscc WHERE Cliid = ? ORDER BY Mccfecha";



// ============================================================
    // MÉTODO AUXILIAR: mapear ResultSet a objeto
    // ============================================================
    private clsMovimientoTransacciones mapearMovimiento(ResultSet rs) throws SQLException {
        clsMovimientoTransacciones mov = new clsMovimientoTransacciones();
        mov.setMccid(rs.getInt("Mccid"));
        mov.setMccfecha(rs.getString("Mccfecha"));
        mov.setMccmonto(rs.getDouble("Mccmonto"));
        mov.setMcctipo(rs.getString("Mcctipo"));
        mov.setMccconcepto(rs.getString("Mccconcepto"));
        mov.setMccestado(rs.getString("Mccestado"));
        mov.setMccsaldo(rs.getDouble("Mccsaldo"));
        mov.setCliid(rs.getInt("Cliid"));
        mov.setProcodigo(rs.getInt("Procodigo"));
        mov.setAcrecodigo(rs.getInt("Acrecodigo"));
        mov.setVenid(rs.getInt("Venid"));
        mov.setTTid(rs.getInt("TTid"));
        mov.setMccmodulo(rs.getString("Mccmodulo"));
        mov.setMccorigenid(rs.getInt("Mccorigenid"));
        return mov;
    }

    
    // SELECT - todos los movimientos
    public List<clsMovimientoTransacciones> select() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<clsMovimientoTransacciones> lista = new ArrayList<>();

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_SELECT);
            rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapearMovimiento(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return lista;
    }

    
    // INSERT - registrar un movimiento nuevo
    public int insert(double monto, String tipo, String concepto,
                      double saldo, int cliid, int procodigo,
                      int acrecodigo, int venid, int TTid,
                      String modulo, int origenid) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_INSERT);
            
            stmt.setDouble(1, monto);
            stmt.setString(2, tipo);      
            stmt.setString(3, concepto);  
            stmt.setDouble(4, saldo);

            // Validación del ID de Cliente (Parámetro 5)
            if (cliid > 0) {
                stmt.setInt(5, cliid);
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }

            // Validación del Código de Proveedor (Parámetro 6)
            if (procodigo > 0) {
                stmt.setInt(6, procodigo);
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }

            // Validación del Código de Acreedor (Parámetro 7)
            if (acrecodigo > 0) {
                stmt.setInt(7, acrecodigo);
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }

            // Validación del ID de Vendedor (Parámetro 8)
            if (venid > 0) {
                stmt.setInt(8, venid);
            } else {
                stmt.setNull(8, java.sql.Types.INTEGER);
            }

            // Parámetros restantes obligatorios
            stmt.setInt(9, TTid);
            stmt.setString(10, modulo);    
            stmt.setInt(11, origenid);

            rows = stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return rows;
    }

    // QUERY POR CÓDIGO
    public clsMovimientoTransacciones queryPorCodigo(int mccid) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        clsMovimientoTransacciones mov = null;

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_QUERY_POR_CODIGO);
            stmt.setInt(1, mccid);
            rs = stmt.executeQuery();
            if (rs.next()) {
                mov = mapearMovimiento(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return mov;
    }

    // ============================================================
    // QUERY POR CLIENTE - Estado de cuenta (El Concepto)
    // ============================================================
    public List<clsMovimientoTransacciones> queryPorCliente(int cliid) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<clsMovimientoTransacciones> lista = new ArrayList<>();

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_QUERY_POR_CLIENTE);
            stmt.setInt(1, cliid);
            rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapearMovimiento(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return lista;
    }

    // ============================================================
    // QUERY POR PROVEEDOR - Estado de cuenta (El Concepto)
    // ============================================================
    public List<clsMovimientoTransacciones> queryPorProveedor(int procodigo) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<clsMovimientoTransacciones> lista = new ArrayList<>();

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_QUERY_POR_PROVEEDOR);
            stmt.setInt(1, procodigo);
            rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapearMovimiento(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return lista;
    }

    // ============================================================
    // QUERY POR RANGO DE FECHAS
    // ============================================================
    public List<clsMovimientoTransacciones> queryPorFechas(String fechaInicio, String fechaFin) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<clsMovimientoTransacciones> lista = new ArrayList<>();

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_QUERY_POR_FECHAS);
            stmt.setString(1, fechaInicio);
            stmt.setString(2, fechaFin);
            rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapearMovimiento(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return lista;
    }

    // ============================================================
    // QUERY POR TIPO (CARGO o ABONO)
    // ============================================================
    public List<clsMovimientoTransacciones> queryPorTipo(String tipo) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<clsMovimientoTransacciones> lista = new ArrayList<>();

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_QUERY_POR_TIPO);
            stmt.setString(1, tipo); // 'CARGO' o 'ABONO'
            rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapearMovimiento(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return lista;
    }

    // ============================================================
    // SALDO ACTUAL - para El Concepto
    // ============================================================
    public double saldoCliente(int cliid) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        double saldo = 0.00;

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_SALDO_CLIENTE);
            stmt.setInt(1, cliid);
            rs = stmt.executeQuery();
            if (rs.next()) {
                saldo = rs.getDouble("saldo");
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return saldo;
    }

    public double saldoProveedor(int procodigo) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        double saldo = 0.00;

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_SALDO_PROVEEDOR);
            stmt.setInt(1, procodigo);
            rs = stmt.executeQuery();
            if (rs.next()) {
                saldo = rs.getDouble("saldo");
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return saldo;
    }
    
    //Query para Conciliación
    
    public List<clsMovimientoTransacciones> queryParaConciliacion(int idBanco, String fechaInicio, String fechaFin) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<clsMovimientoTransacciones> lista = new ArrayList<>();

        // Usamos LIKE 'BANCO%' para que acepte tanto "BANCO" como "BANCOS"
        String SQL_CONCILIACION = "SELECT * FROM movimientoscc " +
                                 "WHERE Acrecodigo = ? " +
                                 "AND Mccmodulo LIKE 'BANCO%' " +
                                 "AND Mccfecha BETWEEN ? AND ? ORDER BY Mccfecha";

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_CONCILIACION);
            stmt.setInt(1, idBanco);
            stmt.setString(2, fechaInicio);
            stmt.setString(3, fechaFin);

            rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapearMovimiento(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return lista;
    }
}
