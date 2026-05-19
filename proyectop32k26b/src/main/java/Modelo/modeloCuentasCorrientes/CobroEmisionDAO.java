//Britany Mishel Hernandez Davila 9959-24-4178
//
package Modelo.modeloCuentasCorrientes;

import Controlador.Bancos.clsCuentaBancaria;
import Controlador.Bancos.clsMovimientoBancario;
import Controlador.controladorCuentasCorrientes.clsCobroEmision;
import Controlador.controladorCuentasCorrientes.clsCuentasPorCobrar;
import Modelo.Bancos.CuentaBancariaDAO;
import Modelo.Bancos.MovimientoBancarioDAO;
import Modelo.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class CobroEmisionDAO {
    private static final String SQL_SELECT = "SELECT Cobemid, Cpccodigo, Movbid, Cobfecha, Cobmonto, Cobtipo FROM cobrosemision";
    private static final String SQL_INSERT ="INSERT INTO cobrosemision(Cpccodigo, Movbid, Cobfecha, Cobmonto, Cobtipo) VALUES(?, ?, ?, ?, ?)";
    private static final String SQL_DELETE = "DELETE FROM cobrosemision WHERE Cobemid=?";
    private static final String SQL_QUERY = "SELECT Cobemid, Cpccodigo, Movbid, Cobfecha, Cobmonto, Cobtipo FROM cobrosemision WHERE Cobemid = ?";
 
    
    public List<clsCobroEmision> select() {
        //Declaracion de variables
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        clsCobroEmision cobro = null;
        //Definicion de matriz
        List<clsCobroEmision> cobros = new ArrayList<clsCobroEmision>();

        try {
            //Conexion con la base de datos
            conn = Modelo.Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_SELECT);
            rs = stmt.executeQuery();

            while (rs.next()) {

                int idCOBEM = rs.getInt("Cobemid");
                int codigoCPC = rs.getInt("Cpccodigo");
                int idMOVB = rs.getInt("Movbid");
                String fechaCOB = rs.getString("Cobfecha");
                double montoCOB = rs.getDouble("Cobmonto");
                String tipoCOB = rs.getString("Cobtipo");

                cobro = new clsCobroEmision();//Objeto

                // Asignacion de valores
                cobro.setIdCOBEM(idCOBEM);
                cobro.setCodigoCPC(codigoCPC);
                cobro.setIdMOVB(idMOVB);
                cobro.setFechaCOB(fechaCOB);
                cobro.setMontoCOB(montoCOB);
                cobro.setTipoCOB(tipoCOB);

                cobros.add(cobro);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Modelo.Conexion.close(rs);
            Modelo.Conexion.close(stmt);
            Modelo.Conexion.close(conn);
        }
        return cobros;
    }
//Metodo para insertar valores en la base de datos en la tabla cobrosEmision
    public int insert(clsCobroEmision cobro) {
        //Declaracion de variables
        Connection conn = null;
        PreparedStatement stmt = null;
        
        int rows = 0;
  
        try {  
            //Conexion con la base de datos
            conn = Modelo.Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_INSERT);

            //Asignacion de parametros
            stmt.setInt(1, cobro.getCodigoCPC());
            stmt.setInt(2, cobro.getIdMOVB());
            stmt.setString(3, cobro.getFechaCOB());
            stmt.setDouble(4, cobro.getMontoCOB());
            stmt.setString(5, cobro.getTipoCOB());
            
            //Mensaque de la accion realizada en consola
            System.out.println("Ejecutando query: " + SQL_INSERT);
            rows = stmt.executeUpdate();
            System.out.println("Registros afectados: " + rows);
        } catch (SQLException ex) {  
            ex.printStackTrace(System.out);  
        } finally {  
            Modelo.Conexion.close(stmt);
            Modelo.Conexion.close(conn); 
        }
        return rows; //Retorna el numero de registros afectados
    }
     
 //Metodo para eliminar valores en la base de datos en la tabla cobrosEmision    
    public int delete(clsCobroEmision cobro){
        //Declaracion de variables
        Connection conn = null;
        PreparedStatement stmt = null;
        
        int rows = 0;
        
        try{
            //Conexion con la base de datos
            conn = Modelo.Conexion.getConnection();
            //Mensaque de la accion realizada en consola
            System.out.println("Ejecutando query: "+ SQL_DELETE);
            stmt = conn.prepareStatement(SQL_DELETE);
            
            //Codigo de la cuenta por cobrar a eliminar
            stmt.setInt(1, cobro.getIdCOBEM());
            
            rows = stmt.executeUpdate();
            // Mostrar registros eliminados
            System.out.println("Registros eliminados: " + rows);
            
        } catch (SQLException ex){
            ex.printStackTrace(System.out);
        } finally {
            Modelo.Conexion.close(stmt);
            Modelo.Conexion.close(conn);
        }
        return rows; //Retorna el numero de registros afectados
    }
 //Metodo query para los valores en la base de datos en la tabla cobrosEmision   
    public clsCobroEmision query(clsCobroEmision cobro) {
        //Definicion de variables
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
             
        try {
            conn = Modelo.Conexion.getConnection(); //Conexion con la base de datos
            //Mensaque de la accion realizada en consola
            System.out.println("Ejecutando query:" + SQL_QUERY);
            stmt = conn.prepareStatement(SQL_QUERY);
            //Parametro de busqueda
            stmt.setInt(1, cobro.getIdCOBEM());
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                //Valores desde la base de datos
                int idCOBEM = rs.getInt("Cobemid");
                int codigoCPC = rs.getInt("Cpccodigo");
                int idMOVB = rs.getInt("Movbid");
                String fechaCOB = rs.getString("Cobfecha");
                double montoCOB = rs.getDouble("Cobmonto");
                String tipoCOB = rs.getString("Cobtipo");

                cobro = new clsCobroEmision(); //Creacion del objeto cobro
                
                //Asignacion de valores en el objeto 
                cobro.setIdCOBEM(idCOBEM);
                cobro.setCodigoCPC(codigoCPC);
                cobro.setIdMOVB(idMOVB);
                cobro.setFechaCOB(fechaCOB);
                cobro.setMontoCOB(montoCOB);
                cobro.setTipoCOB(tipoCOB);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return cobro;//Retorns el objeto encontrado
    }
     
    private int buscarCuentaBancariaPorCliente(int cliid) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Modelo.Conexion.getConnection();
            String sql = "SELECT CBANid FROM CuentaBancaria WHERE Cliid = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, cliid);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("CBANid");
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Modelo.Conexion.close(rs);
            Modelo.Conexion.close(stmt);
            Modelo.Conexion.close(conn);
        }
        return 0;
    }
    
    private int obtenerUltimoMovimientoPorCuenta(int cbanId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = Conexion.getConnection();
            String sql = "SELECT MAX(Movbid) as UltimoId FROM MovimientoBancario WHERE CBANid = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, cbanId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("UltimoId");
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return 0;
    }
    
    
    public int registrarCobro(int codigoCPC, double montoCobro, String fechaCobro,String tipoCobro) {
        //Variables        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ResultSet rsGenerated = null;
        
        try {
            conn = Modelo.Conexion.getConnection();
            //Buscar Cuenta por cobrar
            cuentasPorCobrarDAO daoCPC = new cuentasPorCobrarDAO();
            clsCuentasPorCobrar cuentaBuscar = new clsCuentasPorCobrar();
            cuentaBuscar.setCodigoCPC(codigoCPC);
        
            clsCuentasPorCobrar cuenta = daoCPC.query(cuentaBuscar);
        
            if (cuenta == null) {
                System.out.println("Cuenta por cobrar no encontrada: " + codigoCPC);
                return 0;
            }

            double saldoActual = cuenta.getSaldoCPC();

            if (montoCobro > saldoActual) {
                System.out.println("Monto excede saldo pendiente");
                return 0;
            }
                     
            //Buscar Cuenta Bancaria del cliente
            String sqlCuentaBanco = "SELECT CBANid FROM cuentabancaria WHERE Cliid = ?";
            stmt = conn.prepareStatement(sqlCuentaBanco);
            stmt.setInt(1, cuenta.getIdCLI());
            rs = stmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Cliente sin cuenta bancaria - Cliid: " + cuenta.getIdCLI());
                return 0;
            }

            int cbanId = rs.getInt("CBANid");
            System.out.println("Cuenta bancaria encontrada - CBANid: " + cbanId);
           
            
            //Registrar Movimiento Bancario
            String sqlInsertMov = "INSERT INTO movimientobancario (Movbfechamovimiento,	Movbmonto, Movdescripcion, "
                    + "	CBANid, TTid , Movbtipomov, Movbreferencia, Movbconciliado) VALUES (NOW(), ?, ?, ?, ?, ?, ?, ?)";
                        
            stmt = conn.prepareStatement(sqlInsertMov, Statement.RETURN_GENERATED_KEYS);
            stmt.setDouble(1, montoCobro);
            stmt.setString(2, "Cobro CPC " + codigoCPC);
            stmt.setInt(3, cbanId);
            stmt.setInt(4, 5);  // TTid = Cobro
            stmt.setString(5, "Credito");
            stmt.setString(6, "COBRO-CPC-" + codigoCPC);
            stmt.setString(7, "N");
        
            if (stmt.executeUpdate() <= 0) {
              System.out.println("Error al insertar movimiento bancario");
              return 0;
            }     
            // Obtener ID del movimiento generado
            int movId = 0;
            rsGenerated = stmt.getGeneratedKeys();
            if (rsGenerated.next()) {
                movId = rsGenerated.getInt(1);
            }
            System.out.println("Movimiento bancario registrado - ID: " + movId);
            
            //Registro del cobro 
            clsCobroEmision cobro = new clsCobroEmision();
            cobro.setCodigoCPC(codigoCPC);
            cobro.setIdMOVB(movId);
            cobro.setFechaCOB(fechaCobro);
            cobro.setMontoCOB(montoCobro);
            cobro.setTipoCOB(tipoCobro);
            int resultadoCobro = insert(cobro);

            if (resultadoCobro <= 0) {
                System.out.println("Error al insertar cobro emision");
                return 0;
            }
            
            //Actualizar saldo              
            cuenta.setSaldoCPC(montoCobro);
            daoCPC.actualizarSaldo(cuenta);
            
            double nuevoSaldo = saldoActual - montoCobro;
            //Actualizar estado
            if (nuevoSaldo <= 0.01) {
                cuenta.setEstadoCPC("Pagada");
                daoCPC.updateEstado(cuenta);
            }
            System.out.println("Cobro registrado exitosamente");
            return 1;
       
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            return 0;
        } finally{    
            Modelo.Conexion.close(rsGenerated);
            Modelo.Conexion.close(rs);   
            Modelo.Conexion.close(stmt); 
            Modelo.Conexion.close(conn);
        }
    }
}
