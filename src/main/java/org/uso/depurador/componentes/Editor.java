package org.uso.depurador.componentes;

import java.awt.Color;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.CaretStyle;

import com.sun.javafx.jmx.HighlightRegion;

public class Editor extends RSyntaxTextArea {
	public Editor() {
		this.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
		this.setCodeFoldingEnabled(true);
		this.setAutoIndentEnabled(true);
		this.setAnimateBracketMatching(true);		
		this.setMarginLineEnabled(true);
		
	}
}
