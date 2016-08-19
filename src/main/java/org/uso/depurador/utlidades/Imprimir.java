package org.uso.depurador.utlidades;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTextArea;

public class Imprimir {
	
	public static void imprimirConsola(JTextArea consola, String mensaje) {
		consola.append(new SimpleDateFormat("d/M/Y HH:MM:s").format(new Date())+"  : "+mensaje+"\n");
	}
	
	public static void imprimirConsolaError(JTextArea consola, String mensaje) {
		consola.append(new SimpleDateFormat("d/M/Y HH:MM:s").format(new Date())+"  : "+mensaje+"\n");
	}

}
