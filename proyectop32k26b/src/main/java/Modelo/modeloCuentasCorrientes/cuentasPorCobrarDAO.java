//Britany Mishel Hernandez Davila 9959-24-4178
//
package Modelo.modeloCuentasCorrientes;

import Controlador.Bancos.clsMovimientoBancario;
import Modelo.Bancos.MovimientoBancarioDAO;
import Controlador.controladorCuentasCorrientes.clsCuentasPorCobrar;
import Modelo.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; 
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;


public class cuentasPorCobrarDAO {
    //Acciones en la base de datos de cuentasporcobra
    private static final String SQL_SELECT = "SELECT Cpccodigo, Cliid, Cpcfecha,Cpcmonto,Cpcsaldo,Cpcestado FROM cuentasporcobrar";
    private static final String SQL_INSERT = "INSERT INTO cuentasporcobrar(Cliid, Cpcfecha,Cpcmonto,Cpcsaldo,Cpcestado) VALUES(?, ?, ?, ?, ?)";
    //Metodos para actualizar valores
    private static final String SQL_UPDATE = "UPDATE cuentasporcobrar SET Cliid=?, Cpcfecha=?, Cpcmonto=?, Cpcsaldo=?, Cpcestado=? WHERE Cpccodigo = ?";
    private static final String SQL_UPDATE_ESTADO = "UPDATE cuentasporcobrar SET Cpcestado = ? WHERE Cpccodigo = ?";
    private static final String SQL_UPDATE_SALDO = "UPDATE cuentasporcobrar SET Cpcsaldo = Cpcsaldo - ? WHERE Cpccodigo = ?";
    //Elimina aunque se tenia la idea de que en ves de liminar se anulara quedando registro
    private static final String SQL_DELETE = "DELETE FROM cuentasporcobrar WHERE Cpccodigo=?";
    //Busquedas especificas
    private static final String SQL_QUERY = "SELECT Cpccodigo, Cliid, Cpcfecha,Cpcmonto,Cpcsaldo,Cpcestado FROM cuentasporcobrar WHERE Cpccodigo = ?";
    private static final String SQL_BUSCAR_CLIENTE = "SELECT Cpccodigo, Cliid, Cpcfecha,Cpcmonto,Cpcsaldo,Cpcestado FROM cuentasporcobrar WHERE Cliid = ?";
    private static final String SQL_BUSCAR_MONTO = "SELECT Cpccodigo, Cliid, Cpcfecha,Cpcmonto,Cpcsaldo,Cpcestado FROM cuentasporcobrar WHERE Cpcmonto = ?";
    private static final String SQL_BUSCAR_FECHA = "SELECT Cpccodigo, Cliid, Cpcfecha,Cpcmonto,Cpcsaldo,Cpcestado FROM cuentasporcobrar WHERE Cpcfecha = ?";
     
    //Busqueda de factura por numero
    private static final String SQL_BUSCAR_FACTURA ="SELECT Cliid, Facfecha, Factotal FROM facturasventa WHERE Facid = ?";    
    
    public List<clsCuentasPorCobrar> select() {
        //Declaracion de variables
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        clsCuentasPorCobrar cuenta = null;
        //Definicion de matriz
        List<clsCuentasPorCobrar> cuentas = new ArrayList<clsCuentasPorCobrar>();

        try {
            //Conexion con la base de datos
            conn = Modelo.Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_SELECT);
            rs = stmt.executeQuery();

            while (rs.next()) {

                int codigoCPC = rs.getInt("Cpccodigo");
                int idCLI = rs.getInt("Cliid");
                String fechaCPC = rs.getString("Cpcfecha");
                double montoCPC = rs.getDouble("Cpcmonto");
                double saldoCPC = rs.getDouble("Cpcsaldo");
                String estadoCPC = rs.getString("Cpcestado");

                cuenta = new clsCuentasPorCobrar();

                cuenta.setCodigoCPC(codigoCPC);
                cuenta.setIdCLI(idCLI);
                cuenta.setFechaCPC(fechaCPC);
                cuenta.setMontoCPC(montoCPC);
                cuenta.setSaldoCPC(saldoCPC);
                cuenta.setEstadoCPC(estadoCPC);

                cuentas.add(cuenta);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Modelo.Conexion.close(rs);
            Modelo.Conexion.close(stmt);
            Modelo.Conexion.close(conn);
        }
        return cuentas;
    }

//Metodo para insertar valores en la base de datos en la tabla cuentasporcobrar
    public int insert(clsCuentasPorCobrar cuenta) {
        //Declaracion de variables
        Connection conn = null;
        PreparedStatement stmt = null;
        
        int rows = 0;
  
        try {  
            //Conexion con la base de datos
            conn = Modelo.Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_INSERT);

            //Asignacion de parametros
            stmt.setInt(1, cuenta.getIdCLI());
            stmt.setString(2, cuenta.getFechaCPC());
            stmt.setDouble(3, cuenta.getMontoCPC());
            stmt.setDouble(4, cuenta.getSaldoCPC());
            stmt.setString(5, cuenta.getEstadoCPC());
            
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
    
//Metodo para actualizar los valores en la base de datos en la tabla cuentasporcobrar    
    public int update(clsCuentasPorCobrar cuenta) {
        //Declaracion de variables
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;

        try {
            //Conexion con la base de datos
            conn = Modelo.Conexion.getConnection();
            //Mensaque de la accion realizada en consola
            System.out.println("Ejecutando query: " + SQL_UPDATE);           
            stmt = conn.prepareStatement(SQL_UPDATE);
            
            //Asignacion de valores en los parametros
            stmt.setInt(1, cuenta.getIdCLI());
            stmt.setString(2, cuenta.getFechaCPC());
            stmt.setDouble(3, cuenta.getMontoCPC());
            stmt.setDouble(4, cuenta.getSaldoCPC());
            stmt.setString(5, cuenta.getEstadoCPC());
            //Codigo de la cuenta que se actualizara
            stmt.setInt(6, cuenta.getCodigoCPC());

            rows = stmt.executeUpdate();
            System.out.println("Registros actualizados: " + rows);    
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Modelo.Conexion.close(stmt);
            Modelo.Conexion.close(conn);
        }
        return rows; //Retorna el numero de registros afectados
    }   
 
//Metodo para eliminar valores en la base de datos en la tabla cuentasporcobrar    
    public int delete(clsCuentasPorCobrar cuenta){
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
            stmt.setInt(1, cuenta.getCodigoCPC());
            
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
 
//Metodo query para los valores en la base de datos en la tabla cuentasporcobrar   
    public clsCuentasPorCobrar query(clsCuentasPorCobrar cuenta) {
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
            stmt.setInt(1, cuenta.getCodigoCPC());
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                //Valores desde la base de datos
                int codigo = rs.getInt("Cpccodigo");
                int idCliente = rs.getInt("Cliid");
                String fecha = rs.getString("Cpcfecha");
                double monto = rs.getDouble("Cpcmonto");
                double saldo = rs.getDouble("Cpcsaldo");
                String estado = rs.getString("Cpcestado");

                cuenta = new clsCuentasPorCobrar(); //Creacion del objeto cuenta
                
                //Asignacion de valores en el objeto 
                cuenta.setCodigoCPC(codigo);
                cuenta.setIdCLI(idCliente);
                cuenta.setFechaCPC(fecha);
                cuenta.setMontoCPC(monto);
                cuenta.setSaldoCPC(saldo);
                cuenta.setEstadoCPC(estado);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return cuenta;//Retorns el objeto encontrado
    }

//Método para actualizar el saldo despues de realizar un cobro
    public int actualizarSaldo(clsCuentasPorCobrar cuenta) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;

        try {
            conn = Modelo.Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE_SALDO);
            stmt.setDouble(1, cuenta.getSaldoCPC());
            stmt.setInt(2, cuenta.getCodigoCPC());
  
            rows = stmt.executeUpdate();

        } catch (SQLException ex) {       
            ex.printStackTrace(System.out);    
        } finally {    
            Modelo.Conexion.close(stmt);
            Modelo.Conexion.close(conn);  
        }  
        return rows;
    }

//Metodo para realizar una busqueda por medio del id del cliente    
    public List<clsCuentasPorCobrar> buscarPorCliente(clsCuentasPorCobrar cuenta){
        //Declaracion de variables
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        //Definicion de matriz
        List<clsCuentasPorCobrar> cuentas = new ArrayList<>();
            
        try{
            //Conexion con la base de datos
            conn = Modelo.Conexion.getConnection();
            //Mensaque de la accion realizada en consola
            System.out.println("Ejecutando query:" + SQL_BUSCAR_CLIENTE);
            stmt = conn.prepareStatement(SQL_BUSCAR_CLIENTE);
            //Parametro de busqueda
            stmt.setInt(1, cuenta.getIdCLI());
            rs = stmt.executeQuery();
                        
            while(rs.next()){
                //Valores desde la base de datos
                int codigo = rs.getInt("Cpccodigo");
                int idCliente = rs.getInt("Cliid");
                String fecha = rs.getString("Cpcfecha");
                double monto = rs.getDouble("Cpcmonto");
                double saldo = rs.getDouble("Cpcsaldo");
                String estado = rs.getString("Cpcestado");

                cuenta = new clsCuentasPorCobrar(); //Creacion del objeto cuenta
                
                //Asignacion de valores en el objeto 
                cuenta.setCodigoCPC(codigo);
                cuenta.setIdCLI(idCliente);
                cuenta.setFechaCPC(fecha);
                cuenta.setMontoCPC(monto);
                cuenta.setSaldoCPC(saldo);
                cuenta.setEstadoCPC(estado);
                
                cuentas.add(cuenta);
            }           
        }catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally{
            Modelo.Conexion.close(stmt);
            Modelo.Conexion.close(conn);          
        }
        return cuentas;  
    }
    
//Metodo para realizar una busqueda por medio del monto en la cuenta    
    public List<clsCuentasPorCobrar> buscarPorMonto(clsCuentasPorCobrar cuenta){
        //Declaracion de variables
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        //Definicion de matriz
        List<clsCuentasPorCobrar> cuentas = new ArrayList<>();
            
        try{
            //Conexion con la base de datos
            conn = Modelo.Conexion.getConnection();
            //Mensaque de la accion realizada en consola
            System.out.println("Ejecutando query:" + SQL_BUSCAR_MONTO);
            stmt = conn.prepareStatement(SQL_BUSCAR_MONTO);
            //Parametro de busqueda
            stmt.setDouble(1, cuenta.getMontoCPC());
            rs = stmt.executeQuery();
                        
            while(rs.next()){
                //Valores desde la base de datos
                int codigo = rs.getInt("Cpccodigo");
                int idCliente = rs.getInt("Cliid");
                String fecha = rs.getString("Cpcfecha");
                double monto = rs.getDouble("Cpcmonto");
                double saldo = rs.getDouble("Cpcsaldo");
                String estado = rs.getString("Cpcestado");

                cuenta = new clsCuentasPorCobrar(); //Creacion del objeto cuenta
                
                //Asignacion de valores en el objeto 
                cuenta.setCodigoCPC(codigo);
                cuenta.setIdCLI(idCliente);
                cuenta.setFechaCPC(fecha);
                cuenta.setMontoCPC(monto);
                cuenta.setSaldoCPC(saldo);
                cuenta.setEstadoCPC(estado);
                
                cuentas.add(cuenta);
            }           
        }catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally{
            Modelo.Conexion.close(stmt);
            Modelo.Conexion.close(conn);          
        }
        return cuentas;  
    }
    
//Metodo para realizar una busqueda por medio del id del cliente    
    public List<clsCuentasPorCobrar> buscarPorFecha(clsCuentasPorCobrar cuenta){
        //Declaracion de variables
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        //Definicion de matriz
        List<clsCuentasPorCobrar> cuentas = new ArrayList<>();
            
        try{
            //Conexion con la base de datos
            conn = Modelo.Conexion.getConnection();
            //Mensaque de la accion realizada en consola
            System.out.println("Ejecutando query:" + SQL_BUSCAR_FECHA);
            stmt = conn.prepareStatement(SQL_BUSCAR_FECHA);
            //Parametro de busqueda
            stmt.setString(1, cuenta.getFechaCPC());
            rs = stmt.executeQuery();
                        
            while(rs.next()){
                //Valores desde la base de datos
                int codigo = rs.getInt("Cpccodigo");
                int idCliente = rs.getInt("Cliid");
                String fecha = rs.getString("Cpcfecha");
                double monto = rs.getDouble("Cpcmonto");
                double saldo = rs.getDouble("Cpcsaldo");
                String estado = rs.getString("Cpcestado");

                cuenta = new clsCuentasPorCobrar(); //Creacion del objeto cuenta
                
                //Asignacion de valores en el objeto 
                cuenta.setCodigoCPC(codigo);
                cuenta.setIdCLI(idCliente);
                cuenta.setFechaCPC(fecha);
                cuenta.setMontoCPC(monto);
                cuenta.setSaldoCPC(saldo);
                cuenta.setEstadoCPC(estado);
                
                cuentas.add(cuenta);
            }           
        }catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally{
            Modelo.Conexion.close(stmt);
            Modelo.Conexion.close(conn);          
        }
        return cuentas;  
    }
 
//Metodo para obtener el saldo de una cuenta   
    public double obtenerSaldo(clsCuentasPorCobrar cuenta){
        //Declaracion de variables        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        double saldo = 0;

        try{
            //Conexion con la base de datos
            conn = Modelo.Conexion.getConnection();        
            //Buca el saldo por codigo del cobro
            String sql = "SELECT Cpcsaldo FROM cuentasporcobrar WHERE Cpccodigo=?";
            stmt = conn.prepareStatement(sql);
            //Codigo de la cuenta a buscar
            stmt.setInt(1, cuenta.getCodigoCPC());
            rs = stmt.executeQuery();

            if(rs.next()){
                saldo = rs.getDouble("Cpcsaldo");
            }
        }catch(SQLException ex){
            ex.printStackTrace(System.out);
        }finally{
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return saldo;
    }
//Metodo para actualizar el estado    
    public int updateEstado(clsCuentasPorCobrar cuenta){
        //Declaracion de variable
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
   
        try{
            //Conexion con la base de datos
            conn = Modelo.Conexion.getConnection();
            //Mensaque de la accion realizada en consola            
            System.out.println("Ejecutando query: " + SQL_UPDATE_ESTADO);
            stmt = conn.prepareStatement(SQL_UPDATE_ESTADO);

            //Asignacion de valores en el objeto
            stmt.setString(1, cuenta.getEstadoCPC());
            stmt.setInt(2, cuenta.getCodigoCPC());

            rows = stmt.executeUpdate();
            System.out.println("Registros actualizados: " + rows);
        }catch(SQLException ex){
            ex.printStackTrace(System.out);
        }finally{
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return rows;
    }
 
    //Metodo para obtener datos de una factura
public Object[] buscarFactura(int idFactura){

    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    Object[] datos = null;

    try{
        conn = Modelo.Conexion.getConnection();

        stmt = conn.prepareStatement(SQL_BUSCAR_FACTURA);
        stmt.setInt(1, idFactura);

        rs = stmt.executeQuery();

        if(rs.next()){

            datos = new Object[3];

            datos[0] = rs.getInt("Cliid");
            datos[1] = rs.getDate("Facfecha");
            datos[2] = rs.getDouble("Factotal");
        }

    }catch(SQLException ex){
        ex.printStackTrace(System.out);

    }finally{
        Conexion.close(rs);
        Conexion.close(stmt);
        Conexion.close(conn);
    }

    return datos;
}
}
