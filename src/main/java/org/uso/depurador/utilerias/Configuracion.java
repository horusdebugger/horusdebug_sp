package org.uso.depurador.utilerias;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JOptionPane;

public class Configuracion {
	
	private File archivo;
	private Properties propiedades;
	
	public Configuracion() {
		archivo = new File("config.properties");
		propiedades = new Properties();
	}
	
	public void setPropiedad(String llave, String valor) {
		try {
			FileWriter writer = new FileWriter(archivo);
			propiedades.setProperty(llave, valor);
			propiedades.store(writer, "ARCHIVO DE CONFIGURACION - Depurador HORUS");
			writer.close();
		} catch (IOException e) {
			System.err.println("No se pudo escribir el archivo de configuración...");
		}
	}
	

	public String getPropiedad(String llave) {
		FileReader reader;
		try {
			reader = new FileReader(archivo);
		 
			propiedades.load(reader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return propiedades.getProperty(llave);
		
	}
}
