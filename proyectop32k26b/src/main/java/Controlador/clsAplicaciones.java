//Anthony Suc marzo 2026
package Controlador;

public class clsAplicaciones {

    // Atributos (igual que en la BD)
    private int Aplcodigo;
    private String Aplnombre;
    private String Aplestado;

    // Constructor vacío
    public clsAplicaciones() {
    }

    // Constructor con parámetros
    public clsAplicaciones(int Aplcodigo, String Aplnombre, String Aplestado) {
        this.Aplcodigo = Aplcodigo;
        this.Aplnombre = Aplnombre;
        this.Aplestado = Aplestado;
    }

    // GETTERS Y SETTERS
    public int getAplcodigo() {
        return Aplcodigo;
    }

    public void setAplcodigo(int Aplcodigo) {
        this.Aplcodigo = Aplcodigo;
    }

    public String getAplnombre() {
        return Aplnombre;
    }

    public void setAplnombre(String Aplnombre) {
        this.Aplnombre = Aplnombre;
    }

    public String getAplestado() {
        return Aplestado;
    }

    public void setAplestado(String Aplestado) {
        this.Aplestado = Aplestado;
    }

    @Override
    public String toString() {
        return "Aplicaciones{" +
                "Aplcodigo=" + Aplcodigo +
                ", Aplnombre='" + Aplnombre + '\'' +
                ", Aplestado='" + Aplestado + '\'' +
                '}';
    }
}