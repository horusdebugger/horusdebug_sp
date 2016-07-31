package org.uso.depurador.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.ScrollPane;
import java.awt.print.Book;
import java.sql.Connection;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.GutterIconInfo;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.uso.depurador.componentes.BarraHerramientas;
import org.uso.depurador.componentes.Editor;
import org.uso.depurador.componentes.Menu;
import org.uso.depurador.componentes.ScrollEditor;
import org.uso.depurador.componentes.arbol.BDArbol;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.FloatingWindow;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.WindowBar;
import net.infonode.docking.mouse.DockingWindowActionMouseButtonListener;
import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;
import net.infonode.util.Direction;

public class Main extends JFrame {
	
	private Connection conn;

	// ventana ra�z
	private RootWindow ventanaRaiz;

	// vistas estaticas
	private View[] vistas = new View[6];

	// contienen a las vistas
	private ViewMap vistasMap = new ViewMap();

	// tema de las ventanas
	private DockingWindowsTheme tema = new ShapedGradientDockingTheme();

	// propiedades del tema de las ventas
	private RootWindowProperties propiedades = new RootWindowProperties();

	// donde los layouts son almacenados
	private byte[][] layouts = new byte[3][];
	
	public Editor editor;
	public ScrollEditor scrollPane;
	
	public BDArbol arbol;

	private RTextScrollPane getEditor() {
		this.editor = new Editor();
		this.scrollPane = new ScrollEditor(editor, true);	
		
		//GutterIconInfo array[] = scrollPane.getGutter().getBookmarks();
		
		return scrollPane;
	}
	
	public Main(Connection cn) {
		this.conn = cn;
		this.arbol = new BDArbol(conn);
		this.setTitle("Depurador HORUS-IDE");
		crearVentanaRaiz();
		setDefaultLayout();
		mostrarVentana();
		agregarMenu();
		agregarBarraHerramientas();
	}
	

	public void agregarMenu() {
		Menu menu = new Menu();
		this.setJMenuBar(menu);
	}

	public void agregarBarraHerramientas() {
		BarraHerramientas barra = new BarraHerramientas(editor, scrollPane);
		this.getContentPane().add(barra, BorderLayout.NORTH);
		barra.setFloatable(false);
	}
	
	
	

	private void crearVentanaRaiz() {
		vistas[0] = new View("Bases de Datos", null, new JScrollPane(arbol));
		vistas[0].getWindowProperties().setCloseEnabled(false);
		vistas[0].getWindowProperties().setUndockEnabled(false);
		vistasMap.addView(0, vistas[0]);

		vistas[1] = new View("Procedimientos", null, new JScrollPane(new JTextArea("Procedimientos")));
		vistas[1].getWindowProperties().setCloseEnabled(false);
		vistas[1].getWindowProperties().setUndockEnabled(false);
		vistasMap.addView(1, vistas[1]);

		vistas[2] = new View("Editor", null, getEditor());
		vistas[2].getWindowProperties().setCloseEnabled(false);
		vistas[2].getWindowProperties().setUndockEnabled(false);
		vistasMap.addView(2, vistas[2]);

		vistas[3] = new View("Consola", null, new JScrollPane(new JTextArea("Consola")));
		vistas[3].getWindowProperties().setCloseEnabled(false);
		vistas[3].getWindowProperties().setUndockEnabled(false);
		vistasMap.addView(3, vistas[3]);
		
		vistas[4] = new View("Errores", null, new JScrollPane(new JTextArea("Errores")));
		vistas[4].getWindowProperties().setCloseEnabled(false);
		vistas[4].getWindowProperties().setUndockEnabled(false);
		vistasMap.addView(4, vistas[4]);
		
		vistas[5] = new View("Variables", null, new JScrollPane(new JTextArea("Variables")));
		vistas[5].getWindowProperties().setCloseEnabled(false);
		vistas[5].getWindowProperties().setUndockEnabled(false);
		vistasMap.addView(5, vistas[5]);
		
		ventanaRaiz = DockingUtil.createRootWindow(vistasMap, null, true);

		propiedades.addSuperObject(tema.getRootWindowProperties());

		ventanaRaiz.getRootWindowProperties().addSuperObject(propiedades);

		ventanaRaiz.getWindowBar(Direction.DOWN).setEnabled(true);

		ventanaRaiz.addListener(new DockingWindowAdapter() {
			public void windowAdded(DockingWindow addedToWindow, DockingWindow addedWindow) {
				// updateViews(addedWindow, true);

				// If the added window is a floating window, then update it
				if (addedWindow instanceof FloatingWindow) {
				}
				// updateFloatingWindow((FloatingWindow) addedWindow);
			}

			public void windowRemoved(DockingWindow removedFromWindow, DockingWindow removedWindow) {
				// updateViews(removedWindow, false);
			}

			public void windowClosing(DockingWindow window) throws OperationAbortedException {
				// Confirm close operation
				if (JOptionPane.showConfirmDialog(Main.this,
						"Really close window '" + window + "'?") != JOptionPane.YES_OPTION) {
				}
				// throw new OperationAbortedException("Window close was
				// aborted!");
			}

			public void windowDocking(DockingWindow window) throws OperationAbortedException {
				// Confirm dock operation
				if (JOptionPane.showConfirmDialog(Main.this,
						"Really dock window '" + window + "'?") != JOptionPane.YES_OPTION) {
				} // throw new OperationAbortedException("Window dock was
					// aborted!");
			}

			public void windowUndocking(DockingWindow window) throws OperationAbortedException {
				// Confirm undock operation
				if (JOptionPane.showConfirmDialog(Main.this,
						"Really undock window '" + window + "'?") != JOptionPane.YES_OPTION) {
				} // throw new OperationAbortedException("Window undock was
					// aborted!");
			}

		});

		ventanaRaiz.addTabMouseButtonListener(DockingWindowActionMouseButtonListener.MIDDLE_BUTTON_CLOSE_LISTENER);

	}

	private void mostrarVentana() {
		// frame.getContentPane().add(createToolBar(), BorderLayout.NORTH);
		this.getContentPane().add(ventanaRaiz, BorderLayout.CENTER);
		// frame.setJMenuBar(createMenuBar());
		this.setSize(new Dimension(800, 600));
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setVisible(true);
	}
	
	public static TabWindow Tools;

	private void setDefaultLayout() {
		
		

		Tools = new TabWindow(new View[]{vistas[3], vistas[4], vistas[5]});
		SplitWindow BdyProc = new SplitWindow(false, 0.3f, vistas[0], Tools);
		Tools.getWindowProperties().setCloseEnabled(false);
		Tools.getWindowProperties().setUndockEnabled(false);
		SplitWindow EditorConsola = new SplitWindow(false, 0.7f, vistas[2], Tools);
		SplitWindow principal = new SplitWindow(true, 0.2f, BdyProc, EditorConsola);
		
		ventanaRaiz.setWindow(principal);
		
		// tabWindow.add(vistas[0]);
		WindowBar windowBar = ventanaRaiz.getWindowBar(Direction.DOWN);

		while (windowBar.getChildWindowCount() > 0)
			windowBar.getChildWindow(0).close();

		// windowBar.addTab(vistas[3]);
		// windowBar.addTab(views[4]);
	}

}
