/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo.modeloCuentasCorrientes;
import Controlador.controladorCuentasCorrientes.clscuentasporpagar;
import Modelo.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author dulce
 */
public class cuentasporpagarDAO {
    private static final String SQL_SELECT = "SELECT Cppcodigo, Procodigo, Acrecodigo, Venid, Cppfechaemision, Cppmontototal, Cppsaldopendiente, Cppestado, TTid, Cpporigenid FROM cuentasporpagar";
    private static final String SQL_INSERT = "INSERT INTO cuentasporpagar (Procodigo, Acrecodigo, Venid, Cppfechaemision, Cppmontototal, Cppsaldopendiente, Cppestado, TTid, Cpporigenid) VALUES(?, ?, ?, NOW(), ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE cuentasporpagar SET Procodigo=?, Acrecodigo=?, Venid=?, Cppfechaemision=?, Cppmontototal=?, Cppsaldopendiente=?, Cppestado=?, TTid=?, Cpporigenid=?  WHERE Cppcodigo = ?";
    private static final String SQL_DELETE = "DELETE FROM cuentasporpagar WHERE Cppcodigo=?";
    //Se agregarán diferentes QUERYS para variedad de opciones al momento de buscar una cuenta por pagar.
    private static final String SQL_QUERY_POR_CODIGO = "SELECT Cppcodigo, Procodigo, Acrecodigo, Venid, Cppfechaemision, Cppmontototal, Cppsaldopendiente, Cppestado, TTid, Cpporigenid FROM cuentasporpagar WHERE Cppcodigo=?";
    private static final String SQL_QUERY_POR_PROVEEDOR = "SELECT Cppcodigo, Procodigo, Acrecodigo, Venid, Cppfechaemision, Cppmontototal, Cppsaldopendiente, Cppestado, TTid, Cpporigenid FROM cuentasporpagar WHERE Procodigo=?";
    private static final String SQL_QUERY_POR_ACREEDOR = "SELECT Cppcodigo, Procodigo, Acrecodigo, Venid, Cppfechaemision, Cppmontototal, Cppsaldopendiente, Cppestado, TTid, Cpporigenid FROM cuentasporpagar WHERE Acrecodigo=?";
    private static final String SQL_QUERY_POR_ESTADO = "SELECT Cppcodigo, Procodigo, Acrecodigo, Venid, Cppfechaemision, Cppmontototal, Cppsaldopendiente, Cppestado, TTid, Cpporigenid FROM cuentasporpagar WHERE Cppestado=?";
    private static final String SQL_QUERY_POR_FECHAS = "SELECT Cppcodigo, Procodigo, Acrecodigo, Venid, Cppfechaemision, Cppmontototal, Cppsaldopendiente, Cppestado, TTid, Cpporigenid FROM cuentasporpagar WHERE Cppfechaemision BETWEEN ? AND ?";
    private static final String SQL_QUERY_POR_MONTO = "SELECT Cppcodigo, Procodigo, Acrecodigo, Venid, Cppfechaemision, Cppmontototal, Cppsaldopendiente, Cppestado, TTid, Cpporigenid FROM cuentasporpagar WHERE Cppmontototal=?";
    private static final String SQL_QUERY_POR_SALDO = "SELECT Cppcodigo, Procodigo, Acrecodigo, Venid, Cppfechaemision, Cppmontototal, Cppsaldopendiente, Cppestado, TTid, Cpporigenid FROM cuentasporpagar WHERE Cppsaldopendiente=?";
    //Query para autocompletar campos por numero de factura
    private static final String SQL_QUERY_POR_FACTURA = "SELECT Acrecodigo, Faccomtotal, Faccomestado FROM facturascompras WHERE Faccomnumero = ?";

    //SELECT trae todos los registros 
    
    public List<clscuentasporpagar> select(){
        Connection conn = null; 
        PreparedStatement stmt = null;
        ResultSet rs = null;
        clscuentasporpagar cxp = null;
        List<clscuentasporpagar> lista = new ArrayList<>();
        
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_SELECT);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int Cppcodigo        = rs.getInt("Cppcodigo");
                int Procodigo        = rs.getInt("Procodigo");
                int Acrecodigo       = rs.getInt("Acrecodigo");
                int Venid            = rs.getInt("Venid");
                String Cppfechaemision = rs.getString("Cppfechaemision");

                double Cppmontototal  = rs.getDouble("Cppmontototal");
                double Cppsaldopendiente = rs.getDouble("Cppsaldopendiente");

                char Cppestado       = rs.getString("Cppestado").charAt(0);
                int TTid             = rs.getInt("TTid");
                int Cpporigenid      = rs.getInt("Cpporigenid");

                cxp = new clscuentasporpagar();
                cxp.setCppcodigo(Cppcodigo);
                cxp.setProcodigo(Procodigo);
                cxp.setAcrecodigo(Acrecodigo);
                cxp.setVenid(Venid);
                cxp.setCppfechaemision(Cppfechaemision);
                cxp.setCppmontototal(Cppmontototal);
                cxp.setCppsaldopendiente(Cppsaldopendiente);
                cxp.setCppestado(Cppestado);
                cxp.setTTid(TTid);
                cxp.setCpporigenid(Cpporigenid);

                lista.add(cxp);
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
    
    // INSERT
    public int insert(clscuentasporpagar cxp) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_INSERT);
            if (cxp.getProcodigo() == 0) {
                stmt.setNull(1, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(1, cxp.getProcodigo());
            }

            if (cxp.getAcrecodigo() == 0) {
                stmt.setNull(2, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(2, cxp.getAcrecodigo());
            }
            if (cxp.getVenid() == 0) {
                stmt.setNull(3, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(3, cxp.getVenid());
            }

            stmt.setDouble(4, cxp.getCppmontototal());

            stmt.setDouble(5, cxp.getCppsaldopendiente());
            stmt.setString(6, String.valueOf(cxp.getCppestado()));
            stmt.setInt(7, cxp.getTTid());
            stmt.setInt(8, cxp.getCpporigenid());

            System.out.println("Ejecutando query: " + SQL_INSERT);
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
    
    // UPDATE
    public int update(clscuentasporpagar cxp) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE);
            stmt.setInt(1, cxp.getProcodigo());
            stmt.setInt(2, cxp.getAcrecodigo());
            stmt.setInt(3, cxp.getVenid());
            stmt.setString(4, cxp.getCppfechaemision());

            stmt.setDouble(5, cxp.getCppmontototal());

            stmt.setDouble(6, cxp.getCppsaldopendiente());
            stmt.setString(7, String.valueOf(cxp.getCppestado()));
            stmt.setInt(8, cxp.getTTid());
            stmt.setInt(9, cxp.getCpporigenid());
            stmt.setInt(10, cxp.getCppcodigo());

            System.out.println("Ejecutando query: " + SQL_UPDATE);
            rows = stmt.executeUpdate();
            System.out.println("Registros actualizados: " + rows);
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return rows;
    }
    
    // DELETE
    public int delete(clscuentasporpagar cxp) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_DELETE);
            stmt.setInt(1, cxp.getCppcodigo());

            System.out.println("Ejecutando query: " + SQL_DELETE);
            rows = stmt.executeUpdate();
            System.out.println("Registros eliminados: " + rows);
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return rows;
    }
    
    // SELECT por código
    public clscuentasporpagar selectPorCodigo(clscuentasporpagar cxp) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Conexion.getConnection();
            String SQL_SELECT_ID = null;
            stmt = conn.prepareStatement(SQL_SELECT_ID);
            stmt.setInt(1, cxp.getCppcodigo());
            rs = stmt.executeQuery();
            while (rs.next()) {
                cxp.setProcodigo(rs.getInt("Procodigo"));
                cxp.setAcrecodigo(rs.getInt("Acrecodigo"));
                cxp.setVenid(rs.getInt("Venid"));
                cxp.setCppfechaemision(rs.getString("Cppfechaemision"));
                cxp.setCppmontototal(rs.getDouble("Cppmontototal"));
                cxp.setCppsaldopendiente(rs.getDouble("Cppsaldopendiente"));
                cxp.setCppestado(rs.getString("Cppestado").charAt(0));
                cxp.setTTid(rs.getInt("TTid"));
                cxp.setCpporigenid(rs.getInt("Cpporigenid"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return cxp;
    }
    
    // Query por código
    public clscuentasporpagar queryPorCodigo(clscuentasporpagar cxp) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_QUERY_POR_CODIGO);
            stmt.setInt(1, cxp.getCppcodigo());
            rs = stmt.executeQuery();
            while (rs.next()) {
                cxp.setProcodigo(rs.getInt("Procodigo"));
                cxp.setAcrecodigo(rs.getInt("Acrecodigo"));
                cxp.setVenid(rs.getInt("Venid"));
                cxp.setCppfechaemision(rs.getString("Cppfechaemision"));
                cxp.setCppmontototal(rs.getDouble("Cppmontototal"));
                cxp.setCppsaldopendiente(rs.getDouble("Cppsaldopendiente"));
                cxp.setCppestado(rs.getString("Cppestado").charAt(0));
                cxp.setTTid(rs.getInt("TTid"));
                cxp.setCpporigenid(rs.getInt("Cpporigenid"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return cxp;
    }

    // Query por proveedor
    public List<clscuentasporpagar> queryPorProveedor(int Procodigo) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        clscuentasporpagar cxp = null;
        List<clscuentasporpagar> lista = new ArrayList<>();

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_QUERY_POR_PROVEEDOR);
            stmt.setInt(1, Procodigo);
            rs = stmt.executeQuery();
            while (rs.next()) {
                cxp = new clscuentasporpagar();
                cxp.setCppcodigo(rs.getInt("Cppcodigo"));
                cxp.setProcodigo(rs.getInt("Procodigo"));
                cxp.setAcrecodigo(rs.getInt("Acrecodigo"));
                cxp.setVenid(rs.getInt("Venid"));
                cxp.setCppfechaemision(rs.getString("Cppfechaemision"));
                cxp.setCppmontototal(rs.getDouble("Cppmontototal"));
                cxp.setCppsaldopendiente(rs.getDouble("Cppsaldopendiente"));
                cxp.setCppestado(rs.getString("Cppestado").charAt(0));
                cxp.setTTid(rs.getInt("TTid"));
                cxp.setCpporigenid(rs.getInt("Cpporigenid"));
                lista.add(cxp);
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

    // Query por acreedor
    public List<clscuentasporpagar> queryPorAcreedor(int Acrecodigo) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        clscuentasporpagar cxp = null;
        List<clscuentasporpagar> lista = new ArrayList<>();

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_QUERY_POR_ACREEDOR);
            stmt.setInt(1, Acrecodigo);
            rs = stmt.executeQuery();
            while (rs.next()) {
                cxp = new clscuentasporpagar();
                cxp.setCppcodigo(rs.getInt("Cppcodigo"));
                cxp.setProcodigo(rs.getInt("Procodigo"));
                cxp.setAcrecodigo(rs.getInt("Acrecodigo"));
                cxp.setVenid(rs.getInt("Venid"));
                cxp.setCppfechaemision(rs.getString("Cppfechaemision"));
                cxp.setCppmontototal(rs.getDouble("Cppmontototal"));
                cxp.setCppsaldopendiente(rs.getDouble("Cppsaldopendiente"));
                cxp.setCppestado(rs.getString("Cppestado").charAt(0));
                cxp.setTTid(rs.getInt("TTid"));
                cxp.setCpporigenid(rs.getInt("Cpporigenid"));
                lista.add(cxp);
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

    // Query por estado
    public List<clscuentasporpagar> queryPorEstado(char Cppestado) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        clscuentasporpagar cxp = null;
        List<clscuentasporpagar> lista = new ArrayList<>();

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_QUERY_POR_ESTADO);
            stmt.setString(1, String.valueOf(Cppestado));
            rs = stmt.executeQuery();
            while (rs.next()) {
                cxp = new clscuentasporpagar();
                cxp.setCppcodigo(rs.getInt("Cppcodigo"));
                cxp.setProcodigo(rs.getInt("Procodigo"));
                cxp.setAcrecodigo(rs.getInt("Acrecodigo"));
                cxp.setVenid(rs.getInt("Venid"));
                cxp.setCppfechaemision(rs.getString("Cppfechaemision"));
                cxp.setCppmontototal(rs.getDouble("Cppmontototal"));
                cxp.setCppsaldopendiente(rs.getDouble("Cppsaldopendiente"));
                cxp.setCppestado(rs.getString("Cppestado").charAt(0));
                cxp.setTTid(rs.getInt("TTid"));
                cxp.setCpporigenid(rs.getInt("Cpporigenid"));
                lista.add(cxp);
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

    // Query por rango de fechas
    public List<clscuentasporpagar> queryPorFechas(String fechaInicio, String fechaFin) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        clscuentasporpagar cxp = null;
        List<clscuentasporpagar> lista = new ArrayList<>();

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_QUERY_POR_FECHAS);
            stmt.setString(1, fechaInicio);
            stmt.setString(2, fechaFin);
            rs = stmt.executeQuery();
            while (rs.next()) {
                cxp = new clscuentasporpagar();
                cxp.setCppcodigo(rs.getInt("Cppcodigo"));
                cxp.setProcodigo(rs.getInt("Procodigo"));
                cxp.setAcrecodigo(rs.getInt("Acrecodigo"));
                cxp.setVenid(rs.getInt("Venid"));
                cxp.setCppfechaemision(rs.getString("Cppfechaemision"));
                cxp.setCppmontototal(rs.getDouble("Cppmontototal"));
                cxp.setCppsaldopendiente(rs.getDouble("Cppsaldopendiente"));
                cxp.setCppestado(rs.getString("Cppestado").charAt(0));
                cxp.setTTid(rs.getInt("TTid"));
                cxp.setCpporigenid(rs.getInt("Cpporigenid"));
                lista.add(cxp);
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

    // Query por monto total
    public List<clscuentasporpagar> queryPorMonto(double Cppmontototal) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        clscuentasporpagar cxp = null;
        List<clscuentasporpagar> lista = new ArrayList<>();

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_QUERY_POR_MONTO);
            stmt.setDouble(1, Cppmontototal);
            rs = stmt.executeQuery();
            while (rs.next()) {
                cxp = new clscuentasporpagar();
                cxp.setCppcodigo(rs.getInt("Cppcodigo"));
                cxp.setProcodigo(rs.getInt("Procodigo"));
                cxp.setAcrecodigo(rs.getInt("Acrecodigo"));
                cxp.setVenid(rs.getInt("Venid"));
                cxp.setCppfechaemision(rs.getString("Cppfechaemision"));
                cxp.setCppmontototal(rs.getDouble("Cppmontototal"));
                cxp.setCppsaldopendiente(rs.getDouble("Cppsaldopendiente"));
                cxp.setCppestado(rs.getString("Cppestado").charAt(0));
                cxp.setTTid(rs.getInt("TTid"));
                cxp.setCpporigenid(rs.getInt("Cpporigenid"));
                lista.add(cxp);
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

    // Query por saldo pendiente
    public List<clscuentasporpagar> queryPorSaldo(double Cppsaldopendiente) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        clscuentasporpagar cxp = null;
        List<clscuentasporpagar> lista = new ArrayList<>();

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_QUERY_POR_SALDO);
            stmt.setDouble(1, Cppsaldopendiente);
            rs = stmt.executeQuery();
            while (rs.next()) {
                cxp = new clscuentasporpagar();
                cxp.setCppcodigo(rs.getInt("Cppcodigo"));
                cxp.setProcodigo(rs.getInt("Procodigo"));
                cxp.setAcrecodigo(rs.getInt("Acrecodigo"));
                cxp.setVenid(rs.getInt("Venid"));
                cxp.setCppfechaemision(rs.getString("Cppfechaemision"));
                cxp.setCppmontototal(rs.getDouble("Cppmontototal"));
                cxp.setCppsaldopendiente(rs.getDouble("Cppsaldopendiente"));
                cxp.setCppestado(rs.getString("Cppestado").charAt(0));
                cxp.setTTid(rs.getInt("TTid"));
                cxp.setCpporigenid(rs.getInt("Cpporigenid"));
                lista.add(cxp);
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
    
    //Query por factura acreedor
        public clscuentasporpagar queryPorFactura(String faccomnumero) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        clscuentasporpagar cxp = new clscuentasporpagar();
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_QUERY_POR_FACTURA);
            stmt.setString(1, faccomnumero);
            rs = stmt.executeQuery();
            if (rs.next()) {
                cxp.setAcrecodigo(rs.getInt("Acrecodigo"));
                cxp.setCppmontototal(rs.getDouble("Faccomtotal"));
                cxp.setCppestado(rs.getString("Faccomestado").charAt(0));
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return cxp;
    }
}

