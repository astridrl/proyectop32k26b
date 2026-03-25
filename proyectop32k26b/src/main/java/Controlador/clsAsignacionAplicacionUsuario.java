package Controlador;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
//Karina Alejandra Arriaza Ortiz 9959-24-14190
public class clsAsignacionAplicacionUsuario {

    // Llaves primaria/foránea (ajustado a la BD)
    private int Aplcodigo;
    private int UsuId;

    // Permisos
    private String APLUins;
    private String APLUsel;
    private String APLUupd;
    private String APLUdel;
    private String APLUrep;

    // Constructor vacío
    public clsAsignacionAplicacionUsuario() {
    }

    // Constructor completo
    public clsAsignacionAplicacionUsuario(int Aplcodigo, int UsuId, String APLUins, String APLUsel, String APLUupd, String APLUdel, String APLUrep) {
        this.Aplcodigo = Aplcodigo;
        this.UsuId = UsuId;
        this.APLUins = APLUins;
        this.APLUsel = APLUsel;
        this.APLUupd = APLUupd;
        this.APLUdel = APLUdel;
        this.APLUrep = APLUrep;
    }

    // GETTERS Y SETTERS

    public int getAplcodigo() {
        return Aplcodigo;
    }

    public void setAplcodigo(int Aplcodigo) {
        this.Aplcodigo = Aplcodigo;
    }

    public int getUsuId() {
        return UsuId;
    }

    public void setUsuId(int UsuId) {
        this.UsuId = UsuId;
    }

    public String getAPLUins() {
        return APLUins;
    }

    public void setAPLUins(String APLUins) {
        this.APLUins = APLUins;
    }

    public String getAPLUsel() {
        return APLUsel;
    }

    public void setAPLUsel(String APLUsel) {
        this.APLUsel = APLUsel;
    }

    public String getAPLUupd() {
        return APLUupd;
    }

    public void setAPLUupd(String APLUupd) {
        this.APLUupd = APLUupd;
    }

    public String getAPLUdel() {
        return APLUdel;
    }

    public void setAPLUdel(String APLUdel) {
        this.APLUdel = APLUdel;
    }

    public String getAPLUrep() {
        return APLUrep;
    }

    public void setAPLUrep(String APLUrep) {
        this.APLUrep = APLUrep;
    }

    // GENERAR BITÁCORA 
    public clsBitacora generarBitacora(String accion) {

        clsBitacora bitacora = new clsBitacora();

        // Usuario conectado (compatible con su clase)
        bitacora.setUsucodigo(clsUsuarioConectado.getUsuId());

        // Aplicación actual
        bitacora.setAplcodigo(this.Aplcodigo);

        // Fecha como STRING (como ustedes lo manejan)
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        bitacora.setBitfecha(ahora.format(formato));

        // Datos básicos
        bitacora.setBitip("127.0.0.1");
        bitacora.setBitequipo("PC");

        // Acción realizada
        bitacora.setBitaccion(accion);

        return bitacora;
    }

    @Override
    public String toString() {
        return "clsAsignacionAplicacionUsuario{" +
                "Aplcodigo=" + Aplcodigo +
                ", UsuId=" + UsuId +
                ", APLUins='" + APLUins + '\'' +
                ", APLUsel='" + APLUsel + '\'' +
                ", APLUupd='" + APLUupd + '\'' +
                ", APLUdel='" + APLUdel + '\'' +
                ", APLUrep='" + APLUrep + '\'' +
                '}';
    }
}
