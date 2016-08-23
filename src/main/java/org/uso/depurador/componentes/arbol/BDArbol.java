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

	ArrayList<BD> bds = new ArrayList<>();

	Principal ventana;

	public BDArbol(Principal principal) { 
		this.ventana = principal;
		/*
			try {
				//DatabaseMetaData meta = (DatabaseMetaData) this.conn.getMetaData();
				System.out.println(conn.getCatalog());
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		*/

		this.setCellRenderer(new MyTreeCellRenderer());

		llenarArbol();
		/*this.setSelectionRow(2);
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) BDArbol.this.getLastSelectedPathComponent();
		((DefaultTreeModel) BDArbol.this.getModel()).nodeChanged(node);*/

		this.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) BDArbol.this.getLastSelectedPathComponent();
				((DefaultTreeModel) BDArbol.this.getModel()).nodeChanged(node);
				if (e.getClickCount() == 2) {
					if (node == null)
						return;
					Object padre = node.getParent().toString();
					try {
					Statement stmt = ventana.conexion.getConexion().createStatement();
		            ResultSet rs;
		 
		            rs = stmt.executeQuery("SHOW CREATE PROCEDURE `"+padre.toString()+"`.`"+node.getUserObject().toString()+"`");
		            String contenido = null;
		            JButton boton = new JButton("<html><b>x</b></html>");
	                boton.setOpaque(false);
	                boton.setContentAreaFilled(false);
	                boton.setBorderPainted(false);
	                boton.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent arg0) {
							if(ventana.editores.getTabCount()>1) {
								ventana.editores.removeTab(ventana.pestanaDebug);
							}
						}
					});
		            while ( rs.next() ) {
		                contenido = rs.getString("Create Procedure");
		            }
		            
		            if(ventana.editores.getTabCount()>1) {
		            	ventana.editores.removeTab(ventana.pestanaDebug);
		            }
		            ventana.pestanaDebug = new TitledTab("Procedimiento: "+node.getUserObject().toString(), null, ventana.scrollEditorDebug, boton);
	                ventana.editorDebug.setText(contenido);
	                ventana.editorDebug.setEditable(false);
	                ventana.editores.addTab(ventana.pestanaDebug);
		            
		            ventana.procedimiento = "`"+padre.toString()+"`.`"+node.getUserObject().toString()+"`";
		            ventana.procedimiento_bd = node.getUserObject().toString();
		            ventana.editores.setSelectedTab(ventana.pestanaDebug);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});

	}

	public void llenarArbol() {

		bds.clear();

		BDServidor root = new BDServidor();
		root.setNombre("mysql");
		root.setIcono(servidorIcon);
		try {
			Statement stmt = ventana.conexion.getConexion().createStatement();
			ResultSet rs = stmt.executeQuery("show databases");

			while (rs.next()) {
				BD bd = new BD();
				bd.setNombre(rs.getString("Database"));
				bds.add(bd);
			}

			rs.close();
			stmt.close();

			for (BD bd : bds) {
				/*
				 * Statement stmt2 = this.conn.createStatement(); ResultSet rs2
				 * = stmt2.executeQuery(
				 * "SELECT TABLE_NAME FROM information_schema.TABLES " +
				 * "WHERE TABLE_SCHEMA = '" + bd.getNombre() + "'");
				 * ArrayList<BDTabla> tablas = new ArrayList<>(); while
				 * (rs2.next()) { BDTabla tabla = new BDTabla();
				 * tabla.setPadre(bd);
				 * tabla.setNombre(rs2.getString("TABLE_NAME"));
				 * tablas.add(tabla); }
				 */
				Statement stmt3 = ventana.conexion.getConexion().createStatement();
				ResultSet rs3 = stmt3.executeQuery("SHOW PROCEDURE STATUS WHERE db = '" + bd.getNombre() + "'");
				ArrayList<BDProc> procs = new ArrayList<>();
				while (rs3.next()) {
					BDProc proc = new BDProc();
					proc.setPadre(bd);
					proc.setNombre(rs3.getString("Name"));
					procs.add(proc);
				}

				// bd.setTablas(tablas);
				bd.setProcedimientos(procs);
				// rs2.close();
				// stmt2.close();
				rs3.close();
				stmt3.close();
			}

			root.setBds(bds);

			DefaultMutableTreeNode raiz = new DefaultMutableTreeNode(root);

			for (BD db : root.getBds()) {
				DefaultMutableTreeNode nodo = new DefaultMutableTreeNode(db);
				for (BDTabla tabla : db.getTablas()) {
					DefaultMutableTreeNode nodo2 = new DefaultMutableTreeNode(tabla);
					nodo.add(nodo2);
				}

				for (BDProc proc : db.getProcedimientos()) {
					DefaultMutableTreeNode nodo2 = new DefaultMutableTreeNode(proc);
					nodo.add(nodo2);

				}

				raiz.add(nodo);
			}

			DefaultTreeModel modelo = new DefaultTreeModel(raiz);
			this.setModel(modelo);
		} catch (Exception ex) {
			Imprimir.imprimirConsolaError(ventana.consolaErrores, ex.getMessage());
		}
	}

}
