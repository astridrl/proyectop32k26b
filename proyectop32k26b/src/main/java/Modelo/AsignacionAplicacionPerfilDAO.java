/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;
import Controlador.clsAsignacionAplicacionPerfil;
import Controlador.clsAplicaciones;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Xander Reyes
 */
public class AsignacionAplicacionPerfilDAO {
           
    // Consulta que obtiene todos los registros de asignaciones según un perfil específico
private static final String SQL_SELECT_POR_PERFIL = 
    "SELECT * FROM asignacionaplicacionperfil WHERE Percodigo = ?"; // ? es el parámetro dinámico del código de perfil

// Consulta que obtiene aplicaciones que NO están asignadas a un perfil
private static final String SQL_SELECT_DISPONIBLES = 
    "SELECT * FROM aplicaciones WHERE Aplcodigo NOT IN (SELECT Aplcodigo FROM asignacionaplicacionperfil WHERE Percodigo = ?)"; // Subconsulta para excluir asignadas

// Consulta para insertar una nueva asignación de aplicación a perfil con permisos
private static final String SQL_INSERT = 
    "INSERT INTO asignacionaplicacionperfil (Aplcodigo, Percodigo, APLPins, APLPsel, APLPupd, APLPdel, APLPrep) VALUES (?,?,?,?,?,?,?)"; // 7 parámetros a insertar

// Consulta para eliminar una asignación específica (por aplicación y perfil)
private static final String SQL_DELETE =
    "DELETE FROM asignacionaplicacionperfil WHERE Aplcodigo=? AND Percodigo=?"; // Eliminación por clave compuesta

// Consulta para actualizar los permisos de una asignación existente
private static final String SQL_UPDATE = 
    "UPDATE asignacionaplicacionperfil SET APLPins=?, APLPsel=?, APLPupd=?, APLPdel=?, APLPrep=? " + 
    "WHERE Aplcodigo=? AND Percodigo=?"; // Primero permisos, luego condiciones

// Consulta para obtener datos específicos de asignaciones por perfil
private static final String SQL_QUERY =
    "SELECT Aplcodigo, Percodigo, APLPins, APLPsel, APLPupd, APLPdel, APLPdel FROM asignacionaplicacionperfil WHERE Percodigo=?"; // Nota: APLPdel está repetido (posible error)
    

public List<String> obtenerNombresPerfiles() {
    List<String> listaPerfiles = new ArrayList<>();
    String sql = "SELECT Pernombre FROM perfiles"; 
    
    try (Connection conn = Conexion.getConnection(); 
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
         
        while (rs.next()) {
            listaPerfiles.add(rs.getString("Pernombre"));
        }
    } catch (SQLException e) {
        System.out.println("Error al cargar perfiles: " + e.toString());
    }
    return listaPerfiles;
} 
public int obtenerIdPorNombre(String nombrePerfil) {
    int idPerfil = -1;
    String sql = "SELECT Percodigo FROM perfiles WHERE Pernombre = ?"; 
    
    try (Connection conn = Conexion.getConnection(); 
         PreparedStatement stmt = conn.prepareStatement(sql)) {
         
        stmt.setString(1, nombrePerfil);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                idPerfil = rs.getInt("Percodigo");
            }
        }
    } catch (SQLException e) {
        System.out.println("Error al buscar ID por nombre: " + e.toString());
    }
    return idPerfil;
}


// Método que verifica si un perfil existe en la tabla perfiles
public boolean verificarExistenciaPerfil(int perCodigo) {

    String sql = "SELECT COUNT(*) FROM perfiles WHERE Percodigo = ?"; // Cuenta registros con ese código

    try (Connection conn = Conexion.getConnection(); // Abre conexión a BD
         PreparedStatement stmt = conn.prepareStatement(sql)) { // Prepara la consulta

        stmt.setInt(1, perCodigo); // Asigna el valor al parámetro ?

        ResultSet rs = stmt.executeQuery(); // Ejecuta la consulta

        if (rs.next()) { // Si hay resultado
            return rs.getInt(1) > 0; // Retorna true si existe al menos un registro
        }

    } catch (SQLException ex) {
        ex.printStackTrace(System.out); // Muestra error en consola
    }

    return false; // Retorna false si no existe o hay error
}


// Obtiene lista de aplicaciones asignadas a un perfil
public List<clsAsignacionAplicacionPerfil> obtenerAsignadas(int Percodigo) {

    List<clsAsignacionAplicacionPerfil> lista = new ArrayList<>(); // Lista de resultados

    try (Connection conn = Conexion.getConnection(); // Conexión a BD
         PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_POR_PERFIL)) { // Prepara consulta

        stmt.setInt(1, Percodigo); // Asigna parámetro

        ResultSet rs = stmt.executeQuery(); // Ejecuta consulta

        while (rs.next()) { // Recorre resultados
            clsAsignacionAplicacionPerfil asignacion = new clsAsignacionAplicacionPerfil(); // Crea objeto
            asignacion.setAplcodigo(rs.getInt("Aplcodigo")); // Asigna código de aplicación
            lista.add(asignacion); // Agrega a lista
        }

    } catch (SQLException ex) {
        ex.printStackTrace(System.out);
    }

    return lista; // Retorna lista
}


// Método duplicado que también verifica existencia de perfil
public boolean existePerfil(int perCodigo) {

    String sql = "SELECT COUNT(*) FROM perfiles WHERE Percodigo = ?";

    try (Connection conn = Conexion.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, perCodigo);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getInt(1) > 0; // Retorna true si existe
        }

    } catch (SQLException ex) {
        ex.printStackTrace(System.out);
    }

    return false;
}


// Obtiene aplicaciones disponibles (no asignadas a un perfil)
public List<clsAplicaciones> obtenerDisponibles(int Percodigo) {

    List<clsAplicaciones> lista = new ArrayList<>();

    try (Connection conn = Conexion.getConnection();
         PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_DISPONIBLES)) {

        stmt.setInt(1, Percodigo);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            clsAplicaciones app = new clsAplicaciones(); // Crea objeto aplicación
            app.setAplcodigo(rs.getInt("Aplcodigo")); // Código
            app.setAplnombre(rs.getString("Aplnombre")); // Nombre
            app.setAplestado(rs.getString("Aplestado")); // Estado
            lista.add(app); // Agrega a lista
        }

    } catch (SQLException ex) {
        ex.printStackTrace(System.out);
    }

    return lista;
}


// Obtiene un registro específico de asignación
public clsAsignacionAplicacionPerfil obtenerRegistroEspecifico(int aplCodigo, int perCodigo) {

    clsAsignacionAplicacionPerfil asig = null; // Inicializa en null

    String sql = "SELECT * FROM asignacionaplicacionperfil WHERE Aplcodigo = ? AND Percodigo = ?";

    try (Connection conn = Conexion.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, aplCodigo); // Parámetro 1
        stmt.setInt(2, perCodigo); // Parámetro 2

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            asig = new clsAsignacionAplicacionPerfil(rs.getInt("Aplcodigo"), rs.getInt("Percodigo"));
            asig.setAPLPins(rs.getString("APLPins")); // Permiso insertar
            asig.setAPLPsel(rs.getString("APLPsel")); // Permiso seleccionar
            asig.setAPLPupd(rs.getString("APLPupd")); // Permiso actualizar
            asig.setAPLPdel(rs.getString("APLPdel")); // Permiso eliminar
            asig.setAPLPrep(rs.getString("APLPrep")); // Permiso reportes
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return asig;
}


// Actualiza un registro existente
public int update(clsAsignacionAplicacionPerfil asignacion) {

    Connection conn = null; // Conexión
    PreparedStatement stmt = null; // Sentencia preparada
    int rows = 0; // Filas afectadas

    try {

        conn = Conexion.getConnection(); // Abre conexión
        stmt = conn.prepareStatement(SQL_UPDATE); // Prepara UPDATE

        // Asigna permisos
        stmt.setString(1, String.valueOf(asignacion.getAPLPins()));
        stmt.setString(2, String.valueOf(asignacion.getAPLPsel()));
        stmt.setString(3, String.valueOf(asignacion.getAPLPupd()));
        stmt.setString(4, String.valueOf(asignacion.getAPLPdel()));
        stmt.setString(5, String.valueOf(asignacion.getAPLPrep()));

        // Asigna condiciones (WHERE)
        stmt.setInt(6, asignacion.getAplcodigo());
        stmt.setInt(7, asignacion.getPercodigo());

        rows = stmt.executeUpdate(); // Ejecuta actualización

        System.out.println("Registros actualizados:" + rows);

    } catch (SQLException ex) {
        ex.printStackTrace(System.out);
    } finally {
        Conexion.close(stmt); // Cierra statement
        Conexion.close(conn); // Cierra conexión
    }

    return rows;
}


// Inserta un nuevo registro
public int insert(clsAsignacionAplicacionPerfil asig) {

    try (Connection conn = Conexion.getConnection();
         PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {

        stmt.setInt(1, asig.getAplcodigo());
        stmt.setInt(2, asig.getPercodigo());

        stmt.setString(3, String.valueOf(asig.getAPLPins()));
        stmt.setString(4, String.valueOf(asig.getAPLPsel()));
        stmt.setString(5, String.valueOf(asig.getAPLPupd()));
        stmt.setString(6, String.valueOf(asig.getAPLPdel()));
        stmt.setString(7, String.valueOf(asig.getAPLPrep()));

        return stmt.executeUpdate(); // Ejecuta INSERT

    } catch (SQLException ex) {
        ex.printStackTrace();
        return 0;
    }
}


// Inserta o actualiza según existencia
public int guardarOActualizar(clsAsignacionAplicacionPerfil asig) {

    clsAsignacionAplicacionPerfil existe = 
        obtenerRegistroEspecifico(asig.getAplcodigo(), asig.getPercodigo()); // Busca si existe

    if (existe == null) {
        return insert(asig); // Inserta si no existe
    } else {
        return update(asig); // Actualiza si existe
    }
}


// Elimina un registro específico
public int borrarRegistroEspecifico(int aplCodigo, int perCodigo) {

    String sql = "DELETE FROM asignacionaplicacionperfil WHERE Aplcodigo = ? AND Percodigo = ?";

    int rows = 0;

    try (Connection conn = Conexion.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, aplCodigo);
        stmt.setInt(2, perCodigo);

        rows = stmt.executeUpdate(); // Ejecuta DELETE

    } catch (SQLException ex) {
        ex.printStackTrace(System.out);
    }

    return rows;
}


// Elimina todos los registros de un perfil
public int borrarTodoDePerfil(int perCodigo) {

    String sql = "DELETE FROM asignacionaplicacionperfil WHERE Percodigo = ?";

    try (Connection conn = Conexion.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, perCodigo);

        return stmt.executeUpdate();

    } catch (SQLException ex) {
        ex.printStackTrace(System.out);
        return 0;
    }
}


// Elimina usando objeto
public int delete(clsAsignacionAplicacionPerfil asignacion) {

    Connection conn = null;
    PreparedStatement stmt = null;
    int rows = 0;

    try {
        conn = Conexion.getConnection();
        stmt = conn.prepareStatement(SQL_DELETE);

        stmt.setInt(1, asignacion.getAplcodigo());
        stmt.setInt(2, asignacion.getPercodigo());

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
  
}
