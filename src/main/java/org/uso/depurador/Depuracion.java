package org.uso.depurador;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.fife.ui.rtextarea.GutterIconInfo;
import org.uso.depurador.componentes.Parametro;
import org.uso.depurador.main.Principal;
import org.uso.depurador.utlidades.Imprimir;
import org.uso.depurador.utlidades.Utilidades;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jdk.internal.org.xml.sax.InputSource;

public class Depuracion {

	public Principal ventana;
	private List<Parametro> parametros = new ArrayList<>();
	private List<String> lineas = new ArrayList<>();
	private List<Node> sentencias = new ArrayList<>();
	private List<String> sentenciasFinales = new ArrayList<>();

	File archivo = new File("codigo.proc");

	int linea = 0;
	int sentenciaPos = 0;

	int lineaBegin = 0;

	String codigoInicial = "";

	public boolean completa;

	List<Integer> puntos = new ArrayList<>();

	int lineaPunto = 0;
	int puntoPos = 0;

	public Depuracion(Principal ventana, boolean completa) {
		this.ventana = ventana;
		this.completa = completa;
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

			this.codigoInicial = ((Element) root).getAttribute("valor");
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
				ventana.scrollEditorDebug.getGutter().addLineTrackingIcon(this.linea - 1, new ImageIcon(
						getClass().getResource("/org/uso/depurador/componentes/iconos/flecha_orange.png")));
			} catch (BadLocationException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	String traducirSentencia(Node sentencia) {
		String linea = "";
		Element elemento = (Element) sentencia;

		if (elemento.getAttribute("tipo").equals("declaracion")) {
			linea = elemento.getAttribute("valor");
			ventana.consolas.setSelectedTab(3);
		} else if (elemento.getAttribute("tipo").equals("asignacion")) {
			linea = elemento.getAttribute("valor");
			ventana.consolas.setSelectedTab(3);
		} else if (elemento.getAttribute("tipo").equals("select")) {
			linea = elemento.getAttribute("valor");
			ejecutarSQL(elemento);
		} else if (elemento.getAttribute("tipo").equals("if")) {
			calcularPosicionCaret(Integer.parseInt(elemento.getAttribute("inicio")));
		} else if (elemento.getAttribute("tipo").equals("escalar")) {
			linea += "SET ";
			linea += elemento.getAttribute("oprn1") + " ";
			linea += elemento.getAttribute("opr") + " ";
			linea += getValor(elemento);
			ventana.consolas.setSelectedTab(3);
		} else if (elemento.getAttribute("tipo").equals("delete")) {
			linea = elemento.getAttribute("valor");
			ejecutarSQL(elemento);
		} else if (elemento.getAttribute("tipo").equals("insert")) {
			linea = elemento.getAttribute("valor");
			ejecutarSQL(elemento);
		} else if (elemento.getAttribute("tipo").equals("update")) {
			linea = elemento.getAttribute("valor");
			ejecutarSQL(elemento);
		}
		return linea;
	}

	String traducirSentencia() {
		String linea = "";
		Node sentencia = this.sentencias.get(this.sentenciaPos);
		Element elemento = (Element) sentencia;

		if (elemento.getAttribute("tipo").equals("declaracion")) {
			linea = elemento.getAttribute("valor");
			ventana.consolas.setSelectedTab(3);
		} else if (elemento.getAttribute("tipo").equals("asignacion")) {
			linea = elemento.getAttribute("valor");
			ventana.consolas.setSelectedTab(3);
		} else if (elemento.getAttribute("tipo").equals("select")) {
			linea = elemento.getAttribute("valor");
			ejecutarSQL(elemento);
		} else if (elemento.getAttribute("tipo").equals("if")) {
			calcularPosicionCaret(Integer.parseInt(elemento.getAttribute("inicio")));
		} else if (elemento.getAttribute("tipo").equals("escalar")) {
			linea += "SET ";
			linea += elemento.getAttribute("oprn1") + " ";
			linea += elemento.getAttribute("opr") + " ";
			linea += getValor(elemento);
			ventana.consolas.setSelectedTab(3);
		} else if (elemento.getAttribute("tipo").equals("delete")) {
			linea = elemento.getAttribute("valor");
			ejecutarSQL(elemento);
		} else if (elemento.getAttribute("tipo").equals("insert")) {
			linea = elemento.getAttribute("valor");
			ejecutarSQL(elemento);
		} else if (elemento.getAttribute("tipo").equals("update")) {
			linea = elemento.getAttribute("valor");
			ejecutarSQL(elemento);
		} else if(elemento.getAttribute("tipo").equals("while")) {
			calcularPosicionCaret(Integer.parseInt(elemento.getAttribute("inicio")));
		} else if(elemento.getAttribute("tipo").equals("end")) {
			calcularPosicionCaret(Integer.parseInt(elemento.getAttribute("inicio")));
		}
		return linea;
	}

	String getValor(Element nodo) {
		String valor = "";
		try {
			Statement st = ventana.conexion.getConexion().createStatement();
			System.out.println("OPRN2: " + nodo.getAttribute("oprn2"));
			ResultSet rs = st.executeQuery(nodo.getAttribute("oprn2"));
			while (rs.next()) {
				valor = rs.getString(1);
			}
			System.out.println("VALOR: " + valor);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return valor + " ;";
	}

	void ejecutarSQL(Node sentencia) {
		Statement sentenciasql = null;
		String sql = ((Element) sentencia).getAttribute("valor");
		try {
			sentenciasql = ventana.conexion.getConexion().createStatement();
			boolean tipo = sentenciasql.execute(sql);
			// System.out.println("Sentencia: " + sql);
			if (tipo) {
				Utilidades utilidades = new Utilidades();
				utilidades.consultar(((Element) sentencia).getAttribute("valor"), ventana);
			} else {
				Imprimir.imprimirConsola(ventana.consola, "Sentencia SQL ejecutada correctamente.");
				Imprimir.imprimirConsola(ventana.consola, "Filas afectadas: " + sentenciasql.getUpdateCount());
				ventana.consolas.setSelectedTab(0);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
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
			comandos[0] = "dep.exe";

			comandos[1] = "codigo.proc";

			comandos[2] = "-x";

			System.out.println(Arrays.toString(comandos));

			Process proc = rt.exec(comandos);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

			// System.out.println("Salida del programa:\n");

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
		writer.close();
	}

	public void ejecutarDepurador() {
		try {

			establecerPuntos();

			Runtime rt = Runtime.getRuntime();
			String[] comandos = new String[3];
			comandos[0] = "dep.exe";

			comandos[1] = "codigo.proc";

			comandos[2] = "-x";

			Process proc = rt.exec(comandos);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

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

			String err = null;
			String errores = "";
			while ((err = stdError.readLine()) != null) {
				errores += err + "\n";
				System.err.println(err);
			}
			if (!errores.equals("")) {
				Imprimir.imprimirConsolaError(ventana.consolaErrores, errores);
			}

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
			// calcularPosicionCaret(this.linea);

			this.codigoInicial = ((Element) root).getAttribute("valor");
			// this.sentenciasFinales.add(traducirSentencia());

			// ejecutarCodigoFinal();

			if (this.puntos.size() != 0) {
				this.ventana.arbolBD.setEnabled(false);
				this.ventana.barra.siguiente.setEnabled(true);
				this.ventana.barra.atras.setEnabled(true);
				this.ventana.barra.play.setEnabled(false);
				this.ventana.barra.play_pausado.setEnabled(false);
				this.ventana.barra.detener.setEnabled(true);
				this.ventana.editorDebug.setBackground(Color.lightGray);
				this.lineaPunto = puntos.get(0);
				System.out.println("Lineapunto: " + this.lineaPunto);
				this.puntoPos = 0;
				int numSentencia = 0;
				for (int i = 0; i < sentencias.size(); i++) {
					if (this.lineaPunto >= Integer.parseInt(((Element) sentencias.get(i)).getAttribute("inicio"))) {
						System.out.println(((Element) sentencias.get(i)).getAttribute("valor"));
						this.sentenciaPos = (numSentencia++);
					}
				}
			if (comprobarPosicionPuntoCorrecta()) {
				for (int i = 0; i <= sentenciaPos; i++) {
					this.sentenciasFinales.add(traducirSentencia(this.sentencias.get(i)));
				}
			}
			ejecutarCodigoFinal();
			calcularPosicionCaret(this.lineaPunto);
			for (int i = 0; i < puntos.size(); i++) {
				ventana.scrollEditorDebug.getGutter().addLineTrackingIcon(puntos.get(i) - 1,
						new ImageIcon("breakpoint.png"));
			}

			} else {
				for (int i = 0; i < sentencias.size(); i++) {
					this.sentenciasFinales.add(traducirSentencia(this.sentencias.get(i)));
				}
				ejecutarCodigoFinal();
				calcularPosicionCaret(Integer.parseInt(((Element)root).getAttribute("end")));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	boolean comprobarPosicionPuntoCorrecta() {
		boolean control = false;

		for (int i = 0; i < sentencias.size(); i++) {

				if (puntos.get(puntoPos) >= Integer.parseInt(((Element) sentencias.get(i)).getAttribute("inicio"))) {
					control = true;
					break;
				}
				
		}
		return control;
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
		if (!completa) {
			if (sentenciaPos < sentencias.size() - 1) {
				this.sentenciaPos++;
				this.linea = Integer.parseInt(((Element) this.sentencias.get(sentenciaPos)).getAttribute("inicio"));
				calcularPosicionCaret(
						Integer.parseInt(((Element) this.sentencias.get(sentenciaPos)).getAttribute("inicio")));
				this.sentenciasFinales.add(traducirSentencia());

				try {
					ventana.scrollEditorDebug.getGutter().removeAllTrackingIcons();
					ventana.scrollEditorDebug.getGutter().addLineTrackingIcon(linea - 1, new ImageIcon(
							getClass().getResource("/org/uso/depurador/componentes/iconos/flecha_orange.png")));
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ejecutarCodigoFinal();
			}
		} else {
			sentenciasFinales.clear();
			this.puntoPos++;
			System.out.println("PuntoPos: " + puntoPos + " - " + "PuntosSize: " + this.puntos.size());
			if (puntoPos == this.puntos.size()) {
				ventana.barra.siguiente.setEnabled(false);
				ventana.barra.atras.setEnabled(false);
				ventana.barra.detener.setEnabled(false);
				ventana.barra.play.setEnabled(true);
				ventana.barra.play_pausado.setEnabled(true);
				ventana.editorDebug.setCurrentLineHighlightColor(Color.getHSBColor(255, 255, 170));
				ventana.arbolBD.setEnabled(true);
				ventana.editorDebug.setBackground(Color.white);
				ventana.editorDebug.setCaretPosition(ventana.editorDebug.getText().length());
				ventana.scrollEditorDebug.getGutter().setBookmarkingEnabled(true);
				ventana.editorDebug.setBackground(Color.white);
				ventana.editorDebug.setCurrentLineHighlightColor(Color.getHSBColor(255, 255, 170));
				ventana.depurador.completa = false;
				ventana.scrollEditorDebug.getGutter().removeAllTrackingIcons();
			} else {
				this.lineaPunto = puntos.get(puntoPos);

				int numSentencia = 0;
				for (int i = 0; i < sentencias.size(); i++) {

					if (this.lineaPunto >= Integer.parseInt(((Element) sentencias.get(i)).getAttribute("inicio"))) {
						System.out.println(((Element) sentencias.get(i)).getAttribute("valor"));
						this.sentenciaPos = (numSentencia++);
					}
				}
				if (comprobarPosicionPuntoCorrecta()) {
					for (int i = 0; i <= sentenciaPos; i++) {
						this.sentenciasFinales.add(traducirSentencia(this.sentencias.get(i)));
					}
				}
				calcularPosicionCaret(this.lineaPunto);
				ejecutarCodigoFinal();

			}
		}
	}

	public void atras() {
		if (!completa) {
			if (sentenciaPos > 0) {
				this.sentenciaPos--;
				this.linea = Integer.parseInt(((Element) this.sentencias.get(sentenciaPos)).getAttribute("inicio"));
				calcularPosicionCaret(
						Integer.parseInt(((Element) this.sentencias.get(sentenciaPos)).getAttribute("inicio")));

				traducirSentencia();
				this.sentenciasFinales.remove(this.sentenciaPos + 1);

				ventana.scrollEditorDebug.getGutter().removeAllTrackingIcons();
				try {
					ventana.scrollEditorDebug.getGutter().addLineTrackingIcon(linea - 1, new ImageIcon(
							getClass().getResource("/org/uso/depurador/componentes/iconos/flecha_orange.png")));
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ejecutarCodigoFinal();
			}
		} else {
			sentenciasFinales.clear();
			if (puntoPos > 0) {
				this.puntoPos--;
				this.lineaPunto = puntos.get(puntoPos);

				int numSentencia = 0;
				for (int i = 0; i < sentencias.size(); i++) {

					if (this.lineaPunto >= Integer.parseInt(((Element) sentencias.get(i)).getAttribute("inicio"))) {
						System.out.println(((Element) sentencias.get(i)).getAttribute("valor"));
						this.sentenciaPos = (numSentencia++);
					}
				}
				if(comprobarPosicionPuntoCorrecta()) {
					for (int i = 0; i <= sentenciaPos; i++) {
						this.sentenciasFinales.add(traducirSentencia(this.sentencias.get(i)));
					}
				}
				calcularPosicionCaret(this.lineaPunto);
				ejecutarCodigoFinal();
			}
		}
	}

	public void establecerPuntos() {
		GutterIconInfo puntos[] = ventana.scrollEditorDebug.getGutter().getBookmarks();
		System.out.println("Tam puntos: " + puntos.length);
		int i = 0;
		for (GutterIconInfo punto : puntos) {
			try {
				this.puntos
						.add(Integer.parseInt(ventana.editorDebug.getLineOfOffset(punto.getMarkedOffset()) + "") + 1);
				System.out.println("Punto: " + this.puntos.get(i));
				i++;
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
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
			File archivo = new File("codigoFinal.proc");
			archivo.createNewFile();
			FileWriter escritor = new FileWriter(archivo);
			String codigo = this.codigoInicial + "\n";
			for (int i = 0; i < this.sentenciasFinales.size(); i++) {
				codigo += this.sentenciasFinales.get(i) + "\n";
			}
			codigo += "END";
			escritor.write(codigo);
			escritor.close();

			Runtime rt = Runtime.getRuntime();
			String[] comandos = new String[this.parametros.size() + 2];
			for (int i = 0; i <= this.parametros.size() + 1; i++) {
				if (i == 0) {
					comandos[i] = "dep.exe";
				} else if (i == 1) {
					comandos[i] = "codigoFinal.proc";
				} else {
					comandos[i] = this.parametros.get(i - 2).getValor();
				}
			}

			// System.out.println(Arrays.toString(comandos));

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
			proc.destroy();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}