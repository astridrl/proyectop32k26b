/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Vista;

import Controlador.clsBitacora;
import Modelo.BitacoraDAO;
import Modelo.AplicacionesDAO;
import Controlador.clsAplicaciones;
import Controlador.clsAsignacionAplicacionPerfil;
import Modelo.AsignacionAplicacionPerfilDAO;
import Controlador.clsSeguridad;
import Controlador.clsBitacora;
import Controlador.clsUsuario;
import Controlador.clsUsuarioConectado;
import Modelo.BitacoraDAO;
import Modelo.Conexion;
import Modelo.PermisosDAO;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.DefaultListModel;
import java.awt.HeadlessException;
import javax.swing.DefaultComboBoxModel;




/**
 *
 * @author Angel R
 */
public class frmProcesoAplicacionPerfil extends javax.swing.JInternalFrame {

    int codigoAplicacion = 10011;
    
    /**
     * Creates new form frmProcesoAplicacionPerfil
     */
    
    public frmProcesoAplicacionPerfil() {
        initComponents();
        
        cargarComboPerfiles(); // Esto ejecuta la carga al abrir la ventana
    // 1. Permitir que la ventana se pueda cerrar
    setClosable(true); 
    // 2. Permitir que se pueda minimizar (opcional)
    setIconifiable(true);
    // 3. Permitir que se pueda maximizar (opcional)
    setMaximizable(false);
    // 4. Hacer que se destruya la instancia al cerrar para liberar memoria
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        limpiarPermisos();
        
        
   
    DefaultListModel modeloD = new DefaultListModel();
    DefaultListModel modeloA = new DefaultListModel();
    AppDis.setModel(modeloD);
    AppAsig.setModel(modeloA);
    }
     public void cargarPermisos() {
    int usuId = clsUsuarioConectado.getUsuId();
    PermisosDAO permisosDAO = new PermisosDAO();

    //METODO PARA EL SISTEMA DE SEGURIDAD DE PERMISOS AGREGAR A SUS FORMULARIOS CORRESPONDIENTES
    // Todos usan código 10 = Mantenimiento Usuario
    guardar.setEnabled( permisosDAO.puedeInsertar (usuId, 10011) );
    btnObtener.setEnabled  ( permisosDAO.puedeBuscar   (usuId, 10011) );
    //actualizar
    AppDis.setEnabled( permisosDAO.puedeModificar(usuId, 10011) );
    btnPasarUno.setEnabled( permisosDAO.puedeModificar(usuId, 10011) );
    btnPasarTodos.setEnabled( permisosDAO.puedeModificar(usuId, 10011) );
    //delete
    btnRegresarUno.setEnabled( permisosDAO.puedeModificar(usuId, 10011) );
    btnRegresarTodos.setEnabled( permisosDAO.puedeModificar(usuId, 10011) );
        
    AppAsig.setEnabled ( permisosDAO.puedeEliminar (usuId, 10011) );
   // btnReportes.setEnabled( permisosDAO.puedeReportar (usuId, 10) );
}
    
     private void cargarComboPerfiles() {
    AsignacionAplicacionPerfilDAO dao = new AsignacionAplicacionPerfilDAO();
    List<String> perfiles = dao.obtenerNombresPerfiles();
    
    comboPerfil.removeAllItems(); // Limpia elementos previos
    comboPerfil.addItem("Seleccione un perfil..."); // Opción por defecto
    
    for (String perfil : perfiles) {
        comboPerfil.addItem(perfil);
    }
}

private void moverAAsignadas() {
    //Obtener el modelo actual de la lista
    DefaultListModel modeloDis = (DefaultListModel) AppDis.getModel();
    DefaultListModel modeloAsig = (DefaultListModel) AppAsig.getModel();

    //Obtener el valor seleccionado
    Object seleccionado = AppDis.getSelectedValue();

    if (seleccionado != null) {
        //Pasarlo a la otra lista
        modeloAsig.addElement(seleccionado);
        //Quitarlo de la original
        modeloDis.removeElement(seleccionado);
    } else {
        JOptionPane.showMessageDialog(this, "Seleccione un código de la lista izquierda");
    }
    }

private void moverADisponibles() {
    DefaultListModel modeloAsig = (DefaultListModel) AppAsig.getModel();
    DefaultListModel modeloDis = (DefaultListModel) AppDis.getModel();
    
    Object seleccionado = AppAsig.getSelectedValue();

    if (seleccionado != null) {
        try {
            int idApp = Integer.parseInt(seleccionado.toString());
            int idPerfil = Integer.parseInt(codigoPerfil.getText());

            AsignacionAplicacionPerfilDAO dao = new AsignacionAplicacionPerfilDAO();
            clsAsignacionAplicacionPerfil asignacionABorrar = new clsAsignacionAplicacionPerfil();
            asignacionABorrar.setAplcodigo(idApp);
            asignacionABorrar.setPercodigo(idPerfil);
            
            int filasBorradas = dao.delete(asignacionABorrar); 
            // -------------------------------------------------------------

            //Si se borró con éxito en la BD, procedemos con Bitácora e Interfaz
            if (filasBorradas > 0) {
                BitacoraDAO bitacoradao = new BitacoraDAO();
                int idUsuario = clsUsuarioConectado.getUsuId();
                bitacoradao.insert(idUsuario, codigoAplicacion, "Borrar");

                //Actualizar la interfaz visual
                modeloDis.addElement(seleccionado);
                modeloAsig.removeElement(seleccionado);
                limpiarPermisos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar el registro de la base de datos.");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error con los códigos de ID: " + e.getMessage());
        }
    } else {
        JOptionPane.showMessageDialog(this, "Seleccione un código de la lista derecha (Asignadas).");
    }   
}

private void pasarTodosAAsignadas() {
    DefaultListModel modeloDis = (DefaultListModel) AppDis.getModel();
    DefaultListModel modeloAsig = (DefaultListModel) AppAsig.getModel();

    // Recorremos todos los elementos del modelo origen
    for (int i = 0; i < modeloDis.getSize(); i++) {
        modeloAsig.addElement(modeloDis.getElementAt(i));
    }
    // Una vez copiados, limpiamos la lista de disponibles
    modeloDis.clear();
}

private void regresarTodosADisponibles() {
  DefaultListModel modeloAsig = (DefaultListModel) AppAsig.getModel();
    DefaultListModel modeloDis = (DefaultListModel) AppDis.getModel();
    String idPerfilStr = codigoPerfil.getText();

    if (idPerfilStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No hay un perfil seleccionado.");
        return;
    }

    try {
        int idPerfil = Integer.parseInt(idPerfilStr);
        AsignacionAplicacionPerfilDAO dao = new AsignacionAplicacionPerfilDAO();

        // 1. Llamada al DAO (Aquí se hace el trabajo sucio de la BD)
        int filasBorradas = dao.borrarTodoDePerfil(idPerfil);

        BitacoraDAO bitacoradao = new BitacoraDAO();
        bitacoradao.insert(clsUsuarioConectado.getUsuId(), codigoAplicacion, "Borrar");
        // 2. Actualizar la Interfaz Gráfica
        // Pasamos todos los elementos de "Asignadas" a "Disponibles" visualmente
        for (int i = 0; i < modeloAsig.getSize(); i++) {
            modeloDis.addElement(modeloAsig.getElementAt(i));
        }

        // 3. Limpiar todo
        modeloAsig.clear();
        limpiarPermisos();
        
        if (filasBorradas > 0) {
            JOptionPane.showMessageDialog(this, "Se han quitado todos los registros de la base de datos.");
        }

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "El código de perfil debe ser un número válido.");
    }
}
private void limpiarPermisos() {
    // Desmarcamos todos los RadioButtons
    jRadioButton1.setSelected(false);
    jRadioButton2.setSelected(false);
    jRadioButton3.setSelected(false);
    jRadioButton4.setSelected(false);
    jRadioButton5.setSelected(false);
    
    // Opcional: Desactivarlos para que no se puedan tocar si no hay selección
    jRadioButton1.setEnabled(false);
    jRadioButton2.setEnabled(false);
    jRadioButton3.setEnabled(false);
    jRadioButton4.setEnabled(false);
    jRadioButton5.setEnabled(false);
    
    appSelect.setText("Seleccione aplicación para ver permisos");
}
private void activarPermisos() {
    jRadioButton1.setEnabled(true);
    jRadioButton2.setEnabled(true);
    jRadioButton3.setEnabled(true);
    jRadioButton4.setEnabled(true);
    jRadioButton5.setEnabled(true);
}

        private void limpiarListasYPermisos() {
    // 1. Limpiar los modelos de las listas
    DefaultListModel modeloDis = (DefaultListModel) AppDis.getModel();
    DefaultListModel modeloAsig = (DefaultListModel) AppAsig.getModel();
    
    modeloDis.clear();
    modeloAsig.clear();

    // 2. Desmarcar todos los RadioButtons
    jRadioButton1.setSelected(false);
    jRadioButton2.setSelected(false);
    jRadioButton3.setSelected(false);
    jRadioButton4.setSelected(false);
    jRadioButton5.setSelected(false);
    
    // 3. Desactivarlos (importante para que no editen sin una App seleccionada)
    jRadioButton1.setEnabled(false);
    jRadioButton2.setEnabled(false);
    jRadioButton3.setEnabled(false);
    jRadioButton4.setEnabled(false);
    jRadioButton5.setEnabled(false);
    
    // 4. Resetear etiquetas informativas
    appSelect.setText("Seleccione aplicación para ver permisos");
}
        
        
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnObtener = new javax.swing.JButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        btnPasarUno = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        btnRegresarUno = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        btnPasarTodos = new javax.swing.JButton();
        guardar = new javax.swing.JButton();
        btnRegresarTodos = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        codigoPerfil = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        AppDis = new javax.swing.JList<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        AppAsig = new javax.swing.JList<>();
        jRadioButton3 = new javax.swing.JRadioButton();
        appSelect = new javax.swing.JLabel();
        jRadioButton4 = new javax.swing.JRadioButton();
        comboPerfil = new javax.swing.JComboBox<>();

        setTitle("Asignacion Aplicación Perfil");

        btnObtener.setText("Obtener info");
        btnObtener.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnObtenerActionPerformed(evt);
            }
        });

        jRadioButton5.setText("Reporte");

        btnPasarUno.setText(">");
        btnPasarUno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPasarUnoActionPerformed(evt);
            }
        });

        jLabel5.setText("Aplcodigo");

        btnRegresarUno.setText("<");
        btnRegresarUno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegresarUnoActionPerformed(evt);
            }
        });

        jLabel6.setText("Aplcodigo");

        btnPasarTodos.setText(">>");
        btnPasarTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPasarTodosActionPerformed(evt);
            }
        });

        guardar.setText("Guardar");
        guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarActionPerformed(evt);
            }
        });

        btnRegresarTodos.setText("<<");
        btnRegresarTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegresarTodosActionPerformed(evt);
            }
        });

        jLabel4.setText("Ingrese codigo de perfil");

        jRadioButton1.setText("Insert");

        jRadioButton2.setText("Select");

        jLabel2.setText("Aplicaciones Disponibles");

        jLabel3.setText("Aplicaciones Asignadas");

        AppDis.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                AppDisMouseClicked(evt);
            }
        });
        AppDis.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                AppDisValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(AppDis);

        AppAsig.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                AppAsigMouseClicked(evt);
            }
        });
        AppAsig.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                AppAsigValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(AppAsig);

        jRadioButton3.setText("Update");

        appSelect.setText("Seleccione aplicación para ver permisos");

        jRadioButton4.setText("Delete");

        comboPerfil.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(102, 102, 102)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnPasarUno)
                            .addComponent(btnRegresarUno)
                            .addComponent(btnPasarTodos, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRegresarTodos, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(32, 32, 32)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6))
                                .addGap(54, 54, 54)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jRadioButton1)
                                    .addComponent(jRadioButton2)
                                    .addComponent(jRadioButton3)
                                    .addComponent(jRadioButton4)
                                    .addComponent(jRadioButton5)
                                    .addComponent(guardar))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addComponent(appSelect)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(codigoPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(comboPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnObtener)
                        .addGap(40, 40, 40))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(codigoPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnObtener)
                    .addComponent(comboPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addComponent(appSelect)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnPasarUno)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnRegresarUno)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnPasarTodos)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnRegresarTodos))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jRadioButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRadioButton2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRadioButton3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRadioButton4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRadioButton5)))
                        .addGap(18, 18, 18)
                        .addComponent(guardar))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2)))
                .addContainerGap(47, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void btnObtenerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnObtenerActionPerformed
        // TODO add your handling code here:
        if (comboPerfil.getSelectedIndex() <= 0) { 
        JOptionPane.showMessageDialog(this, "Por favor, seleccione un perfil de la lista.");
        return; 
    }
    
    // 2. Obtener el nombre del perfil seleccionado
    String nombreSeleccionado = comboPerfil.getSelectedItem().toString();
    AsignacionAplicacionPerfilDAO dao = new AsignacionAplicacionPerfilDAO();
    
    // 3. Obtener el ID numérico a partir del nombre en la base de datos
    int idPerfil = dao.obtenerIdPorNombre(nombreSeleccionado);

    // 4. Tu lógica original adaptada al ID encontrado
    if (idPerfil != -1 && dao.verificarExistenciaPerfil(idPerfil)) { 
         codigoPerfil.setText(String.valueOf(idPerfil));
        AppAsig.clearSelection(); 
        limpiarPermisos(); 
        
        // Proceder a cargar las listas con el ID recuperado
        cargarListas(idPerfil);

        // --- REGISTRO EN BITÁCORA COMO CONSULTA ---
        BitacoraDAO bitacoradao = new BitacoraDAO();
        bitacoradao.insert(clsUsuarioConectado.getUsuId(), codigoAplicacion, "Consulta");
        // ------------------------------------------
    } else {
        // Alerta y limpieza si el ID es incorrecto
        JOptionPane.showMessageDialog(this, "ERROR: El perfil '" + nombreSeleccionado + "' no es válido en el sistema.");
        limpiarListasYPermisos();
    }


    }//GEN-LAST:event_btnObtenerActionPerformed

    private void cargarListas(int idPerfil) {
    //Obtener los modelos de las listas para manipularlos
    DefaultListModel modeloD = (DefaultListModel) AppDis.getModel();
    DefaultListModel modeloA = (DefaultListModel) AppAsig.getModel();
    
    //Limpiar lo que haya actualmente en pantalla
    modeloD.clear();
    modeloA.clear();
    AppAsig.clearSelection(); 
    //Instanciar el DAO
    AsignacionAplicacionPerfilDAO asigDao = new AsignacionAplicacionPerfilDAO();

    //Llenar Lista de Asignadas (registros en la tabla asignacion)
    List<clsAsignacionAplicacionPerfil> asignadas = asigDao.obtenerAsignadas(idPerfil);
    for (clsAsignacionAplicacionPerfil asig : asignadas) {
        // Agregamos el código de la aplicación al modelo
        modeloA.addElement(asig.getAplcodigo());
    }

    //Llenar Lista de Disponibles (aplicaciones que NO tiene ese perfil)
    List<clsAplicaciones> disponibles = asigDao.obtenerDisponibles(idPerfil);
    for (clsAplicaciones app : disponibles) {
        modeloD.addElement(app.getAplcodigo());
    }
    AppAsig.revalidate();
    AppAsig.repaint();
}
    private void btnPasarUnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPasarUnoActionPerformed
        // TODO add your handling code here:
        moverAAsignadas();
    }//GEN-LAST:event_btnPasarUnoActionPerformed

    private void btnRegresarUnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegresarUnoActionPerformed
        // TODO add your handling code here:
        moverADisponibles();
    }//GEN-LAST:event_btnRegresarUnoActionPerformed

    private void btnPasarTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPasarTodosActionPerformed
        // TODO add your handling code here:
        pasarTodosAAsignadas();
    }//GEN-LAST:event_btnPasarTodosActionPerformed

    private void guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarActionPerformed
        // TODO add your handling code here:
        String idPerfilStr = codigoPerfil.getText();
        Object appSeleccionada = AppAsig.getSelectedValue();

        if (idPerfilStr.isEmpty() || appSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un perfil y seleccionar una aplicación de la lista 'Asignadas'");
            return;
        }

        try {
            int idPerfil = Integer.parseInt(idPerfilStr);
            int idApp = Integer.parseInt(appSeleccionada.toString());

            //Crear el objeto con los datos de los RadioButtons
            clsAsignacionAplicacionPerfil asig = new clsAsignacionAplicacionPerfil();
            asig.setPercodigo(idPerfil);
            asig.setAplcodigo(idApp);

            asig.setAPLPins(jRadioButton1.isSelected() ? "1" : "0"); // Insertar
           asig.setAPLPsel(jRadioButton2.isSelected() ? "1" : "0"); // Seleccionar
            asig.setAPLPupd(jRadioButton3.isSelected() ? "1" : "0"); // Actualizar
           asig.setAPLPdel(jRadioButton4.isSelected() ? "1" : "0"); // Eliminar
           asig.setAPLPrep(jRadioButton5.isSelected() ? "1" : "0"); // Reportes

            //Llamar al DAO para guardar o actualizar
            AsignacionAplicacionPerfilDAO dao = new AsignacionAplicacionPerfilDAO();
            
            boolean resultadoExitoso = false;
            String accionParaBitacora = "";

            

         if (dao.obtenerRegistroEspecifico(idApp, idPerfil) == null) {
            if(dao.insert(asig) > 0) {
                resultadoExitoso = true;
                accionParaBitacora = "Insertar";
                JOptionPane.showMessageDialog(this, "Aplicación asignada con éxito");
            }
        } else {
            if(dao.update(asig) > 0) {
                resultadoExitoso = true;
                accionParaBitacora = "Actualizar";
                JOptionPane.showMessageDialog(this, "Permisos actualizados con éxito");
            }
        }

        // 4. EJECUCIÓN DE BITÁCORA (Solo si la base de datos confirmó el cambio)
        if (resultadoExitoso) {
            try {
                int idUsuario = clsUsuarioConectado.getUsuId();
                
                // Si el ID de usuario es 0, la bitácora suele fallar por llaves foráneas
                if (idUsuario != 0) {
                    BitacoraDAO bitacoradao = new BitacoraDAO();
                    // Usamos el código de aplicación global o el seleccionado
                    bitacoradao.insert(idUsuario, codigoAplicacion, accionParaBitacora);
                } else {
                    System.out.println("Advertencia: ID de usuario es 0, bitácora podría no grabarse.");
                }
            } catch (Exception e) {
                System.out.println("Error al registrar en bitácora: " + e.getMessage());
            }
        }

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "El código de perfil debe ser numérico");
    }
    }//GEN-LAST:event_guardarActionPerformed

    private void btnRegresarTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegresarTodosActionPerformed
        // TODO add your handling code here:
        regresarTodosADisponibles();
    }//GEN-LAST:event_btnRegresarTodosActionPerformed

    private void AppDisMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AppDisMouseClicked
        // TODO add your handling code here

    }//GEN-LAST:event_AppDisMouseClicked

    private void AppDisValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_AppDisValueChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_AppDisValueChanged

    private void AppAsigMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AppAsigMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_AppAsigMouseClicked

    private void AppAsigValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_AppAsigValueChanged
        // TODO add your handling code here:
            if (!evt.getValueIsAdjusting()) {
        Object seleccionado = AppAsig.getSelectedValue();
        
        if (seleccionado != null) {
            activarPermisos(); // Habilita los RadioButtons (setEnabled(true))

            try {
                // Convertimos el valor seleccionado a entero (ID de la aplicación)
                int idApp = Integer.parseInt(seleccionado.toString());
                int idPerfil = Integer.parseInt(codigoPerfil.getText());

                // Consultar a la BD los permisos específicos para este Perfil y App
                AsignacionAplicacionPerfilDAO dao = new AsignacionAplicacionPerfilDAO();
                clsAsignacionAplicacionPerfil actual = dao.obtenerRegistroEspecifico(idApp, idPerfil);

                if (actual != null) {
                    // Usamos .trim() para limpiar espacios y .equalsIgnoreCase para comparar contenido
                    jRadioButton1.setSelected("S".equalsIgnoreCase(actual.getAPLPins().trim())); // Insertar
                    jRadioButton2.setSelected("S".equalsIgnoreCase(actual.getAPLPsel().trim())); // Seleccionar
                    jRadioButton3.setSelected("S".equalsIgnoreCase(actual.getAPLPupd().trim())); // Actualizar
                    jRadioButton4.setSelected("S".equalsIgnoreCase(actual.getAPLPdel().trim())); // Eliminar
                    jRadioButton5.setSelected("S".equalsIgnoreCase(actual.getAPLPrep().trim())); // Reportes
                    
                    appSelect.setText("App seleccionada: " + idApp);
                } else {
                    // Si el registro no existe en la tabla de asignación, limpiamos pero dejamos activo
                    limpiarPermisos(); 
                    activarPermisos();
                    appSelect.setText("Nueva asignación para App: " + idApp);
                }
            } catch (NumberFormatException e) {
                System.out.println("Error al convertir IDs: " + e.getMessage());
            }
        }
    }
    }//GEN-LAST:event_AppAsigValueChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> AppAsig;
    private javax.swing.JList<String> AppDis;
    private javax.swing.JLabel appSelect;
    private javax.swing.JButton btnObtener;
    private javax.swing.JButton btnPasarTodos;
    private javax.swing.JButton btnPasarUno;
    private javax.swing.JButton btnRegresarTodos;
    private javax.swing.JButton btnRegresarUno;
    private javax.swing.JTextField codigoPerfil;
    private javax.swing.JComboBox<String> comboPerfil;
    private javax.swing.JButton guardar;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
