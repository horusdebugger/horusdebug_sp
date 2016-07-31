package org.uso.depurador.conexion;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {

	private String host;
	private String usuario;
	private String pass;
	private String puerto;
	private String bd;
	
	private final String driver = "com.mysql.jdbc.Driver";

	private Connection conexion;
	
	public Conexion(String host, String usuario, String pass, String puerto, String bd) {
		super();
		this.host = host;
		this.usuario = usuario;
		this.pass = pass;
		this.puerto = puerto;
		this.bd = bd;
		
		String cad_con = "jdbc:mysql://"+host+":"+puerto;
		
		if(!bd.equals("")) {
			cad_con += ("/"+bd);
		}
		
		try {
			Class.forName(driver);
			this.conexion = DriverManager.getConnection(cad_con, this.usuario, this.pass);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}

	public Connection getConexion() {
		return conexion;
	}
	
}
