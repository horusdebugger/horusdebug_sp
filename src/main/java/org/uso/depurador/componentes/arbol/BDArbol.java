package org.uso.depurador.componentes.arbol;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.uso.depurador.componentes.ScrollEditor;
import org.uso.depurador.conexion.Conexion;
import org.uso.depurador.main.Principal;
import org.uso.depurador.utlidades.Imprimir;

import com.mysql.jdbc.DatabaseMetaData;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import net.infonode.tabbedpanel.TabbedPanel;
import net.infonode.tabbedpanel.titledtab.TitledTab;

public class BDArbol extends JTree {

	private ImageIcon servidorIcon = new ImageIcon(
			getClass().getResource("/org/uso/depurador/componentes/iconos/server_database.png"));
	private ImageIcon bdIcon = new ImageIcon(
			getClass().getResource("/org/uso/depurador/componentes/iconos/database.png"));
	private ImageIcon tablaIcon = new ImageIcon(
			getClass().getResource("/org/uso/depurador/componentes/iconos/table.png"));
	private ImageIcon procIcon = new ImageIcon(
			getClass().getResource("/org/uso/depurador/componentes/iconos/table_gear.png"));
	private ImageIcon tablaRootIcon = new ImageIcon(
			getClass().getResource("/org/uso/depurador/componentes/iconos/folder_table.png"));
	private ImageIcon tablaCampoIcon = new ImageIcon(
			getClass().getResource("/org/uso/depurador/componentes/iconos/bullet_black.png"));

	ArrayList<BD> bds = new ArrayList<>();

	Principal ventana;

	public BDArbol(Principal principal) {
		this.ventana = principal;
		/*
		 * try { //DatabaseMetaData meta = (DatabaseMetaData)
		 * this.conn.getMetaData(); System.out.println(conn.getCatalog()); }
		 * catch (SQLException e1) { // TODO Auto-generated catch block
		 * e1.printStackTrace(); }
		 */

		this.setCellRenderer(new MyTreeCellRenderer());

		llenarArbol();
		/*
		 * this.setSelectionRow(2); DefaultMutableTreeNode node =
		 * (DefaultMutableTreeNode) BDArbol.this.getLastSelectedPathComponent();
		 * ((DefaultTreeModel) BDArbol.this.getModel()).nodeChanged(node);
		 */

		this.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) BDArbol.this.getLastSelectedPathComponent();
				((DefaultTreeModel) BDArbol.this.getModel()).nodeChanged(node);
				if (BDArbol.this.isEnabled()) {
					if (e.getClickCount() == 2) {
						if (node == null)
							return;
						Object padre = node.getParent().toString();
						try {
							Statement stmt = ventana.conexion.getConexion().createStatement();
							ResultSet rs;

							rs = stmt.executeQuery("SHOW CREATE PROCEDURE `" + padre.toString() + "`.`"
									+ node.getUserObject().toString() + "`");
							String contenido = null;
							JButton boton = new JButton("<html><b>x</b></html>");
							boton.setOpaque(false);
							boton.setContentAreaFilled(false);
							boton.setBorderPainted(false);
							boton.addActionListener(new ActionListener() {

								@Override
								public void actionPerformed(ActionEvent arg0) {

									if (ventana.barra.detener.isEnabled()) {
										int opcion = JOptionPane.showConfirmDialog(ventana,
												"Desea cancelar la depuración en proceso?", "ADVERTENCIA",
												JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
										if (opcion == JOptionPane.OK_OPTION) {
											ventana.editores.removeTab(ventana.pestanaDebug);
											ventana.editorDebug
													.setCurrentLineHighlightColor(Color.getHSBColor(255, 255, 170));
										}
									} else {
										ventana.editores.removeTab(ventana.pestanaDebug);
										ventana.editorDebug
												.setCurrentLineHighlightColor(Color.getHSBColor(255, 255, 170));
									}
								}
							});
							while (rs.next()) {
								contenido = rs.getString("Create Procedure");
							}

							if (ventana.editores.getTabCount() > 1) {
								ventana.editores.removeTab(ventana.pestanaDebug);
							}
							ventana.pestanaDebug = new TitledTab("Procedimiento: " + node.getUserObject().toString(),
									null, ventana.scrollEditorDebug, boton);
							ventana.editorDebug.setText(contenido);
							// ventana.editorDebug.setEditable(false);
							ventana.editores.addTab(ventana.pestanaDebug);

							ventana.procedimiento = "`" + padre.toString() + "`.`" + node.getUserObject().toString()
									+ "`";
							ventana.procedimiento_bd = node.getUserObject().toString();
							ventana.editores.setSelectedTab(ventana.pestanaDebug);
							ventana.barra.play.setEnabled(true);
							ventana.barra.play_pausado.setEnabled(true);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		});

	}

	public void llenarArbol() {

		bds.clear();
		
		// servidor de base de datos
		BDServidor root = new BDServidor();
		root.setNombre("mysql");
		root.setIcono(servidorIcon);
		
		try {
			Statement stmtBaseDeDatos = ventana.conexion.getConexion().createStatement();
			ResultSet rs = stmtBaseDeDatos.executeQuery("show databases");
			
			
			while (rs.next()) {
				BD bd = new BD();
				bd.setNombre(rs.getString("Database"));
				bds.add(bd);
			}

			rs.close();
			stmtBaseDeDatos.close();

			for (BD bd : bds) {

				Statement stmtTablas = ventana.conexion.getConexion().createStatement();
				ResultSet rsTablas = stmtTablas.executeQuery("SELECT TABLE_NAME FROM information_schema.TABLES "
						+ "WHERE TABLE_SCHEMA = '" + bd.getNombre() + "'");
				ArrayList<BDTabla> tablas = new ArrayList<>();
				BDRootTabla folder_tablas = new BDRootTabla();
				folder_tablas.setNombre("Tablas");
				folder_tablas.setIcono(tablaRootIcon);
				while (rsTablas.next()) {
					BDTabla tabla = new BDTabla();
					tabla.setPadre(bd);
					tabla.setNombre(rsTablas.getString("TABLE_NAME"));
					tablas.add(tabla);
				}
				folder_tablas.setTablas(tablas);

				Statement stmtProcedimientos = ventana.conexion.getConexion().createStatement();
				ResultSet rsProcedimientos = stmtProcedimientos.executeQuery("SHOW PROCEDURE STATUS WHERE db = '" + bd.getNombre() + "'");
				ArrayList<BDProc> procs = new ArrayList<>();
				while (rsProcedimientos.next()) {
					BDProc proc = new BDProc();
					proc.setPadre(bd);
					proc.setNombre(rsProcedimientos.getString("Name"));
					procs.add(proc);
				}
				System.out.println("A punto de entrar en llenado de campos...");
				for (int i = 0; i < folder_tablas.getTablas().size(); i++) {
					Statement stmtCampos = ventana.conexion.getConexion().createStatement();
					ResultSet rsCampos = stmtCampos.executeQuery(
							"SELECT `COLUMN_NAME`  FROM `INFORMATION_SCHEMA`.`COLUMNS` WHERE `TABLE_SCHEMA`='"
									+ bd.getNombre() + "' AND `TABLE_NAME`='" + folder_tablas.getTablas().get(i).getNombre() + "';");
					
					ArrayList<BDTablaCampo> listaCampos = new ArrayList<>();
					System.out.println("SELECT `COLUMN_NAME`  FROM `INFORMATION_SCHEMA`.`COLUMNS` WHERE `TABLE_SCHEMA`='"
									+ bd.getNombre() + "' AND `TABLE_NAME`='" + folder_tablas.getTablas().get(i).getNombre() + "'");
					while (rsCampos.next()) {
						BDTablaCampo campo = new BDTablaCampo();
						campo.setNombre(rsCampos.getString("COLUMN_NAME"));
						campo.setIcono(tablaCampoIcon);
						listaCampos.add(campo);
					}
					folder_tablas.getTablas().get(i).setCampos(listaCampos);
				}

				// bd.setTablas(tablas);
				bd.setProcedimientos(procs);
				bd.setFolder_tabla(folder_tablas);
				rsTablas.close();
				stmtTablas.close();
				rsProcedimientos.close();
				stmtProcedimientos.close();
			}

			root.setBds(bds);

			DefaultMutableTreeNode raiz = new DefaultMutableTreeNode(root);

			for (BD db : root.getBds()) {
				DefaultMutableTreeNode nodo = new DefaultMutableTreeNode(db);

				for (BDProc proc : db.getProcedimientos()) {
					DefaultMutableTreeNode nodo2 = new DefaultMutableTreeNode(proc);
					nodo.add(nodo2);
				}
				BDRootTabla folder_tabla = db.getFolder_tabla();
				DefaultMutableTreeNode nodo3 = new DefaultMutableTreeNode(folder_tabla);

				for (BDTabla tabla : folder_tabla.getTablas()) {
					DefaultMutableTreeNode nodo4 = new DefaultMutableTreeNode(tabla);
					for(BDTablaCampo campo : tabla.getCampos()) {
						DefaultMutableTreeNode nodo5 = new DefaultMutableTreeNode(campo);
						nodo4.add(nodo5);
					}
					nodo3.add(nodo4);
				}

				nodo.add(nodo3);
				raiz.add(nodo);
			}

			DefaultTreeModel modelo = new DefaultTreeModel(raiz);
			this.setModel(modelo);
		} catch (Exception ex) {
			Imprimir.imprimirConsolaError(ventana.consolaErrores, ex.getMessage());
		}
	}

}
