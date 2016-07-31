package org.uso.depurador.componentes.arbol;

import java.util.ArrayList;

import javax.swing.ImageIcon;

public class BDServidor {

	private String nombre;
	private ImageIcon icono = new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/server_database.png"));
	private ArrayList<BD> bds = new ArrayList<>();
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

	public ArrayList<BD> getBds() {
		return bds;
	}

	public void setBds(ArrayList<BD> bds) {
		this.bds = bds;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.nombre;
	}
	
}
