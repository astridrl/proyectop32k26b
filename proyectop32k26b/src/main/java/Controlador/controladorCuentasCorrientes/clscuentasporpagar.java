/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador.controladorCuentasCorrientes;

/**
 *
 * @author dulce
 */
public class clscuentasporpagar {
    private int Cppcodigo;
    private int Procodigo;
    private int Acrecodigo;
    private int Venid;
    private String Cppfechaemision;
    private double Cppmontototal;
    private double Cppsaldopendiente;
    private char Cppestado;
    private int TTid;
    private int Cpporigenid;

    //Constructor vacio
    public clscuentasporpagar() {
    }
    
    //Constructor con parametros
    public clscuentasporpagar(int Cppcodigo, int Procodigo, int Acrecodigo, int Venid, String Cppfechaemision, double Cppmontototal, double Cppsaldopendiente, char Cppestado, int TTid, int Cpporigenid) {
        this.Cppcodigo = Cppcodigo;
        this.Procodigo = Procodigo;
        this.Acrecodigo = Acrecodigo;
        this.Venid = Venid;
        this.Cppfechaemision = Cppfechaemision;
        this.Cppmontototal = Cppmontototal;
        this.Cppsaldopendiente = Cppsaldopendiente;
        this.Cppestado = Cppestado;
        this.TTid = TTid;
        this.Cpporigenid = Cpporigenid;
    }
    
    //Getters and Setters
    public int getCppcodigo() {return Cppcodigo;}
    public void setCppcodigo(int Cppcodigo) {this.Cppcodigo = Cppcodigo;}
    
    public int getProcodigo() {return Procodigo;}
    public void setProcodigo(int Procodigo) {this.Procodigo = Procodigo;}
    
    public int getAcrecodigo() {return Acrecodigo;}
    public void setAcrecodigo(int Acrecodigo) {this.Acrecodigo = Acrecodigo;}
    
    public int getVenid() {return Venid;}
    public void setVenid(int Venid) {this.Venid = Venid;}
    
    public String getCppfechaemision() {return Cppfechaemision;}
    public void setCppfechaemision(String Cppfechaemision) {this.Cppfechaemision = Cppfechaemision;}
    

    public double getCppmontototal() {return Cppmontototal;}
    public void setCppmontototal(double Cppmontototal) {this.Cppmontototal = Cppmontototal;}


    public double getCppsaldopendiente() {return Cppsaldopendiente;}
    public void setCppsaldopendiente(double Cppsaldopendiente) {this.Cppsaldopendiente = Cppsaldopendiente;}

    public char getCppestado() {return Cppestado;}
    public void setCppestado(char Cppestado) {this.Cppestado = Cppestado;}

    public int getTTid() {return TTid;}
    public void setTTid(int TTid) {this.TTid = TTid;}

    public int getCpporigenid() {return Cpporigenid;}
    public void setCpporigenid(int Cpporigenid) {this.Cpporigenid = Cpporigenid;}
    
    @Override
    public String toString() {
        return "cuentasporpagar{" + "Cppcodigo=" + Cppcodigo + ", Procodigo=" + Procodigo + ", Acrecodigo=" + Acrecodigo + ", Venid=" + Venid + ", Cppfechaemision='" + Cppfechaemision + ", Cppmontototal='" + Cppmontototal + ", Cppsaldopendiente='" + Cppsaldopendiente + ", Cppestado='" + Cppestado + ", TTid='" + TTid + ", Cpporigenid='" +'}';
    }
}
