package org.uso.depurador.componentes;

import javax.swing.ImageIcon;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

public class ScrollEditor extends RTextScrollPane {
	
	public ScrollEditor(RSyntaxTextArea editor, boolean lineNumber) {
		super(editor, lineNumber);
		this.setFoldIndicatorEnabled(true);
		this.setIconRowHeaderEnabled(true);
		this.getGutter().setBookmarkingEnabled(true);
		this.getGutter().setBookmarkIcon(new ImageIcon("breakpoint.png"));
	}

}
