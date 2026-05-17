/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo.modeloPlanilla;

import Controlador.controladorPlanilla.clsConceptosPlanillas;
import Modelo.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Meilyn Garcia
 */
public class ConceptosPlanillasDAO {

    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    // INSERTAR
    public boolean insertar(clsConceptosPlanillas cp) {

        String sql = "INSERT INTO conceptosplanilla "
                + "(Connombre, Contipo, Conporcentaje, Conmonto, Conaplica, Conestado) "
                + "VALUES (?,?,?,?,?,?)";

        try {

            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);

            ps.setString(1, cp.getNombre());
            ps.setString(2, cp.getTipo());
            ps.setDouble(3, cp.getPorcentaje());
            ps.setDouble(4, cp.getMonto());
            ps.setString(5, cp.getAplica());
            ps.setInt(6, cp.getEstado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println("Error INSERT conceptosplanilla: " + e.getMessage());
            return false;
        }
    }

    // LISTAR
    public List<clsConceptosPlanillas> listar() {

        List<clsConceptosPlanillas> lista = new ArrayList<>();

        String sql = "SELECT * FROM conceptosplanilla WHERE Conestado = 1";

        try {

            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {

                clsConceptosPlanillas cp = new clsConceptosPlanillas();

                cp.setCodigo(rs.getInt("Concodigo"));
                cp.setNombre(rs.getString("Connombre"));
                cp.setTipo(rs.getString("Contipo"));
                cp.setPorcentaje(rs.getDouble("Conporcentaje"));
                cp.setMonto(rs.getDouble("Conmonto"));
                cp.setAplica(rs.getString("Conaplica"));
                cp.setEstado(rs.getInt("Conestado"));

                lista.add(cp);
            }

        } catch (SQLException e) {

            System.out.println("Error LISTAR conceptosplanilla: " + e.getMessage());
        }

        return lista;
    }

    // BUSCAR
    public clsConceptosPlanillas buscar(int codigo) {

        String sql = "SELECT * FROM conceptosplanilla WHERE Concodigo = ?";

        try {

            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);

            ps.setInt(1, codigo);

            rs = ps.executeQuery();

            if (rs.next()) {

                clsConceptosPlanillas cp = new clsConceptosPlanillas();

                cp.setCodigo(rs.getInt("Concodigo"));
                cp.setNombre(rs.getString("Connombre"));
                cp.setTipo(rs.getString("Contipo"));
                cp.setPorcentaje(rs.getDouble("Conporcentaje"));
                cp.setMonto(rs.getDouble("Conmonto"));
                cp.setAplica(rs.getString("Conaplica"));
                cp.setEstado(rs.getInt("Conestado"));

                return cp;
            }

        } catch (SQLException e) {

            System.out.println("Error BUSCAR conceptosplanilla: " + e.getMessage());
        }

        return null;
    }

    // MODIFICAR
    public boolean modificar(clsConceptosPlanillas cp) {

        String sql = "UPDATE conceptosplanilla SET "
                + "Connombre = ?, "
                + "Contipo = ?, "
                + "Conporcentaje = ?, "
                + "Conmonto = ?, "
                + "Conaplica = ?, "
                + "Conestado = ? "
                + "WHERE Concodigo = ?";

        try {

            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);

            ps.setString(1, cp.getNombre());
            ps.setString(2, cp.getTipo());
            ps.setDouble(3, cp.getPorcentaje());
            ps.setDouble(4, cp.getMonto());
            ps.setString(5, cp.getAplica());
            ps.setInt(6, cp.getEstado());
            ps.setInt(7, cp.getCodigo());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println("Error UPDATE conceptosplanilla: " + e.getMessage());
            return false;
        }
    }

    // ELIMINAR LOGICO
    public boolean eliminar(int codigo) {

        String sql = "UPDATE conceptosplanilla "
                + "SET Conestado = 0 "
                + "WHERE Concodigo = ?";

        try {

            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);

            ps.setInt(1, codigo);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println("Error DELETE conceptosplanilla: " + e.getMessage());
            return false;
        }
    }

    // CARGAR TABLA
    public DefaultTableModel cargarDatos() {

        String[] encabezados = {
            "Codigo",
            "Nombre",
            "Tipo",
            "Porcentaje",
            "Monto",
            "Aplica",
            "Estado"
        };

        DefaultTableModel modelo = new DefaultTableModel(null, encabezados);

        String sql = "SELECT * FROM conceptosplanilla WHERE Conestado = 1";

        try {

            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {

                Object[] fila = new Object[7];

                fila[0] = rs.getInt("Concodigo");
                fila[1] = rs.getString("Connombre");
                fila[2] = rs.getString("Contipo");
                fila[3] = rs.getDouble("Conporcentaje");
                fila[4] = rs.getDouble("Conmonto");
                fila[5] = rs.getString("Conaplica");
                fila[6] = rs.getInt("Conestado");

                modelo.addRow(fila);
            }

        } catch (SQLException e) {

            System.out.println("Error CARGAR TABLA conceptosplanilla: " + e.getMessage());
        }

        return modelo;
    }
}
