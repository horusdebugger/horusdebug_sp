package org.uso.depurador.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;

import org.uso.depurador.componentes.BarraHerramientas;
import org.uso.depurador.componentes.Editor;
import org.uso.depurador.componentes.Menu;
import org.uso.depurador.componentes.ScrollEditor;
import org.uso.depurador.componentes.arbol.BDArbol;
import org.uso.depurador.componentes.Parametro;

import com.sun.org.apache.bcel.internal.generic.DCONST;

import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.WindowBar;
import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.PropertiesUtil;
import net.infonode.docking.util.ViewMap;
import net.infonode.tabbedpanel.TabbedPanel;
import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.infonode.util.Direction;

public class Principal extends JFrame {

	// Vetanas dock
	private View bdView;
	private View consolaView;
	private View consolaSQLView;
	private View consolaErroresView;
	private View editorView;
	// ventana raiz
	private RootWindow ventanaRaiz;
	// mapa de ventanas dock
	private ViewMap mapa = new ViewMap();
	// tema de las ventanas
	private DockingWindowsTheme tema = new ShapedGradientDockingTheme();
	// propiedades del tema de las ventas
	private RootWindowProperties propiedades = new RootWindowProperties();
	// contenedor de pestanas
	public TabbedPanel editores;
	private TabWindow consolas;
	// conexion
	public Connection conexion;
	public ScrollEditor scrollEditor;
	public Editor editor;
	// archivo
	public File archivo = null;
	// control de archivo
	public boolean control = false;
	// procedimiento
	public String procedimiento = "";
	public String procedimiento_barra = ""; 
	//parametros
	public List<Parametro> parametros = null;
	public Principal(Connection c) {
		this.conexion = c;
		crearJMenuBar();
		crearJToolbar();
		crearDocks();
		crearVentana();
	}

	void crearDocks() {
		
		this.bdView = new View("Bases de datos", null, new JScrollPane(new BDArbol(conexion,this)));
		this.bdView.getWindowProperties().setCloseEnabled(false);
		this.bdView.getWindowProperties().setUndockEnabled(false);
		this.consolaView = new View("Consola", null, new JScrollPane(new JTextArea()));
		this.consolaView.getWindowProperties().setCloseEnabled(false);
		//this.consolaView.getWindowProperties().setMinimizeEnabled(false);
		this.consolaView.getWindowProperties().setUndockEnabled(false);
		this.consolaErroresView = new View("Errores", null, new JScrollPane(new JTextArea()));
		this.consolaErroresView.getWindowProperties().setCloseEnabled(false);
		//this.consolaErroresView.getWindowProperties().setMinimizeEnabled(false);
		this.consolaErroresView.getWindowProperties().setUndockEnabled(false);
		this.consolaSQLView = new View("Consola SQL", null, new JScrollPane(new JTextArea()));
		this.consolaSQLView.getWindowProperties().setCloseEnabled(false);
		//this.consolaSQLView.getWindowProperties().setMinimizeEnabled(false);
		this.consolaSQLView.getWindowProperties().setUndockEnabled(false);
		this.editores = new TabbedPanel();
		this.editorView = new View("", null, editores);
		this.editorView.getWindowProperties().setUndockEnabled(false);
		this.editorView.getWindowProperties().setCloseEnabled(false);
		
		this.mapa.addView(0, bdView);
		this.mapa.addView(1, consolaView);
		this.mapa.addView(2, consolaErroresView);
		this.mapa.addView(3, consolaSQLView);
		
		
		this.ventanaRaiz = DockingUtil.createRootWindow(mapa, true);
		this.tema = new ShapedGradientDockingTheme();
		this.ventanaRaiz.getRootWindowProperties().addSuperObject( tema.getRootWindowProperties());
		this.propiedades =  PropertiesUtil.createTitleBarStyleRootWindowProperties();
		
		// Enable title bar style
		this.ventanaRaiz.getRootWindowProperties().addSuperObject(propiedades);
		// Disable title bar style
		this.ventanaRaiz.getRootWindowProperties().removeSuperObject(propiedades);
		//propiedades.addSuperObject(tema.getRootWindowProperties());

		this.ventanaRaiz.getRootWindowProperties().addSuperObject(propiedades);
		this.ventanaRaiz.getWindowBar(Direction.DOWN).setEnabled(true);

		this.consolas = new TabWindow();
		this.consolas.addTab(consolaView);
		this.consolas.addTab(consolaErroresView);
		this.consolas.addTab(consolaSQLView);
		this.consolas.getWindowProperties().setCloseEnabled(false);
		this.consolas.getWindowProperties().setUndockEnabled(false);
		
		this.editor = new Editor();
		this.scrollEditor = new ScrollEditor(editor, true);

		TitledTab temp = new TitledTab("Nuevo", null, scrollEditor, null);
		this.editores.addTab(temp);
		

		SplitWindow editores_consolas = new SplitWindow(false, 0.6f, editorView, consolas);
		editores_consolas.getWindowProperties().setCloseEnabled(false);
		
		SplitWindow bd_editores_consolas = new SplitWindow(true, 0.2f, this.bdView, editores_consolas);
		bd_editores_consolas.getWindowProperties().setCloseEnabled(false);
		
		ventanaRaiz.setWindow(bd_editores_consolas);
		
		WindowBar windowBar = ventanaRaiz.getWindowBar(Direction.DOWN);
		windowBar.getWindowProperties().setCloseEnabled(false);

		while (windowBar.getChildWindowCount() > 0)
			windowBar.getChildWindow(0).close();
		
		this.editor.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				control = true;
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				control = true;
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	void crearVentana() {
		this.getContentPane().add(ventanaRaiz, BorderLayout.CENTER);
		this.setSize(new Dimension(800, 600));
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setVisible(true);
	}

	void crearJMenuBar() {
		this.setJMenuBar(new Menu(this));
	}

	void crearJToolbar() {
		BarraHerramientas barra = new BarraHerramientas(this);
		this.getContentPane().add(barra, BorderLayout.NORTH);
		barra.setFloatable(false);
	}

}
