package org.uso.depurador.componentes;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Menu extends JMenuBar{
	public Menu() {
	
		JMenu archivo = new JMenu("Archivo");
		this.add(archivo);
		
		JMenu herramientas = new JMenu("Herramientas");
		JMenu idioma = new JMenu("Idioma");
		JMenuItem espanol = new JMenuItem("Español");
		JMenuItem ingles = new JMenuItem("Inglés");
		idioma.add(espanol);
		idioma.add(ingles);
		herramientas.add(idioma);
		this.add(herramientas);
		
		this.add(new JMenu("Ayuda"));
	}
}
