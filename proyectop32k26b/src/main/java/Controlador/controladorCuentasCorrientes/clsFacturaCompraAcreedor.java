/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Controlador.controladorCuentasCorrientes;

import java.util.List;

import Modelo.modeloCuentasCorrientes.facturacompraAcreedorDAO;
/**
 *
 * @author astri
 */
/**
 * Clase controlador para Facturas de Compra de Acreedores.
 * Tabla: facturascompras
 *
 */
public class clsFacturaCompraAcreedor {
 
  
    private int            facComId;           // PK  AUTO_INCREMENT
    private String         facComNumero;       // UNIQUE – ingresado manualmente
    private String facComFecha;        // DEFAULT current_timestamp
    private int            proCodigo;          // FK proveedores (999 si es solo acreedor) se creo el prob 999 para que se pusiera algo en la tabla
    private double     facComSubtotal;
    private double     facComIva;
    private double     facComTotal;
    private String         facComEstado;       // 'Vigente' por defecto
    private int            acreCodigo;         // FK acreedores (0 si no aplica)
 
    public clsFacturaCompraAcreedor() {
        this.facComEstado = "Vigente";
    }
 
    /** Constructor*/
    public clsFacturaCompraAcreedor(
        int facComId,
        String facComNumero,
        String facComFecha,
        int proCodigo,
        double facComSubtotal,
        double facComIva,
        double facComTotal,
        String facComEstado,
        int acreCodigo) {

    this.facComId = facComId;
    this.facComNumero = facComNumero;
    this.facComFecha = facComFecha;
    this.proCodigo = proCodigo;
    this.facComSubtotal = facComSubtotal;
    this.facComIva = facComIva;
    this.facComTotal = facComTotal;
    this.facComEstado = facComEstado;
    this.acreCodigo = acreCodigo;
}
 
    // get y set
    public int getFacComId()                          { return facComId; }
    public void setFacComId(int facComId)             { this.facComId = facComId; }
 
    public String getFacComNumero()                   { return facComNumero; }
    public void setFacComNumero(String facComNumero)  { this.facComNumero = facComNumero; }
 
    public String getFacComFecha()                        { return facComFecha; }
    public void setFacComFecha(String facComFecha)        { this.facComFecha = facComFecha; }
 
    public int getProCodigo()                         { return proCodigo; }
    public void setProCodigo(int proCodigo)           { this.proCodigo = proCodigo; }
 
    public double getFacComSubtotal()                           { return facComSubtotal; }
    public void setFacComSubtotal(double facComSubtotal)        { this.facComSubtotal = facComSubtotal; }
 
    public double getFacComIva()                                { return facComIva; }
    public void setFacComIva(double facComIva)                  { this.facComIva = facComIva; }
 
    public double getFacComTotal()                              { return facComTotal; }
    public void setFacComTotal(double facComTotal)              { this.facComTotal = facComTotal; }
 
    public String getFacComEstado()                                 { return facComEstado; }
    public void setFacComEstado(String facComEstado)                { this.facComEstado = facComEstado; }
 
    public int getAcreCodigo()                        { return acreCodigo; }
    public void setAcreCodigo(int acreCodigo)         { this.acreCodigo = acreCodigo; }
 
 
    @Override
    public String toString() {
        return "FacturaCompra{"
                + "facComId="       + facComId
                + ", facComNumero=" + facComNumero
                + ", facComFecha="  + facComFecha
                + ", proCodigo="    + proCodigo
                + ", acreCodigo="   + acreCodigo
                + ", subtotal="     + facComSubtotal
                + ", iva="          + facComIva
                + ", total="        + facComTotal
                + ", estado="       + facComEstado
                + '}';
    }
 
    // MÉTODOS CRUD  (


    public int setIngresarFacturaCompra(clsFacturaCompraAcreedor factura) {
        facturacompraAcreedorDAO dao = new facturacompraAcreedorDAO();
        return dao.ingresaFacturaCompra(factura);
    }
 
    /** Retorna todas las facturas de compra. */
    public List<clsFacturaCompraAcreedor> getListadoFacturasCompra() {
        facturacompraAcreedorDAO dao = new facturacompraAcreedorDAO();
        return dao.consultaFacturasCompra();
    }
 
    /** Busca una factura por su PK. */
    public clsFacturaCompraAcreedor getBuscarFacturaPorId(clsFacturaCompraAcreedor factura) {
        facturacompraAcreedorDAO dao = new facturacompraAcreedorDAO();
        return dao.consultaFacturaPorId(factura);
    }
 
    /** Busca facturas por número . */
    public clsFacturaCompraAcreedor getBuscarFacturaPorNumero(clsFacturaCompraAcreedor factura) {
        facturacompraAcreedorDAO dao = new facturacompraAcreedorDAO();
        return dao.consultaFacturaPorNumero(factura);
    }
 
    /** Retorna todas las facturas de un acreedor específico. */
    public List<clsFacturaCompraAcreedor> getBuscarFacturasPorAcreedor(clsFacturaCompraAcreedor factura) {
        facturacompraAcreedorDAO dao = new facturacompraAcreedorDAO();
        return dao.consultaFacturasPorAcreedor(factura);
    }
 
    /** Retorna facturas filtradas por estado ('Vigente' / 'Anulada'). */
    public List<clsFacturaCompraAcreedor> getBuscarFacturasPorEstado(clsFacturaCompraAcreedor factura) {
        facturacompraAcreedorDAO dao = new facturacompraAcreedorDAO();
        return dao.consultaFacturasPorEstado(factura);
    }
 

    public int setModificarFacturaCompra(clsFacturaCompraAcreedor factura) {
        facturacompraAcreedorDAO dao = new facturacompraAcreedorDAO();
        return dao.actualizaFacturaCompra(factura);
    }
 
    /**
     * Anula la factura (cambia estado a 'Anulada') y revierte el stock.
     */
    public int setAnularFacturaCompra(clsFacturaCompraAcreedor factura) {
        facturacompraAcreedorDAO dao = new facturacompraAcreedorDAO();
        return dao.anulaFacturaCompra(factura);
    }
    // DELETE  
    public int setEliminarFacturaCompra(clsFacturaCompraAcreedor factura) {
        facturacompraAcreedorDAO dao = new facturacompraAcreedorDAO();
        return dao.borraFacturaCompra(factura);
    }
}