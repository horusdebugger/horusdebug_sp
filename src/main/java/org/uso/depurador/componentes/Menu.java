package org.uso.depurador.componentes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.uso.depurador.main.Principal;

import net.infonode.docking.View;
import net.infonode.tabbedpanel.titledtab.TitledTab;

public class Menu extends JMenuBar implements ActionListener{
	
	/*MENU*/
	private JMenu archivo;
	private JMenu herramientas;
	private JMenu ayuda;
	
	/*OPCIONES DE ARCHIVO*/
	private JMenuItem abrirArchivo;
	private JMenuItem cerrarDepurador;
	
	
	/*Editor*/
	private Principal ventanaPrincipal;
	
	public Menu(Principal principal) {
		
		this.ventanaPrincipal = principal;
	
		this.archivo = new JMenu("Archivo");
		this.add(archivo);
		
		this.abrirArchivo = new JMenuItem("Abrir archivo");
		this.cerrarDepurador = new JMenuItem("Cerrar");
		this.cerrarDepurador.addActionListener(this);
		this.abrirArchivo.addActionListener(this);
		this.archivo.add(abrirArchivo);
		this.archivo.add(cerrarDepurador);
		
		
		this.herramientas = new JMenu("Herramientas");
		JMenu idioma = new JMenu("Idioma");
		JMenuItem espanol = new JMenuItem("Español");
		JMenuItem ingles = new JMenuItem("Inglés");
		idioma.add(espanol);
		idioma.add(ingles);
		herramientas.add(idioma);
		this.add(herramientas);
		
		this.add(new JMenu("Ayuda"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final JFileChooser fc = new JFileChooser();
		if(e.getSource() == this.abrirArchivo) {
			int returnVal = fc.showOpenDialog(Menu.this);

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
	            FileInputStream fis = null;
				try {
					fis = new FileInputStream(file);
					System.out.println("Total file size to read (in bytes) : "
							+ fis.available());
					String contenido = "";
					int content;
					while ((content = fis.read()) != -1) {
						contenido += (char) content;
					}
					
					ventanaPrincipal.editores.removeTab(ventanaPrincipal.editores.getTabAt(0));
					
					ventanaPrincipal.editor.setText(contenido);
					TitledTab pestanaTemp = new TitledTab(file.getName(), null, ventanaPrincipal.scrollEditor, null);
					ventanaPrincipal.editores.addTab(pestanaTemp);
					ventanaPrincipal.archivo = file;

					//ventanaPrincipal.editores.addTab(editorTemp);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				
	        } 
		} else if (e.getSource() == this.cerrarDepurador) {
			System.out.println("cerrar depurador");
		}
		
	}
}
