package org.uso.depurador.utlidades;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.uso.depurador.main.Principal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Utilidades {
	List<Variable> listado = new ArrayList<>();
	
	public void LeerArchivoXML() {
		try {
			File fXmlFile = new File("variables.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

			NodeList nList = doc.getElementsByTagName("variable");

			System.out.println("----------------------------");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					
					Variable v = new Variable();

					v.setNombre( eElement.getElementsByTagName("nombre").item(0).getTextContent());
					v.setTipo(eElement.getElementsByTagName("tipo").item(0).getTextContent());
					v.setValor(eElement.getElementsByTagName("valor").item(0).getTextContent());
					
					listado.add(v);
				}
			}
		    } catch (Exception e) {
			e.printStackTrace();
		    }
	}
	
	public JTable getTablaVariables() {
		
		JTable tabla = new JTable();
		
		DefaultTableModel modelo = new DefaultTableModel(){
			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		modelo.setColumnIdentifiers(new Object[]{"<html><b>Nombre</b></html>", "<html><b>Tipo</b></html>", "<html><b>Valor</b></html>"});
		
		for(int i = 0; i<listado.size(); i++) {
			modelo.addRow(new Object[]{listado.get(i).getNombre(), listado.get(i).getTipo(), listado.get(i).getValor()});
		}
		tabla.setModel(modelo);
		tabla.getColumnModel().getColumn(0).setPreferredWidth(200);
		tabla.getColumnModel().getColumn(1).setPreferredWidth(100);
		tabla.getColumnModel().getColumn(2).setPreferredWidth(800);
		System.out.println(tabla.getRowCount());
		
		return tabla;
	}
	
	public void consultar(String sql, Principal principal) {
		DefaultTableModel modelo = (DefaultTableModel) principal.tablaConsultas.getModel();
		
		try {
			
			modelo.setRowCount(0);
			
			Statement sentencia = principal.conexion.createStatement();
			ResultSet resultados = sentencia.executeQuery(sql);
			
			ResultSetMetaData metadata = resultados.getMetaData();
			
			List<Object> columnas = new ArrayList<>();
 			
			for(int i = 1; i<=metadata.getColumnCount(); i++) {
				columnas.add(metadata.getColumnLabel(i));
			}
			
			Object columnasNombres[] = new Object[columnas.size()];
			
			for(int i = 0; i<columnasNombres.length; i++) {
				columnasNombres[i] = columnas.get(i);
			}
			modelo.setColumnIdentifiers(columnasNombres);
			principal.tablaConsultas.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			
			while(resultados.next()) {
				Object fila[] = new Object[columnasNombres.length];
				for(int i = 0; i < fila.length; i++) {
					fila[i] = resultados.getObject(i+1);
				}
				modelo.addRow(fila);
			}
			modelo.fireTableDataChanged();
			principal.tablaConsultas.setModel(modelo);
			for(int i = 0; i<columnasNombres.length; i++) {
				principal.tablaConsultas.getColumnModel().getColumn(i).setPreferredWidth(250);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	static class Variable {
		private String nombre;
		private String tipo;
		private String valor;
		public String getNombre() {
			return nombre;
		}
		public void setNombre(String nombre) {
			this.nombre = nombre;
		}
		public String getTipo() {
			return tipo;
		}
		public void setTipo(String tipo) {
			this.tipo = tipo;
		}
		public String getValor() {
			return valor;
		}
		public void setValor(String valor) {
			this.valor = valor;
		}
		
		
	}

}
