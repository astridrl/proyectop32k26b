package Controlador;

import Modelo.AsignacionAplicacionUsuarioDAO;
import java.util.List;

public class clsAsignacionAplicacionUsuario {

    private int Aplcodigo;
    private int UsuId;
    private String APLUins;
    private String APLUsel;
    private String APLUupd;
    private String APLUdel;
    private String APLUrep;

    public clsAsignacionAplicacionUsuario() {}

    public clsAsignacionAplicacionUsuario(int Aplcodigo, int UsuId,
            String APLUins, String APLUsel, String APLUupd,
            String APLUdel, String APLUrep) {
        this.Aplcodigo = Aplcodigo;
        this.UsuId     = UsuId;
        this.APLUins   = APLUins;
        this.APLUsel   = APLUsel;
        this.APLUupd   = APLUupd;
        this.APLUdel   = APLUdel;
        this.APLUrep   = APLUrep;
    }

    // GETTERS Y SETTERS
    public int getAplcodigo() { return Aplcodigo; }
    public void setAplcodigo(int Aplcodigo) { this.Aplcodigo = Aplcodigo; }
    public int getUsuId() { return UsuId; }
    public void setUsuId(int UsuId) { this.UsuId = UsuId; }
    public String getAPLUins() { return APLUins; }
    public void setAPLUins(String APLUins) { this.APLUins = APLUins; }
    public String getAPLUsel() { return APLUsel; }
    public void setAPLUsel(String APLUsel) { this.APLUsel = APLUsel; }
    public String getAPLUupd() { return APLUupd; }
    public void setAPLUupd(String APLUupd) { this.APLUupd = APLUupd; }
    public String getAPLUdel() { return APLUdel; }
    public void setAPLUdel(String APLUdel) { this.APLUdel = APLUdel; }
    public String getAPLUrep() { return APLUrep; }
    public void setAPLUrep(String APLUrep) { this.APLUrep = APLUrep; }

    // ── MÉTODOS DE NEGOCIO ────────────────────────────────────────────────

    public int setAsignarAplicacion(clsAsignacionAplicacionUsuario asignacion) {
        return new AsignacionAplicacionUsuarioDAO().ingresaAsignacion(asignacion);
    }
    
    public int setModificarPermisos(clsAsignacionAplicacionUsuario asig) {
    return new AsignacionAplicacionUsuarioDAO().actualizaAsignacion(asig);
    }

    public int setQuitarAplicacion(clsAsignacionAplicacionUsuario asignacion) {
        return new AsignacionAplicacionUsuarioDAO().borrarAsignacion(asignacion);
    }

    public List<clsAplicaciones> getAplicacionesAsignadas(int usuId) {
        return new AsignacionAplicacionUsuarioDAO().getAplicacionesAsignadas(usuId);
    }

    public List<clsAplicaciones> getAplicacionesDisponibles(int usuId) {
        return new AsignacionAplicacionUsuarioDAO().getAplicacionesDisponibles(usuId);
    }
    
    public clsAsignacionAplicacionUsuario getPermisos(int usuId, int aplCodigo) {
    return new AsignacionAplicacionUsuarioDAO()
            .getPermisos(usuId, aplCodigo);
}
    

    @Override
    public String toString() {
        return "clsAsignacionAplicacionUsuario{Aplcodigo=" + Aplcodigo
                + ", UsuId=" + UsuId + '}';
    }
}
