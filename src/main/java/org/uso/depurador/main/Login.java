package org.uso.depurador.main;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.uso.depurador.conexion.Conexion;
import org.uso.depurador.utilerias.Configuracion;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.Label;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JButton;
import java.awt.FlowLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import jdk.nashorn.internal.runtime.regexp.joni.Config;

import javax.swing.ImageIcon;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ItemListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.awt.event.ItemEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField txtHost;
	private JTextField txtUsuario;
	private JPasswordField txtPass;
	private JTextField txtPort;
	private JLabel lblPuerto;
	private JLabel lblBaseDeDatos;
	private JButton btnCancelar;
	private JLabel lblNewLabel;
	private JLabel lblUsuario;
	private JLabel lblContrasea;
	private JButton btnConectar;
	private JButton btnProbar;
	private JComboBox txtDb;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Login() {
		
		

		setResizable(false);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 476, 341);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblHost = new JLabel("Host:");
		lblHost.setBounds(10, 116, 90, 14);
		contentPane.add(lblHost);

		txtHost = new JTextField();
		txtHost.setText("localhost");
		txtHost.setBounds(110, 113, 346, 20);
		contentPane.add(txtHost);
		txtHost.setColumns(10);

		lblUsuario = new JLabel("Usuario:");
		lblUsuario.setBounds(10, 141, 90, 14);
		contentPane.add(lblUsuario);

		txtUsuario = new JTextField();
		txtUsuario.setText("root");
		txtUsuario.setBounds(110, 138, 346, 20);
		contentPane.add(txtUsuario);
		txtUsuario.setColumns(10);

		lblContrasea = new JLabel("Contrase\u00F1a:");
		lblContrasea.setBounds(10, 166, 90, 14);
		contentPane.add(lblContrasea);

		txtPass = new JPasswordField();
		txtPass.setEchoChar('*');
		txtPass.setBounds(110, 163, 346, 20);
		contentPane.add(txtPass);

		lblPuerto = new JLabel("Puerto:");
		lblPuerto.setBounds(10, 191, 90, 14);
		contentPane.add(lblPuerto);

		txtPort = new JTextField();
		txtPort.setText("3306");
		txtPort.setBounds(110, 188, 76, 20);
		contentPane.add(txtPort);
		txtPort.setColumns(10);

		lblBaseDeDatos = new JLabel("Base de Datos:");
		lblBaseDeDatos.setBounds(10, 216, 90, 14);
		contentPane.add(lblBaseDeDatos);

		txtDb = new JComboBox();
		txtDb.getEditor().getEditorComponent().addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {

				String host = txtHost.getText();
				String usuario = txtUsuario.getText();
				String pass = String.valueOf(txtPass.getPassword());
				String puerto = txtPort.getText();
				txtDb.removeAllItems();
				txtDb.addItem("");
				try {
					Conexion c = new Conexion(txtHost.getText(), txtUsuario.getText(),
							String.valueOf(txtPass.getPassword()), txtPort.getText(), "");
					c.conectar();
					Statement sm = (Statement) c.getConexion().createStatement();
					ResultSet rs = sm.executeQuery("show databases");

					while (rs.next()) {
						txtDb.addItem(rs.getString("Database"));
					}
					c.cerrarConexion();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		txtDb.addItem("");
		txtDb.setEditable(true);
		txtDb.setBounds(110, 213, 346, 20);
		contentPane.add(txtDb);

		btnCancelar = new JButton("Cancelar");
		btnCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		btnCancelar.setIcon(new ImageIcon(Login.class.getResource("/org/uso/depurador/componentes/iconos/cancel.png")));
		btnCancelar.setBounds(336, 272, 120, 30);
		contentPane.add(btnCancelar);

		btnConectar = new JButton("Conectar");
		btnConectar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String txtDB = txtDb.getSelectedItem().toString();
				Conexion c = new Conexion(txtHost.getText(), txtUsuario.getText(),
						String.valueOf(txtPass.getPassword()), txtPort.getText(), txtDB);
				c.conectar();
				if (c.getConexion() != null) {
					Principal main = new Principal(c);
					dispose();
				} else {
					JOptionPane.showMessageDialog(Login.this, "No se pudo conectar a MySQL. :(", "ERROR!",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnConectar
				.setIcon(new ImageIcon(Login.class.getResource("/org/uso/depurador/componentes/iconos/connect.png")));
		btnConectar.setBounds(206, 272, 120, 30);
		contentPane.add(btnConectar);

		btnProbar = new JButton("Probar");
		btnProbar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String txtDB = txtDb.getSelectedItem().toString();
				Conexion c = new Conexion(txtHost.getText(), txtUsuario.getText(),
						String.valueOf(txtPass.getPassword()), txtPort.getText(), txtDB);
				if (c.probarConexion()) {

					JOptionPane.showMessageDialog(Login.this, "Conexión exitosa!.", "EXITO!",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(Login.this, "No se pudo conectar a MySQL. :(", "ERROR!",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnProbar.setIcon(new ImageIcon(Login.class.getResource("/org/uso/depurador/componentes/iconos/monitor.png")));
		btnProbar.setBounds(10, 272, 120, 30);
		contentPane.add(btnProbar);

		JPanel panel = new JPanel();
		panel.setBackground(new Color(204, 204, 204));
		panel.setBounds(0, 0, 476, 102);
		contentPane.add(panel);
		panel.setLayout(null);

		lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(
				new ImageIcon(Login.class.getResource("/org/uso/depurador/componentes/iconos/logo_horus.png")));
		lblNewLabel.setBounds(162, 11, 155, 80);
		panel.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Configuracion c = new Configuracion();
				c.setPropiedad("LANG", "org.uso.depurador.lang.LANG_es");
				setBundles(c.getPropiedad("LANG"));
			}
		});
		lblNewLabel_1.setIcon(new ImageIcon(Login.class.getResource("/org/uso/depurador/componentes/iconos/es.png")));
		lblNewLabel_1.setBounds(414, 77, 16, 14);
		panel.add(lblNewLabel_1);

		JLabel label = new JLabel("");
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Configuracion c = new Configuracion();
				c.setPropiedad("LANG", "org.uso.depurador.lang.LANG_en");
				setBundles(c.getPropiedad("LANG"));
			}
		});
		label.setIcon(new ImageIcon(Login.class.getResource("/org/uso/depurador/componentes/iconos/us.png")));
		label.setBounds(440, 77, 16, 14);
		panel.add(label);
		
		Configuracion c = new Configuracion();
		if(c.getPropiedad("LANG")==null) {
			c.setPropiedad("LANG", "org.uso.depurador.lang.LANG");
			setBundles(c.getPropiedad("LANG"));
		} else {
			String idioma = c.getPropiedad("LANG");
			setBundles(idioma);
		}

	}

	private void setBundles(String propertyFile) {
		ResourceBundle rb = ResourceBundle.getBundle(propertyFile);

		String keyBundle = this.getClass().getName();

		this.setTitle(rb.getString("LANG.this"));
		this.lblPuerto.setText(rb.getString("LANG.lblPuerto"));
		this.lblBaseDeDatos.setText(rb.getString("LANG.lblBaseDeDatos"));
		this.btnCancelar.setText(rb.getString("LANG.btnCancelar"));
		this.btnConectar.setText(rb.getString("LANG.btnConectar"));
		this.btnProbar.setText(rb.getString("LANG.btnProbar"));
		this.lblUsuario.setText(rb.getString("LANG.lblUsuario"));
		this.lblContrasea.setText(rb.getString("LANG.lblContrasea"));
		this.txtHost.setToolTipText(rb.getString("LANG.tipDigiteHost"));

	}
}
