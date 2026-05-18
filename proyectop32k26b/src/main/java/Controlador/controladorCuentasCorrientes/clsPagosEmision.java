/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador.controladorCuentasCorrientes;
/**
 *
 * @author miais
 */
public class clsPagosEmision {
    private int Pagemid;
    private int Cppcodigo;
    private int Movbid;
    private String Pagefecha;
    private double Pagemonto;
    private String Pagetipo;
    
    public clsPagosEmision() {}

    public clsPagosEmision(int Pagemid, int Cppcodigo, int Movbid, String Pagefecha, double Pagemonto, String Pagetipo) {
        this.Pagemid = Pagemid;
        this.Cppcodigo = Cppcodigo;
        this.Movbid = Movbid;
        this.Pagefecha = Pagefecha;
        this.Pagemonto = Pagemonto;
        this.Pagetipo = Pagetipo;
    }
    
    public int getPagemid() {return Pagemid;}
    public void setPagemid(int Pagemid) {this.Pagemid = Pagemid;}

    public int getCppcodigo() {return Cppcodigo;}
    public void setCppcodigo(int Cppcodigo) {this.Cppcodigo = Cppcodigo;}

    public int getMovbid() {return Movbid;}
    public void setMovbid(int Movbid) {this.Movbid = Movbid;}

    public String getPagefecha() {return Pagefecha;}
    public void setPagefecha(String Pagefecha) {this.Pagefecha = Pagefecha;}

    public double getPagemonto() {return Pagemonto;}
    public void setPagemonto(double Pagemonto) {this.Pagemonto = Pagemonto;}

    public String getPagetipo() {return Pagetipo;}
    public void setPagetipo(String Pagetipo) {this.Pagetipo = Pagetipo;}

    @Override
    public String toString() {
        return "clsPagosEmision{Pagemid=" + Pagemid + ", Cppcodigo=" + Cppcodigo + ", Movbid=" + Movbid + ", Pagefecha=" + Pagefecha + ", Pagemonto=" + Pagemonto + ", Pagetipo=" + Pagetipo + "}";
    }
}
