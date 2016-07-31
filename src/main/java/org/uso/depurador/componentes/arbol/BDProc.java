package org.uso.depurador.componentes.arbol;

import javax.swing.ImageIcon;

public class BDProc {

	private int id;
	private String nombre;
	private ImageIcon icono = new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/table_gear.png"));
	private BD padre;
	
	public BD getPadre() {
		return padre;
	}
	public void setPadre(BD padre) {
		this.padre = padre;
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
	public ImageIcon getIcono() {
		return icono;
	}
	public void setIcono(ImageIcon icono) {
		this.icono = icono;
	}	
	
	@Override
    public String toString() {
        return this.nombre ;
    }    
	
}
