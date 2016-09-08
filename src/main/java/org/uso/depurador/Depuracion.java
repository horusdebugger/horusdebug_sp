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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

public class Depuracion {

	public Principal ventana;
	private List<Parametro> parametros = new ArrayList<>();
	private List<String> lineas = new ArrayList<>();
	private List<Node> sentencias = new ArrayList<>();

	File archivo = new File("depuracion/codigo.proc");

	int linea = 0;
	int sentenciaPos = 0;

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
			// Element proc = (Element) nList.item(0);
			Node root = nList.item(0);
			NodeList sentence = root.getChildNodes();
			// int posicion = 0;
			for (int i = 0; i < sentence.getLength(); i++) {
				if (sentence.item(i).getNodeType() == Node.ELEMENT_NODE) {
					this.sentencias.add(sentence.item(i));
					// posicion =
					// Integer.parseInt(((Element)sentence.item(i)).getAttribute("inicio"));
				}
			}

			calcularPosicionCaret(Integer.parseInt(((Element) this.sentencias.get(0)).getAttribute("inicio")));
			this.sentenciaPos = 0;
			// int posicion = Integer.parseInt(proc.getAttribute("begin"));
			// int posicion =
			// Integer.parseInt(((Element)nodo).getAttribute("inicio"));
			this.linea = Integer.parseInt(((Element) this.sentencias.get(0)).getAttribute("inicio"));
			System.out.println(this.linea);
			calcularPosicionCaret(this.linea);
			ventana.barra.play_pausado.setEnabled(false);
			ventana.barra.play.setEnabled(false);
			ventana.barra.siguiente.setEnabled(true);
			ventana.barra.atras.setEnabled(true);
			ventana.barra.detener.setEnabled(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ventana.editorDebug.setBackground(Color.lightGray);
	}

	void calcularPosicionCaret(int linea) {
		int posicion = 0;
		if(linea == 1) {
			posicion = lineas.get(0).toString().length();
		} else {
			for(int i = 0; i<=linea-1; i++) {
				posicion += lineas.get(i).toString().length();
			}
		}
		ventana.editorDebug.setCaretPosition(posicion+linea-1);
		ventana.editorDebug.requestFocus();
	}

	public void ejecutarDepuradorPausado() {
		try {
			Runtime rt = Runtime.getRuntime();
			String[] comandos = new String[this.parametros.size() + 3];
			for (int i = 0; i <= this.parametros.size() + 2; i++) {
				if (i == 0) {
					comandos[i] = "depuracion/dep.exe";
				} else if (i == 1) {
					comandos[i] = "depuracion/codigo.proc";
				} else if (i == 2) {
					comandos[i] = "-x";
				} else {
					comandos[i] = this.parametros.get(i - 3).getValor();
				}
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
				Imprimir.imprimirConsola(ventana.consolaErrores, mensajes);
			}
			Utilidades util = new Utilidades();
			util.LeerArchivoXMLVariables("out.xml");
			util.getTablaXMLVariables(ventana.tablaVariables);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		moverBegin();
	}

	public String extraerCodigoInicioFinal(int li, int lf) {

		String fragmento = "";

		for (int i = 0; i < lineas.size(); i++) {
			if (i >= li && i <= lf) {
				fragmento += lineas.get(i).toString();
			}
		}

		return fragmento;

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
					comandos[i] = "depuracion/dep.exe";
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
					Imprimir.imprimirConsola(ventana.consolaErrores, mensajes);
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
		for(int i = 0; i<lineas.size(); i++) {
			System.out.println(lineas.get(i).toString() + " longitud => " + lineas.get(i).toString().length());
		}
	}

	public void siguiente() {
		this.sentenciaPos++;
		this.linea = Integer.parseInt(((Element) this.sentencias.get(sentenciaPos)).getAttribute("inicio"));
		calcularPosicionCaret(Integer.parseInt(((Element) this.sentencias.get(sentenciaPos)).getAttribute("inicio")));
	}

	public void atras() {
		this.sentenciaPos--;
		this.linea = Integer.parseInt(((Element) this.sentencias.get(sentenciaPos)).getAttribute("inicio"));
		calcularPosicionCaret(Integer.parseInt(((Element) this.sentencias.get(sentenciaPos)).getAttribute("inicio")));
	}

	public void imprimirListaLineas() {
		int tam = lineas.size();
		System.out.println("Lineas del arreglo: " + tam);
		for (int i = 0; i < this.lineas.size(); i++) {
			System.out.println(lineas.get(i).toString());
		}
	}

	public List<Parametro> getParametros() {
		return parametros;
	}

	public void setParametros(List<Parametro> parametros) {
		this.parametros = parametros;
	}

}