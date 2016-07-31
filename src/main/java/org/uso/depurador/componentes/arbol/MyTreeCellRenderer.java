package org.uso.depurador.componentes.arbol;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

/**
 * @web http://www.jc-mouse.net/
 * @author Mouse
 */
public class MyTreeCellRenderer extends DefaultTreeCellRenderer {

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		// altura de cada nodo
		tree.setRowHeight(26);

		setOpaque(true);
		// color de texto
		setForeground(Color.black);

		if (selected) {
			if (((DefaultMutableTreeNode) value).getLevel() != 0) {
				setForeground(Color.BLUE);
				setFont(new Font("Helvetica", Font.BOLD, 12));
			}
		} else {
			setFont(new Font("Helvetica", Font.PLAIN, 12));
		}
		// -- Asigna iconos
		// si value es la raiz
		/*
		 * if ( tree.getModel().getRoot().equals( (DefaultMutableTreeNode) value
		 * ) ) { setIcon( ((BD)((DefaultMutableTreeNode)
		 * value).getUserObject()).getIcono() ); }
		 */
		if (((DefaultMutableTreeNode) value).getUserObject() instanceof BD) {
			setIcon(((BD) ((DefaultMutableTreeNode) value).getUserObject()).getIcono());
		} else if (((DefaultMutableTreeNode) value).getUserObject() instanceof BDTabla) {
			setIcon(((BDTabla) ((DefaultMutableTreeNode) value).getUserObject()).getIcono());
		} else if (((DefaultMutableTreeNode) value).getUserObject() instanceof BDProc) {
			setIcon(((BDProc) ((DefaultMutableTreeNode) value).getUserObject()).getIcono());
		} else if (((DefaultMutableTreeNode) value).getUserObject() instanceof BDServidor) {
			setIcon(((BDServidor) ((DefaultMutableTreeNode) value).getUserObject()).getIcono());
		}

		return this;
	}

}// end:MyTreeCellRenderer