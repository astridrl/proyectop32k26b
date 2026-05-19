//Britany Mishel Hernandez Davila 9959-24-4178
package Vista.vistaCuentasCorrientes;

import Controlador.clsUsuarioConectado;
import Modelo.modeloCuentasCorrientes.cuentasPorCobrarDAO;
import Controlador.controladorCuentasCorrientes.clsCuentasPorCobrar;
import Modelo.BitacoraDAO;
import Modelo.PermisosDAO;
import java.awt.Dimension;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;


public class frmCuentasPorCobrar extends javax.swing.JInternalFrame {
    //---Constantes registro en bitacora---
    int idUsuario = Controlador.clsUsuarioConectado.getUsuId(); //este se mandó a llamar del clsUsuarioConectado
    private static final int Aplcodigo = 4032; //Codigo de aplicacion dado en clase para bitacora

    /**
     * Creates new form frmCuentasPorCobrar
     */
    public frmCuentasPorCobrar() {
        initComponents();
        
        tblCuentasPorCobrar.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            llenartablaConClik(evt); // Llama a tu función pasándole el evento de mouse
        }
    });
        
        
        //Diseño del form
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Cuentas Por Cobrar");
        setVisible(true);
        
        //Elementos del form 
        dateBusqueda.setVisible(false);
        txtSaldo.setEditable(false);
        txtEstadoCuenta.setEditable(false);
        txtIdCuentaCobro.setEditable(false);
        txtSaldo.setEditable(false);
        llenadoDeTablas();
        
    }

    public void limpiarTextos(){ 
        txtNumFactura.setText("");
        txtIdCuentaCobro.setText("");
        txtIdCliente.setText("");
        txtMontoTotal.setText("");
        txtCodBuscar.setText("");
        txtEstadoCuenta.setText("");
        dateEmision.setDate(null);
    }

    public void llenadoDeTablas(){
        cuentasPorCobrarDAO dao = new cuentasPorCobrarDAO();
        List<clsCuentasPorCobrar> lista = dao.select();
        
        llenadoDeTablas(lista);    
    }
    
    
    public void llenadoDeTablas(List<clsCuentasPorCobrar> lista){
        DefaultTableModel modelo = (DefaultTableModel) tblCuentasPorCobrar.getModel();
        modelo.setRowCount(0);

        Object[] fila = new Object[6];

        for (int i = 0; i < lista.size(); i++) {
            fila[0] = lista.get(i).getCodigoCPC();
            fila[1] = lista.get(i).getIdCLI();
            fila[2] = lista.get(i).getFechaCPC();
            fila[3] = lista.get(i).getMontoCPC();
            fila[4] = lista.get(i).getSaldoCPC();
            fila[5] = lista.get(i).getEstadoCPC();
            
            modelo.addRow(fila);
        }
    }
    
    private void llenartablaConClik(java.awt.event.MouseEvent evt){
        // Carga los datos de la tabla en las casillas con un click
        int fila = tblCuentasPorCobrar.getSelectedRow();

        txtIdCuentaCobro.setText(tblCuentasPorCobrar.getValueAt(fila, 0).toString());
        txtCodBuscar.setText(tblCuentasPorCobrar.getValueAt(fila, 0).toString());
        txtIdCliente.setText(tblCuentasPorCobrar.getValueAt(fila, 1).toString());
        txtMontoTotal.setText(tblCuentasPorCobrar.getValueAt(fila, 3).toString());
        txtSaldo.setText(tblCuentasPorCobrar.getValueAt(fila, 4).toString());
        txtEstadoCuenta.setText(tblCuentasPorCobrar.getValueAt(fila, 5).toString());

        // Cargar la fecha en el JDateChooser para que no se quede vacío
        try {
            String fechaTabla = tblCuentasPorCobrar.getValueAt(fila, 2).toString();
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            dateEmision.setDate(formato.parse(fechaTabla));
        } catch (Exception e) {
            dateEmision.setDate(null);
        }
    }
    
    public boolean validarCampos(){
        //Validacion ID cliente
        if (txtIdCliente.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(this,"Debe ingresar el ID del cliente");
            txtIdCliente.requestFocus();
            return false;
        }
        try{
            Integer.parseInt(txtIdCliente.getText());
        }catch(NumberFormatException e){

            JOptionPane.showMessageDialog(this, "El ID del cliente debe ser numérico");
            txtIdCliente.requestFocus();
            return false;
        }
        //Validacion de la  fecha
        if(dateEmision.getDate() == null){
            JOptionPane.showMessageDialog(this,"Debe ingresar una fecha");return false;
        }

        // Validacion del Monto
        if(txtMontoTotal.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Debe ingresar el monto");
            txtMontoTotal.requestFocus();
            return false;
        }
        try{
            double monto =Double.parseDouble(txtMontoTotal.getText());
            if(monto <= 0){
                JOptionPane.showMessageDialog(this,"El monto debe ser mayor a 0");
                txtMontoTotal.requestFocus();
                return false;
            }
        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(this,"El monto debe ser numérico");
            txtMontoTotal.requestFocus();
            return false;
        }
        return true;
    }

    public List<clsCuentasPorCobrar> filtrarPorEstado(
        //Metodo para filtrar los datos por estaso
        List<clsCuentasPorCobrar> listaOriginal){
        List<clsCuentasPorCobrar> listaFiltrada = new ArrayList<>();

        for(clsCuentasPorCobrar cuenta : listaOriginal){
            String estado =cuenta.getEstadoCPC().toLowerCase();

        //Cuentas pendientes
        if(jRadioButton1.isSelected()){
            if(estado.equals("pendiente")) 
                listaFiltrada.add(cuenta);
        }
        //Cuentas Pagas
        else if(jRadioButton3.isSelected()){
            if(estado.equals("pagada"))
                listaFiltrada.add(cuenta);
        }
        else{ listaFiltrada.add(cuenta); }
        }
        
        return listaFiltrada;
    }
    
        
//metodo gregado por Astrid, para los permisos del mdi
    public void cargarPermisos() {
        int usuId = clsUsuarioConectado.getUsuId();
        PermisosDAO permisosDAO = new PermisosDAO();

        //METODO PARA EL SISTEMA DE SEGURIDAD DE PERMISOS AGREGAR A SUS FORMULARIOS CORRESPONDIENTES
        // Todos usan código 10 = Mantenimiento Usuario
        btnRegistrar.setEnabled( permisosDAO.puedeInsertar (usuId, 10) );
        btnBuscar.setEnabled  ( permisosDAO.puedeBuscar   (usuId, 10) );
        btnModificar.setEnabled( permisosDAO.puedeModificar(usuId, 10) );
        btnReportes.setEnabled( permisosDAO.puedeReportar (usuId, 10) );
    }
   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        tblCobros = new javax.swing.JScrollPane();
        tblCuentasPorCobrar = new javax.swing.JTable();
        btnReportes = new javax.swing.JButton();
        btnAyudas = new javax.swing.JButton();
        txtCodBuscar = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        txtIdCliente = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        dateEmision = new com.toedter.calendar.JDateChooser();
        btnBuscarNumFactura = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtNumFactura = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtIdCuentaCobro = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        txtSaldo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtMontoTotal = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        btnLimpiar = new javax.swing.JButton();
        lbEstadoCuenta = new javax.swing.JLabel();
        txtEstadoCuenta = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        cboBuscar = new javax.swing.JComboBox<>();
        btnRegistrar = new javax.swing.JButton();
        dateBusqueda = new com.toedter.calendar.JDateChooser();

        setBackground(new java.awt.Color(221, 221, 221));
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        tblCuentasPorCobrar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo C.Co", "Id Cliente", "Fecha C.Co", "Monto C.Co", "Saldo C.Co", "Estado C.Co"
            }
        ));
        tblCuentasPorCobrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCuentasPorCobrarMouseClicked(evt);
            }
        });
        tblCobros.setViewportView(tblCuentasPorCobrar);

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

        txtCodBuscar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtCodBuscar.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnBuscar.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        btnBuscar.setText("Buscar");
        btnBuscar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        btnModificar.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        btnModificar.setText("Modificar");
        btnModificar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 102, 102));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Registro de Cuentas por Cobrar");
        jLabel7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 2, true));

        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(98, 140, 140), 1, true));

        jLabel9.setFont(new java.awt.Font("Yu Gothic UI Light", 1, 14)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("ID Cliente: ");

        txtIdCliente.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtIdCliente.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setFont(new java.awt.Font("Yu Gothic UI Light", 1, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Fecha Emision:");

        dateEmision.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnBuscarNumFactura.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btnBuscarNumFactura.setText(" Buscar");
        btnBuscarNumFactura.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnBuscarNumFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarNumFacturaActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel5.setText("N.Factura: ");

        txtNumFactura.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtNumFactura.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel11.setFont(new java.awt.Font("Yu Gothic UI Light", 1, 14)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("ID Cueta.CO");

        txtIdCuentaCobro.setBackground(new java.awt.Color(225, 225, 225));
        txtIdCuentaCobro.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtIdCuentaCobro.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(6, 6, 6)
                        .addComponent(txtIdCuentaCobro, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNumFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuscarNumFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(6, 6, 6)
                        .addComponent(txtIdCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dateEmision, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNumFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarNumFactura)
                    .addComponent(jLabel5))
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtIdCuentaCobro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtIdCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(dateEmision, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(14, 14, 14))
        );

        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(98, 140, 140), 1, true));

        txtSaldo.setBackground(new java.awt.Color(225, 225, 225));
        txtSaldo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtSaldo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setFont(new java.awt.Font("Yu Gothic UI Light", 1, 14)); // NOI18N
        jLabel3.setText("Monto Total (Q):");

        txtMontoTotal.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtMontoTotal.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel10.setFont(new java.awt.Font("Yu Gothic UI Light", 1, 14)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Saldo Total (Q):");

        btnLimpiar.setBackground(new java.awt.Color(223, 242, 231));
        btnLimpiar.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        btnLimpiar.setText("Limpiar");
        btnLimpiar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        lbEstadoCuenta.setFont(new java.awt.Font("Yu Gothic UI Light", 1, 14)); // NOI18N
        lbEstadoCuenta.setText("Estado Cuenta:");

        txtEstadoCuenta.setBackground(new java.awt.Color(225, 225, 225));
        txtEstadoCuenta.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtEstadoCuenta.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnLimpiar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel10)
                            .addComponent(lbEstadoCuenta))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtEstadoCuenta)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtMontoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtSaldo, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(txtMontoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(txtSaldo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(txtEstadoCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbEstadoCuenta)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLimpiar, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Buscar Por:");

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setFont(new java.awt.Font("Yu Gothic UI Light", 1, 13)); // NOI18N
        jRadioButton1.setText("Pendientes");
        jRadioButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setFont(new java.awt.Font("Yu Gothic UI Light", 1, 13)); // NOI18N
        jRadioButton3.setText("Pagadas");

        cboBuscar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cboBuscar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Codigo de Cuenta", "ID del Cliente", "Monto", "Fecha" }));
        cboBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboBuscarActionPerformed(evt);
            }
        });

        btnRegistrar.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        btnRegistrar.setText("Registrar");
        btnRegistrar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });

        dateBusqueda.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 493, Short.MAX_VALUE)
                        .addComponent(btnReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnAyudas, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(cboBuscar, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(dateBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtCodBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jRadioButton1)
                            .addGap(18, 18, 18)
                            .addComponent(jRadioButton3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                            .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(tblCobros, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGap(21, 21, 21)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(btnRegistrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnModificar, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)))))
                        .addComponent(jSeparator1)))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnModificar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnRegistrar)
                        .addGap(26, 26, 26))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCodBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jRadioButton1)
                        .addComponent(jRadioButton3)
                        .addComponent(btnBuscar))
                    .addComponent(dateBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(tblCobros, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnReportes)
                    .addComponent(btnAyudas))
                .addGap(28, 28, 28))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
        //Boton para registrar una cuenta por cobrar
        if(!validarCampos()){return;}
        
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        String fecha = formato.format(dateEmision.getDate());
        clsCuentasPorCobrar cuenta = new clsCuentasPorCobrar();
        
        cuenta.setIdCLI(Integer.parseInt(txtIdCliente.getText()));    
        cuenta.setFechaCPC(fecha);     
        //Se registra el mismo valor para saldo como para monto
        cuenta.setMontoCPC(Double.parseDouble(txtMontoTotal.getText()));        
        cuenta.setSaldoCPC(Double.parseDouble(txtMontoTotal.getText()));
        cuenta.setEstadoCPC("Pendiente");//Estado inicial

        cuentasPorCobrarDAO dao = new cuentasPorCobrarDAO();
        int registros = dao.insert(cuenta);

        JOptionPane.showMessageDialog(null,"Registros insertados: " + 
                registros,"Sistema",JOptionPane.INFORMATION_MESSAGE);
   
        llenadoDeTablas();
        limpiarTextos();

        //Agruegué el Registro de la accion en bitacora    
        BitacoraDAO bitacoraDAO = new BitacoraDAO();    
        bitacoraDAO.insert(idUsuario, Aplcodigo, "INSERT");
    }//GEN-LAST:event_btnRegistrarActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        //Boton para modificar una cuenta por cobrar  
        if(!validarCampos()){
        return;
    }
    
    if (txtCodBuscar.getText().trim().isEmpty() || txtSaldo.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Debe buscar una cuenta o seleccionarla de la tabla antes de intentar modificar.");
        return;
    }
    
    try{
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        String fecha = formato.format(dateEmision.getDate());
        clsCuentasPorCobrar cuenta = new clsCuentasPorCobrar();

        cuenta.setCodigoCPC(Integer.parseInt(txtCodBuscar.getText()));
        cuenta.setIdCLI(Integer.parseInt(txtIdCliente.getText()));
        cuenta.setFechaCPC(fecha);
        
        double monto = Double.parseDouble(txtMontoTotal.getText());
        double saldo = Double.parseDouble(txtSaldo.getText());
        cuenta.setMontoCPC(monto);
        cuenta.setSaldoCPC(saldo);
        
        // Validación automática del estado según el saldo
        if(saldo <= 0){    
            cuenta.setEstadoCPC("Pagada");
        } else {
            cuenta.setEstadoCPC("Pendiente");    
        }

        cuentasPorCobrarDAO dao = new cuentasPorCobrarDAO();
        int registros = dao.update(cuenta);
        
        JOptionPane.showMessageDialog(this, "Registros modificados: " + registros,
                "Sistema", JOptionPane.INFORMATION_MESSAGE);
                
        llenadoDeTablas();
        limpiarTextos();

        // Registro en bitácora
        BitacoraDAO bitacoraDAO = new BitacoraDAO();    
        bitacoraDAO.insert(idUsuario, Aplcodigo, "UPDATE");
        
    } catch(Exception e){
        JOptionPane.showMessageDialog(this, "Error al modificar: " + e.getMessage());
    }
    }//GEN-LAST:event_btnModificarActionPerformed

    private void btnReportesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportesActionPerformed
        Map p = new HashMap();
        JasperReport report;
        JasperPrint print; 
        
        try{
            Connection connectio = Modelo.Conexion.getConnection();
            report = JasperCompileManager.compileReport(new File("").getAbsolutePath()
            +"/src/main/java/Reportes/CuentasCorrientes/ReporteGeneralCobros.jrxml");
            print = JasperFillManager.fillReport(report, p, connectio);
            
            JasperViewer view = new JasperViewer(print, false);
            
            view.setTitle("ReporteGeneralCobros");
            view.setVisible(true);
        } catch (Exception e){}
    }//GEN-LAST:event_btnReportesActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        //Boton para limpiar los campos
        llenadoDeTablas();
        limpiarTextos();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        //Boton de busqueda por medio de diferentes filtros   
        String busqueda = cboBuscar.getSelectedItem().toString();
        //Objetos
        cuentasPorCobrarDAO dao = new cuentasPorCobrarDAO();
        clsCuentasPorCobrar cuenta=new clsCuentasPorCobrar();
        
        List<clsCuentasPorCobrar> lista = new ArrayList<>();
        //
        try{
            switch(busqueda){           
                case "Codigo de Cuenta":
                    cuenta.setCodigoCPC(Integer.parseInt(txtCodBuscar.getText()));
                    clsCuentasPorCobrar resultado = dao.query(cuenta);
                    if (resultado !=null)
                        lista.add(resultado);
                    break;
                case "ID del Cliente":
                    cuenta.setIdCLI(Integer.parseInt(txtCodBuscar.getText()));
                    lista = dao.buscarPorCliente(cuenta);
                    break;
                case "Monto":
                    cuenta.setMontoCPC(Double.parseDouble(txtCodBuscar.getText()));
                    lista = dao.buscarPorMonto(cuenta);
                    break;
                case "Fecha":
                    //Confirmacion de fecha ingresada
                    if(dateBusqueda.getDate() == null){  
                        JOptionPane.showMessageDialog(this,"Seleccione una fecha");
                        return;
                    }
                     //Conversion del formato de la fecha
                    SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
                    String fecha = formato.format(dateBusqueda.getDate());
                    
                    cuenta.setFechaCPC(fecha);
                    lista = dao.buscarPorFecha(cuenta);
                    break;                 
            }
            //En caso de no encontrar registros en la busqueda
            if(lista.isEmpty())
                JOptionPane.showMessageDialog(this,"No se encontraron registros");
           
            lista= filtrarPorEstado(lista); //Filtra la busqueda ya echa por estado
            
            //Llenar la tabla
            llenadoDeTablas(lista);        
            //Registro de la accion en bitacora    
            BitacoraDAO bitacoraDAO = new BitacoraDAO();    
            bitacoraDAO.insert(idUsuario, Aplcodigo, "QUERY");
        } catch(Exception e){
             JOptionPane.showMessageDialog(this,"Error en búsqueda: " + e.getMessage());
        }
    }//GEN-LAST:event_btnBuscarActionPerformed

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

    private void btnBuscarNumFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarNumFacturaActionPerformed
        // TODO add your handling code here:
        try{    
            if(txtNumFactura.getText().trim().isEmpty()){
                    
                JOptionPane.showMessageDialog(this,"Ingrese el ID de la factura");  
                return;               
            }               
            int idFactura = Integer.parseInt(txtNumFactura.getText().trim());                
            cuentasPorCobrarDAO dao = new cuentasPorCobrarDAO();               
            Object[] datos = dao.buscarFactura(idFactura);
              
            if(datos != null){                   
                txtIdCliente.setText(datos[0].toString());                  
                dateEmision.setDate((Date) datos[1]);                   
                txtMontoTotal.setText(datos[2].toString());        
            }else{                    
                JOptionPane.showMessageDialog(this,"Factura no encontrada");               
            }        
        }catch(NumberFormatException e){            
            JOptionPane.showMessageDialog(this,"Ingrese un ID válido");         
        }catch(Exception e){     
            JOptionPane.showMessageDialog(this,"Error: " + e.getMessage());         
            e.printStackTrace();      
        }
    }//GEN-LAST:event_btnBuscarNumFacturaActionPerformed

    private void cboBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboBuscarActionPerformed
        //combo Box del tipo de busqueda que se dese 
        String busqueda = cboBuscar.getSelectedItem().toString();
        
        switch(busqueda){
            case "Fecha":
                dateBusqueda.setVisible(true);
                txtCodBuscar.setVisible(false);
                break;
            default:
                dateBusqueda.setVisible(false);
                txtCodBuscar.setVisible(true);
                break;
        }
        
    }//GEN-LAST:event_cboBuscarActionPerformed

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void tblCuentasPorCobrarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCuentasPorCobrarMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblCuentasPorCobrarMouseClicked

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
            java.util.logging.Logger.getLogger(frmCuentasPorCobrar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmCuentasPorCobrar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmCuentasPorCobrar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmCuentasPorCobrar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmCuentasPorCobrar().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAyudas;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnBuscarNumFactura;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JButton btnReportes;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cboBuscar;
    private com.toedter.calendar.JDateChooser dateBusqueda;
    private com.toedter.calendar.JDateChooser dateEmision;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lbEstadoCuenta;
    private javax.swing.JScrollPane tblCobros;
    private javax.swing.JTable tblCuentasPorCobrar;
    private javax.swing.JTextField txtCodBuscar;
    private javax.swing.JTextField txtEstadoCuenta;
    private javax.swing.JTextField txtIdCliente;
    private javax.swing.JTextField txtIdCuentaCobro;
    private javax.swing.JTextField txtMontoTotal;
    private javax.swing.JTextField txtNumFactura;
    private javax.swing.JTextField txtSaldo;
    // End of variables declaration//GEN-END:variables
}
