//Boris de Leon 9959-24-6203
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;
import java.util.List;
import Modelo.UsuarioDAO;
/**
 *
 * @author visitante
 */
public class clsUsuario {
    private int UsuId;
    private String UsuNombre;
    private String UsuContrasena;
    private String UsuUltimaSesion;
    private String UsuEstatus;
    private String UsuNombreReal;
    private String UsuCorreo;
    private String UsuTelefono;
    private String UsuDireccion;
    
    public int getUsuId() {
        return UsuId;
    }

    public void setUsuId(int UsuId) {
        this.UsuId = UsuId;
    }

    public String getUsuNombre() {
        return UsuNombre;
    }

    public void setUsuNombre(String UsuNombre) {
        this.UsuNombre = UsuNombre;
    }

    public String getUsuContrasena() {
        return UsuContrasena;
    }

    public void setUsuContrasena(String UsuContrasena) {
        this.UsuContrasena = UsuContrasena;
    }

    public String getUsuUltimaSesion() {
        return UsuUltimaSesion;
    }

    public void setUsuUltimaSesion(String UsuUltimaSesion) {
        this.UsuUltimaSesion = UsuUltimaSesion;
    }

    public String getUsuEstatus() {
        return UsuEstatus;
    }

    public void setUsuEstatus(String UsuEstatus) {
        this.UsuEstatus = UsuEstatus;
    }

    public String getUsuNombreReal() {
        return UsuNombreReal;
    }

    public void setUsuNombreReal(String UsuNombreReal) {
        this.UsuNombreReal = UsuNombreReal;
    }

    public String getUsuCorreo() {
        return UsuCorreo;
    }

    public void setUsuCorreo(String UsuCorreo) {
        this.UsuCorreo = UsuCorreo;
    }

    public String getUsuTelefono() {
        return UsuTelefono;
    }

    public void setUsuTelefono(String UsuTelefono) {
        this.UsuTelefono = UsuTelefono;
    }

    public String getUsuDireccion() {
        return UsuDireccion;
    }

    public void setUsuDireccion(String UsuDireccion) {
        this.UsuDireccion = UsuDireccion;
    }

    public clsUsuario(int UsuId, String UsuNombre, String UsuContrasena, String UsuUltimaSesion, String UsuEstatus, String UsuNombreReal, String UsuCorreo, String UsuTelefono, String UsuDireccion) {
        this.UsuId = UsuId;
        this.UsuNombre = UsuNombre;
        this.UsuContrasena = UsuContrasena;
        this.UsuUltimaSesion = UsuUltimaSesion;
        this.UsuEstatus = UsuEstatus;
        this.UsuNombreReal = UsuNombreReal;
        this.UsuCorreo = UsuCorreo;
        this.UsuTelefono = UsuTelefono;
        this.UsuDireccion = UsuDireccion;
   }

    public clsUsuario(int UsuId, String UsuNombre, String UsuContrasena, String UsuUltimaSesion, String UsuEstatus, String UsuNombreReal, String UsuCorreo, String UsuTelefono) {
        this.UsuId = UsuId;
        this.UsuNombre = UsuNombre;
        this.UsuContrasena = UsuContrasena;
        this.UsuUltimaSesion = UsuUltimaSesion;
        this.UsuEstatus = UsuEstatus;
        this.UsuNombreReal = UsuNombreReal;
        this.UsuCorreo = UsuCorreo;
        this.UsuTelefono = UsuTelefono;
    }

    public clsUsuario(int UsuId, String UsuNombre, String UsuContrasena, String UsuUltimaSesion, String UsuEstatus, String UsuNombreReal, String UsuCorreo) {
        this.UsuId = UsuId;
        this.UsuNombre = UsuNombre;
        this.UsuContrasena = UsuContrasena;
        this.UsuUltimaSesion = UsuUltimaSesion;
        this.UsuEstatus = UsuEstatus;
        this.UsuNombreReal = UsuNombreReal;
        this.UsuCorreo = UsuCorreo;
    }

    public clsUsuario(int UsuId, String UsuNombre, String UsuContrasena, String UsuUltimaSesion, String UsuEstatus, String UsuNombreReal) {
        this.UsuId = UsuId;
        this.UsuNombre = UsuNombre;
        this.UsuContrasena = UsuContrasena;
        this.UsuUltimaSesion = UsuUltimaSesion;
        this.UsuEstatus = UsuEstatus;
        this.UsuNombreReal = UsuNombreReal;
    }

    public clsUsuario(int UsuId, String UsuNombre, String UsuContrasena, String UsuUltimaSesion, String UsuEstatus) {
        this.UsuId = UsuId;
        this.UsuNombre = UsuNombre;
        this.UsuContrasena = UsuContrasena;
        this.UsuUltimaSesion = UsuUltimaSesion;
        this.UsuEstatus = UsuEstatus;
    }

    public clsUsuario(int UsuId, String UsuNombre, String UsuContrasena, String UsuUltimaSesion) {
        this.UsuId = UsuId;
        this.UsuNombre = UsuNombre;
        this.UsuContrasena = UsuContrasena;
        this.UsuUltimaSesion = UsuUltimaSesion;
    }

    public clsUsuario(int UsuId, String UsuNombre, String UsuContrasena) {
        this.UsuId = UsuId;
        this.UsuNombre = UsuNombre;
        this.UsuContrasena = UsuContrasena;
    }

    public clsUsuario(int UsuId, String UsuNombre) {
        this.UsuId = UsuId;
        this.UsuNombre = UsuNombre;
    }

    public clsUsuario(int UsuId) {
        this.UsuId = UsuId;
    }

    public clsUsuario() {
    }

    @Override
    public String toString() {
        return "clsUsuario{" + "UsuId=" + UsuId + ", UsuNombre=" + UsuNombre + ", UsuContrasena=" + UsuContrasena + ", UsuUltimaSesion=" + UsuUltimaSesion + ", UsuEstatus=" + UsuEstatus + ", UsuNombreReal=" + UsuNombreReal + ", UsuCorreo=" + UsuCorreo + ", UsuTelefono=" + UsuTelefono + ", UsuDireccion=" + UsuDireccion + '}';
    }

    
  
    //Metodos de acceso a la capa controlador
    public clsUsuario getBuscarInformacionUsuarioPorNombre(clsUsuario usuario)
    {
        UsuarioDAO daousuario = new UsuarioDAO();
        return daousuario.consultaUsuariosPorNombre(usuario);
    }
    public clsUsuario getBuscarInformacionUsuarioPorId(clsUsuario usuario)
    {
        UsuarioDAO daousuario = new UsuarioDAO();
        return daousuario.consultaUsuariosPorId(usuario);
    }    
    public List<clsUsuario> getListadoUsuarios()
    {
        UsuarioDAO daousuario = new UsuarioDAO();
        List<clsUsuario> listadoUsuarios = daousuario.consultaUsuarios();
        return listadoUsuarios;
    }
    public int setBorrarUsuario(clsUsuario usuario)
    {
        UsuarioDAO daousuario = new UsuarioDAO();
        return daousuario.borrarUsuarios(usuario);
    }          
    public int setIngresarUsuario(clsUsuario usuario)
    {
        UsuarioDAO daousuario = new UsuarioDAO();
        return daousuario.ingresaUsuarios(usuario);
    }              
    public int setModificarUsuario(clsUsuario usuario)
    {
        UsuarioDAO daousuario = new UsuarioDAO();
        return daousuario.actualizaUsuarios(usuario);
    }
    
    //Modificación realizada por Dulce María Martínez Arévalo - 9959-24-4564
    //Permite realizar el cambio de contraseña
    //Método para cambiar contraseña
    public int setRestablecerContrasena(String nuevaContrasena) {
    UsuarioDAO daousuario = new UsuarioDAO();
    // Primero traer todos los datos del usuario
    clsUsuario usuarioCompleto = new clsUsuario();
    usuarioCompleto.setUsuId(this.UsuId);
    usuarioCompleto = daousuario.consultaUsuariosPorId(usuarioCompleto);
    // Solo cambiar la contraseña
    usuarioCompleto.setUsuContrasena(nuevaContrasena);
    // Guardar todo
    return daousuario.actualizaUsuarios(usuarioCompleto);
    }
}
