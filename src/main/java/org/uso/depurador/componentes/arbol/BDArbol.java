package org.uso.depurador.componentes.arbol;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import com.mysql.jdbc.DatabaseMetaData;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

public class BDArbol extends JTree {

	private ImageIcon servidorIcon = new ImageIcon(
			getClass().getResource("/org/uso/depurador/componentes/iconos/server_database.png"));
	private ImageIcon bdIcon = new ImageIcon(
			getClass().getResource("/org/uso/depurador/componentes/iconos/database.png"));
	private ImageIcon tablaIcon = new ImageIcon(
			getClass().getResource("/org/uso/depurador/componentes/iconos/table.png"));
	private ImageIcon procIcon = new ImageIcon(
			getClass().getResource("/org/uso/depurador/componentes/iconos/table_gear.png"));

	Connection conn;

	ArrayList<BD> bds = new ArrayList<>();

	public BDArbol(Connection conn) {

		this.conn = conn;

			try {
				DatabaseMetaData meta = (DatabaseMetaData) this.conn.getMetaData();
				System.out.println(conn.getCatalog());
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}


		this.setCellRenderer(new MyTreeCellRenderer());

		llenarArbol();
		/*this.setSelectionRow(2);
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) BDArbol.this.getLastSelectedPathComponent();
		((DefaultTreeModel) BDArbol.this.getModel()).nodeChanged(node);*/

		this.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) BDArbol.this.getLastSelectedPathComponent();
				((DefaultTreeModel) BDArbol.this.getModel()).nodeChanged(node);
				if (e.getClickCount() == 1) {
					if (node == null)
						return;
					Object nodeInfo = node.getUserObject();
					
					System.out.println(nodeInfo.toString());
					
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
			Statement stmt = this.conn.createStatement();
			ResultSet rs = stmt.executeQuery("show databases");

			while (rs.next()) {
				BD bd = new BD();
				bd.setNombre(rs.getString("Database"));
				bds.add(bd);
			}

			rs.close();
			stmt.close();

			for (BD bd : bds) {
				Statement stmt2 = this.conn.createStatement();
				ResultSet rs2 = stmt2.executeQuery("SELECT TABLE_NAME FROM information_schema.TABLES "
						+ "WHERE TABLE_SCHEMA = '" + bd.getNombre() + "'");
				ArrayList<BDTabla> tablas = new ArrayList<>();
				while (rs2.next()) {
					BDTabla tabla = new BDTabla();
					tabla.setPadre(bd);
					tabla.setNombre(rs2.getString("TABLE_NAME"));
					tablas.add(tabla);
				}
				Statement stmt3 = this.conn.createStatement();
				ResultSet rs3 = stmt3.executeQuery("SHOW PROCEDURE STATUS WHERE db = '" + bd.getNombre() + "'");
				ArrayList<BDProc> procs = new ArrayList<>();
				while (rs3.next()) {
					BDProc proc = new BDProc();
					proc.setPadre(bd);
					proc.setNombre(rs3.getString("Name"));
					procs.add(proc);
				}

				bd.setTablas(tablas);
				bd.setProcedimientos(procs);
				rs2.close();
				stmt2.close();
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

		}
	}

}
