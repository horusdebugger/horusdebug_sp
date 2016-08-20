package org.uso.depurador.componentes;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;

import org.fife.ui.rtextarea.GutterIconInfo;
import org.uso.depurador.main.Main;
import org.uso.depurador.main.Principal;
import org.uso.depurador.utlidades.Imprimir;
import org.uso.depurador.utlidades.Utilidades;

import javafx.stage.FileChooser;
import net.infonode.docking.View;
import net.infonode.tabbedpanel.titledtab.TitledTab;

public class BarraHerramientas extends JToolBar implements ActionListener {

	/* Elementos de la barra de tareas */
	private JButton nuevo, abrir, guardar, play, siguiente, atras, buscar, detener, ejecutarSQL, actualizar;
	private JComboBox<String> bds;

	/* Ventana */
	private Principal ventana;

	public BarraHerramientas(Principal ventana) {
		this.ventana = ventana;

		this.nuevo = new JButton();
		nuevo.setToolTipText("Nuevo");
		nuevo.setIcon(new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/page_white.png")));

		this.abrir = new JButton();
		abrir.setToolTipText("Abrir");
		abrir.setIcon(new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/folder.png")));

		this.guardar = new JButton();
		guardar.setToolTipText("Guardar");
		guardar.setIcon(new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/disk.png")));

		this.play = new JButton();
		play.setToolTipText("Iniciar Depuración");
		play.setIcon(new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/resultset_next.png")));

		this.siguiente = new JButton();
		siguiente.setToolTipText("Siguiente");
		siguiente.setIcon(
				new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/resultset_first.png")));

		this.atras = new JButton();
		atras.setToolTipText("Atrás");
		atras.setIcon(
				new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/resultset_last.png")));

		this.buscar = new JButton();
		buscar.setToolTipText("Buscar en el documento");
		buscar.setIcon(new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/find.png")));

		this.detener = new JButton();
		detener.setToolTipText("Detener depuración");
		this.detener.setIcon(new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/stop2.png")));

		this.ejecutarSQL = new JButton();
		this.ejecutarSQL.setToolTipText("Ejecutar sentencia SQL");
		this.ejecutarSQL.setIcon(new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/lightning.png")));
		
		this.actualizar = new JButton();
		this.actualizar.setToolTipText("Actualizar interfaz");
		this.actualizar.setIcon(new ImageIcon(getClass().getResource("/org/uso/depurador/componentes/iconos/arrow_refresh.png")));
		
		guardar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				/*
				 * GutterIconInfo[] icons =
				 * ventana.scrollPane.getGutter().getBookmarks(); for (int i =
				 * 0; i < icons.length; i++) { Icon icon = icons[i].getIcon();
				 * int offset = icons[i].getMarkedOffset(); try {
				 * System.out.println(ventana.editor.getLineOfOffset(offset)); }
				 * catch (BadLocationException e1) { // TODO Auto-generated
				 * catch block e1.printStackTrace(); } }
				 */

			}
		});

		this.add(nuevo);
		this.add(abrir);
		this.add(guardar);
		this.addSeparator();
		this.add(actualizar);
		this.addSeparator();
		this.add(play);
		this.add(detener);
		this.detener.setEnabled(false);
		this.add(siguiente);
		this.add(atras);
		this.addSeparator();
		this.add(buscar);
		this.addSeparator();
		this.add(ejecutarSQL);
		this.addSeparator();
		this.add(new JLabel("Conectado a: "));
		


		// asignaci�n de eventos a elementos de la toolbar
		this.play.addActionListener(this);
		this.nuevo.addActionListener(this);
		this.abrir.addActionListener(this);
		this.guardar.addActionListener(this);
		this.ejecutarSQL.addActionListener(this);
		this.actualizar.addActionListener(this);
		
		
		llenarBDS();

	}
	
	void llenarBDS(){
		this.bds = new JComboBox<>();
		this.bds.setMaximumSize(new Dimension(200, 100));
		try {
			Statement sentencia = ventana.conexion.getConexion().createStatement();
			ResultSet result = sentencia.executeQuery("show databases");
			bds.addItem("");
			while(result.next()) {
				bds.addItem(result.getString("Database"));
			}
			result.close();
			bds.setSelectedItem(ventana.conexion.getConexion().getCatalog());
			System.out.println(ventana.conexion.getConexion().getCatalog());
		} catch (SQLException e) {
			Imprimir.imprimirConsolaError(ventana.consolaErrores, e.getMessage());
			e.printStackTrace();
		}
		this.bds.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//ventana.conexion.cerrarConexion();
				ventana.conexion.setBd(((JComboBox<String>)e.getSource()).getSelectedItem().toString());
				ventana.conexion.conectar();
			}
		});
		
		
		this.add(this.bds);
		
	}
	
	void ejecutarSQL() {
		String sql = ventana.editor.getSelectedText();
		Statement sentencia = null;
		try {
			sentencia = ventana.conexion.getConexion().createStatement();
			boolean tipo = sentencia.execute(sql);
			if(tipo) {
				Utilidades utilidades = new Utilidades();
				utilidades.consultar(sql, ventana);
			} else {
				Imprimir.imprimirConsola(ventana.consola, "Sentencia SQL ejecutada correctamente.");
				ventana.consolas.setSelectedTab(0);
			}
		} catch (Exception ex) {
			Imprimir.imprimirConsola(ventana.consolaErrores, ex.getMessage());
			ventana.consolas.setSelectedTab(1);
		}
	}

	void ejecutar() {
		
		ventana.parametros = new ArrayList<>();
		
		try {
			Statement stmt = ventana.conexion.getConexion().createStatement();
            ResultSet rs;
 
            rs = stmt.executeQuery("SELECT PARAMETER_NAME, DATA_TYPE FROM information_schema.parameters "
            		+ "WHERE SPECIFIC_NAME = '"+ventana.procedimiento_barra+"'");
            while ( rs.next() ) {
            	Parametro p = new Parametro();
            	p.setNombre(rs.getString("PARAMETER_NAME"));
            	p.setTipo(rs.getString("DATA_TYPE"));
            	ventana.parametros.add(p);
            }
            rs.close();
            pedirDatos(ventana.parametros);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		
		/*try {
			PrintWriter writer = null;

			writer = new PrintWriter("temp", "UTF-8");

			// writer.print(ventana.editor.getText());
			writer.close();

			Runtime rt = Runtime.getRuntime();
			String[] commands = { "sql.exe", "temp" };
			Process proc = null;

			proc = rt.exec(commands);

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
					// ventana.consolaNormal.setText(mensajes);
				}
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
					// ventana.consolaErrores.setText(errores);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}*/
	}

	void pedirDatos(List<Parametro> listado) {
		for(int i =0; i<listado.size(); i++) {
			String valor = JOptionPane.showInputDialog(ventana, "Ingrese el parametro:\n Identificador: "+listado.get(i).getNombre()+"\n Tipo: "+listado.get(i).getTipo());
			ventana.parametros.get(i).setValor(valor);
		}
		for(int i =0; i<listado.size(); i++) {
			System.out.println("Nombre: "+listado.get(i).getNombre());
			System.out.println("Tipo: "+listado.get(i).getTipo());
			System.out.println("Valor: "+listado.get(i).getValor());
		}
	}
	
	void nuevo() {
		if (this.ventana.control) {
			int resultado = JOptionPane.showConfirmDialog(this.ventana,
					"Hay cambios sin guardar. Desea guardar los cambios?.", "Informaci�n", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (resultado == JOptionPane.YES_OPTION) {
				if (this.ventana.archivo != null) {
					try {
						FileWriter fw = new FileWriter(this.ventana.archivo.getAbsoluteFile());
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write(this.ventana.editor.getText());
						bw.close();
						this.ventana.archivo = null;
						this.ventana.editor.setText("");
						this.ventana.pestanaEditor.setText("Nuevo");
						this.ventana.control = false;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					this.ventana.editor.setText("");
				} else {
					JFileChooser dialogSave = new JFileChooser();
					int guardarResultado = dialogSave.showSaveDialog(this.ventana);
					if (guardarResultado == JFileChooser.APPROVE_OPTION) {
						this.ventana.archivo = dialogSave.getSelectedFile();
						if (this.ventana.archivo == null) {
							try {
								this.ventana.archivo.createNewFile();
								FileWriter fw = new FileWriter(this.ventana.archivo.getAbsoluteFile());
								BufferedWriter bw = new BufferedWriter(fw);
								bw.write(this.ventana.editor.getText());
								bw.close();
								
								this.ventana.archivo = null;
								this.ventana.editor.setText("");
								this.ventana.pestanaEditor.setText("Nuevo");
								this.ventana.control = false;
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}

					}
				}

				

			} else if (resultado == JOptionPane.NO_OPTION) {

				this.ventana.editor.setText("");
				this.ventana.pestanaEditor.setText("Nuevo");
				this.ventana.control = false;
				this.ventana.archivo = null;
			}
		} else {
			this.ventana.editor.setText("");
			ventana.pestanaEditor.setText("Nuevo");
			this.ventana.control = false;
			
		}
		
	}

	void abrir() {
		JFileChooser elegir = new JFileChooser();
		int resultado = elegir.showOpenDialog(this.ventana);

		switch (resultado) {
		case JFileChooser.APPROVE_OPTION: {

			int control_temp = 0;
			if (ventana.control) {
				control_temp = 1;
			} else {
				control_temp = 2;
			}

			switch (control_temp) {
			case 1: {
				int opcion1 = JOptionPane.showConfirmDialog(ventana,
						"Hay cambios sin guardar. Desea guardar los cambios?.", "Informaci�n",
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

				switch (opcion1) {
				case JFileChooser.APPROVE_OPTION: {
					int op_temp = 0;
					if(ventana.archivo==null) {
						op_temp = 1;
					} else {
						op_temp = 2;
					}
					switch(op_temp) {
					case 1: {
						JFileChooser elegir_op_temp = new JFileChooser();
						int result = elegir_op_temp.showSaveDialog(ventana);
						switch(result) {
							case JFileChooser.APPROVE_OPTION: {
								try {
								File archivo_temp = new File(elegir_op_temp.getSelectedFile().getAbsolutePath());
								FileWriter fw = new FileWriter(archivo_temp.getAbsoluteFile());
								BufferedWriter bw = new BufferedWriter(fw);
								bw.write(this.ventana.editor.getText());
								bw.close();
								ventana.archivo = elegir.getSelectedFile();
								try {
									BufferedReader br = new BufferedReader(new FileReader(ventana.archivo.getAbsolutePath()));
									try {
									    StringBuilder sb = new StringBuilder();
									    String line = br.readLine();

									    while (line != null) {
									        sb.append(line);
									        sb.append(System.lineSeparator());
									        line = br.readLine();
									    }
									    String contenido = sb.toString();
									    
									    ventana.editor.setText(contenido);
									    ventana.scrollEditor = new ScrollEditor(ventana.editor, true);
									    ventana.pestanaEditor.setText(ventana.archivo.getName());
										this.ventana.control = false;
									} finally {
									    br.close();
									}
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								break;
							}
							case JFileChooser.CANCEL_OPTION: {
								ventana.archivo = elegir.getSelectedFile();
								try {
									BufferedReader br = new BufferedReader(new FileReader(ventana.archivo.getAbsolutePath()));
									try {
									    StringBuilder sb = new StringBuilder();
									    String line = br.readLine();

									    while (line != null) {
									        sb.append(line);
									        sb.append(System.lineSeparator());
									        line = br.readLine();
									    }
									    String contenido = sb.toString();
									    
									    ventana.editor.setText(contenido);
									    ventana.scrollEditor = new ScrollEditor(ventana.editor, true);
									    ventana.pestanaEditor.setText(ventana.archivo.getName());
										this.ventana.control = false;
									} finally {
									    br.close();
									}
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								break;
							}
							}
						break;
					}
					case 2: {
						break;
					}
					}
					break;
				}
				case JFileChooser.CANCEL_OPTION: {
					try {
						BufferedReader br = new BufferedReader(new FileReader(elegir.getSelectedFile().getAbsolutePath()));
						try {
						    StringBuilder sb = new StringBuilder();
						    String line = br.readLine();

						    while (line != null) {
						        sb.append(line);
						        sb.append(System.lineSeparator());
						        line = br.readLine();
						    }
						    String contenido = sb.toString();
						    
						    ventana.editor.setText(contenido);
						    ventana.scrollEditor = new ScrollEditor(ventana.editor, true);
						    ventana.pestanaEditor.setText(ventana.archivo.getName());
							this.ventana.control = false;
							this.ventana.archivo = elegir.getSelectedFile();
						} finally {
						    br.close();
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					break;
				}
				default:
					break;
				}

				break;
			}
			case 2: {
				ventana.archivo = elegir.getSelectedFile();
				try {
				BufferedReader br = new BufferedReader(new FileReader(ventana.archivo.getAbsolutePath()));
				try {
				    StringBuilder sb = new StringBuilder();
				    String line = br.readLine();

				    while (line != null) {
				        sb.append(line);
				        sb.append(System.lineSeparator());
				        line = br.readLine();
				    }
				    String contenido = sb.toString();
				    
				    ventana.editor.setText(contenido);
				    ventana.pestanaEditor.setText(ventana.archivo.getName());
				    ventana.control = false;

				} finally {
				    br.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
				break;
			}
			}

			break;

		}
		default:
			break;
		}

	}

	void guardar() {
		if(ventana.archivo == null) {
			JFileChooser elegir = new JFileChooser();
			int opcion = elegir.showSaveDialog(ventana);
			switch(opcion) {
				case JFileChooser.APPROVE_OPTION: {
					try {
						File archivo_temp = new File(elegir.getSelectedFile().getName());
						FileWriter fw = new FileWriter(archivo_temp.getAbsoluteFile());
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write(this.ventana.editor.getText());
						bw.close();
						
						BufferedReader br = new BufferedReader(new FileReader(archivo_temp.getAbsolutePath()));
						
						 StringBuilder sb = new StringBuilder();
						    String line = br.readLine();

						    while (line != null) {
						        sb.append(line);
						        sb.append(System.lineSeparator());
						        line = br.readLine();
						    }
						    String contenido = sb.toString();
						    
						
						ventana.editor.setText(contenido);
					    ventana.scrollEditor = new ScrollEditor(ventana.editor, true);
					    this.ventana.editores.removeTab(this.ventana.editores.getTabAt(0));
						TitledTab pestanaTemp = new TitledTab(elegir.getSelectedFile().getName(), null, ventana.scrollEditor, null);
						this.ventana.editores.addTab(pestanaTemp);
						this.ventana.control = false;
						this.ventana.archivo = elegir.getSelectedFile();
						ventana.control = false;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					break;
				}
				case JFileChooser.CANCEL_OPTION: {
					break;
				}
				default: {
					break;
				}
			}
		} else {
			try {
			FileWriter fw = new FileWriter(ventana.archivo.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(this.ventana.editor.getText());
			bw.close();
			ventana.control = false;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.play) {
			ejecutar();
		} else if (e.getSource() == this.nuevo) {
			nuevo();
		} else if (e.getSource() == this.abrir) {
			abrir();
		} else if (e.getSource() == this.guardar) {
			guardar();
		} else if (e.getSource() == this.ejecutarSQL) {
			ejecutarSQL();
		} else if (e.getSource() == this.actualizar) {
			ventana.arbolBD.llenarArbol();
		}
	}
}
