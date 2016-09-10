package org.uso.depurador.componentes.arbol;

import java.util.ArrayList;

import javax.swing.ImageIcon;

public class BD {
	
	private int id;
	private String nombre;
	private ImageIcon icono = new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/database.png"));
	private ArrayList<BDProc> procedimientos = new ArrayList<>();
	private BDRootTabla folder_tabla = new BDRootTabla();
	
	
	public ImageIcon getIcono() {
		return icono;
	}
	public void setIcono(ImageIcon icono) {
		this.icono = icono;
	}
	
	public BDRootTabla getFolder_tabla() {
		return folder_tabla;
	}
	public void setFolder_tabla(BDRootTabla folder_tabla) {
		this.folder_tabla = folder_tabla;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public ArrayList<BDProc> getProcedimientos() {
		return procedimientos;
	}
	public void setProcedimientos(ArrayList<BDProc> procedimientos) {
		this.procedimientos = procedimientos;
	}
	@Override
    public String toString() {
        return this.nombre;
    }    
}
