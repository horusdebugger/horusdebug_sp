package org.uso.depurador.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.print.Book;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;

import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.GutterIconInfo;
import org.uso.depurador.Depuracion;
import org.uso.depurador.componentes.BarraDeEstado;
import org.uso.depurador.componentes.BarraHerramientas;
import org.uso.depurador.componentes.Editor;
import org.uso.depurador.componentes.Menu;
import org.uso.depurador.componentes.ScrollEditor;
import org.uso.depurador.componentes.arbol.BDArbol;
import org.uso.depurador.conexion.Conexion;
import org.uso.depurador.utlidades.Utilidades;
import org.uso.depurador.componentes.Parametro;
import org.uso.depurador.componentes.PopMenuConsolasListener;
import org.uso.depurador.componentes.PopMenuTablaConsultaListener;

import com.sun.org.apache.bcel.internal.generic.DCONST;

import javafx.scene.control.Separator;
import javafx.scene.layout.Border;
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
	
	public Depuracion depurar;

	// Vetanas dock
	private View bdView;
	private View consolaView;
	public View consultaView;
	private View consolaErroresView;
	private View consolaVariables;
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
	public TabWindow consolas;
	public TitledTab pestanaDebug;
	public TitledTab pestanaEditor;
	// conexion
	public Conexion conexion;
	public ScrollEditor scrollEditor;
	public ScrollEditor scrollEditorDebug;
	public Editor editor;
	public Editor editorDebug;
	// archivo
	public File archivo = null;
	// control de archivo
	public boolean control = false;
	// procedimiento
	public String procedimiento = null;
	public String procedimiento_bd = null; 
	//parametros
	public List<Parametro> parametros = null;
	//tablas
	public JTable tablaConsultas = new JTable(new DefaultTableModel(){
		@Override
		public boolean isCellEditable(int row, int column) {
			// TODO Auto-generated method stub
			return false;
		}
	});
	// Barra de estado
	BarraDeEstado barraEstado = new BarraDeEstado();
	// Textareas de consolas
	public JTextArea consola = new JTextArea();
	public JTextArea consolaErrores = new JTextArea();
	// arbol
	public BDArbol arbolBD;
	// conexion
	public Principal(Conexion c) {
		this.conexion = c;
		this.arbolBD = new BDArbol(this);
		crearJMenuBar();
		crearJToolbar();
		crearDocks();
		crearVentana();
		crearBarradeEstado();
		/*try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    } catch (InstantiationException e) {
	        e.printStackTrace();
	    } catch (IllegalAccessException e) {
	        e.printStackTrace();
	    } catch (UnsupportedLookAndFeelException e) {
	        e.printStackTrace();
	    }*/
	}

	void crearDocks() {
		
		this.bdView = new View("Bases de datos", null, new JScrollPane(this.arbolBD));
		this.bdView.getWindowProperties().setCloseEnabled(false);
		this.bdView.getWindowProperties().setUndockEnabled(false);
		this.consola.setEditable(false);
		this.consola.addMouseListener(new PopMenuConsolasListener(this));
		this.consola.setFont(new Font("Lucida console", Font.PLAIN, 12));
		this.consolaView = new View("Consola", null, new JScrollPane(consola));
		this.consolaView.getWindowProperties().setCloseEnabled(false);
		//this.consolaView.getWindowProperties().setMinimizeEnabled(false);
		this.consolaView.getWindowProperties().setUndockEnabled(false);
		this.consolaErrores.setEditable(false);
		this.consolaErrores.setForeground(Color.RED);
		this.consolaErrores.addMouseListener(new PopMenuConsolasListener(this));
		this.consolaErrores.setFont(new Font("Lucida console", Font.PLAIN, 12));
		this.consolaErroresView = new View("Errores", null, new JScrollPane(consolaErrores));
		this.consolaErroresView.getWindowProperties().setCloseEnabled(false);

		//this.consolaErroresView.getWindowProperties().setMinimizeEnabled(false);
		this.consolaErroresView.getWindowProperties().setUndockEnabled(false);
		tablaConsultas.addMouseListener(new PopMenuTablaConsultaListener(this));
		this.consultaView = new View("Consulta", null, new JScrollPane(tablaConsultas));
		this.consultaView.getWindowProperties().setCloseEnabled(false);
		//this.consolaSQLView.getWindowProperties().setMinimizeEnabled(false);
		this.consultaView.getWindowProperties().setUndockEnabled(false);
		Utilidades util = new Utilidades();
		util.LeerArchivoXML();
		this.consolaVariables = new View("Variables", null, new JScrollPane(util.getTablaVariables()));
		this.editores = new TabbedPanel();
		this.editorView = new View("", null, editores);
		this.editorView.getWindowProperties().setUndockEnabled(false);
		this.editorView.getWindowProperties().setCloseEnabled(false);
		
		this.mapa.addView(0, bdView);
		this.mapa.addView(1, consolaView);
		this.mapa.addView(2, consolaErroresView);
		this.mapa.addView(3, consultaView);
		this.mapa.addView(4, consolaVariables);
		
		
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
		this.consolas.addTab(consultaView);
		this.consolas.addTab(consolaVariables);
		this.consolas.getWindowProperties().setCloseEnabled(false);
		this.consolas.getWindowProperties().setUndockEnabled(false);
		this.consolas.setSelectedTab(0);
		
		this.editor = new Editor();
		
		this.editorDebug = new Editor();
		this.scrollEditor = new ScrollEditor(editor, true);
	
		this.scrollEditorDebug = new ScrollEditor(editorDebug, true);
		
		this.pestanaEditor = new TitledTab("Nuevo", null, scrollEditor, null);
		this.editores.addTab(this.pestanaEditor);
		

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

			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				control = true;
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
	
	void crearBarradeEstado() {
		getContentPane().add(barraEstado, java.awt.BorderLayout.SOUTH);
	}

}
