package org.uso.depurador.componentes;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.JTextArea;

import org.uso.depurador.main.Principal;

public class PopMenuTablaConsultaListener extends MouseAdapter {
private Principal principal;
	
	public PopMenuTablaConsultaListener(Principal principal) {
		this.principal = principal;
	}
	
	public void mousePressed(MouseEvent e){
	        if (e.isPopupTrigger())
	            doPop(e);
	    }

	    public void mouseReleased(MouseEvent e){
	        if (e.isPopupTrigger())
	            doPop(e);
	    }

	    private void doPop(MouseEvent e){
	        PopMenuTablaConsulta menu = new PopMenuTablaConsulta(this.principal, (JTable)e.getComponent());
	        menu.show(e.getComponent(), e.getX(), e.getY());
	    }
}
