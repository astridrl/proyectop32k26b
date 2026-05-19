//Britany Mishel Hernandez Davila 9959-24-4178
package Vista.vistaCuentasCorrientes;

import Controlador.Bancos.clsMovimientoBancario;
import Modelo.modeloCuentasCorrientes.CobroEmisionDAO;
import Controlador.controladorCuentasCorrientes.clsCobroEmision;
import Controlador.controladorCuentasCorrientes.clsCuentasPorCobrar;
import Modelo.Bancos.MovimientoBancarioDAO;
import Modelo.BitacoraDAO;
import Modelo.modeloCuentasCorrientes.cuentasPorCobrarDAO;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;


public class frmCobrosEmision extends javax.swing.JInternalFrame {

    
    //---Constantes registro en bitacora---
    int idUsuario = Controlador.clsUsuarioConectado.getUsuId(); //este se mandó a llamar del clsUsuarioConectado
    private static final int Aplcodigo = 4033; //Codigo de aplicacion dado en clase para bitacora

    /**
     * Creates new form frmCobrosEmision
     */
    public frmCobrosEmision() {
        initComponents();
                //Diseño del form
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Cobros Emision");
        setVisible(true);
        
        txtSaldoPendiente.setEditable(false);
        txtMovimientoID.setEditable(false);
        llenadoDeTablas();
        
    }
    //Metodo de limpiado
    private void limpiarCampos() {    
        txtNumCuenta.setText("");
        txtSaldoPendiente.setText("");
        txtMontoCobro.setText("");
        txtTipoCobro.setText("");
        txtMovimientoID.setText("");
    }
    
    public void llenadoDeTablas(){
        cuentasPorCobrarDAO dao = new cuentasPorCobrarDAO();
        CobroEmisionDAO dao2 = new CobroEmisionDAO();
        
        List<clsCuentasPorCobrar> lista = dao.select();
        List<clsCobroEmision> lista2 = dao2.select();
        
        llenadoDeTablasce(lista2);
    }
    
    
        public void llenadoDeTablasce(List<clsCobroEmision> lista){
        DefaultTableModel modelo = (DefaultTableModel) tblCobrosEmision.getModel();
        modelo.setRowCount(0);

        Object[] fila = new Object[6];

        for (int i = 0; i < lista.size(); i++) {
        fila[0] = lista.get(i).getIdCOBEM();
        fila[1] = lista.get(i).getCodigoCPC();
        fila[2] = lista.get(i).getIdMOVB();
        fila[3] = lista.get(i).getFechaCOB();
        fila[4] = lista.get(i).getMontoCOB();
        fila[5] = lista.get(i).getTipoCOB();

        modelo.addRow(fila);
        }
    }
    
    private void cargarCuenta() {
        //LLenar las texbox al buscar
        try {
            int codigo = Integer.parseInt(txtNumCuenta.getText());

            clsCuentasPorCobrar cuenta =new clsCuentasPorCobrar();
            cuenta.setCodigoCPC(codigo);

            cuentasPorCobrarDAO dao =new cuentasPorCobrarDAO();
            cuenta = dao.query(cuenta);

            if (cuenta != null) {
                txtSaldoPendiente.setText(String.valueOf(cuenta.getSaldoCPC()));
            } else {
                JOptionPane.showMessageDialog(this,"Cuenta no encontrada");
                limpiarCampos();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,"Ingrese un código válido");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Error: " + e.getMessage());
        }

}


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tblCobros = new javax.swing.JScrollPane();
        tblCobrosEmision = new javax.swing.JTable();
        btnReportes = new javax.swing.JButton();
        btnAyudas = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        btnRegistrarCobro = new javax.swing.JButton();
        txtMovimientoID = new javax.swing.JTextField();
        btnBuscarCuenta = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtNumCuenta = new javax.swing.JTextField();
        btnLimpiar = new javax.swing.JButton();
        txtSaldoPendiente = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtMontoCobro = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtTipoCobro = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tblCobrosEmision.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Co.Emision", "Codigo C.Co", "ID Mov.Ban", "Fecha Emision", "Monto del Cobro", "Tipo"
            }
        ));
        tblCobros.setViewportView(tblCobrosEmision);

        btnReportes.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        btnReportes.setText("Reportes");
        btnReportes.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnReportes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportesActionPerformed(evt);
            }
        });

        btnAyudas.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        btnAyudas.setText("Ayuda");
        btnAyudas.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnAyudas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAyudasActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 102, 102));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Cobro Emision");
        jLabel7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 2, true));

        btnRegistrarCobro.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        btnRegistrarCobro.setText("Registrar");
        btnRegistrarCobro.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnRegistrarCobro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarCobroActionPerformed(evt);
            }
        });

        txtMovimientoID.setBackground(new java.awt.Color(225, 225, 225));
        txtMovimientoID.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtMovimientoID.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnBuscarCuenta.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btnBuscarCuenta.setText(" Buscar");
        btnBuscarCuenta.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnBuscarCuenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarCuentaActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel5.setText("Cod. C.p.Cobrar:");

        txtNumCuenta.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtNumCuenta.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnLimpiar.setBackground(new java.awt.Color(223, 242, 231));
        btnLimpiar.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        btnLimpiar.setText("Limpiar");
        btnLimpiar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        txtSaldoPendiente.setBackground(new java.awt.Color(225, 225, 225));
        txtSaldoPendiente.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtSaldoPendiente.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel3.setText("Monto Cobro (Q):");

        txtMontoCobro.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtMontoCobro.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel10.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Saldo Pendiente(Q):");

        jLabel6.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel6.setText("Movimiento ID");

        txtTipoCobro.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtTipoCobro.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel8.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel8.setText("Tipo de Cobro:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnAyudas, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(14, 14, 14)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(27, 27, 27)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(txtNumCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(btnBuscarCuenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addComponent(txtSaldoPendiente, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtMontoCobro, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtTipoCobro, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtMovimientoID, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnLimpiar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnRegistrarCobro, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(tblCobros, javax.swing.GroupLayout.PREFERRED_SIZE, 632, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(28, 28, 28))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnReportes)
                    .addComponent(btnAyudas)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(45, 45, 45)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtNumCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5))
                    .addComponent(btnBuscarCuenta))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSaldoPendiente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMontoCobro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTipoCobro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(btnRegistrarCobro))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMovimientoID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(btnLimpiar))
                .addGap(29, 29, 29)
                .addComponent(tblCobros, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnReportesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportesActionPerformed
        Map p = new HashMap();
        JasperReport report;
        JasperPrint print;

        try{
            Connection connectio = Modelo.Conexion.getConnection();
            report = JasperCompileManager.compileReport(new File("").getAbsolutePath()
                +"/src/main/java/Reportes/CuentasCorrientes/emisionCobrosReporte.jrxml");
            print = JasperFillManager.fillReport(report, p, connectio);

            JasperViewer view = new JasperViewer(print, false);

            view.setTitle("ReporteGeneralCobros");
            view.setVisible(true);
        } catch (Exception e){}
    }//GEN-LAST:event_btnReportesActionPerformed

    private void btnAyudasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAyudasActionPerformed
        // TODO add your handling code here:
        try {
            if ((new File("src\\main\\java\\\\Ayudas\\CuentasCorrientes\\AyudasCuentasCorrientes.chm")).exists()) {
                Process p = Runtime
                .getRuntime()
                .exec("rundll32 url.dll,FileProtocolHandler src\\main\\java\\Ayudas\\CuentasCorrientes\\AyudasCuentasCorrientes.chm");
                p.waitFor();
            } else {
                System.out.println("La ayuda no Fue encontrada");
            }
            System.out.println("Correcto");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnAyudasActionPerformed

    private void btnRegistrarCobroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarCobroActionPerformed
        //Boton para registrar el cobro a una cuenta
        try{           
            int codigoCuenta =Integer.parseInt(txtNumCuenta.getText());
            double montoCobro = Double.parseDouble(txtMontoCobro.getText());
            String tipoCobro = txtTipoCobro.getText().trim();
                                
            // Validaciones básicas
            if (tipoCobro.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar un tipo de cobro");
                return;}

            if (montoCobro <= 0) {
                JOptionPane.showMessageDialog(this, "El monto debe ser mayor a cero");
                return;}
            
            // Obtener fecha actual
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fechaCobro = sdf.format(new java.util.Date());

            //LLamado del metodo para el registro 
            CobroEmisionDAO cobroDAO = new CobroEmisionDAO();
            int resultado = cobroDAO.registrarCobro(codigoCuenta, montoCobro, fechaCobro, tipoCobro);
            
            //Comprobar resultados        
            if (resultado > 0) {
                llenadoDeTablas();            
                limpiarCampos();
                JOptionPane.showMessageDialog(this, "Cobro registrado exitosamente\n"                
                        + "Código CPC: " + codigoCuenta + "\n"
                                + "Monto: Q" + String.format("%.2f", montoCobro));
                        
            // Registrar en bitácora
            BitacoraDAO bitacoraDAO = new BitacoraDAO();
            bitacoraDAO.insert(idUsuario, Aplcodigo, "INSERT");
            
            }else{
                 JOptionPane.showMessageDialog(this,"Error al registrar el cobro\n"
                + "Verifique que el cliente tenga una cuenta bancaria asociada\n"
                + "y que el código de CPC sea válido.");
            }       
        } catch (NumberFormatException e){
            JOptionPane.showMessageDialog(this, "Datos no validos");
        }catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Error: " + e.getMessage());
        }
    }//GEN-LAST:event_btnRegistrarCobroActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        //Boton para limpiar los valores
        limpiarCampos();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnBuscarCuentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarCuentaActionPerformed
        //Boton para buscar el codigo de la cuenta a cobrar
        try{
            int codigo = Integer.parseInt(txtNumCuenta.getText());
            
            clsCuentasPorCobrar  cuenta = new clsCuentasPorCobrar();
            cuenta.setCodigoCPC(codigo);
            
            cuentasPorCobrarDAO dao = new cuentasPorCobrarDAO();
            
            cuenta = dao.query(cuenta);
            
            if (cuenta != null){
                txtSaldoPendiente.setText(String.valueOf(cuenta.getSaldoCPC()));
            } else{
                JOptionPane.showMessageDialog(this, "Cuenta no encontrada");
                txtSaldoPendiente.setText("");
            }
            cargarCuenta();
            llenadoDeTablas();
            //Registro de la accion en bitacora    
            BitacoraDAO bitacoraDAO = new BitacoraDAO();    
            bitacoraDAO.insert(idUsuario, Aplcodigo, "QUERY");
        }catch (NumberFormatException e){
            JOptionPane.showMessageDialog(this, "Ingreso no valido");
        }

    }//GEN-LAST:event_btnBuscarCuentaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(frmCobrosEmision.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmCobrosEmision.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmCobrosEmision.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmCobrosEmision.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmCobrosEmision().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAyudas;
    private javax.swing.JButton btnBuscarCuenta;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnRegistrarCobro;
    private javax.swing.JButton btnReportes;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane tblCobros;
    private javax.swing.JTable tblCobrosEmision;
    private javax.swing.JTextField txtMontoCobro;
    private javax.swing.JTextField txtMovimientoID;
    private javax.swing.JTextField txtNumCuenta;
    private javax.swing.JTextField txtSaldoPendiente;
    private javax.swing.JTextField txtTipoCobro;
    // End of variables declaration//GEN-END:variables
}
