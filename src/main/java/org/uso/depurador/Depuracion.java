package org.uso.depurador;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.uso.depurador.componentes.Parametro;
import org.uso.depurador.main.Principal;
import org.uso.depurador.utlidades.Imprimir;
import org.uso.depurador.utlidades.Utilidades;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;

public class Depuracion {

	public Principal ventana;
	private List<Parametro> parametros = new ArrayList<>();
	private List<String> lineas = new ArrayList<>();
	private List<Node> sentencias = new ArrayList<>();
	private List<String> sentenciasFinales = new ArrayList<>();

	File archivo = new File("depuracion/codigo.proc");

	int linea = 0;
	int sentenciaPos = 0;

	int lineaBegin = 0;

	String codigoInicial = "";

	public Depuracion(Principal ventana) {
		this.ventana = ventana;
	}

	public void iniciarDepuracionPausada() {
		try {
			archivo.createNewFile();
			escribirArchivo();
			llenarListaLineas();
			ejecutarDepuradorPausado();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void iniciarDepuracion() {
		try {
			archivo.createNewFile();
			escribirArchivo();
			llenarListaLineas();
			ejecutarDepurador();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void moverBegin() {
		try {
			File fXmlFile = new File("ctrl.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("procedimiento");
			Node root = nList.item(0);
			NodeList sentence = root.getChildNodes();
			for (int i = 0; i < sentence.getLength(); i++) {
				if (sentence.item(i).getNodeType() == Node.ELEMENT_NODE) {
					this.sentencias.add(sentence.item(i));
				}
			}
			this.sentenciaPos = 0;
			this.linea = Integer.parseInt(((Element) this.sentencias.get(0)).getAttribute("inicio"));
			calcularPosicionCaret(this.linea);
			
			this.codigoInicial = "CREATE DEFINER=`root`@`localhost` PROCEDURE `procedimiento`(IN id INT) "
					+ "BEGIN"/*((Element)root).getAttribute("valor")*/;
			this.sentenciasFinales.add(traducirSentencia());
			
			
			ejecutarCodigoFinal();
			
			ventana.barra.play_pausado.setEnabled(false);
			ventana.barra.play.setEnabled(false);
			ventana.barra.siguiente.setEnabled(true);
			ventana.barra.atras.setEnabled(true);
			ventana.barra.detener.setEnabled(true);
			ventana.editorDebug.setBackground(Color.lightGray);
			ventana.editorDebug.setCurrentLineHighlightColor(Color.orange);
			
			ventana.arbolBD.setEnabled(false);
			
			try {
				ventana.scrollEditorDebug.getGutter().removeAllTrackingIcons();
				ventana.scrollEditorDebug.getGutter().addLineTrackingIcon(this.linea-1, 
						new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/flecha_orange.png")));
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	String traducirSentencia() {
		String linea = "";
		Node sentencia = this.sentencias.get(this.sentenciaPos);
		Element elemento = (Element) sentencia;
		if(elemento.getAttribute("tipo").equals("declaracion")) {
			linea = elemento.getAttribute("valor");
			ventana.consolas.setSelectedTab(3);
		} else if(elemento.getAttribute("tipo").equals("asignacion")) {
			linea = elemento.getAttribute("valor");
			ventana.consolas.setSelectedTab(3);
		} else if(elemento.getAttribute("tipo").equals("select")) {
			linea = elemento.getAttribute("valor");
			ejecutarSQL(elemento);
		} else if(elemento.getAttribute("tipo").equals("if")) {
			linea = elemento.getAttribute("valor");
		} else if(elemento.getAttribute("tipo").equals("escalar")) {
			String cad = elemento.getAttribute("oprn1");
			cad += elemento.getAttribute("opr");
			cad += getValor(elemento);
		}
		return linea;
	}
	
	String getValor(Element nodo) {
		String valor = "";
		try {
			Statement st = ventana.conexion.getConexion().createStatement();
			ResultSet rs = st.executeQuery(nodo.getAttribute("oprn2"));
			while(rs.next()) {
				//valor = rs.getString();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	void ejecutarSQL(Node sentencia){
		Statement sentenciasql = null;
		try {
			sentenciasql = ventana.conexion.getConexion().createStatement();
			boolean tipo = sentenciasql.execute(((Element)sentencia).getAttribute("valor"));
			if (tipo) {
				Utilidades utilidades = new Utilidades();
				utilidades.consultar(((Element)sentencia).getAttribute("valor"), ventana); 
			} else {
				Imprimir.imprimirConsola(ventana.consola, "Sentencia SQL ejecutada correctamente.");
				ventana.consolas.setSelectedTab(0);
			}
		} catch (Exception ex) {
			Imprimir.imprimirConsola(ventana.consolaErrores, ex.getMessage());
			ventana.consolas.setSelectedTab(1);
		}
		
	}
	
	void calcularPosicionCaret(int linea) {
		int posicion = 0;
		if (linea == 1) {
			posicion = lineas.get(0).toString().length();
		} else {
			for (int i = 0; i <= linea - 1; i++) {
				posicion += lineas.get(i).toString().length();
			}
		}
		ventana.editorDebug.setCaretPosition(posicion + linea - 1);
		ventana.editorDebug.requestFocus();
	}

	public void ejecutarDepuradorPausado() {
		try {

			Runtime rt = Runtime.getRuntime();
			String[] comandos = new String[3];
			for (int i = 0; i < comandos.length; i++) {
				if (i == 0) {
					comandos[i] = "depuracion/dep.exe";
				} else if (i == 1) {
					comandos[i] = "depuracion/codigo.proc";
				} else if (i == 2) {
					comandos[i] = "-x";
				} /*else {
					comandos[i] = this.parametros.get(i - 3).getValor();
				}*/
			}

			System.out.println(Arrays.toString(comandos));

			Process proc = rt.exec(comandos);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

			System.out.println("Salida del programa:\n");

			String salida = null;
			String mensajes = "";
			while ((salida = stdInput.readLine()) != null) {
				mensajes += salida + "\n";
			}
			if (!mensajes.equals("")) {
				Imprimir.imprimirConsola(ventana.consola, mensajes);
			}
			Utilidades util = new Utilidades();
			util.LeerArchivoXMLVariables("out.xml");
			util.getTablaXMLVariables(ventana.tablaVariables);
			ventana.scrollEditorDebug.getGutter().setBookmarkingEnabled(false);
			moverBegin();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}

	public void escribirArchivo() throws Exception {
		FileWriter writer = new FileWriter(this.archivo);
		writer.write(ventana.editorDebug.getText());
		// writer.flush();
		writer.close();
	}

	public void ejecutarDepurador() {
		try {

			Runtime rt = Runtime.getRuntime();
			String[] comandos = new String[this.parametros.size() + 2];
			for (int i = 0; i <= this.parametros.size() + 1; i++) {
				if (i == 0) {
					comandos[i] = "depuracion/dep";
				} else if (i == 1) {
					comandos[i] = "depuracion/codigo.proc";
				} else {
					comandos[i] = this.parametros.get(i - 2).getValor();
				}
			}

			Process proc = rt.exec(comandos);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

			System.out.println("Salida del programa:\n");
			try {
				String salida = null;
				String mensajes = "";
				while ((salida = stdInput.readLine()) != null) {
					mensajes += salida + "\n";
				}
				if (!mensajes.equals("")) {
					Imprimir.imprimirConsola(ventana.consola, mensajes);
				}
				Utilidades util = new Utilidades();
				util.LeerArchivoXMLVariables("out.xml");
				util.getTablaXMLVariables(ventana.tablaVariables);

			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("Errores del programa:\n");
			try {
				String err = null;
				String errores = "";
				while ((err = stdError.readLine()) != null) {
					errores += err + "\n";
					System.err.println(err);
				}
				if (!errores.equals("")) {
					Imprimir.imprimirConsolaError(ventana.consolaErrores, errores);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void moverCaret(int pos) {
		ventana.editorDebug.setCaretPosition(pos);
	}

	public void llenarListaLineas() {
		try {
			lineas = Files.readAllLines(this.archivo.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void siguiente() {
		if (sentenciaPos < sentencias.size() - 1) {
			this.sentenciaPos++;
			this.linea = Integer.parseInt(((Element) this.sentencias.get(sentenciaPos)).getAttribute("inicio"));
			calcularPosicionCaret(
					Integer.parseInt(((Element) this.sentencias.get(sentenciaPos)).getAttribute("inicio")));
			this.sentenciasFinales.add(traducirSentencia());
			
			try {
				ventana.scrollEditorDebug.getGutter().removeAllTrackingIcons();
				ventana.scrollEditorDebug.getGutter().addLineTrackingIcon(linea-1, 
						new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/flecha_orange.png")));
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ejecutarCodigoFinal();
		}
	}
	

	public void atras() {
		if (sentenciaPos > 0) {
			this.sentenciaPos--;
			this.linea = Integer.parseInt(((Element) this.sentencias.get(sentenciaPos)).getAttribute("inicio"));
			calcularPosicionCaret(
					Integer.parseInt(((Element) this.sentencias.get(sentenciaPos)).getAttribute("inicio")));

			traducirSentencia();
			this.sentenciasFinales.remove(this.sentenciaPos+1);
			
			ventana.scrollEditorDebug.getGutter().removeAllTrackingIcons();
			try {
				ventana.scrollEditorDebug.getGutter().addLineTrackingIcon(linea-1, 
						new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/flecha_orange.png")));
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			ejecutarCodigoFinal();
		}
	}

	public void imprimirListaLineas() {
		int tam = lineas.size();
		System.out.println("Lineas del arreglo: " + tam);
		/*
		 * for (int i = 0; i < this.lineas.size(); i++) {
		 * System.out.println(lineas.get(i).toString()); }
		 */
	}

	public List<Parametro> getParametros() {
		return parametros;
	}

	public void setParametros(List<Parametro> parametros) {
		this.parametros = parametros;
	}

	public void ejecutarCodigoFinal() {
		try {
			File archivo = new File("depuracion/codigoFinal.proc");
			archivo.createNewFile();
			FileWriter escritor = new FileWriter(archivo);
			String codigo = this.codigoInicial+"\n";
			for(int i = 0; i<this.sentenciasFinales.size(); i++) {
				codigo += this.sentenciasFinales.get(i)+"\n";
			}
			codigo += "END";
			escritor.write(codigo);
			escritor.close();

			Runtime rt = Runtime.getRuntime();
			String[] comandos = new String[this.parametros.size() + 2];
			for (int i = 0; i <= this.parametros.size() + 1; i++) {
				if (i == 0) {
					comandos[i] = "depuracion/dep.exe";
				} else if (i == 1) {
					comandos[i] = "depuracion/codigoFinal.proc";
				} else {
					comandos[i] = this.parametros.get(i - 2).getValor();
				}
			}

			//System.out.println(Arrays.toString(comandos));
			
			Process proc = rt.exec(comandos);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

			System.out.println("Salida del programa:\n");

			String salida = null;
			String mensajes = "";
			while ((salida = stdInput.readLine()) != null) {
				mensajes += salida + "\n";
			}
			if (!mensajes.equals("")) {
				Imprimir.imprimirConsola(ventana.consola, mensajes);
			}
			Utilidades util = new Utilidades();
			util.LeerArchivoXMLVariables("out.xml");
			util.getTablaXMLVariables(ventana.tablaVariables);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}