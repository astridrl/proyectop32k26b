//Ferdynand Monroy & Anthony Hetzale marzo 2026
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;
import java.sql.*;
/**
 *
 * @author ferito
 */
public class Conexion { //aquí se encuentra la conexion a la BD llamada proyecto
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/sig";
    private static final String JDBC_USER = "usuprueba";
    private static final String JDBC_PASS = "123456";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
    }

    public static void close(ResultSet rs){
        try { rs.close(); } 
        catch (SQLException ex){ ex.printStackTrace(System.out); }
    }

    public static void close(PreparedStatement stmt){
        try { stmt.close(); } 
        catch (SQLException ex){ ex.printStackTrace(System.out); }
    }

    public static void close(Connection conn){
        try { conn.close(); } 
        catch (SQLException ex){ ex.printStackTrace(System.out); }
    } 

    public static Connection getConexion() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
