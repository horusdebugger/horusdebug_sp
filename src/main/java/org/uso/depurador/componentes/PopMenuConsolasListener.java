package org.uso.depurador.componentes;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextArea;

import org.uso.depurador.main.Principal;

public class PopMenuConsolasListener extends MouseAdapter {
	
	private Principal principal;
	
	public PopMenuConsolasListener(Principal principal) {
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
	        PopMenuConsolas menu = new PopMenuConsolas(this.principal, (JTextArea)e.getComponent());
	        menu.show(e.getComponent(), e.getX(), e.getY());
	    }
}
