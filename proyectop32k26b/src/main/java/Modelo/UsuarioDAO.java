/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//Marco. Hernandez 9959-24-6201 
//Marco. Hernandez 24-marzo-2026 1. mantenimiento agregar utilidad de bitacora *posible fallo en login BD no acepta id "foreign key fails"e
package Modelo;


import Controlador.clsUsuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Modelo.BitacoraDAO;
import Controlador.clsUsuarioConectado;

/**
 *
 * @author visitante
 */
public class UsuarioDAO {

    private static final String SQL_SELECT = "SELECT usuid, usunombre, usucontrasena, usuultimasesion, usuestatus, usunombrereal, usucorreoe, usutelefono, usudireccion FROM usuario";
    private static final String SQL_INSERT = "INSERT INTO usuario(usunombre, usucontrasena, usuultimasesion, usuestatus, usunombrereal, usucorreoe, usutelefono, usudireccion) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE usuario SET usunombre=?, usucontrasena=?,  usuultimasesion=?, usuestatus=?, usunombrereal=?, usucorreoe=?, usutelefono=?, usudireccion=? WHERE usuid = ?";
    private static final String SQL_DELETE = "DELETE FROM usuario WHERE usuid=?";
    private static final String SQL_SELECT_NOMBRE = "SELECT usuid, usunombre, usucontrasena, usuultimasesion, usuestatus, usunombrereal, usucorreoe, usutelefono, usudireccion FROM usuario WHERE usunombre = ?";
    private static final String SQL_SELECT_ID = "SELECT usuid, usunombre, usucontrasena, usuultimasesion, usuestatus, usunombrereal, usucorreoe, usutelefono, usudireccion FROM usuario WHERE usuid = ?";     

    public List<clsUsuario> consultaUsuarios() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        List<clsUsuario> usuarios = new ArrayList<>();
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_SELECT);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("usuid");
                String nombre = rs.getString("usunombre");
                String contrasena = rs.getString("usucontrasena");
                String sesion = rs.getString("usuultimasesion");
		String estatus = rs.getString("usuestatus");
                String nombrereal= rs.getString("usunombrereal");
                String correo= rs.getString("usucorreoe");
                String telefono= rs.getString("usutelefono");
		String direccion= rs.getString("usudireccion");
		clsUsuario usuario = new clsUsuario();
                usuario.setUsuId(id);
                usuario.setUsuNombre(nombre);
                usuario.setUsuContrasena(contrasena);
                usuario.setUsuUltimaSesion(sesion);
                usuario.setUsuEstatus(estatus);
                usuario.setUsuNombreReal(nombrereal);
                usuario.setUsuCorreo(correo); 
                usuario.setUsuTelefono(telefono);
                usuario.setUsuDireccion(direccion);
              
                usuarios.add(usuario);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return usuarios;
    }

    public int ingresaUsuarios(clsUsuario usuario) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_INSERT);
            stmt.setString(1, usuario.getUsuNombre());
            stmt.setString(2, usuario.getUsuContrasena());
            stmt.setString(3, usuario.getUsuUltimaSesion());
	    stmt.setString(4, usuario.getUsuEstatus());
            stmt.setString(5, usuario.getUsuNombreReal());
            stmt.setString(6, usuario.getUsuCorreo());
            stmt.setString(7, usuario.getUsuTelefono());
            stmt.setString(8, usuario.getUsuDireccion());
            System.out.println("ejecutando query:" + SQL_INSERT);
            rows = stmt.executeUpdate();
            //
   if (rows > 0) {
    if (clsUsuarioConectado.getUsuId() > 0) {
        BitacoraDAO bitacora = new BitacoraDAO();
        System.out.println("Usuario conectado: " + clsUsuarioConectado.getUsuId());
        bitacora.insert(
            clsUsuarioConectado.getUsuId(),
            10001,
            "INSERT usuario: " + usuario.getUsuNombre()
        );
    } else {
        System.out.println("No se insertó en bitácora: usuario conectado inválido");
    }
}
            
            System.out.println("Registros afectados:" + rows);
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }

        return rows;
    }

    public int actualizaUsuarios(clsUsuario usuario) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            conn = Conexion.getConnection();
            System.out.println("ejecutando query: " + SQL_UPDATE);
            stmt = conn.prepareStatement(SQL_UPDATE);
            stmt.setString(1, usuario.getUsuNombre());
            stmt.setString(2, usuario.getUsuContrasena());
            stmt.setString(3, usuario.getUsuUltimaSesion());
	    stmt.setString(4, usuario.getUsuEstatus());
            stmt.setString(5, usuario.getUsuNombreReal());
            stmt.setString(6, usuario.getUsuCorreo());
            stmt.setString(7, usuario.getUsuTelefono());
            stmt.setString(8, usuario.getUsuDireccion());
            stmt.setInt(9, usuario.getUsuId());
            
            rows = stmt.executeUpdate();
            
 if (rows > 0) {
    if (clsUsuarioConectado.getUsuId() > 0) {
        BitacoraDAO bitacora = new BitacoraDAO();
        bitacora.insert(
            clsUsuarioConectado.getUsuId(),
            10001,
            "UPDATE usuario: " + usuario.getUsuNombre()
        );
    } else {
        System.out.println("No se insertó en bitácora: usuario conectado inválido");
    }
}
            
            
            System.out.println("Registros actualizado:" + rows);

        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }

        return rows;
    }

    public int borrarUsuarios(clsUsuario usuario) {
    Connection conn = null;
    PreparedStatement stmt = null;
    int rows = 0;

    try {
        conn = Conexion.getConnection();
        System.out.println("Ejecutando query:" + SQL_DELETE);

        // MOSTRAR USUARIO CONECTADO
        System.out.println("Usuario conectado antes de eliminar: " + clsUsuarioConectado.getUsuId());

        // BITACORA ANTES DEL DELETE
        if (clsUsuarioConectado.getUsuId() > 0) {
            System.out.println("Insertando en bitacora antes del delete...");

            BitacoraDAO bitacora = new BitacoraDAO();
            bitacora.insert(
                clsUsuarioConectado.getUsuId(),
                10001,
                "DELETE usuario ID: " + usuario.getUsuId()
            );
        }

        // DELETE
        stmt = conn.prepareStatement(SQL_DELETE);
        stmt.setInt(1, usuario.getUsuId());
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

    public clsUsuario consultaUsuariosPorNombre(clsUsuario usuario) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConnection();
            System.out.println("Ejecutando query:" + SQL_SELECT_NOMBRE + " objeto recibido: " + usuario);
            stmt = conn.prepareStatement(SQL_SELECT_NOMBRE);
            //stmt.setInt(1, usuario.getIdUsuario());            
            stmt.setString(1, usuario.getUsuNombre());
            rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("usuid");
                String nombre = rs.getString("usunombre");
                String contrasena = rs.getString("usucontrasena");
                String sesion = rs.getString("usuultimasesion");
		String estatus = rs.getString("usuestatus");
                String nombrereal= rs.getString("usunombrereal");
		String correo= rs.getString("usucorreoe");
		String telefono= rs.getString("usutelefono");
		String direccion= rs.getString("usudireccion");
		
                //usuario = new ClsUsuario();
                usuario.setUsuId(id);
                usuario.setUsuNombre(nombre);
                usuario.setUsuContrasena(contrasena);
                usuario.setUsuUltimaSesion(sesion);
                usuario.setUsuEstatus(estatus);
                usuario.setUsuNombreReal(nombrereal);
                usuario.setUsuCorreo(correo); 
                usuario.setUsuTelefono(telefono);
		usuario.setUsuDireccion(direccion);
                
                System.out.println(" registro consultado: " + usuario);                
            }
            //System.out.println("Registros buscado:" + persona);
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }

        //return personas;  // Si se utiliza un ArrayList
        return usuario;
    }
    public clsUsuario consultaUsuariosPorId(clsUsuario usuario) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConnection();
            System.out.println("Ejecutando query:" + SQL_SELECT_NOMBRE + " objeto recibido: " + usuario);
            stmt = conn.prepareStatement(SQL_SELECT_ID);
            stmt.setInt(1, usuario.getUsuId());            
            //stmt.setString(1, usuario.getNombreUsuario());
            rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("usuid");
                String nombre = rs.getString("usunombre");
                String contrasena = rs.getString("usucontrasena");
                String sesion = rs.getString("usuultimasesion");
		String estatus = rs.getString("usuestatus");
                String nombrereal= rs.getString("usunombrereal");
		String correo= rs.getString("usucorreoe");
		String telefono= rs.getString("usutelefono");
		String direccion= rs.getString("usudireccion");
		
                //usuario = new ClsUsuario();
                usuario.setUsuId(id);
                usuario.setUsuNombre(nombre);
                usuario.setUsuContrasena(contrasena);
                usuario.setUsuUltimaSesion(sesion);
                usuario.setUsuEstatus(estatus);
                usuario.setUsuEstatus(estatus);
                usuario.setUsuNombreReal(nombrereal);
                usuario.setUsuCorreo(correo); 
                usuario.setUsuTelefono(telefono);
		usuario.setUsuDireccion(direccion);
                
                System.out.println(" registro consultado: " + usuario);                
            }
            //System.out.println("Registros buscado:" + persona);
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }

        //return personas;  // Si se utiliza un ArrayList
        return usuario;
    }    
}
