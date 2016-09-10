package org.uso.depurador.componentes;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

public class BarraDeEstado extends JPanel {
	public JLabel posicion;
	public JLabel mensaje;
	
	public BarraDeEstado() {
		super();
		this.setBorder(new BevelBorder(BevelBorder.LOWERED));
		//statusBar.setBackground(Color.lightGray);
		this.setPreferredSize(new Dimension(100, 25));
		this.setLayout(new FlowLayout(FlowLayout.RIGHT));
		posicion = new JLabel("0:0");
		posicion.setText("Linea: " + 1 + " : " + "Columna: "  + 0);
		mensaje = new JLabel("Listo");
		this.add(mensaje);
		this.add(new JLabel(" | "));
		this.add(posicion);
	}	
}
