/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vista.vistaCuentasCorrientes;

import Controlador.Bancos.clsBanco;
import Controlador.controladorCuentasCorrientes.clsMovimientoTransacciones;
import Modelo.Bancos.BancoDAO;
import Modelo.Bancos.ClientesDAO;
import Modelo.Conexion;
import Modelo.modeloCuentasCorrientes.MovimientoTransaccionesDAO;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import Controlador.Bancos.clsCliente;
import Modelo.Compras.ProveedorDAO;

/**
 * @author WINDOWS
 * Módulo de Cuentas Corrientes - Optimizado para Recibos de Proveedores
 */
public class TransaccionesCC extends javax.swing.JInternalFrame {

    // Instancias de Conexión y DAOs
    private Conexion cp = new Conexion(); // Corrección: Instancia de conexión explícita
    private MovimientoTransaccionesDAO movDAO = new MovimientoTransaccionesDAO();
    
    // Modelos de Tablas Unificados y Claros
    private DefaultTableModel modeloEstadoCuenta;
    private DefaultTableModel modeloMovimientos;
    private DefaultTableModel modeloPendientes; // Tabla Superior de Recibos
    private DefaultTableModel modeloAsignadas;  // Tabla Inferior de Recibos
    // REVISA QUE QUEDE ASÍ (Sin la palabra static)
    private Modelo.Bancos.ClientesDAO clientesDAO = new Modelo.Bancos.ClientesDAO();
    private Modelo.Compras.ProveedorDAO proveedorDAO = new Modelo.Compras.ProveedorDAO();
    
    // Modelos de Tablas para Recibos de Clientes (Pestaña 4)
    private DefaultTableModel modeloPendientes1;
    private DefaultTableModel modeloAsignadas1;
    private DefaultTableModel modeloAplicacion1;

    /**
     * Constructor de la Ventana
     */
    public TransaccionesCC() {
        initComponents(); 
        
        // 1. Propiedades de visualización de la ventana
            setClosable(true);
            setIconifiable(true);
            setMaximizable(true);
            setResizable(true);
            setTitle("Transacciones");
            setVisible(true);

        // 2. Listeners para controlar exclusión mutua de Clientes y Proveedores
        jcbClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (jcbClientes.getSelectedItem() != null && !jcbClientes.getSelectedItem().toString().trim().isEmpty() && jcbClientes.getSelectedIndex() > 0) {
                    jcbProveedores.setSelectedIndex(0); // Resetea proveedor
                    jcbProveedores.setEnabled(false);   // Bloquea proveedor
                } else if (jcbClientes.getSelectedIndex() == 0) {
                    jcbProveedores.setEnabled(true);    // Libera si vuelve al estado por defecto
                }
            }
        });

        jcbProveedores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (jcbProveedores.getSelectedItem() != null && !jcbProveedores.getSelectedItem().toString().trim().isEmpty() && jcbProveedores.getSelectedIndex() > 0) {
                    jcbClientes.setSelectedIndex(0); // Resetea cliente
                    jcbClientes.setEnabled(false);   // Bloquea cliente
                } else if (jcbProveedores.getSelectedIndex() == 0) {
                    jcbClientes.setEnabled(true);    // Libera si vuelve al estado por defecto
                }
            }
        });

        jrbProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbProveedorActionPerformed(evt);
            }
        });
        
        // 3. Inicialización estructurada de los Modelos de Tablas (Aquí se configuran los Listeners correctos una sola vez)
        inicializarEstadoCuenta();
        inicializarMovimientos(); 
        inicializarTablasRecibos(); // El método que reparamos anteriormente se encarga de crear todo limpio
        
        // 4. Carga de información de bases de datos y componentes externos
        configurarComponentesAdicionalesRecibo(); 
        cargarComboClientes(); 
        cargarComboProveedores();
        
        // Configuración de ComboBoxes de tipos de recibos
        if (cmbTipoRecibo != null) {
            cmbTipoRecibo.setEnabled(true); 
            cmbTipoRecibo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {
                "RECIBO DE INGRESOS / COBRO",
                "NOTA DE CRÉDITO CLIENTE",
                "ANTICIPO DE CLIENTE"
            }));
            cmbTipoRecibo.setSelectedIndex(0);
            cmbTipoRecibo.revalidate();
            cmbTipoRecibo.repaint();
        }
        
        if (cmbTipoRecibo1 != null) {
            cmbTipoRecibo1.setEnabled(true); 
            cmbTipoRecibo1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {
                "RECIBO DE INGRESOS / COBRO",
                "NOTA DE CRÉDITO CLIENTE",
                "ANTICIPO DE CLIENTE"
            }));
            cmbTipoRecibo1.setSelectedIndex(0);
            cmbTipoRecibo1.revalidate();
            cmbTipoRecibo1.repaint();
        }
        
    }

    
    /**
     * BUSCADOR AUTOMÁTICO: Proyecta proveedor y carga sus facturas mediante el No. Documento
     */
    public void buscarProveedorPorDocumento(String noDocumento) {
        String sql = "SELECT p.Procodigo, p.Pronombre " +
                 "FROM cuentasporpagar c " +
                 "JOIN proveedores p ON c.Procodigo = p.Procodigo " +
                 "WHERE c.Cppcodigo = ? LIMIT 1"; 
                 
        try (Connection con = cp.getConnection(); // Nombre de método corregido
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, noDocumento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    txtIdProveedor.setText(rs.getString("Procodigo"));
                    txtNombreProveedor.setText(rs.getString("Pronombre"));

                    // Pasamos el ID del proveedor para cargar sus deudas
                    cargarFacturasPendientesDelProveedor(rs.getInt("Procodigo"));
                    JOptionPane.showMessageDialog(this, "Proveedor localizado: " + rs.getString("Pronombre"));
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró ningún registro pendiente para ese documento.");
                    txtIdProveedor.setText("");
                    txtNombreProveedor.setText("");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage());
        }
    }

 
    private void buscarClienteYFacturas(String idClienteStr) {
        modeloPendientes1.setRowCount(0);
        Connection con = null;
        
        try {
            con = cp.getConnection();
            int cliid = Integer.parseInt(idClienteStr);

            String sqlCliente = "SELECT Clinombre FROM clientes WHERE Cliid = ?";
            try (PreparedStatement psCli = con.prepareStatement(sqlCliente)) {
                psCli.setInt(1, cliid);
                try (ResultSet rsCli = psCli.executeQuery()) {
                    if (rsCli.next()) {
                        txtIdCliente.setText(idClienteStr);
                        txtNombreCliente.setText(rsCli.getString("Clinombre"));
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "El código de cliente no existe.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            String sqlFacturas = "SELECT Cpccodigo, Cpcfecha, Cpcsaldo, Cpcmonto " +
                                 "FROM cuentasporcobrar " +
                                 "WHERE Cliid = ? AND Cpcsaldo > 0 AND Cpcestado = 'P'";

            try (PreparedStatement psFac = con.prepareStatement(sqlFacturas)) {
                psFac.setInt(1, cliid);
                try (ResultSet rsFac = psFac.executeQuery()) {
                    while (rsFac.next()) {
                        modeloPendientes1.addRow(new Object[]{
                            rsFac.getInt("Cpccodigo"),
                            "CPC-" + rsFac.getInt("Cpccodigo"),
                            rsFac.getDate("Cpcfecha"),
                            rsFac.getDouble("Cpcsaldo")
                        });
                    }
                }
            }

            if (modeloPendientes1.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                    "El cliente no registra facturas pendientes de cobro.",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "El código de cliente debe ser un número.", "Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error SQL: " + e.getMessage(), "Error SQL",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            if (con != null) {
                try { con.close(); } catch (Exception e) { e.printStackTrace(); }
            }
        }
    }
    
    private void validarMontosFormasDePago() {
        DefaultTableModel modelAp = (DefaultTableModel) tblAplicacion1.getModel();
    double totalFormasPago = 0;
    for (int i = 0; i < modelAp.getRowCount(); i++) {
        try {
            Object val = modelAp.getValueAt(i, 2);
            if (val != null && !val.toString().trim().isEmpty()) {
                totalFormasPago += Double.parseDouble(val.toString().trim().replace(",", "."));
            }
        } catch (NumberFormatException e) {
            modelAp.setValueAt(0.00, i, 2);
        }
    }
    double totalRequerido = 0;
    try {
        if (txtTotalPagado1 != null && !txtTotalPagado1.getText().trim().isEmpty()) {
            totalRequerido = Double.parseDouble(txtTotalPagado1.getText().trim().replace(",", "."));
        }
    } catch (Exception e) { totalRequerido = 0; }

    if (totalFormasPago > totalRequerido) {
        JOptionPane.showMessageDialog(this,
            "El monto en Formas de Pago excede al total abonado.",
            "Descuadre", JOptionPane.WARNING_MESSAGE);
    }
        }
    
    /**
     * Inicializa las tablas del nuevo sistema de Recibos (Pestaña 3)
     */
    private void inicializarTablasRecibos() {
        // 1. Tabla Superior: Facturas Pendientes de Pago
        String[] columnasPendientes = {"ID Doc", "No. Factura", "Fecha Emisión", "Saldo Pendiente"};
        modeloPendientes = new DefaultTableModel(columnasPendientes, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblFacturasPendientes.setModel(modeloPendientes);

        // 2. Tabla Inferior: Facturas Asignadas al Recibo Actual
        String[] columnasAsignadas = {"ID Doc", "No. Factura", "Saldo Anterior", "Monto Abonado", "Saldo Actualizado"};
        modeloAsignadas = new DefaultTableModel(columnasAsignadas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 3; // Únicamente la columna 'Monto Abonado' es editable
            }
        };
        tblFacturasAsignadas.setModel(modeloAsignadas);

        // 3. Tabla de Formas de Pago (Añadido para que proyecte la tabla tblAplicacion)
        String[] columnasAplicacion = {"Código", "Descripción (Forma de Pago)", "Valor (Monto)", "Número Doc/Ref", "Banco"};
        DefaultTableModel modeloAplicacion = new DefaultTableModel(columnasAplicacion, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                // Permitimos editar Monto (2), Número (3) y Banco (4)
                return c == 2 || c == 3 || c == 4; 
            }
        };
        tblAplicacion.setModel(modeloAplicacion);

        // Valores iniciales por defecto para las Formas de Pago
        modeloAplicacion.addRow(new Object[]{"EF", "EFECTIVO", 0.00, "N/A", "N/A"});
        modeloAplicacion.addRow(new Object[]{"CH", "CHEQUE", 0.00, "", ""});
        modeloAplicacion.addRow(new Object[]{"TR", "TRANSFERENCIA BANCARIA", 0.00, "", ""});

        // Escuchador para validar montos cuando el usuario cambie el 'Valor' (columna 2)
        modeloAplicacion.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE && e.getColumn() == 2) {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    validarMontosFormasDePago();
                });
            }
        });
    }

    /**
     * Calcula los totales del recibo en tiempo real recorriendo la tabla inferior
     */
    private void calcularTotalesRecibo() {
        calcularTotalesRecibo(false);
    }

    private void calcularTotalesRecibo(boolean esCliente) {
        DefaultTableModel modelo = esCliente ? modeloAsignadas1 : modeloAsignadas;
        javax.swing.JTextField txtTotalFact = esCliente ? txtTotalFacturas1 : txtTotalFacturas;
        javax.swing.JTextField txtTotalPag = esCliente ? txtTotalPagado1 : txtTotalPagado;

        double sumaSaldosAnteriores = 0;
        double sumaMontosAbonados = 0;

        for (int i = 0; i < modelo.getRowCount(); i++) {
            try {
                double saldoAnterior = Double.parseDouble(modelo.getValueAt(i, 2).toString());
                double montoAbonado = Double.parseDouble(modelo.getValueAt(i, 3).toString());
                
                if (montoAbonado > saldoAnterior) {
                    JOptionPane.showMessageDialog(this, "El abono no puede ser mayor al saldo pendiente.");
                    montoAbonado = saldoAnterior;
                    modelo.setValueAt(montoAbonado, i, 3);
                }

                double nuevoSaldo = saldoAnterior - montoAbonado;
                modelo.setValueAt(nuevoSaldo, i, 4);

                sumaSaldosAnteriores += saldoAnterior;
                sumaMontosAbonados += montoAbonado;
            } catch (NumberFormatException e) {
                modelo.setValueAt(0.0, i, 3);
            }
        }

        txtTotalFact.setText(String.format("%.2f", sumaSaldosAnteriores));
        txtTotalPag.setText(String.format("%.2f", sumaMontosAbonados));
    }

    /**
     * Carga las deudas reales del proveedor seleccionado en la tabla superior
     */
    private void cargarFacturasPendientesDelProveedor(int proveedorId) {
        modeloPendientes.setRowCount(0); // Limpia la tabla visual
    
    // SQL adaptado a tus campos reales
    String sql = "SELECT Cppcodigo, Cppfechaemision, Cppsaldopendiente " +
                 "FROM cuentasporpagar " +
                 "WHERE Procodigo = ? AND Cppsaldopendiente > 0";
                 
        try (Connection con = cp.getConnection(); // Nombre de método corregido
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, proveedorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Llenamos las columnas con los parámetros exactos de tu base de datos
                    modeloPendientes.addRow(new Object[]{
                        rs.getInt("Cppcodigo"),
                        "Factura u Origen", // Texto descriptivo predeterminado
                        rs.getDate("Cppfechaemision"), // Usando tu columna real
                        rs.getDouble("Cppsaldopendiente") // Usando tu columna real
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar cuentas pendientes: " + e.getMessage());
        }
    }

    
    private void limpiarFormularioRecibo() {
        txtIdProveedor.setText("");
        txtNombreProveedor.setText("");
        txtBuscarDocumento.setText("");
        txtTotalFacturas.setText("0.00");
        txtTotalPagado.setText("0.00");
        txtIdCliente.setText("");
        txtNombreCliente.setText("");
        txtBuscarDocCliente.setText("");
        txtTotalFacturas1.setText("0.00");
        txtTotalPagado1.setText("0.00");
        if (txtMotivo != null) txtMotivo.setText("");
        if (txtMotivo1 != null) txtMotivo1.setText("");

        modeloPendientes.setRowCount(0);
        modeloAsignadas.setRowCount(0);
        if (modeloPendientes1 != null) modeloPendientes1.setRowCount(0);
        if (modeloAsignadas1 != null) modeloAsignadas1.setRowCount(0);

        DefaultTableModel modelAp = (DefaultTableModel) tblAplicacion.getModel();
        for (int i = 0; i < modelAp.getRowCount(); i++) {
            modelAp.setValueAt(0.00, i, 2);
            if (i > 0) {
                modelAp.setValueAt("", i, 3);
                modelAp.setValueAt("", i, 4);
            }
        }
        DefaultTableModel modelAp1 = (DefaultTableModel) tblAplicacion1.getModel();
        for (int i = 0; i < modelAp1.getRowCount(); i++) {
            modelAp1.setValueAt(0.00, i, 2);
            if (i > 0) {
                modelAp1.setValueAt("", i, 3);
                modelAp1.setValueAt("", i, 4);
            }
        }
        if (dcFecha != null) dcFecha.setDate(new java.util.Date());
        if (dcFecha1 != null) dcFecha1.setDate(new java.util.Date());
    }
    
    // --- MÉTODOS DE LAS OTRAS PESTAÑAS (ESTADO DE CUENTA Y MOVIMIENTOS) ---

    private void inicializarEstadoCuenta() {
        String[] cols = {"Fecha", "Concepto", "Cargo", "Abono", "Saldo"};
        modeloEstadoCuenta = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        jTableEstadoCuenta.setModel(modeloEstadoCuenta);
    }

    private void inicializarMovimientos() {
        jcbTipo.removeAllItems();
        jcbTipo.addItem("CARGO");
        jcbTipo.addItem("ABONO");

        jcbModulo.removeAllItems();
        jcbModulo.addItem("VENTAS");
        jcbModulo.addItem("COMPRAS");
        jcbModulo.addItem("BANCOS");
        jcbModulo.addItem("CC");

        String[] cols = {"Fecha", "Concepto", "Cargo", "Abono", "Saldo"};
        modeloMovimientos = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        jTableMovimientos.setModel(modeloMovimientos);
        cargarTodosMovimientos();
    }

    private void cargarTodosMovimientos() {
        modeloMovimientos.setRowCount(0);
        List<clsMovimientoTransacciones> lista = movDAO.select();
        if (lista == null) return;
        
        for (clsMovimientoTransacciones mov : lista) {
            double cargo = mov.getMcctipo().equals("CARGO") ? mov.getMccmonto() : 0;
            double abono = mov.getMcctipo().equals("ABONO") ? Math.abs(mov.getMccmonto()) : 0;
            
            modeloMovimientos.addRow(new Object[]{
                mov.getMccfecha(),
                mov.getMccconcepto(),
                String.format("Q %.2f", cargo),
                String.format("Q %.2f", abono),
                String.format("Q %.2f", mov.getMccsaldo())
            });
        }
    }

    private void cargarComboClientes() {
        jcbCliente.removeAllItems();
        jcbClientes.removeAllItems();

        jcbCliente.addItem("0 - Seleccione Cliente");
        jcbClientes.addItem("0 - Seleccione Cliente");

        try {
            java.util.List<Controlador.Bancos.clsCliente> listaGenerica = 
                clientesDAO.listar();

            if (listaGenerica != null) {
                for (Controlador.Bancos.clsCliente c : listaGenerica) {
                    String itemFormateado = c.getClid() + " - " + c.getClinombre();

                    jcbCliente.addItem(itemFormateado);
                    jcbClientes.addItem(itemFormateado);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar clientes:\n" + e.getMessage(),
                "Error de Carga", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void cargarComboProveedores() {
    // 1. Limpiamos el combo de proveedores para que no acumule datos
    jcbProveedor.removeAllItems();
    jcbProveedores.removeAllItems();  // ✔ también el plural

    jcbProveedor.addItem("0 - Seleccione Proveedor");
    jcbProveedores.addItem("0 - Seleccione Proveedor");  // ✔

    try {
        java.util.List<Controlador.Compras.clsProveedor> listaProveedores = 
            proveedorDAO.consultaProveedores();

        if (listaProveedores != null && !listaProveedores.isEmpty()) {
            for (Controlador.Compras.clsProveedor prov : listaProveedores) {
                String itemFormateado = prov.getProcodigo() + " - " + prov.getPronombre();
                jcbProveedor.addItem(itemFormateado);
                jcbProveedores.addItem(itemFormateado);  // ✔
            }
        } else {
            System.out.println("Tabla proveedores vacía.");
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
            "Error al cargar Proveedores: " + e.getMessage(),
            "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
    private void limpiarMovimientos() {
        jcbTipo.setSelectedIndex(0);
        txtConcepto.setText("");
        txtMonto.setText("");
        jcbModulo.setSelectedIndex(0);
    }
    
    private int obtenerTTid(String modulo, String tipo) {
        switch (modulo) {
            case "VENTAS":   return 10;
            case "COMPRAS":  return 11;
            case "BANCOS":   return tipo.equals("CARGO") ? 1 : 2;
            case "CC":       return 9;
            default:         return 4;
        }
    }
    
    // El código de abajo continúa con tu initComponents() generado automáticamente...
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgTipo = new javax.swing.ButtonGroup();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jrbCliente = new javax.swing.JRadioButton();
        jrbProveedor = new javax.swing.JRadioButton();
        btnConsultar = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableEstadoCuenta = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        lblSaldoActual = new javax.swing.JTextField();
        jcbCliente = new javax.swing.JComboBox<>();
        jcbProveedor = new javax.swing.JComboBox<>();
        btnAyudaEstadoCuenta = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jcbTipo = new javax.swing.JComboBox<>();
        txtConcepto = new javax.swing.JTextField();
        txtMonto = new javax.swing.JTextField();
        jcbModulo = new javax.swing.JComboBox<>();
        jcbClientes = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btnRegistrar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableMovimientos = new javax.swing.JTable();
        jcbProveedores = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        btnAyudaMovimientos = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblFacturasAsignadas = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblFacturasPendientes = new javax.swing.JTable();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtBuscarDocumento = new javax.swing.JTextField();
        cmbTipoRecibo = new javax.swing.JComboBox<>();
        dcFecha = new com.toedter.calendar.JDateChooser();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblAplicacion = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        btnAgregarFactura = new javax.swing.JButton();
        btnQuitarFactura = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        txtIdProveedor = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtNombreProveedor = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtMotivo = new javax.swing.JTextField();
        btnEliminar = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        txtTotalFacturas = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtTotalPagado = new javax.swing.JTextField();
        btnProcesarRecibo = new javax.swing.JButton();
        generarReporteCuentasPorPagar = new javax.swing.JButton();
        btnAyudaRecibosProveedores = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        tblFacturasAsignadas1 = new javax.swing.JTable();
        jScrollPane8 = new javax.swing.JScrollPane();
        tblFacturasPendientes1 = new javax.swing.JTable();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        txtBuscarDocCliente = new javax.swing.JTextField();
        cmbTipoRecibo1 = new javax.swing.JComboBox<>();
        dcFecha1 = new com.toedter.calendar.JDateChooser();
        jScrollPane9 = new javax.swing.JScrollPane();
        tblAplicacion1 = new javax.swing.JTable();
        jLabel25 = new javax.swing.JLabel();
        btnAgregarFactura1 = new javax.swing.JButton();
        btnQuitarFactura1 = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();
        txtIdCliente = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        txtNombreCliente = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        txtMotivo1 = new javax.swing.JTextField();
        btnEliminar1 = new javax.swing.JButton();
        jLabel29 = new javax.swing.JLabel();
        txtTotalFacturas1 = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        txtTotalPagado1 = new javax.swing.JTextField();
        btnProcesarReciboCliente = new javax.swing.JButton();
        btnAyudaRecibosClientes = new javax.swing.JButton();
        generarReporteRecibosClientes = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel10.setText("Ver Cuenta De:");

        bgTipo.add(jrbCliente);
        jrbCliente.setText("Cliente");
        jrbCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbClienteActionPerformed(evt);
            }
        });

        bgTipo.add(jrbProveedor);
        jrbProveedor.setText("Proveedor");

        btnConsultar.setText("Consultar");
        btnConsultar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultarActionPerformed(evt);
            }
        });

        jButton5.setText("Generar Reporte");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jTableEstadoCuenta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Fecha ", "Concepto", "Cargo", "Abono", "Saldo"
            }
        ));
        jScrollPane4.setViewportView(jTableEstadoCuenta);

        jLabel11.setText("Saldo Actual");

        jcbCliente.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jcbProveedor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbProveedorActionPerformed(evt);
            }
        });

        btnAyudaEstadoCuenta.setText("Ayudas");
        btnAyudaEstadoCuenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAyudaEstadoCuentaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 925, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnConsultar, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jrbCliente)
                                    .addComponent(jrbProveedor))
                                .addGap(59, 59, 59)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnAyudaEstadoCuenta)
                                        .addGap(52, 52, 52))
                                    .addComponent(jcbProveedor, 0, 308, Short.MAX_VALUE)
                                    .addComponent(jcbCliente, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addContainerGap(453, Short.MAX_VALUE))))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblSaldoActual, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jrbCliente)
                    .addComponent(jcbCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jrbProveedor)
                    .addComponent(jcbProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConsultar)
                    .addComponent(jButton5)
                    .addComponent(btnAyudaEstadoCuenta))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(lblSaldoActual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(177, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Estado de Cuenta", jPanel3);

        jcbTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "", "CARGO", "ABONO" }));

        jcbModulo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "", "VENTAS", "CC" }));

        jcbClientes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "", "CLIENTES" }));
        jcbClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbClientesActionPerformed(evt);
            }
        });

        jLabel1.setText("Tipo:");

        jLabel2.setText("Concepto:");

        jLabel3.setText("Monto:");

        jLabel4.setText("Modulo:");

        jLabel5.setText("Clientes:");

        jLabel6.setText("Proveedores:");

        btnRegistrar.setText("Registrar");
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });

        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        jTableMovimientos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Fecha", "Concepto", "Cargo", "Abono", "Saldo"
            }
        ));
        jScrollPane1.setViewportView(jTableMovimientos);

        jcbProveedores.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PROVEEDORES" }));
        jcbProveedores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbProveedoresActionPerformed(evt);
            }
        });

        jButton1.setText("Generar Reporte");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btnAyudaMovimientos.setText("Ayudas");
        btnAyudaMovimientos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAyudaMovimientosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))
                        .addGap(194, 194, 194)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jcbClientes, 0, 378, Short.MAX_VALUE)
                            .addComponent(jcbModulo, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtMonto, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtConcepto, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jcbTipo, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jcbProveedores, javax.swing.GroupLayout.Alignment.LEADING, 0, 378, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(239, 239, 239)
                        .addComponent(btnRegistrar)
                        .addGap(18, 18, 18)
                        .addComponent(btnLimpiar)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnAyudaMovimientos))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 687, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(238, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcbTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMonto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcbModulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcbClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jcbProveedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRegistrar)
                    .addComponent(btnLimpiar)
                    .addComponent(jButton1)
                    .addComponent(btnAyudaMovimientos))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Movimientos", jPanel1);

        jLabel7.setText("No. Recibo");

        tblFacturasAsignadas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Fecha", "Concepto", "Monto", "Estado"
            }
        ));
        jScrollPane2.setViewportView(tblFacturasAsignadas);

        tblFacturasPendientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "No. Factura", "Fecha", "Monto Original", "Saldo Pendientes"
            }
        ));
        jScrollPane3.setViewportView(tblFacturasPendientes);

        jLabel14.setText("Facturas Pendientes a Pagar");

        jLabel15.setText("Facturas Asignadas al Recibo");

        jLabel16.setText("Tipo de Recibo:");

        jLabel17.setText("Fecha de Emisión");

        txtBuscarDocumento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtBuscarDocumentoKeyPressed(evt);
            }
        });

        tblAplicacion.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Codigo", "Descripcion", "Valor", "Numero", "Banco"
            }
        ));
        jScrollPane5.setViewportView(tblAplicacion);

        jLabel8.setText("Formas de Pago");

        btnAgregarFactura.setText("Agregar Facturas al Recibo");
        btnAgregarFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarFacturaActionPerformed(evt);
            }
        });

        btnQuitarFactura.setText("Quitar Facturas al Recibo");
        btnQuitarFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuitarFacturaActionPerformed(evt);
            }
        });

        jLabel9.setText("Proveedor");

        jLabel12.setText("Nombre");

        jLabel13.setText("Motivo");

        btnEliminar.setText("Eliminar Recibo");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        jLabel18.setText("Total Recibo");

        jLabel19.setText("Total Pagado");

        btnProcesarRecibo.setText("Procesar Recibo");
        btnProcesarRecibo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcesarReciboActionPerformed(evt);
            }
        });

        generarReporteCuentasPorPagar.setText("Generar Reportes");
        generarReporteCuentasPorPagar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generarReporteCuentasPorPagarActionPerformed(evt);
            }
        });

        btnAyudaRecibosProveedores.setText("Ayudas");
        btnAyudaRecibosProveedores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAyudaRecibosProveedoresActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 442, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 442, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 442, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addGap(423, 423, 423))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(btnQuitarFactura)
                            .addGap(287, 287, 287)
                            .addComponent(btnAgregarFactura)
                            .addGap(267, 267, 267)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtBuscarDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(cmbTipoRecibo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtIdProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(dcFecha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtNombreProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtMotivo, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnAyudaRecibosProveedores))
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 890, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addGap(23, 23, 23)
                        .addComponent(txtTotalFacturas, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addGap(18, 18, 18)
                        .addComponent(txtTotalPagado, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnProcesarRecibo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(generarReporteCuentasPorPagar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEliminar)
                .addGap(205, 205, 205))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtBuscarDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtIdProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtNombreProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtMotivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(cmbTipoRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel17)
                        .addComponent(dcFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnAyudaRecibosProveedores))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAgregarFactura)
                    .addComponent(btnQuitarFactura))
                .addGap(13, 13, 13)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(80, 80, 80)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEliminar)
                    .addComponent(jLabel18)
                    .addComponent(txtTotalFacturas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnProcesarRecibo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(txtTotalPagado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(generarReporteCuentasPorPagar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Recibos de Proveedores", jPanel2);

        jLabel20.setText("No. Recibo");

        tblFacturasAsignadas1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Fecha", "Concepto", "Monto", "Estado"
            }
        ));
        jScrollPane7.setViewportView(tblFacturasAsignadas1);

        tblFacturasPendientes1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "No. Factura", "Fecha", "Monto Original", "Saldo Pendientes"
            }
        ));
        jScrollPane8.setViewportView(tblFacturasPendientes1);

        jLabel21.setText("Facturas Pendientes a Pagar");

        jLabel22.setText("Facturas Asignadas al Recibo");

        jLabel23.setText("Tipo de Recibo:");

        jLabel24.setText("Fecha de Emisión");

        txtBuscarDocCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtBuscarDocClienteKeyPressed(evt);
            }
        });

        tblAplicacion1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Codigo", "Descripcion", "Valor", "Numero", "Banco"
            }
        ));
        jScrollPane9.setViewportView(tblAplicacion1);

        jLabel25.setText("Formas de Pago");

        btnAgregarFactura1.setText("Agregar Facturas al Recibo");
        btnAgregarFactura1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarFactura1ActionPerformed(evt);
            }
        });

        btnQuitarFactura1.setText("Quitar Facturas al Recibo");
        btnQuitarFactura1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuitarFactura1ActionPerformed(evt);
            }
        });

        jLabel26.setText("Cliente");

        jLabel27.setText("Nombre");

        jLabel28.setText("Motivo");

        btnEliminar1.setText("Eliminar Recibo");
        btnEliminar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminar1ActionPerformed(evt);
            }
        });

        jLabel29.setText("Total Recibo");

        jLabel30.setText("Total Pagado");

        btnProcesarReciboCliente.setText("Procesar Cobro");
        btnProcesarReciboCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcesarReciboClienteActionPerformed(evt);
            }
        });

        btnAyudaRecibosClientes.setText("Ayudas");
        btnAyudaRecibosClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAyudaRecibosClientesActionPerformed(evt);
            }
        });

        generarReporteRecibosClientes.setText("Generar Reportes");
        generarReporteRecibosClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generarReporteRecibosClientesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addGap(23, 23, 23)
                        .addComponent(txtTotalFacturas1, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addGap(18, 18, 18)
                        .addComponent(txtTotalPagado1, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btnProcesarReciboCliente)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnEliminar1))
                    .addComponent(generarReporteRecibosClientes))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 442, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 442, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 442, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25)
                            .addGap(423, 423, 423))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(btnQuitarFactura1)
                            .addGap(287, 287, 287)
                            .addComponent(btnAgregarFactura1)
                            .addGap(267, 267, 267)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtBuscarDocCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(cmbTipoRecibo1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtIdCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(dcFecha1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtMotivo1, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(29, 29, 29)
                        .addComponent(btnAyudaRecibosClientes))
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 890, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(txtBuscarDocCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(txtIdCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(txtNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(txtMotivo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23)
                    .addComponent(cmbTipoRecibo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel24)
                        .addComponent(dcFecha1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnAyudaRecibosClientes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAgregarFactura1)
                    .addComponent(btnQuitarFactura1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel25)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(80, 80, 80)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEliminar1)
                    .addComponent(jLabel29)
                    .addComponent(txtTotalFacturas1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnProcesarReciboCliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(txtTotalPagado1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(generarReporteRecibosClientes))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Recibos de Cientes", jPanel4);

        jScrollPane6.setViewportView(jTabbedPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void btnConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarActionPerformed
        // TODO add your handling code here:
        modeloEstadoCuenta.setRowCount(0);
        List<clsMovimientoTransacciones> lista;
        double saldo = 0;

        if (jrbCliente.isSelected()) {
            if (jcbCliente.getSelectedIndex() <= 0 || jcbCliente.getSelectedItem().toString().contains("Item")) {
                javax.swing.JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente válido de la lista.", "Validación", javax.swing.JOptionPane.WARNING_MESSAGE);
                return; // Detiene el botón de forma segura
            }

            String sel = jcbCliente.getSelectedItem().toString();
            int cliid = Integer.parseInt(sel.split(" - ")[0]);
            lista = movDAO.queryPorCliente(cliid);
            saldo = movDAO.saldoCliente(cliid);

        } else {
            // Protección idéntica para Proveedores
            if (jcbProveedor.getSelectedIndex() <= 0 || jcbProveedor.getSelectedItem().toString().contains("Item")) {
                javax.swing.JOptionPane.showMessageDialog(this, "Por favor, seleccione un proveedor válido de la lista.", "Validación", javax.swing.JOptionPane.WARNING_MESSAGE);
                return; // Detiene el botón de forma segura
            }

            String sel = jcbProveedor.getSelectedItem().toString();
            int procodigo = Integer.parseInt(sel.split(" - ")[0]);
            lista = movDAO.queryPorProveedor(procodigo);
            saldo = movDAO.saldoProveedor(procodigo);
        }

        for (clsMovimientoTransacciones mov : lista) {
            double cargo = 0, abono = 0;
            if (mov.getMcctipo().equals("CARGO")) {
                cargo = mov.getMccmonto();
            } else {
                abono = Math.abs(mov.getMccmonto());
            }
            modeloEstadoCuenta.addRow(new Object[]{
                mov.getMccfecha(),
                mov.getMccconcepto(),
                String.format("Q %.2f", cargo),
                String.format("Q %.2f", abono),
                String.format("Q %.2f", mov.getMccsaldo())
            });
        }
        
        lblSaldoActual.setText(String.format("Q %.2f", saldo));
        lblSaldoActual.setForeground(saldo > 0 ? Color.RED : new Color(0, 128, 0));
    }//GEN-LAST:event_btnConsultarActionPerformed

    private void jrbClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbClienteActionPerformed
        if (jrbCliente.isSelected()) {
        jcbCliente.setEnabled(true);     
        jcbProveedor.setEnabled(false);   
        jcbProveedor.setSelectedIndex(0); 
    }
    }//GEN-LAST:event_jrbClienteActionPerformed
    private void jrbProveedorActionPerformed(java.awt.event.ActionEvent evt) {
        if (jrbProveedor.isSelected()) {
        jcbProveedor.setEnabled(true);   
        jcbCliente.setEnabled(false);    
        jcbCliente.setSelectedIndex(0);  
        }
    }
    private void jcbClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbClientesActionPerformed
        // TODO add your handling code here:
        if (jcbClientes.getItemCount() == 0 || jcbClientes.getSelectedIndex() == -1) {
        return; 
        }
        jcbProveedor.setEnabled(true);
        jcbCliente.setEnabled(false);
        jcbCliente.setSelectedIndex(0);
    }//GEN-LAST:event_jcbClientesActionPerformed

    private void jcbProveedoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbProveedoresActionPerformed
        // TODO add your handling code here:
        if (jcbProveedor.getItemCount() == 0 || jcbProveedor.getSelectedIndex() == -1) {
        return; 
        }

        jcbCliente.setEnabled(true);
        jcbProveedor.setEnabled(false);
        jcbProveedor.setSelectedIndex(0);
    }//GEN-LAST:event_jcbProveedoresActionPerformed

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
         // Validaciones
        if (txtConcepto.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Ingrese un concepto.", "Aviso", JOptionPane.WARNING_MESSAGE);
        return;
        }
        if (txtMonto.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un monto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String tipo     = jcbTipo.getSelectedItem().toString();
            String concepto = txtConcepto.getText().trim();
            double monto    = Double.parseDouble(txtMonto.getText().trim());
            String modulo   = jcbModulo.getSelectedItem().toString();

            if (tipo.equals("ABONO")) monto = monto * -1;

            int cliid = 0, procodigo = 0;

            if (jcbClientes.isEnabled() && jcbClientes.getSelectedItem() != null) {
                cliid = Integer.parseInt(jcbClientes.getSelectedItem().toString().split(" - ")[0]);
            }
            if (jcbProveedores.isEnabled() && jcbProveedores.getSelectedItem() != null) {
                procodigo = Integer.parseInt(jcbProveedores.getSelectedItem().toString().split(" - ")[0]);
            }

            
            if (cliid > 0 && procodigo > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Incoherencia de datos: No puede aplicar un movimiento a un Cliente y a un Proveedor a la vez.", 
                    "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (cliid == 0 && procodigo == 0) {
                JOptionPane.showMessageDialog(this, 
                    "Debe seleccionar obligatoriamente un Cliente o un Proveedor.", 
                    "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // ==========================================

            double saldo = 0;
            if (cliid > 0)       saldo = movDAO.saldoCliente(cliid) + monto;
            else if (procodigo > 0) saldo = movDAO.saldoProveedor(procodigo) + monto;

            int TTid = obtenerTTid(modulo, tipo);

            int resultado = movDAO.insert(
                monto, tipo, concepto,
                saldo, cliid, procodigo,
                0, 0, TTid, modulo, 0
            );

            if (resultado > 0) {
                JOptionPane.showMessageDialog(this, "Movimiento registrado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarMovimientos();
                cargarTodosMovimientos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Monto inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    
    }//GEN-LAST:event_btnRegistrarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        // TODO add your handling code here:
        limpiarMovimientos();
        modeloMovimientos.setRowCount(0);
    }//GEN-LAST:event_btnLimpiarActionPerformed

    
    private void configurarComponentesAdicionalesRecibo() {
        String[] colsPend = {"ID Doc", "No. Factura", "Fecha Emisión", "Saldo Pendiente"};
        modeloPendientes1 = new DefaultTableModel(colsPend, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblFacturasPendientes1.setModel(modeloPendientes1);

        String[] colsAsig = {"ID Doc", "No. Factura", "Saldo Anterior", "Monto Abonado", "Saldo Actualizado"};
        modeloAsignadas1 = new DefaultTableModel(colsAsig, 0) {
            public boolean isCellEditable(int r, int c) { return c == 3; }
        };
        tblFacturasAsignadas1.setModel(modeloAsignadas1);

        String[] colsApl = {"Código", "Descripción", "Valor", "No. Doc/Ref", "Banco"};
        modeloAplicacion1 = new DefaultTableModel(colsApl, 0) {
            public boolean isCellEditable(int r, int c) { return c == 2 || c == 3 || c == 4; }
        };
        tblAplicacion1.setModel(modeloAplicacion1);
        modeloAplicacion1.addRow(new Object[]{"EF", "EFECTIVO",              0.00, "N/A", "N/A"});
        modeloAplicacion1.addRow(new Object[]{"CH", "CHEQUE",                0.00, "",    ""   });
        modeloAplicacion1.addRow(new Object[]{"TR", "TRANSFERENCIA BANCARIA", 0.00, "",    ""   });

        modeloAplicacion1.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE && e.getColumn() == 2) {
                javax.swing.SwingUtilities.invokeLater(() -> validarMontosFormasDePago());
            }
        });
    }
    
    
    private void btnAgregarFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarFacturaActionPerformed
        // TODO add your handling code here:
        int filaSeleccionada = tblFacturasPendientes.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una factura de la tabla de pendientes.");
            return;
        }

        // Obtener datos de la fila de la tabla superior
        Object idDoc = modeloPendientes.getValueAt(filaSeleccionada, 0);
        Object noFactura = modeloPendientes.getValueAt(filaSeleccionada, 1);
        double saldoPendiente = Double.parseDouble(modeloPendientes.getValueAt(filaSeleccionada, 3).toString());

        // Verificar que no se haya agregado previamente a la tabla inferior
        for (int i = 0; i < modeloAsignadas.getRowCount(); i++) {
            if (modeloAsignadas.getValueAt(i, 0).equals(idDoc)) {
                JOptionPane.showMessageDialog(this, "Esta factura ya fue asignada al recibo actual.");
                return;
            }
        }

        // Agregar a la tabla inferior: [ID Doc, No. Factura, Saldo Anterior, Monto Abonado, Saldo Actualizado]
        // Se inicializa con abono de 0.00 y el mismo saldo anterior
        modeloAsignadas.addRow(new Object[]{idDoc, noFactura, saldoPendiente, 0.00, saldoPendiente});

        // Remover de la tabla de pendientes para evitar duplicación visual
        modeloPendientes.removeRow(filaSeleccionada);
        
        calcularTotalesRecibo();
        
    }//GEN-LAST:event_btnAgregarFacturaActionPerformed

    private void txtBuscarDocumentoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarDocumentoKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { 
        String documento = txtBuscarDocumento.getText().trim();
            if (!documento.isEmpty()) {
                buscarProveedorPorDocumento(documento);
            }
        }
    }//GEN-LAST:event_txtBuscarDocumentoKeyPressed

    private void btnQuitarFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitarFacturaActionPerformed
        // TODO add your handling code here:
        int filaSeleccionada = tblFacturasAsignadas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una factura de la tabla de asignadas.");
            return;
        }

        // Recuperar datos para restaurarla
        Object idDoc = modeloAsignadas.getValueAt(filaSeleccionada, 0);
        Object noFactura = modeloAsignadas.getValueAt(filaSeleccionada, 1);
        double saldoAnterior = Double.parseDouble(modeloAsignadas.getValueAt(filaSeleccionada, 2).toString());

        // Regresar a la tabla superior: [ID Doc, No. Factura, Fecha Emisión, Saldo Pendiente]
        modeloPendientes.addRow(new Object[]{idDoc, noFactura, dcFecha.getDate(), saldoAnterior});

        // Eliminar del recibo actual
        modeloAsignadas.removeRow(filaSeleccionada);
        
        calcularTotalesRecibo();
    }//GEN-LAST:event_btnQuitarFacturaActionPerformed

    private void btnProcesarReciboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcesarReciboActionPerformed
        // TODO add your handling code here:
        if (modeloAsignadas.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "Debe asignar al menos una factura al recibo.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
        }
        if (txtIdProveedor.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay un proveedor válido seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idProveedor = Integer.parseInt(txtIdProveedor.getText().trim());
        java.util.Date fechaSeleccionada = dcFecha.getDate();
        java.sql.Timestamp fechaSQL = (fechaSeleccionada != null) 
            ? new java.sql.Timestamp(fechaSeleccionada.getTime()) 
            : new java.sql.Timestamp(System.currentTimeMillis());

        String concepto = txtMotivo != null && !txtMotivo.getText().trim().isEmpty() 
            ? txtMotivo.getText().trim() 
            : "ABONO RECIBO DE PAGO - PROV " + idProveedor;

        Connection con = null;
        try {
            con = cp.getConnection();
            con.setAutoCommit(false); // Iniciamos transacción atómica

            // Queries para tu base de datos real
            String sqlMovimientoCC = "INSERT INTO movimientoscc (Mccfecha, Mccmonto, Mcctipo, Mccconcepto, Mccestado, Mccsaldo, Procodigo, TTid, Mccmodulo, Mccorigenid) " +
                                     "VALUES (?, ?, 'ABONO', ?, 'A', 0.00, ?, ?, 'CC', ?)";

            String sqlUpdateCpp = "UPDATE cuentasporpagar SET Cppsaldopendiente = ? WHERE Cppcodigo = ?";

            try (PreparedStatement psMov = con.prepareStatement(sqlMovimientoCC);
                 PreparedStatement psUpdate = con.prepareStatement(sqlUpdateCpp)) {

                // Recorremos las facturas que agregaste a la tabla inferior
                for (int i = 0; i < modeloAsignadas.getRowCount(); i++) {
                    int cppCodigo = Integer.parseInt(modeloAsignadas.getValueAt(i, 0).toString());
                    double montoAbonado = Double.parseDouble(modeloAsignadas.getValueAt(i, 3).toString());
                    double saldoActualizado = Double.parseDouble(modeloAsignadas.getValueAt(i, 4).toString());

                    if (montoAbonado > 0) {
                        // PASO A: Insertar el abono en movimientoscc apuntando al documento origen
                        psMov.setTimestamp(1, fechaSQL);
                        psMov.setDouble(2, montoAbonado);
                        psMov.setString(3, concepto + " (Factura Ref: " + cppCodigo + ")");
                        psMov.setInt(4, idProveedor);
                        psMov.setInt(5, 8); // Código de tipo transacción estándar para Abonos/Pagos en tu sistema
                        psMov.setInt(6, cppCodigo); // Mccorigenid = Guarda qué factura se está pagando
                        psMov.addBatch();

                        // PASO B: Actualizar el saldo restante en cuentasporpagar
                        psUpdate.setDouble(1, saldoActualizado);
                        psUpdate.setInt(2, cppCodigo);
                        psUpdate.addBatch();
                    }
                }

                // Ejecutamos todo junto en la base de datos
                psMov.executeBatch();
                psUpdate.executeBatch();
            }

            // Si todo corre bien, guardamos permanentemente
            con.commit();
            JOptionPane.showMessageDialog(this, "¡El pago ha sido procesado con éxito en la Cuenta Corriente del Proveedor!", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            limpiarFormularioRecibo();

        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al procesar la cuenta corriente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (con != null) {
                try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }//GEN-LAST:event_btnProcesarReciboActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // TODO add your handling code here:
        String idFacturaStr = JOptionPane.showInputDialog(this, "Ingrese el ID del documento (ID Doc) de la factura para revertir sus abonos activos:", "Anular Abonos de Factura", JOptionPane.QUESTION_MESSAGE);
    
        if (idFacturaStr == null || idFacturaStr.trim().isEmpty()) return;

        int cppCodigo;
        try {
            cppCodigo = Integer.parseInt(idFacturaStr.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El ID debe ser un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(this, "¿Desea anular TODOS los abonos activos del documento No. " + cppCodigo + " y restaurar su saldo?", "Confirmar Reversión", JOptionPane.YES_NO_OPTION);
        if (confirmar != JOptionPane.YES_OPTION) return;

        Connection con = null;
        try {
            con = cp.getConnection();
            con.setAutoCommit(false);

            // 1. Sumar cuántos abonos activos ('A') existen para esa factura para saber cuánto devolverle
            double totalARevertir = 0;
            String sqlSum = "SELECT SUM(Mccmonto) AS total FROM movimientoscc WHERE Mccorigenid = ? AND Mcctipo = 'ABONO' AND Mccestado = 'A'";
            try (PreparedStatement psSum = con.prepareStatement(sqlSum)) {
                psSum.setInt(1, cppCodigo);
                try (ResultSet rs = psSum.executeQuery()) {
                    if (rs.next()) {
                        totalARevertir = rs.getDouble("total");
                    }
                }
            }

            if (totalARevertir <= 0) {
                JOptionPane.showMessageDialog(this, "No se encontraron abonos activos para anular en la factura seleccionada.", "Información", JOptionPane.INFORMATION_MESSAGE);
                con.rollback();
                return;
            }

            // 2. Colocar en estado Inactivo ('I') los abonos en movimientoscc
            String sqlAnularAbonos = "UPDATE movimientoscc SET Mccestado = 'I' WHERE Mccorigenid = ? AND Mcctipo = 'ABONO' AND Mccestado = 'A'";
            try (PreparedStatement psAnula = con.prepareStatement(sqlAnularAbonos)) {
                psAnula.setInt(1, cppCodigo);
                psAnula.executeUpdate();
            }

            // 3. Devolverle el dinero al saldo pendiente en cuentasporpagar
            String sqlRestaurarSaldo = "UPDATE cuentasporpagar SET Cppsaldopendiente = Cppsaldopendiente + ? WHERE Cppcodigo = ?";
            try (PreparedStatement psRest = con.prepareStatement(sqlRestaurarSaldo)) {
                psRest.setDouble(1, totalARevertir);
                psRest.setInt(2, cppCodigo);
                psRest.executeUpdate();
            }

            con.commit();
            JOptionPane.showMessageDialog(this, "¡Abonos anulados! Se restauraron Q " + totalARevertir + " al saldo pendiente de la factura No. " + cppCodigo, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormularioRecibo();

        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al anular movimientos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (con != null) {
                try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void txtBuscarDocClienteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarDocClienteKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            String idBuscar = txtBuscarDocCliente.getText().trim();
            if (!idBuscar.isEmpty()) {
                buscarClienteYFacturas(idBuscar);
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese un código de cliente para buscar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_txtBuscarDocClienteKeyPressed

    private void btnAgregarFactura1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarFactura1ActionPerformed
        int filaSeleccionada = tblFacturasPendientes1.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una factura de la lista de pendientes.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object cxcCodigoObj = modeloPendientes1.getValueAt(filaSeleccionada, 0);
        Object cxcNumeroObj = modeloPendientes1.getValueAt(filaSeleccionada, 1);
        Object saldoPendienteObj = modeloPendientes1.getValueAt(filaSeleccionada, 3);

        if (cxcCodigoObj == null) return;

        int cxcCodigo = Integer.parseInt(cxcCodigoObj.toString());
        String cxcNumero = cxcNumeroObj != null ? cxcNumeroObj.toString() : "";
        double saldoAnterior = saldoPendienteObj != null ? Double.parseDouble(saldoPendienteObj.toString()) : 0.0;

        for (int i = 0; i < modeloAsignadas1.getRowCount(); i++) {
            int codigoAsignado = Integer.parseInt(modeloAsignadas1.getValueAt(i, 0).toString());
            if (codigoAsignado == cxcCodigo) {
                JOptionPane.showMessageDialog(this, "Esta factura ya ha sido agregada al recibo actual.", "Documento Duplicado", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        modeloAsignadas1.addRow(new Object[]{
            cxcCodigo,
            cxcNumero,
            saldoAnterior,
            0.0,
            saldoAnterior
        });

        modeloPendientes1.removeRow(filaSeleccionada);

        calcularTotalesRecibo();
    }//GEN-LAST:event_btnAgregarFactura1ActionPerformed

    private void btnQuitarFactura1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitarFactura1ActionPerformed
        int filaSeleccionada = tblFacturasAsignadas1.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una factura de la tabla de asignadas.");
            return;
        }

        Object idDoc = modeloAsignadas1.getValueAt(filaSeleccionada, 0);
        Object noFactura = modeloAsignadas1.getValueAt(filaSeleccionada, 1);
        double saldoAnterior = Double.parseDouble(modeloAsignadas1.getValueAt(filaSeleccionada, 2).toString());

        modeloPendientes1.addRow(new Object[]{idDoc, noFactura, dcFecha1.getDate(), saldoAnterior});

        modeloAsignadas1.removeRow(filaSeleccionada);
        
        calcularTotalesRecibo();
    }//GEN-LAST:event_btnQuitarFactura1ActionPerformed

    private void btnEliminar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminar1ActionPerformed
        String idFacturaStr = JOptionPane.showInputDialog(this, "Ingrese el ID del documento (ID Doc) de la factura para revertir sus abonos activos:", "Anular Abonos de Factura", JOptionPane.QUESTION_MESSAGE);
        
        if (idFacturaStr == null || idFacturaStr.trim().isEmpty()) return;

        int cxcCodigo;
        try {
            cxcCodigo = Integer.parseInt(idFacturaStr.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El ID debe ser un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(this, "¿Desea anular TODOS los abonos activos del documento No. " + cxcCodigo + " y restaurar su saldo?", "Confirmar Reversión", JOptionPane.YES_NO_OPTION);
        if (confirmar != JOptionPane.YES_OPTION) return;

        Connection con = null;
        try {
            con = cp.getConnection();
            con.setAutoCommit(false);

            double totalARevertir = 0;
            String sqlSum = "SELECT SUM(Mccmonto) AS total FROM movimientoscc WHERE Mccorigenid = ? AND Mcctipo = 'ABONO' AND Mccestado = 'A'";
            try (PreparedStatement psSum = con.prepareStatement(sqlSum)) {
                psSum.setInt(1, cxcCodigo);
                try (ResultSet rsSum = psSum.executeQuery()) {
                    if (rsSum.next()) {
                        totalARevertir = rsSum.getDouble("total");
                    }
                }
            }

            if (totalARevertir <= 0) {
                JOptionPane.showMessageDialog(this, "No se encontraron abonos activos para anular en la factura seleccionada.", "Información", JOptionPane.INFORMATION_MESSAGE);
                con.rollback();
                return;
            }

            String sqlAnularAbonos = "UPDATE movimientoscc SET Mccestado = 'I' WHERE Mccorigenid = ? AND Mcctipo = 'ABONO' AND Mccestado = 'A'";
            try (PreparedStatement psAnula = con.prepareStatement(sqlAnularAbonos)) {
                psAnula.setInt(1, cxcCodigo);
                psAnula.executeUpdate();
            }

            String sqlRestaurarSaldo = "UPDATE cuentasporcobrar SET Cpcsaldo = Cpcsaldo + ? WHERE Cpccodigo = ?";
            try (PreparedStatement psRest = con.prepareStatement(sqlRestaurarSaldo)) {
                psRest.setDouble(1, totalARevertir);
                psRest.setInt(2, cxcCodigo);
                psRest.executeUpdate();
            }

            con.commit();
            JOptionPane.showMessageDialog(this, "¡Abonos anulados! Se restauraron Q " + totalARevertir + " al saldo pendiente de la factura No. " + cxcCodigo, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormularioRecibo();

        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al anular movimientos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (con != null) {
                try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }//GEN-LAST:event_btnEliminar1ActionPerformed

    private void btnProcesarReciboClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcesarReciboClienteActionPerformed
        if (modeloAsignadas1.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "No hay documentos asignados para procesar en este recibo.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
        }

        String idClienteStr = txtIdCliente.getText().trim();
        if (idClienteStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe especificar un cliente válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int cliId = Integer.parseInt(idClienteStr);

        String sqlInsertMovimiento = "INSERT INTO movimientoscc (Mccfecha, Mccmonto, Mcctipo, Mccconcepto, Mccestado, Mccsaldo, Cliid, Procodigo, Acrecodigo, Venid, TTid, Mccmodulo, Mccorigenid) "
                                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String sqlUpdateSaldoCxc = "UPDATE cuentasporcobrar SET Cpcsaldo = ? WHERE Cpccodigo = ?";

        Connection con = null;

        try {
            con = cp.getConnection();
            con.setAutoCommit(false);

            try (PreparedStatement psInsert = con.prepareStatement(sqlInsertMovimiento);
                 PreparedStatement psUpdate = con.prepareStatement(sqlUpdateSaldoCxc)) {

                java.sql.Timestamp fechaActual = new java.sql.Timestamp(new java.util.Date().getTime());
                boolean tieneRegistrosParaProcesar = false;

                for (int i = 0; i < modeloAsignadas1.getRowCount(); i++) {
                    int cxcCodigo = Integer.parseInt(modeloAsignadas1.getValueAt(i, 0).toString());
                    double saldoAnterior = Double.parseDouble(modeloAsignadas1.getValueAt(i, 2).toString());
                    double montoAbonado = Double.parseDouble(modeloAsignadas1.getValueAt(i, 3).toString());
                    double saldoActualizado = Double.parseDouble(modeloAsignadas1.getValueAt(i, 4).toString());

                    if (montoAbonado > 0) {
                        tieneRegistrosParaProcesar = true;

                        psInsert.setTimestamp(1, fechaActual);
                        psInsert.setDouble(2, montoAbonado);
                        psInsert.setString(3, "ABONO");
                        psInsert.setString(4, txtMotivo1.getText().trim().isEmpty() ? "ABONO A FACTURA REF: " + cxcCodigo : txtMotivo1.getText().trim());
                        psInsert.setString(5, "A");
                        psInsert.setDouble(6, saldoActualizado);

                        psInsert.setInt(7, cliId);
                        psInsert.setNull(8, java.sql.Types.INTEGER);
                        psInsert.setNull(9, java.sql.Types.INTEGER);
                        psInsert.setNull(10, java.sql.Types.INTEGER);

                        psInsert.setInt(11, obtenerTTid("CC", "ABONO"));
                        psInsert.setString(12, "CC");
                        psInsert.setInt(13, cxcCodigo);

                        psInsert.addBatch();

                        psUpdate.setDouble(1, saldoActualizado);
                        psUpdate.setInt(2, cxcCodigo);

                        psUpdate.addBatch();
                    }
                }

                if (tieneRegistrosParaProcesar) {
                    psInsert.executeBatch();
                    psUpdate.executeBatch();

                    con.commit();
                    JOptionPane.showMessageDialog(this, "El recibo de cobro del cliente ha sido procesado y asentado con éxito.", "Transacción Exitosa", JOptionPane.INFORMATION_MESSAGE);

                    limpiarFormularioRecibo(); 
                } else {
                    JOptionPane.showMessageDialog(this, "No se ejecutó ninguna acción debido a que los montos abonados están en 0.00.", "Información", JOptionPane.INFORMATION_MESSAGE);
                    con.rollback();
                }

            } catch (SQLException batchEx) {
                if (con != null) {
                    con.rollback();
                }
                throw batchEx;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error crítico en transaccionalidad JDBC (Rollback Ejecutado):\n" + e.getMessage(), "SQL Syntax / Integrity Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }//GEN-LAST:event_btnProcesarReciboClienteActionPerformed

    private void generarReporteCuentasPorPagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generarReporteCuentasPorPagarActionPerformed
        // TODO add your handling code here:
        Connection conn = null;
        Map p = new HashMap();
        JasperReport report;
        JasperPrint print;
            try {
                conn = Conexion.getConnection();
                report = JasperCompileManager.compileReport(new File("").getAbsolutePath()
                    + "/src/main/java/Reportes/CuentasCorrientes/rptRecibosProveedores.jrxml");
                    print = JasperFillManager.fillReport(report, p, conn);
                JasperViewer view = new JasperViewer(print, false);
                    view.setTitle("Reporte Prueba");
                view.setVisible(true);
            } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_generarReporteCuentasPorPagarActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        Connection conn = null;
        Map p = new HashMap();
        JasperReport report;
        JasperPrint print;
            try {
                conn = Conexion.getConnection();
                report = JasperCompileManager.compileReport(new File("").getAbsolutePath()
                    + "/src/main/java/Reportes/CuentasCorrientes/MovimientosTransaccionesReportes.jrxml");
                    print = JasperFillManager.fillReport(report, p, conn);
                JasperViewer view = new JasperViewer(print, false);
                    view.setTitle("Reporte Prueba");
                view.setVisible(true);
            } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        Connection conn = null;
        Map p = new HashMap();
        JasperReport report;
        JasperPrint print;
            try {
                conn = Conexion.getConnection();
                report = JasperCompileManager.compileReport(new File("").getAbsolutePath()
                    + "/src/main/java/Reportes/CuentasCorrientes/reporteEstadoCuenta.jrxml");
                    print = JasperFillManager.fillReport(report, p, conn);
                JasperViewer view = new JasperViewer(print, false);
                    view.setTitle("Reporte Prueba");
                view.setVisible(true);
            } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jcbProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbProveedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jcbProveedorActionPerformed

    private void btnAyudaEstadoCuentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAyudaEstadoCuentaActionPerformed
       try {
        // Usamos el nombre único para evitar heredar archivos viejos
        String nombreArchivo = "AyudasTransaccionesCC.chm";
        String carpetaRaiz = System.getProperty("user.dir") + java.io.File.separator;
        java.io.File archivoAyuda = new java.io.File(carpetaRaiz + nombreArchivo);
        
        // Si no está en la raíz de ejecución, lo busca en el directorio base suelto
        if (!archivoAyuda.exists()) {
            archivoAyuda = new java.io.File(nombreArchivo);
        }
        
        if (archivoAyuda.exists()) {
            String rutaFinal = archivoAyuda.getAbsolutePath();
            
            // Forzamos la apertura directa del archivo nuevo mediante el visor del sistema operativo
            Process p = Runtime.getRuntime().exec("hh.exe " + rutaFinal);
            p.waitFor();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "No se encontró el nuevo archivo de ayuda.\nBuscado como: " + archivoAyuda.getAbsolutePath() + 
                "\n\nAsegúrate de que se llame exactamente 'ManualCC_Final.chm' en tu carpeta.", 
                "Error de Archivo", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    }//GEN-LAST:event_btnAyudaEstadoCuentaActionPerformed

    private void btnAyudaMovimientosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAyudaMovimientosActionPerformed
        // TODO add your handling code here:
        try {
        // Usamos el nombre único para evitar heredar archivos viejos
        String nombreArchivo = "AyudasTransaccionesCC.chm";
        String carpetaRaiz = System.getProperty("user.dir") + java.io.File.separator;
        java.io.File archivoAyuda = new java.io.File(carpetaRaiz + nombreArchivo);
        
        // Si no está en la raíz de ejecución, lo busca en el directorio base suelto
        if (!archivoAyuda.exists()) {
            archivoAyuda = new java.io.File(nombreArchivo);
        }
        
        if (archivoAyuda.exists()) {
            String rutaFinal = archivoAyuda.getAbsolutePath();
            
            // Forzamos la apertura directa del archivo nuevo mediante el visor del sistema operativo
            Process p = Runtime.getRuntime().exec("hh.exe " + rutaFinal);
            p.waitFor();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "No se encontró el nuevo archivo de ayuda.\nBuscado como: " + archivoAyuda.getAbsolutePath() + 
                "\n\nAsegúrate de que se llame exactamente 'ManualCC_Final.chm' en tu carpeta.", 
                "Error de Archivo", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    }//GEN-LAST:event_btnAyudaMovimientosActionPerformed

    private void btnAyudaRecibosClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAyudaRecibosClientesActionPerformed
        // TODO add your handling code here:
        try {
        // Usamos el nombre único para evitar heredar archivos viejos
        String nombreArchivo = "AyudasTransaccionesCC.chm";
        String carpetaRaiz = System.getProperty("user.dir") + java.io.File.separator;
        java.io.File archivoAyuda = new java.io.File(carpetaRaiz + nombreArchivo);
        
        // Si no está en la raíz de ejecución, lo busca en el directorio base suelto
        if (!archivoAyuda.exists()) {
            archivoAyuda = new java.io.File(nombreArchivo);
        }
        
        if (archivoAyuda.exists()) {
            String rutaFinal = archivoAyuda.getAbsolutePath();
            
            // Forzamos la apertura directa del archivo nuevo mediante el visor del sistema operativo
            Process p = Runtime.getRuntime().exec("hh.exe " + rutaFinal);
            p.waitFor();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "No se encontró el nuevo archivo de ayuda.\nBuscado como: " + archivoAyuda.getAbsolutePath() + 
                "\n\nAsegúrate de que se llame exactamente 'ManualCC_Final.chm' en tu carpeta.", 
                "Error de Archivo", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    }//GEN-LAST:event_btnAyudaRecibosClientesActionPerformed

    private void btnAyudaRecibosProveedoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAyudaRecibosProveedoresActionPerformed
        // TODO add your handling code here:
        try {
        // Usamos el nombre único para evitar heredar archivos viejos
        String nombreArchivo = "AyudasTransaccionesCC.chm";
        String carpetaRaiz = System.getProperty("user.dir") + java.io.File.separator;
        java.io.File archivoAyuda = new java.io.File(carpetaRaiz + nombreArchivo);
        
        // Si no está en la raíz de ejecución, lo busca en el directorio base suelto
        if (!archivoAyuda.exists()) {
            archivoAyuda = new java.io.File(nombreArchivo);
        }
        
        if (archivoAyuda.exists()) {
            String rutaFinal = archivoAyuda.getAbsolutePath();
            
            // Forzamos la apertura directa del archivo nuevo mediante el visor del sistema operativo
            Process p = Runtime.getRuntime().exec("hh.exe " + rutaFinal);
            p.waitFor();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "No se encontró el nuevo archivo de ayuda.\nBuscado como: " + archivoAyuda.getAbsolutePath() + 
                "\n\nAsegúrate de que se llame exactamente 'ManualCC_Final.chm' en tu carpeta.", 
                "Error de Archivo", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    }//GEN-LAST:event_btnAyudaRecibosProveedoresActionPerformed

    private void generarReporteRecibosClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generarReporteRecibosClientesActionPerformed
        // TODO add your handling code here:
        Connection conn = null;
        Map p = new HashMap();
        JasperReport report;
        JasperPrint print;
            try {
                conn = Conexion.getConnection();
                report = JasperCompileManager.compileReport(new File("").getAbsolutePath()
                    + "/src/main/java/Reportes/CuentasCorrientes/rptRecibosClientes.jrxml");
                    print = JasperFillManager.fillReport(report, p, conn);
                JasperViewer view = new JasperViewer(print, false);
                    view.setTitle("Reporte Prueba");
                view.setVisible(true);
            } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_generarReporteRecibosClientesActionPerformed

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
            java.util.logging.Logger.getLogger(TransaccionesCC.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TransaccionesCC.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TransaccionesCC.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TransaccionesCC.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TransaccionesCC().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgTipo;
    private javax.swing.JButton btnAgregarFactura;
    private javax.swing.JButton btnAgregarFactura1;
    private javax.swing.JButton btnAyudaEstadoCuenta;
    private javax.swing.JButton btnAyudaMovimientos;
    private javax.swing.JButton btnAyudaRecibosClientes;
    private javax.swing.JButton btnAyudaRecibosProveedores;
    private javax.swing.JButton btnConsultar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnEliminar1;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnProcesarRecibo;
    private javax.swing.JButton btnProcesarReciboCliente;
    private javax.swing.JButton btnQuitarFactura;
    private javax.swing.JButton btnQuitarFactura1;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JComboBox<String> cmbTipoRecibo;
    private javax.swing.JComboBox<String> cmbTipoRecibo1;
    private com.toedter.calendar.JDateChooser dcFecha;
    private com.toedter.calendar.JDateChooser dcFecha1;
    private javax.swing.JButton generarReporteCuentasPorPagar;
    private javax.swing.JButton generarReporteRecibosClientes;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableEstadoCuenta;
    private javax.swing.JTable jTableMovimientos;
    private javax.swing.JComboBox<String> jcbCliente;
    private javax.swing.JComboBox<String> jcbClientes;
    private javax.swing.JComboBox<String> jcbModulo;
    private javax.swing.JComboBox<String> jcbProveedor;
    private javax.swing.JComboBox<String> jcbProveedores;
    private javax.swing.JComboBox<String> jcbTipo;
    private javax.swing.JRadioButton jrbCliente;
    private javax.swing.JRadioButton jrbProveedor;
    private javax.swing.JTextField lblSaldoActual;
    private javax.swing.JTable tblAplicacion;
    private javax.swing.JTable tblAplicacion1;
    private javax.swing.JTable tblFacturasAsignadas;
    private javax.swing.JTable tblFacturasAsignadas1;
    private javax.swing.JTable tblFacturasPendientes;
    private javax.swing.JTable tblFacturasPendientes1;
    private javax.swing.JTextField txtBuscarDocCliente;
    private javax.swing.JTextField txtBuscarDocumento;
    private javax.swing.JTextField txtConcepto;
    private javax.swing.JTextField txtIdCliente;
    private javax.swing.JTextField txtIdProveedor;
    private javax.swing.JTextField txtMonto;
    private javax.swing.JTextField txtMotivo;
    private javax.swing.JTextField txtMotivo1;
    private javax.swing.JTextField txtNombreCliente;
    private javax.swing.JTextField txtNombreProveedor;
    private javax.swing.JTextField txtTotalFacturas;
    private javax.swing.JTextField txtTotalFacturas1;
    private javax.swing.JTextField txtTotalPagado;
    private javax.swing.JTextField txtTotalPagado1;
    // End of variables declaration//GEN-END:variables
}
