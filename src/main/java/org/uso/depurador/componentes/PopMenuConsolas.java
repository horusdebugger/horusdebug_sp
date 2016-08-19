package org.uso.depurador.componentes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;

import org.uso.depurador.main.Principal;
import org.uso.depurador.utlidades.Imprimir;

public class PopMenuConsolas extends JPopupMenu implements ActionListener {
	private JMenuItem limpiar;
	private JMenuItem guardar;
	private JTextArea consola;
	private Principal principal;
    public PopMenuConsolas(Principal principal, JTextArea consola) {
		this.limpiar = new JMenuItem("Limpiar");
		this.limpiar.setIcon(new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/draft.png")));
		this.guardar = new JMenuItem("Guardar");
		this.guardar.setIcon(new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/disk.png")));
		this.consola = consola;
		this.limpiar.addActionListener(this);
		this.guardar.addActionListener(this);
		this.add(this.limpiar);
		this.add(this.guardar);
		this.principal = principal;
    }
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.limpiar) {
			consola.setText("");
		} else if (e.getSource() == this.guardar) {
			Imprimir.imprimirConsola(principal.consola, "Inicio de guardado de texto.");
			JFileChooser archivo = new JFileChooser();
			int resultado = archivo.showSaveDialog(null);
			switch(resultado) {
			case JFileChooser.APPROVE_OPTION: {
				try {
					PrintWriter writer = new PrintWriter(archivo.getSelectedFile().getAbsolutePath(), "UTF-8");
					if(consola == principal.consola) {
						writer.println(principal.consola.getText());
					} else {
						writer.println(principal.consolaErrores.getText());
					}
					writer.close();
					Imprimir.imprimirConsola(principal.consola, "Archivo de texto guardado correctamente.");
				} catch(Exception ex) {
					Imprimir.imprimirConsolaError(principal.consolaErrores, ex.getMessage());
					principal.consolas.setSelectedTab(1);
				}
				break;
			}
			case JFileChooser.CANCEL_OPTION: {
				Imprimir.imprimirConsola(principal.consola, "Guardado de texto cancelado.");
				break;
			}
			default: {
				Imprimir.imprimirConsola(principal.consola, "Ventana de guardado de texto cerrada.");
				break;
			}
			}
		}
	}
}
