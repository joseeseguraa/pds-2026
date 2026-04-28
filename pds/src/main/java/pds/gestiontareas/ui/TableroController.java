package pds.gestiontareas.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import pds.gestiontareas.application.PlantillaService;
import pds.gestiontareas.application.TableroService;
import pds.gestiontareas.application.TarjetaService;
import pds.gestiontareas.application.dto.ListaDTO;
import pds.gestiontareas.application.dto.TableroDTO;
import pds.gestiontareas.domain.model.tablero.id.TableroId;
import pds.gestiontareas.domain.model.tablero.model.Tablero;
import pds.gestiontareas.domain.model.tarjeta.id.TarjetaId;
import pds.gestiontareas.domain.model.tarjeta.model.Tarjeta;


@Controller
public class TableroController {

    @Autowired
    private TableroService tableroService;
    
    @Autowired
    private TarjetaService tarjetaService;
    
    @Autowired
    private PlantillaService plantillaService;

    private TableroId miTableroId;
    
    private Map<String, VBox> columnasVisuales = new HashMap<>();

    @FXML
    private Label lblNombreTablero;
    
    @FXML
    private HBox contenedorListas;
    
    @FXML
    private TextField txtFiltroTexto;
    private String textoFiltroActual = "";

    @FXML
    private Region etiquetaColor;
    
    @FXML
    private Button btnBloquear;
    
    @FXML
    private ColorPicker colorPickerFiltro;
    
    private String colorFiltroActual = null;
    
    @FXML
    public void initialize() {
    	
    	javafx.application.Platform.setImplicitExit(false);
    	
        boolean tableroCargado = false;
        
        while (!tableroCargado) {
            Alert inicioDialog = new Alert(Alert.AlertType.CONFIRMATION);
            inicioDialog.setTitle("Bienvenido a Gestión de Tareas PDS");
            inicioDialog.setHeaderText("Elige una opción para comenzar");
            
            ButtonType btnCrear = new ButtonType("Crear Nuevo Tablero");
            ButtonType btnAcceder = new ButtonType("Acceder con ID");
            ButtonType btnRecuperar = new ButtonType("Recuperar por Email");
            ButtonType btnImportar = new ButtonType("Importar YAML");
            ButtonType btnSalir = new ButtonType("Salir", ButtonData.CANCEL_CLOSE);
            
            inicioDialog.getButtonTypes().setAll(btnCrear, btnAcceder, btnRecuperar, btnImportar, btnSalir);
            
            Optional<ButtonType> resultado = inicioDialog.showAndWait();
            
            if (resultado.isPresent() && resultado.get() == btnCrear) {
                Dialog<String[]> dialogCrear = new Dialog<>();
                dialogCrear.setTitle("Nuevo Tablero");
                dialogCrear.setHeaderText("Introduce los datos para tu nuevo tablero:");

                ButtonType btnCrearTablero = new ButtonType("Crear Tablero", ButtonData.OK_DONE);
                dialogCrear.getDialogPane().getButtonTypes().addAll(btnCrearTablero, ButtonType.CANCEL);

                javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new javafx.geometry.Insets(20, 20, 10, 10));
                TextField txtNombreTablero = new TextField();
                txtNombreTablero.setPromptText("Ej: Proyecto PDS, Tareas Casa...");
                TextField txtEmailDuenio = new TextField();
                txtEmailDuenio.setPromptText("tu@email.com");
                grid.add(new Label("Nombre del Tablero:"), 0, 0);
                grid.add(txtNombreTablero, 1, 0);
                grid.add(new Label("Tu Email:"), 0, 1);
                grid.add(txtEmailDuenio, 1, 1);

                dialogCrear.getDialogPane().setContent(grid);

                dialogCrear.setResultConverter(dialogButton -> {
                    if (dialogButton == btnCrearTablero) {
                        return new String[]{txtNombreTablero.getText().trim(), txtEmailDuenio.getText().trim()};
                    }
                    return null;
                });

                Optional<String[]> resultadoCreacion = dialogCrear.showAndWait();

                if (resultadoCreacion.isPresent()) {
                    String[] datos = resultadoCreacion.get();
                    String nombre = datos[0];
                    String email = datos[1];

                    if (nombre.isEmpty() || email.isEmpty() || !email.contains("@")) {
                        mostrarError("Datos inválidos", "El nombre no puede estar vacío y el correo debe contener un '@'.");
                        continue;
                    }

                    miTableroId = tableroService.crearTablero(nombre, email);

                    tableroService.añadirListaATablero(miTableroId, "Por Hacer");
                    tableroService.añadirListaATablero(miTableroId, "En Progreso");
                    tableroService.añadirListaATablero(miTableroId, "Completadas");

                    Alert infoUrl = new Alert(Alert.AlertType.INFORMATION);
                    infoUrl.setTitle("Tablero Creado");
                    infoUrl.setHeaderText("¡Tablero '" + nombre + "' creado con éxito!");
                    
                    TextArea areaTexto = new TextArea("URL/ID Privada:\n" + miTableroId.getValor());
                    areaTexto.setEditable(false);
                    areaTexto.setWrapText(true);
                    areaTexto.setMaxHeight(60);
                    
                    infoUrl.getDialogPane().setContent(new VBox(10, 
                        new Label("Guarda este código. Es tu URL privada para entrar o compartir:"), 
                        areaTexto));
                    infoUrl.showAndWait();
                    
                    tableroCargado = true;
                }
            } else if (resultado.isPresent() && resultado.get() == btnAcceder) {
                TextInputDialog dialogUrl = new TextInputDialog();
                dialogUrl.setTitle("Acceder a Tablero");
                dialogUrl.setHeaderText("Introduce la URL o ID privado del tablero:");
                dialogUrl.setContentText("URL/ID:");
                Optional<String> urlOpt = dialogUrl.showAndWait();
                
                if (urlOpt.isPresent() && !urlOpt.get().trim().isEmpty()) {
                    try {
                        miTableroId = new TableroId(urlOpt.get().trim());
                        tableroService.obtenerTablero(miTableroId); 
                        tableroCargado = true;
                    } catch (Exception e) {
                        mostrarError("Tablero no encontrado", "Asegúrate de que la URL/ID que te han pasado es correcta.");
                    }
                }
            } else if (resultado.isPresent() && resultado.get() == btnRecuperar) {
                TextInputDialog dialogEmail = new TextInputDialog();
                dialogEmail.setTitle("Recuperar Tableros");
                dialogEmail.setHeaderText("Introduce el correo con el que creaste tu tablero:");
                dialogEmail.setContentText("Email:");
                Optional<String> emailOpt = dialogEmail.showAndWait();
                
                if (emailOpt.isPresent() && !emailOpt.get().trim().isEmpty()) {
                    String emailBuscado = emailOpt.get().trim();
                    List<Tablero> misTableros = tableroService.obtenerTablerosPorEmail(emailBuscado);
                    
                    if (misTableros.isEmpty()) {
                        mostrarError("Sin resultados", "No hemos encontrado ningún tablero asociado al correo: " + emailBuscado);
                    } else if (misTableros.size() == 1) {
                        miTableroId = misTableros.get(0).getId();
                        tableroCargado = true;
                    } else {
                        List<String> opciones = misTableros.stream()
                            .map(t -> t.getNombre() + " (" + t.getId().getValor() + ")")
                            .collect(Collectors.toList());
                            
                        ChoiceDialog<String> dialogSeleccion = new ChoiceDialog<>(opciones.get(0), opciones);
                        dialogSeleccion.setTitle("Mis Tableros");
                        dialogSeleccion.setHeaderText("Hemos encontrado varios tableros. Elige uno:");
                        dialogSeleccion.setContentText("Tablero:");
                        
                        Optional<String> seleccion = dialogSeleccion.showAndWait();
                        if (seleccion.isPresent()) {
                            String idSeleccionado = seleccion.get().substring(seleccion.get().lastIndexOf("(") + 1, seleccion.get().lastIndexOf(")"));
                            miTableroId = new TableroId(idSeleccionado);
                            tableroCargado = true;
                        }
                    }
                }
            } else if (resultado.isPresent() && resultado.get() == btnImportar) {
            	TextInputDialog dialogEmail = new TextInputDialog();
                dialogEmail.setTitle("Importar Plantilla");
                dialogEmail.setHeaderText("Introduce tu correo electrónico para ser el dueño:");
                dialogEmail.setContentText("Email:");
                Optional<String> emailOpt = dialogEmail.showAndWait();
                
                if (emailOpt.isPresent() && !emailOpt.get().trim().isEmpty()) {
                    String email = emailOpt.get().trim();
                    
                    if(!email.contains("@")) {
                        mostrarError("Email inválido", "Por favor, introduce un correo real.");
                        continue;
                    }
                    javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
                    fileChooser.setTitle("Seleccionar Plantilla YAML");
                    fileChooser.getExtensionFilters().add(
                        new javafx.stage.FileChooser.ExtensionFilter("Archivos YAML (*.yaml, *.yml)", "*.yaml", "*.yml")
                    );
                    
                    java.io.File archivoSeleccionado = fileChooser.showOpenDialog(null);
                    
                    if (archivoSeleccionado != null) {
                        try {
                            miTableroId = plantillaService.crearTableroDesdeYaml(archivoSeleccionado, email);
                            
                            Alert infoUrl = new Alert(Alert.AlertType.INFORMATION);
                            infoUrl.setTitle("Plantilla Importada");
                            infoUrl.setHeaderText("¡Tablero creado con éxito desde la plantilla!");
                            
                            TextArea areaTexto = new TextArea("URL/ID Privada:\n" + miTableroId.getValor());
                            areaTexto.setEditable(false);
                            areaTexto.setWrapText(true);
                            areaTexto.setMaxHeight(60);
                            
                            infoUrl.getDialogPane().setContent(new VBox(10, 
                                new Label("Guarda este código. Es tu URL privada para entrar o compartir:"), 
                                areaTexto));
                            infoUrl.showAndWait();
                            
                            tableroCargado = true; 
                            
                        } catch (Exception ex) {
                            mostrarError("Error de Plantilla", "No se pudo leer el archivo YAML.\nDetalle: " + ex.getMessage());
                        }
                    }
                }
                
            } else {
                System.exit(0);
            }
        }
        
        cargarDatosTableroEnPantalla();
    }
    
    private void mostrarError(String titulo, String mensaje) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setHeaderText(titulo);
        error.setContentText(mensaje);
        error.showAndWait();
    }
    
    private void cargarDatosTableroEnPantalla() {
    	TableroDTO tableroReal = tableroService.obtenerTableroDTO(miTableroId);

        if (lblNombreTablero != null) {
            String nombreCompleto = tableroReal.getNombre();
            
            if (nombreCompleto.length() > 15) {
                lblNombreTablero.setText(nombreCompleto.substring(0, 15) + "...");
            } else {
                lblNombreTablero.setText(nombreCompleto);
            }
            
            lblNombreTablero.setTooltip(new javafx.scene.control.Tooltip(nombreCompleto));
        }

        boolean estaBloqueado = tableroReal.isBloqueado();
        
        if (btnBloquear != null) {
            if (estaBloqueado) {
                btnBloquear.setText("Desbloquear Tablero");
                btnBloquear.setStyle("-fx-background-color: #ef5350; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            } else {
                btnBloquear.setText("Bloquear Tablero");
                btnBloquear.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2); -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            }
        }

        for (ListaDTO lista : tableroReal.getListas()) {
            VBox contenedorDeEstaLista = crearColumnaVisual(lista.getTitulo(), estaBloqueado);

            if (lista.getTarjetasIds() != null) {
            	for (String idTarjeta : lista.getTarjetasIds()) {
            	    try {
            	        Tarjeta datosTarjeta = tarjetaService.obtenerTarjeta(idTarjeta);

            	        boolean pasaFiltroColor = false;
            	        if (colorFiltroActual == null) {
            	            pasaFiltroColor = true; 
            	        } else if (colorFiltroActual.equals("SIN_ETIQUETA")) {
            	            pasaFiltroColor = datosTarjeta.getEtiquetas().isEmpty();
            	        } else {
            	            pasaFiltroColor = datosTarjeta.tieneEtiqueta(colorFiltroActual); 
            	        }

            	        boolean pasaFiltroTexto = true;
            	        if (textoFiltroActual != null && !textoFiltroActual.trim().isEmpty()) {
            	            pasaFiltroTexto = datosTarjeta.getTitulo().toLowerCase().contains(textoFiltroActual.toLowerCase());
            	        }

            	        if (pasaFiltroColor && pasaFiltroTexto) {
            	        	TarjetaUIComponent tarjetaGrafica = new TarjetaUIComponent(
            	        		    datosTarjeta.getTitulo(), idTarjeta, lista.getTitulo(), 
            	        		    miTableroId, tarjetaService, tableroService, this
            	        	);
            	        	contenedorDeEstaLista.getChildren().add(tarjetaGrafica);
            	        }

            	    } catch (IllegalArgumentException ex) {
            	        System.out.println("Aviso: Ignorando tarjeta fantasma con ID " + idTarjeta);
            	    }
            	}
            }
        }
        crearBotonAñadirLista(estaBloqueado);
    }

    @FXML
    public void alternarBloqueoUI() {
        tableroService.alternarBloqueo(miTableroId);
        
        contenedorListas.getChildren().clear();
        columnasVisuales.clear();
        cargarDatosTableroEnPantalla();
    }

    private VBox crearColumnaVisual(String nombreLista, boolean estaBloqueado) {
        VBox columna = new VBox();
        columna.setStyle("-fx-background-color: #f4f5f7; -fx-padding: 10; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 4);");
        columna.setPrefWidth(250);
        columna.setMinHeight(Region.USE_PREF_SIZE); 
        columna.setMaxHeight(Region.USE_PREF_SIZE);
        columna.setSpacing(10);

        Label titulo = new Label(nombreLista);
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #172b4d;");

        Button btnBorrarLista = new Button("✕");
        
        String estiloNormal = "-fx-background-color: transparent; -fx-text-fill: #a5adba; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 5 0 5;";
        String estiloHover = "-fx-background-color: transparent; -fx-text-fill: #ef5350; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 5 0 5;";
        
        btnBorrarLista.setStyle(estiloNormal);
        
        btnBorrarLista.setOnMouseEntered(e -> btnBorrarLista.setStyle(estiloHover));
        btnBorrarLista.setOnMouseExited(e -> btnBorrarLista.setStyle(estiloNormal));
        
        Button btnAjustes = new Button("⚙️");
        btnAjustes.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 5 0 5;");
        btnAjustes.setOnAction(e -> abrirAjustesLista(nombreLista));
        
        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);
        
        HBox cabecera = new HBox(titulo, espaciador, btnAjustes, btnBorrarLista);
        cabecera.setAlignment(Pos.CENTER_LEFT);
        cabecera.setStyle("-fx-padding: 0 0 10 0;");
        
        btnBorrarLista.setOnAction(e -> {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Eliminar Lista");
            confirmacion.setHeaderText("¿Borrar la lista '" + nombreLista + "'?");
            confirmacion.setContentText("Todas las tarjetas que contenga desaparecerán de la vista.");
            
            confirmacion.showAndWait().ifPresent(respuesta -> {
                if (respuesta == ButtonType.OK) {
                    tableroService.eliminarLista(miTableroId, nombreLista);
                    contenedorListas.getChildren().remove(columna);
                    columnasVisuales.remove(nombreLista);
                }
            });
        });

        VBox contenedorTarjetas = new VBox();
        contenedorTarjetas.setSpacing(8);
        contenedorTarjetas.setMinHeight(Region.USE_PREF_SIZE);

        Button btnAñadir = new Button("+ Añadir tarjeta");
        btnAñadir.setStyle("-fx-background-color: transparent; -fx-text-fill: #5e6c84; -fx-cursor: hand;");
        btnAñadir.setMaxWidth(Double.MAX_VALUE);
        
        btnAñadir.setOnAction(e -> {
            Dialog<ButtonType> dialogCrear = new Dialog<>();
            dialogCrear.setTitle("Nueva Tarjeta");
            dialogCrear.setHeaderText("Añadir tarea a: " + nombreLista);
            
            TextField txtNombre = new TextField();
            txtNombre.setPromptText("Nombre de la tarjeta...");
            
            ComboBox<String> comboTipo = new ComboBox<>();
            comboTipo.getItems().addAll("Tarea Simple", "Checklist");
            comboTipo.setValue("Tarea Simple");
            
            VBox cajita = new VBox(10, new Label("Nombre:"), txtNombre, new Label("Tipo de Tarjeta:"), comboTipo);
            dialogCrear.getDialogPane().setContent(cajita);
            dialogCrear.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            
            dialogCrear.showAndWait().ifPresent(btn -> {
            	if (btn == ButtonType.OK && !txtNombre.getText().trim().isEmpty()) {
            	    try {
            	        String tipoSeleccionado = comboTipo.getValue().equals("Checklist") ? "CHECKLIST" : "TAREA";            	        
            	        TarjetaId nuevaId = tableroService.crearTarjetaEnLista(miTableroId, nombreLista, txtNombre.getText(), tipoSeleccionado);
            	        TarjetaUIComponent tarjetaGrafica = new TarjetaUIComponent(
            	        	    txtNombre.getText(), nuevaId.getValor(), nombreLista, 
            	        	    miTableroId, tarjetaService, tableroService, this
            	        	);
            	        	contenedorTarjetas.getChildren().add(tarjetaGrafica);
            	    } catch (IllegalStateException ex) {
            	        Alert alerta = new Alert(Alert.AlertType.WARNING);
            	        alerta.setTitle("Límite de lista alcanzado");
            	        alerta.setHeaderText("No se puede crear la tarea");
            	        alerta.setContentText(ex.getMessage());
            	        alerta.showAndWait();
            	    }
            	}
            });
        });

        if (!estaBloqueado) {
            columna.getChildren().addAll(cabecera, contenedorTarjetas, btnAñadir);
        } else {
            btnBorrarLista.setVisible(false);
            btnAjustes.setVisible(false);
            columna.getChildren().addAll(cabecera, contenedorTarjetas);
        }
        
        if (contenedorListas != null) {
            contenedorListas.setAlignment(Pos.TOP_LEFT);
            contenedorListas.getChildren().add(columna);
        }
        
        columnasVisuales.put(nombreLista, contenedorTarjetas);
        return contenedorTarjetas; 
    }

    private void crearBotonAñadirLista(boolean estaBloqueado) {
        if (estaBloqueado) return;
        
        Button btnAñadirLista = new Button("+ Añadir otra lista");
        btnAñadirLista.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5); -fx-text-fill: #172b4d; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 10;");
        btnAñadirLista.setPrefWidth(250);
        btnAñadirLista.setMinHeight(40);

        btnAñadirLista.setOnAction(e -> {
            TextInputDialog dialogo = new TextInputDialog();
            dialogo.setTitle("Nueva Lista");
            dialogo.setHeaderText("Crear una nueva lista de tareas");
            dialogo.setContentText("Nombre de la lista:");

            dialogo.showAndWait().ifPresent(nombre -> {
                if (!nombre.trim().isEmpty()) {
                    tableroService.añadirListaATablero(miTableroId, nombre);
                    contenedorListas.getChildren().remove(btnAñadirLista);
                    crearColumnaVisual(nombre, false);
                    contenedorListas.getChildren().add(btnAñadirLista);
                }
            });
        });

        contenedorListas.getChildren().add(btnAñadirLista);
    }

    @FXML
    private void cambiarColorEtiqueta() {
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.show();

        colorPicker.setOnAction(e -> {
            Color nuevoColor = colorPicker.getValue();
            String hexColor = toHexString(nuevoColor);
            etiquetaColor.setStyle("-fx-background-color: #" + hexColor + "; -fx-background-radius: 4; -fx-min-width: 45; -fx-min-height: 8;");
        });
    }

    @FXML
    public void mostrarHistorial() {
        List<String> historial = tableroService.obtenerHistorialTextos(miTableroId);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Historial de Actividad");
        dialog.setHeaderText("Registro de movimientos del tablero");
        
        dialog.setResizable(true);
        dialog.getDialogPane().setMinWidth(400);
        dialog.getDialogPane().setMinHeight(300);
        dialog.getDialogPane().setPrefWidth(500);

        ListView<String> listaVisual = new ListView<>();
        
        if (historial.isEmpty()) {
            listaVisual.getItems().add("Aún no hay actividad en este tablero.");
        } else {
            Collections.reverse(historial);
            listaVisual.getItems().addAll(historial);
        }

        dialog.getDialogPane().setContent(listaVisual);
        
        ButtonType btnBorrar = new ButtonType("Borrar Historial", ButtonData.LEFT);
        dialog.getDialogPane().getButtonTypes().addAll(btnBorrar, ButtonType.CLOSE);

        Button botonBorrarInterfaz = (Button) dialog.getDialogPane().lookupButton(btnBorrar);
        if (botonBorrarInterfaz != null) {
            botonBorrarInterfaz.setStyle("-fx-text-fill: #ef5350; -fx-font-weight: bold; -fx-cursor: hand;");
            
            botonBorrarInterfaz.addEventFilter(ActionEvent.ACTION, e -> {
                e.consume();
                
                Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                confirmacion.setTitle("Borrar Historial");
                confirmacion.setHeaderText("¿Estás seguro de que quieres vaciar el historial?");
                confirmacion.setContentText("Esta acción no se puede deshacer.");
                
                confirmacion.showAndWait().ifPresent(respuesta -> {
                    if (respuesta == ButtonType.OK) {
                        tableroService.limpiarHistorial(miTableroId);
                        dialog.close();
                        mostrarHistorial();
                    }
                });
            });
        }

        dialog.showAndWait();
    }
    
    @FXML
    public void compartirTableroUI() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Compartir Tablero");
        dialog.setHeaderText("Invita a otros usuarios a colaborar");

        TextField txtDueño = new TextField();
        txtDueño.setPromptText("Tu email (Para verificar que eres el dueño)");
        TextField txtInvitado = new TextField();
        txtInvitado.setPromptText("Email a invitar");

        VBox content = new VBox(10, new Label("Firma de autorización:"), txtDueño, new Label("Usuario a invitar:"), txtInvitado);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                	tableroService.compartirTablero(miTableroId, txtDueño.getText().trim(), txtInvitado.getText().trim());
                    mostrarInformacion("Éxito", "El tablero se ha compartido con " + txtInvitado.getText().trim());
                } catch (Exception e) {
                    mostrarError("No se pudo compartir", e.getMessage());
                }
            }
        });
    }
    
    @FXML
    public void aplicarFiltro() {
        if (colorPickerFiltro != null && colorPickerFiltro.getValue() != null) {
            colorFiltroActual = "#" + colorPickerFiltro.getValue().toString().substring(2, 8);
            
            contenedorListas.getChildren().clear();
            columnasVisuales.clear();
            cargarDatosTableroEnPantalla();
        }
    }
    
    @FXML
    public void aplicarFiltroTexto(javafx.scene.input.KeyEvent event) {
        if (txtFiltroTexto != null) {
            textoFiltroActual = txtFiltroTexto.getText();

            contenedorListas.getChildren().clear();
            columnasVisuales.clear();
            cargarDatosTableroEnPantalla();
        }
    }

    @FXML
    public void filtrarSinEtiquetas() {
        colorFiltroActual = "SIN_ETIQUETA"; 
        
        contenedorListas.getChildren().clear();
        columnasVisuales.clear();
        cargarDatosTableroEnPantalla();
    }

    @FXML
    public void limpiarFiltro() {
        colorFiltroActual = null; 
        textoFiltroActual = "";
        
        if (txtFiltroTexto != null) {
            txtFiltroTexto.clear();
        }
        
        contenedorListas.getChildren().clear();
        columnasVisuales.clear();
        cargarDatosTableroEnPantalla();
    }
    
    private String toHexString(Color color) {
        return String.format("%02X%02X%02X",
            (int)(color.getRed() * 255),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255));
    }
    
    private void abrirAjustesLista(String nombreLista) {
        try {
            Tablero tablero = tableroService.obtenerTablero(miTableroId);
            pds.gestiontareas.domain.model.tablero.model.ListaTareas listaDatos = tablero.getListas().stream()
                    .filter(l -> l.getTitulo().equals(nombreLista))
                    .findFirst().orElse(null);

            if (listaDatos == null) return;

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Ajustes de la lista: " + nombreLista);
            dialog.setHeaderText("Configurar reglas y límites Kanban");

            javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
            grid.setHgap(10); 
            grid.setVgap(10);
            grid.setPadding(new javafx.geometry.Insets(20, 20, 10, 10));

            TextField txtLimite = new TextField();
            if (listaDatos.getLimiteTarjetas() != null) {
                txtLimite.setText(String.valueOf(listaDatos.getLimiteTarjetas()));
            }
            txtLimite.setPromptText("Ej: 5 (Vacío = sin límite)");

            ComboBox<String> comboRequerida = new ComboBox<>();
            comboRequerida.getItems().add("Ninguna (Libre)");
            for (pds.gestiontareas.domain.model.tablero.model.ListaTareas l : tablero.getListas()) {
                if (!l.getTitulo().equals(nombreLista)) {
                    comboRequerida.getItems().add(l.getTitulo());
                }
            }
            
            if (listaDatos.getListasPrecedentesRequeridas() != null && !listaDatos.getListasPrecedentesRequeridas().isEmpty()) {
                comboRequerida.setValue(listaDatos.getListasPrecedentesRequeridas().get(0));
            } else {
                comboRequerida.setValue("Ninguna (Libre)");
            }

            grid.add(new Label("Límite máximo de tareas (WIP):"), 0, 0);
            grid.add(txtLimite, 1, 0);
            grid.add(new Label("Obligatorio venir desde la lista:"), 0, 1);
            grid.add(comboRequerida, 1, 1);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Integer nuevoLimite = null;
                    if (!txtLimite.getText().trim().isEmpty()) {
                        try {
                            nuevoLimite = Integer.parseInt(txtLimite.getText().trim());
                        } catch (NumberFormatException e) {}
                    }
                    
                    String requerida = comboRequerida.getValue();
                    if ("Ninguna (Libre)".equals(requerida)) requerida = null;

                    tableroService.actualizarReglasLista(miTableroId, nombreLista, nuevoLimite, requerida);
                    
                    contenedorListas.getChildren().clear();
                    columnasVisuales.clear();
                    cargarDatosTableroEnPantalla();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void recargarTablero() {
        if (contenedorListas != null) {
            contenedorListas.getChildren().clear();
        }
        columnasVisuales.clear();
        cargarDatosTableroEnPantalla();
    }
    
    private void mostrarInformacion(String titulo, String mensaje) {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setHeaderText(titulo);
        info.setContentText(mensaje);
        info.showAndWait();
    }
    
    @FXML
    public void volverAlMenuPrincipal() {
        miTableroId = null;
        if (contenedorListas != null) {
            contenedorListas.getChildren().clear();
        }
        columnasVisuales.clear();
        
        initialize();
    }
}