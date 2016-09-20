package org.uso.depurador;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.uso.depurador.componentes.Editor;
import org.uso.depurador.componentes.ScrollEditor;
import org.uso.depurador.main.Principal;

import com.mysql.fabric.xmlrpc.base.Array;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.sql.Statement;
import java.awt.event.ActionEvent;

public class CrearProcedimiento extends JFrame {

	private JPanel contentPane;
	private JTextField txtNombre;
	private JTextField txtParametro;
	private JTable table;
	private DefaultTableModel modelo;
	ArrayList<Parametro> parametros = new ArrayList<>();
	private ScrollEditor scrollEditor;
	private Editor editor;
	private Principal ventana;
	
	class Parametro {
		String nombre;
		String tipo;
		String modo;
	}

	public void llenarTabla() {
		this.modelo.setRowCount(0);
		for(int i =0; i<parametros.size(); i++) {
			this.modelo.addRow(new Object[]{parametros.get(i).nombre, parametros.get(i).tipo, parametros.get(i).modo});
		}
		this.table.setModel(modelo);
		this.modelo.fireTableDataChanged();
	}

	private String getParametros() {
		String cod = "";
		for(int i = 0; i<parametros.size(); i++) {
			if(i == 0) {
				cod += (parametros.get(i).modo + " " + parametros.get(i).nombre + " " + parametros.get(i).tipo);
			} else {
				cod += (", "+parametros.get(i).modo + " " + parametros.get(i).nombre + " " + parametros.get(i).tipo);
			}
		}
		return cod;
	}

	/**
	 * Create the frame.
	 */
	public CrearProcedimiento(Principal ventana) {
		this.ventana = ventana;
		setResizable(false);
		setTitle("Crear Procedimiento Almacenado");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 646, 637);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNombre = new JLabel("Nombre:");
		lblNombre.setBounds(10, 14, 66, 14);
		contentPane.add(lblNombre);
		
		txtNombre = new JTextField();
		txtNombre.setBounds(86, 11, 232, 20);
		contentPane.add(txtNombre);
		txtNombre.setColumns(10);
		
		JLabel lblParametro = new JLabel("Parametro:");
		lblParametro.setBounds(10, 60, 68, 14);
		contentPane.add(lblParametro);
		
		txtParametro = new JTextField();
		txtParametro.setBounds(89, 54, 116, 20);
		contentPane.add(txtParametro);
		txtParametro.setColumns(10);
		
		JLabel lblTipo = new JLabel("Tipo:");
		lblTipo.setBounds(228, 60, 33, 14);
		contentPane.add(lblTipo);
		
		JComboBox cmbTipo = new JComboBox();
		cmbTipo.setEditable(true);
		cmbTipo.setModel(new DefaultComboBoxModel(new String[] {"int", "double", "varchar()"}));
		cmbTipo.setBounds(271, 57, 96, 20);
		contentPane.add(cmbTipo);
		
		JLabel lblModo = new JLabel("Modo:");
		lblModo.setBounds(377, 60, 46, 14);
		contentPane.add(lblModo);
		
		JComboBox cmbModo = new JComboBox();
		cmbModo.setModel(new DefaultComboBoxModel(new String[] {"IN"}));
		cmbModo.setBounds(433, 57, 46, 20);
		contentPane.add(cmbModo);
		
		JButton btnBorrar = new JButton("");
		btnBorrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parametros.remove(table.getSelectedRow());
				llenarTabla();
			}
		});
		btnBorrar.setIcon(new ImageIcon(CrearProcedimiento.class.getResource("/org/uso/depurador/componentes/iconos/delete.png")));
		btnBorrar.setBounds(597, 56, 33, 23);
		contentPane.add(btnBorrar);
		
		JButton btnEditar = new JButton("");
		btnEditar.setIcon(new ImageIcon(CrearProcedimiento.class.getResource("/org/uso/depurador/componentes/iconos/pencil.png")));
		btnEditar.setBounds(564, 56, 33, 23);
		contentPane.add(btnEditar);
		
		JButton btnAgregar = new JButton("");
		btnAgregar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Parametro parametro = new Parametro();
				parametro.nombre = txtParametro.getText();
				parametro.tipo = cmbTipo.getSelectedItem().toString();
				parametro.modo = cmbModo.getSelectedItem().toString();
				parametros.add(parametro);
				llenarTabla();
				txtParametro.setText("");
				cmbTipo.setSelectedIndex(0);
				cmbModo.setSelectedIndex(0);
			}
		});
		btnAgregar.setIcon(new ImageIcon(CrearProcedimiento.class.getResource("/org/uso/depurador/componentes/iconos/add.png")));
		btnAgregar.setBounds(530, 56, 33, 23);
		contentPane.add(btnAgregar);
		
		JLabel lblCodigo = new JLabel("Codigo:");
		lblCodigo.setBounds(10, 195, 46, 14);
		contentPane.add(lblCodigo);
		
		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.setIcon(new ImageIcon(CrearProcedimiento.class.getResource("/org/uso/depurador/componentes/iconos/cancel.png")));
		btnCancelar.setBounds(514, 575, 116, 23);
		contentPane.add(btnCancelar);
		
		JButton btnGuardar = new JButton("Guardar");
		btnGuardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String codigo = "CREATE PROCEDURE ";
				codigo += txtNombre.getText() +" ";
				codigo += "("+getParametros()+")";
				codigo += "\n"+editor.getText();
				try {
					Statement sentence = ventana.conexion.getConexion().createStatement();
					sentence.executeUpdate(codigo);
					JOptionPane.showMessageDialog(CrearProcedimiento.this, "Procedimiento Almacenado creado CORRECTAMENTE.", "INFORMACION", JOptionPane.INFORMATION_MESSAGE);
					sentence.close();
					CrearProcedimiento.this.dispose();
					ventana.arbolBD.llenarArbol();
				} catch(Exception ex) {
					JOptionPane.showMessageDialog(CrearProcedimiento.this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnGuardar.setIcon(new ImageIcon(CrearProcedimiento.class.getResource("/org/uso/depurador/componentes/iconos/script_save.png")));
		btnGuardar.setBounds(391, 575, 116, 23);
		contentPane.add(btnGuardar);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 85, 620, 109);
		contentPane.add(scrollPane);
		
		table = new JTable();
		this.modelo = new DefaultTableModel(){
			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		this.modelo.setColumnIdentifiers(new Object[] {"Parametro", "Tipo", "Modo"});
		table.setModel(modelo);
		scrollPane.setViewportView(table);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(10, 220, 620, 344);
		this.editor = new Editor();
		this.editor.setText("BEGIN\n	\nEND");
		this.scrollEditor = new ScrollEditor(editor, true);
		panel.add(scrollEditor, BorderLayout.CENTER);
		contentPane.add(panel);
		
	}
}
