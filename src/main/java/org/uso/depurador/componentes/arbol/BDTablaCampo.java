package org.uso.depurador.componentes.arbol;

import java.util.ArrayList;

import javax.swing.ImageIcon;

public class BDTablaCampo {
	private String nombre;
	private ImageIcon icono = new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/bullet_black.png"));
	
	private int id;

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
		// TODO Auto-generated method stub
		return this.nombre;
	}
	
}
