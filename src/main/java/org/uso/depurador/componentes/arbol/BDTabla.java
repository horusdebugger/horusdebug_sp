package org.uso.depurador.componentes.arbol;

import javax.swing.ImageIcon;

public class BDTabla {
	
	private int id;
	private String nombre;
	private ImageIcon icono = new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/table.png"));
	private ImageIcon iconoVacio = new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/table.png"));
	private BD padre;
	
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
	public ImageIcon getIconoVacio() {
		return iconoVacio;
	}
	public void setIconoVacio(ImageIcon iconoVacio) {
		this.iconoVacio = iconoVacio;
	}
	
	public BD getPadre() {
		return padre;
	}
	public void setPadre(BD padre) {
		this.padre = padre;
	}
	@Override
    public String toString() {
        return this.nombre ;
    }    
	
}
