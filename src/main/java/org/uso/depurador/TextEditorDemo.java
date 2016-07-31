package org.uso.depurador;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.*;
import javax.swing.text.BadLocationException;

import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;

public class TextEditorDemo extends JFrame implements ActionListener {

	RSyntaxTextArea textArea;
	
	public TextEditorDemo() {

//		RSyntaxTextArea editor = new RSyntaxTextArea(30, 100);
//		editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
//		editor.setCodeFoldingEnabled(true);
//		editor.setText("Hola, \n, como estas?");
		
		textArea = new RSyntaxTextArea(40,100);
		RTextScrollPane scrollPane = new RTextScrollPane(textArea, true);
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		textArea.setCodeFoldingEnabled(true);
		scrollPane.setFoldIndicatorEnabled(true);
		scrollPane.setIconRowHeaderEnabled(true);
		
		//Icon errorIcon = new ImageIcon(getClass().getResource("/cancel.png"));
		 

		//textArea.setText("line 1\nline 2\nline 3");
		//try {
			//GutterIconInfo tag = scrollPane.getGutter().addLineTrackingIcon(1, errorIcon);
			//GutterIconInfo tag = scrollPane.getGutter().setBookmarkingEnabled(true);
		//} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		//}

		this.add(scrollPane);

		JButton btnEjecutar = new JButton("Ejecutar");
		btnEjecutar.addActionListener(this);

		JToolBar toolbar = new JToolBar("Toolbar");
		toolbar.setFloatable(false);
		toolbar.add(btnEjecutar);
		this.add(toolbar, BorderLayout.NORTH);

		setTitle("Prueba Depurador IEEE");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);

	}

	public static void main(String[] args) {
		// Start all Swing applications on the EDT.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new TextEditorDemo().setVisible(true);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
			ejecutarEXE();

	}

	public void ejecutarEXE() {
		
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
		writer.print(textArea.getText());
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
				JOptionPane.showMessageDialog(this, mensajes, "Info", JOptionPane.INFORMATION_MESSAGE);
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
				JOptionPane.showMessageDialog(this, errores, "Errores", JOptionPane.ERROR_MESSAGE);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
