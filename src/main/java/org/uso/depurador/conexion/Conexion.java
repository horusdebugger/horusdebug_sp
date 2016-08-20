package org.uso.depurador.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

	private String host;
	private String usuario;
	private String pass;
	private String puerto;
	private String bd;
	private String cad_con;

	private final String driver = "com.mysql.jdbc.Driver";

	private Connection conexion;

	public Conexion(String host, String usuario, String pass, String puerto, String bd) {
		super();
		this.host = host;
		this.usuario = usuario;
		this.pass = pass;
		this.puerto = puerto;
		this.bd = bd;
		this.cad_con = "jdbc:mysql://" + host + ":" + puerto;

		try {
			Class.forName(driver);
			//this.conexion = DriverManager.getConnection(cad_con, this.usuario, this.pass);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getPuerto() {
		return puerto;
	}

	public void setPuerto(String puerto) {
		this.puerto = puerto;
	}

	public String getBd() {
		return bd;
	}

	public void setBd(String bd) {
		this.bd = bd;
	}

	public void conectar() {
		try {
			Class.forName(driver);
		this.conexion = DriverManager.getConnection(cad_con+( !bd.equals("") ? "/"+this.bd : ""), this.usuario, this.pass);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean probarConexion() {
		try {
			Connection conex = DriverManager.getConnection(this.cad_con, this.usuario, this.pass);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public Connection getConexion() {
		return conexion;
	}
	
	public void cerrarConexion() {
		try {
			this.conexion.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
