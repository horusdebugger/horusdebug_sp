package org.uso.depurador;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.text.BadLocationException;

import org.uso.depurador.main.Principal;

public class Depuracion {
	
	public Principal ventana;
	
	public Depuracion(Principal ventana) {
		// TODO Auto-generated constructor stub
		this.ventana = ventana;
		moverDespuesBegin();
	}
	
	public void moverDespuesBegin() {
		String codigo = ventana.editorDebug.getText();
		int posicionBegin = codigo.indexOf("END")+3;
		//ventana.editorDebug.setCaretPosition(posicionBegin);
		try {
			int linea = ventana.editorDebug.getLineOfOffset(posicionBegin);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
