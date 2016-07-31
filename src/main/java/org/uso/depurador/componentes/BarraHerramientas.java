package org.uso.depurador.componentes;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;

import org.fife.ui.rtextarea.GutterIconInfo;
import org.uso.depurador.main.Main;

public class BarraHerramientas extends JToolBar {
	
	Editor editor;
	
	public BarraHerramientas(Editor editor, ScrollEditor scroll) {
		this.editor = editor;
		
		JButton abrir = new JButton();
		/*Border emptyBorder = BorderFactory.createEmptyBorder();
		abrir.setBorder(emptyBorder);*/
		abrir.setIcon(new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/folder.png")));
		
		JButton guardar = new JButton();
		guardar.setIcon(new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/disk.png")));
		
		JButton play = new JButton();
		play.setIcon(new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/resultset_next.png")));
		
		JButton siguiente = new JButton();
		siguiente.setIcon(new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/resultset_first.png")));
		
		JButton atras = new JButton();
		atras.setIcon(new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/resultset_last.png")));
		
		JButton buscar = new JButton();
		buscar.setIcon(new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/find.png")));
		
				
		guardar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				GutterIconInfo[] icons = scroll.getGutter().getBookmarks();
				for (int i = 0; i < icons.length; i++) {
					Icon icon = icons[i].getIcon();
					int offset = icons[i].getMarkedOffset();
					try {
						System.out.println(editor.getLineOfOffset(offset));
					} catch (BadLocationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
			}
		});
		
		abrir.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {

				PrintWriter writer = null;
				try {
					writer = new PrintWriter("temp", "UTF-8");
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				writer.print(BarraHerramientas.this.editor.getText());
				writer.close();
				
				Runtime rt = Runtime.getRuntime();
				String[] commands = {"asigna.exe", "temp"};
				Process proc = null;
				try {
					proc = rt.exec(commands);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				BufferedReader stdInput = new BufferedReader(new 
				     InputStreamReader(proc.getInputStream()));

				BufferedReader stdError = new BufferedReader(new 
				     InputStreamReader(proc.getErrorStream()));

				// read the output from the command
				System.out.println("Salida del programa:\n");
				try {
					String salida = null;
					String mensajes = "";
					while ((salida = stdInput.readLine()) != null) {
						mensajes += salida;
						System.out.println(salida);
					}
					if(!mensajes.equals("")) {
						JOptionPane.showMessageDialog(null, mensajes, "Info", JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// read any errors from the attempted command
				System.out.println("Errores del programa:\n");
				try {
					String err = null;
					String errores = "";
					while ((err = stdError.readLine()) != null) {
						errores += err;
						System.err.println(err);
					}
					if(!errores.equals("")) {
						JOptionPane.showMessageDialog(null, errores, "Errores", JOptionPane.ERROR_MESSAGE);
						Main.Tools.setSelectedTab(1);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});

		this.add(abrir);
		this.add(guardar);
		this.addSeparator();

		this.add(play);
		this.add(siguiente);
		this.add(atras);
		
		this.addSeparator();
		
		this.add(buscar);
	}
}
