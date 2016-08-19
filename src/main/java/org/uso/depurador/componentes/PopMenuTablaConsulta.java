package org.uso.depurador.componentes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.uso.depurador.main.Principal;
import org.uso.depurador.utlidades.Imprimir;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PopMenuTablaConsulta extends JPopupMenu implements ActionListener {

	private JMenuItem limpiar;
	private JMenuItem guardar;
	private JMenuItem guardarAJSON;
	private JTable tabla;
	private Principal principal;

	public PopMenuTablaConsulta(Principal principal, JTable tabla) {
		this.limpiar = new JMenuItem("Limpiar tabla");
		this.limpiar.setIcon(new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/draft.png")));
		this.guardar = new JMenuItem("Guardar tabla en XML");
		this.guardar.setIcon(new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/xml.png")));
		this.guardarAJSON = new JMenuItem("Guardar tabla en JSON");
		this.guardarAJSON.setIcon(new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/json.png")));
		this.tabla = tabla;
		this.limpiar.addActionListener(this);
		this.guardar.addActionListener(this);
		this.guardarAJSON.addActionListener(this);
		this.add(this.limpiar);
		this.add(this.guardar);
		this.add(this.guardarAJSON);
		this.principal = principal;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == limpiar) {
			((DefaultTableModel) principal.tablaConsultas.getModel()).setRowCount(0);
			((DefaultTableModel) principal.tablaConsultas.getModel()).setColumnIdentifiers(new Object[] {});
		} else if (e.getSource() == guardar) {
			JFileChooser archivo = new JFileChooser();
			archivo.setFileFilter(new FileNameExtensionFilter("xml files (*.xml)", "xml"));
			int result = archivo.showSaveDialog(null);
			switch (result) {
			case JFileChooser.APPROVE_OPTION: {
				try {
				if (archivo.getSelectedFile().getName().substring(archivo.getSelectedFile().getName().lastIndexOf("."),
						archivo.getSelectedFile().getName().length()).equals(".xml")) {
					try {
						DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
						DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

						// root elements
						Document doc = docBuilder.newDocument();
						Element rootElement = doc.createElement("registros");
						doc.appendChild(rootElement);

						// staff elements
						
						for(int i = 0; i < principal.tablaConsultas.getRowCount(); i++) {
							Element elemento = doc.createElement("registro");
							for(int j = 0; j < principal.tablaConsultas.getColumnCount(); j++) {
								Element hijo = doc.createElement(principal.tablaConsultas.getColumnName(j));
								hijo.appendChild(doc.createTextNode(principal.tablaConsultas.getValueAt(i, j).toString()));
								elemento.appendChild(hijo);
							}
							rootElement.appendChild(elemento);
						}

						// write the content into xml file
						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer = transformerFactory.newTransformer();
						transformer.setOutputProperty(OutputKeys.INDENT, "yes");
						transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
						DOMSource source = new DOMSource(doc);
						StreamResult resultado = new StreamResult(archivo.getSelectedFile());

						// Output to console for testing
						// StreamResult result = new StreamResult(System.out);

						transformer.transform(source, resultado);

						Imprimir.imprimirConsola(principal.consola, "Archivo de consulta guardado a xml correctamente, en la dirección: "+archivo.getSelectedFile().getAbsolutePath());
						
					} catch (Exception ex) {
						Imprimir.imprimirConsolaError(principal.consolaErrores, ex.getMessage());
					}
				} else {
					
				}
				} catch(Exception ex) {
					Imprimir.imprimirConsolaError(principal.consolaErrores, "Solo se permiten archivo con extensión .xml o .json.");
				}
				break;
			}
			case JFileChooser.CANCEL_OPTION: {
				Imprimir.imprimirConsolaError(principal.consola, "Cancelando guardado de tabla de consulta.");
				break;
			}
			default: {
				Imprimir.imprimirConsolaError(principal.consola, "Cancelando guardado de tabla de consulta.");
				break;
			}
			}
		} else if (e.getSource() == this.guardarAJSON) {
			
		}

	}

}
