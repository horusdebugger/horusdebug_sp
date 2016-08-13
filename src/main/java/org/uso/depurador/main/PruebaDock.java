package org.uso.depurador.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.uso.depurador.componentes.Editor;
import org.uso.depurador.componentes.Menu;

import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.WindowBar;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;
import net.infonode.util.Direction;
import net.infonode.tabbedpanel.*;
import net.infonode.tabbedpanel.titledtab.TitledTab;

public class PruebaDock extends JFrame {
	
	private View archivoView;
	private View consolaView;
	
	private ArrayList<View> editor = new ArrayList<>();
	
	private RootWindow ventanaRaiz;
	
	private ViewMap mapa = new ViewMap();
	
	private TabWindow editores;
	
	void init() {
		//this.setJMenuBar(new Menu(this));
		
		JButton btnAgregar = new JButton("Agregar");
		
		archivoView = new View("Explorador", null, new JScrollPane(btnAgregar));
		consolaView = new View("Consola", null, new JScrollPane(new JTextArea("Consola")));
		
		mapa.addView(0, archivoView);
		mapa.addView(1, consolaView);
		
		ventanaRaiz = DockingUtil.createRootWindow(mapa, true);
		
		
		ventanaRaiz.getWindowBar(Direction.DOWN).setEnabled(true);
		
		SplitWindow principal = new SplitWindow(false, 0.2f, archivoView, consolaView);
		
		editor.add(new View("Archivo1.cpp", null, new JScrollPane(new JTextArea("Hola1"))));
		editor.add(new View("Archivo2.cpp", null, new JScrollPane(new JTextArea("Hola2"))));
		
		editores = new TabWindow();
		editores.addTab(editor.get(0));
		editores.addTab(editor.get(1));
		
		SplitWindow secundario = new SplitWindow(true, 0.2f, principal, editores);
		
		btnAgregar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				editor.add(new View(("Archivo"+(editor.size()+1)+".cpp"), null, new JScrollPane(new JTextArea("Hola"))));
				editores.addTab(editor.get(editor.size()-1));
				System.out.println(editores.getChildWindowCount());
			}
		});
		
		ventanaRaiz.setWindow(secundario);
		
		WindowBar windowBar = ventanaRaiz.getWindowBar(Direction.DOWN);

		while (windowBar.getChildWindowCount() > 0)
			windowBar.getChildWindow(0).close();
		
		
		
		this.getContentPane().add(ventanaRaiz, BorderLayout.CENTER);
		// frame.setJMenuBar(createMenuBar());
		this.setSize(new Dimension(800, 600));
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setVisible(true);
	}

	public PruebaDock() {
		init();
	}

}
