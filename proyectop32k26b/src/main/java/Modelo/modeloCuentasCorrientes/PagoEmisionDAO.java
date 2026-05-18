/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo.modeloCuentasCorrientes;
import Controlador.controladorCuentasCorrientes.clsPagosEmision;
import Controlador.controladorCuentasCorrientes.clscuentasporpagar;
import Modelo.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author miais
 */
public class PagoEmisionDAO {
    private static final String SQL_SELECT = "SELECT Pagemid, Cppcodigo, Movbid, Pagefecha, Pagemonto, Pagetipo FROM pagosemision";
    private static final String SQL_INSERT = "INSERT INTO pagosemision (Cppcodigo, Movbid, Pagefecha, Pagemonto, Pagetipo) VALUES (?, ?, NOW(), ?, ?)";
    private static final String SQL_UPDATE_SALDO = "UPDATE cuentasporpagar SET Cppsaldopendiente = Cppsaldopendiente - ? WHERE Cppcodigo = ?";
    private static final String SQL_UPDATE_ESTADO = "UPDATE cuentasporpagar SET Cppestado = 'I' WHERE Cppcodigo = ? AND Cppsaldopendiente <= 0";
    private static final String SQL_SELECT_SALDO = "SELECT Cppsaldopendiente FROM cuentasporpagar WHERE Cppcodigo = ?";
    private static final String SQL_SELECT_ID = "SELECT Pagemid, Cppcodigo, Movbid, Pagefecha, Pagemonto, Pagetipo FROM pagosemision WHERE Pagemid = ?";
    private static final String SQL_SELECT_CXP = "SELECT Pagemid, Cppcodigo, Movbid, Pagefecha, Pagemonto, Pagetipo FROM pagosemision WHERE Cppcodigo = ?";

    // SELECT todos los pagos
    public List<clsPagosEmision> select() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        clsPagosEmision pago = null;
        List<clsPagosEmision> lista = new ArrayList<>();

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_SELECT);
            rs = stmt.executeQuery();
            while (rs.next()) {
                pago = new clsPagosEmision();
                pago.setPagemid(rs.getInt("Pagemid"));
                pago.setCppcodigo(rs.getInt("Cppcodigo"));
                pago.setMovbid(rs.getInt("Movbid"));
                pago.setPagefecha(rs.getString("Pagefecha"));
                pago.setPagemonto(rs.getDouble("Pagemonto"));
                pago.setPagetipo(rs.getString("Pagetipo"));
                lista.add(pago);
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

    // INSERT pago + actualizar saldo (transaccional)
    public int registrarPago(clsPagosEmision pago) {
        Connection conn = null;
        PreparedStatement stmtInsert = null;
        PreparedStatement stmtSaldo = null;
        PreparedStatement stmtEstado = null;
        int rows = 0;

        try {
            conn = Conexion.getConnection();
            conn.setAutoCommit(false); // inicio de transacción

            // 1. Insertar el pago
            stmtInsert = conn.prepareStatement(SQL_INSERT);
            stmtInsert.setInt(1, pago.getCppcodigo());
            stmtInsert.setInt(2, pago.getMovbid());
            stmtInsert.setDouble(3, pago.getPagemonto());
            stmtInsert.setString(4, pago.getPagetipo());
            rows = stmtInsert.executeUpdate();

            // 2. Actualizar saldo pendiente
            stmtSaldo = conn.prepareStatement(SQL_UPDATE_SALDO);
            stmtSaldo.setDouble(1, pago.getPagemonto());
            stmtSaldo.setInt(2, pago.getCppcodigo());
            stmtSaldo.executeUpdate();

            // 3. Si saldo quedó en 0, marcar como inactiva
            stmtEstado = conn.prepareStatement(SQL_UPDATE_ESTADO);
            stmtEstado.setInt(1, pago.getCppcodigo());
            stmtEstado.executeUpdate();

            conn.commit(); // confirmar transacción
            System.out.println("Pago registrado correctamente");

        } catch (SQLException ex) {
            try {
                if (conn != null) conn.rollback(); // revertir si hay error
            } catch (SQLException e) {
                e.printStackTrace(System.out);
            }
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(stmtInsert);
            Conexion.close(stmtSaldo);
            Conexion.close(stmtEstado);
            Conexion.close(conn);
        }
        return rows;
    }

    // Obtener saldo pendiente de una cuenta
    public double obtenerSaldo(int Cppcodigo) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        double saldo = 0;

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_SELECT_SALDO);
            stmt.setInt(1, Cppcodigo);
            rs = stmt.executeQuery();
            if (rs.next()) {
                saldo = rs.getDouble("Cppsaldopendiente");
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

    // SELECT pagos por cuenta
    public List<clsPagosEmision> selectPorCuenta(int Cppcodigo) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        clsPagosEmision pago = null;
        List<clsPagosEmision> lista = new ArrayList<>();

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_SELECT_CXP);
            stmt.setInt(1, Cppcodigo);
            rs = stmt.executeQuery();
            while (rs.next()) {
                pago = new clsPagosEmision();
                pago.setPagemid(rs.getInt("Pagemid"));
                pago.setCppcodigo(rs.getInt("Cppcodigo"));
                pago.setMovbid(rs.getInt("Movbid"));
                pago.setPagefecha(rs.getString("Pagefecha"));
                pago.setPagemonto(rs.getDouble("Pagemonto"));
                pago.setPagetipo(rs.getString("Pagetipo"));
                lista.add(pago);
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
