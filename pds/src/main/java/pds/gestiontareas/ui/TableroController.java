package pds.gestiontareas.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import pds.gestiontareas.application.PlantillaService;
import pds.gestiontareas.application.TableroService;
import pds.gestiontareas.application.TarjetaService;
import pds.gestiontareas.application.dto.ItemChecklistDTO;
import pds.gestiontareas.application.dto.ListaDTO;
import pds.gestiontareas.application.dto.TableroDTO;
import pds.gestiontareas.application.dto.TarjetaDTO;
import pds.gestiontareas.application.dto.plantilla.TableroPlantillaDTO;
import pds.gestiontareas.domain.model.tablero.id.TableroId;

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

    @FXML private Label lblNombreTablero;
    @FXML private HBox contenedorListas;
    @FXML private TextField txtFiltroTexto;
    @FXML private Region etiquetaColor;
    @FXML private Button btnBloquear;
    @FXML private ColorPicker colorPickerFiltro;
    
    private String textoFiltroActual = "";
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

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 20, 10, 10));
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

                    mostrarInformacion("Tablero Creado", "¡Tablero '" + nombre + "' creado con éxito!\n\nGuarda esta URL/ID Privada:\n" + miTableroId.getValor());
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
                        tableroService.obtenerDatosTablero(miTableroId); 
                        tableroCargado = true;
                    } catch (Exception e) {
                        mostrarError("Tablero no encontrado", "Asegúrate de que la URL/ID que te han pasado es correcta.");
                    }
                }
            } else if (resultado.isPresent() && resultado.get() == btnRecuperar) {
                TextInputDialog dialogEmail = new TextInputDialog();
                dialogEmail.setTitle("Recuperar Tableros");
                dialogEmail.setHeaderText("Introduce el correo:");
                dialogEmail.setContentText("Email:");
                Optional<String> emailOpt = dialogEmail.showAndWait();
                
                if (emailOpt.isPresent() && !emailOpt.get().trim().isEmpty()) {
                    List<String> idsTableros = tableroService.obtenerTablerosPorEmail(emailOpt.get().trim())
                            .stream().map(t -> t.getId().getValor()).collect(Collectors.toList());
                    
                    if (idsTableros.isEmpty()) {
                        mostrarError("Sin resultados", "No hemos encontrado ningún tablero.");
                    } else if (idsTableros.size() == 1) {
                        miTableroId = new TableroId(idsTableros.get(0));
                        tableroCargado = true;
                    } else {
                        ChoiceDialog<String> dialogSeleccion = new ChoiceDialog<>(idsTableros.get(0), idsTableros);
                        dialogSeleccion.setTitle("Mis Tableros");
                        dialogSeleccion.setHeaderText("Elige un ID:");
                        Optional<String> seleccion = dialogSeleccion.showAndWait();
                        if (seleccion.isPresent()) {
                            miTableroId = new TableroId(seleccion.get());
                            tableroCargado = true;
                        }
                    }
                }
            } else if (resultado.isPresent() && resultado.get() == btnImportar) {
                TextInputDialog dialogEmail = new TextInputDialog();
                dialogEmail.setTitle("Importar Plantilla");
                dialogEmail.setHeaderText("Introduce tu correo electrónico:");
                dialogEmail.setContentText("Email:");
                Optional<String> emailOpt = dialogEmail.showAndWait();
                
                if (emailOpt.isPresent() && !emailOpt.get().trim().isEmpty()) {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Seleccionar Plantilla YAML");
                    fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Archivos YAML (*.yaml, *.yml)", "*.yaml", "*.yml")
                    );
                    
                    java.io.File archivoSeleccionado = fileChooser.showOpenDialog(null);
                    if (archivoSeleccionado != null) {
                        try {
                            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                            TableroPlantillaDTO plantillaDTO = mapper.readValue(archivoSeleccionado, TableroPlantillaDTO.class);
                            
                            miTableroId = plantillaService.crearTableroDesdePlantilla(plantillaDTO, emailOpt.get().trim());
                            
                            mostrarInformacion("Plantilla Importada", "¡Tablero creado con éxito desde la plantilla!\n\nID Privada:\n" + miTableroId.getValor());
                            tableroCargado = true; 
                        } catch (Exception ex) {
                            mostrarError("Error de Plantilla", "No se pudo leer el archivo YAML.\n" + ex.getMessage());
                        }
                    }
                }
            } else {
                System.exit(0);
            }
        }
        cargarDatosTableroEnPantalla();
    }
    
    private void mostrarInformacion(String titulo, String mensaje) {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle(titulo);
        info.setHeaderText(null);
        
        TextArea areaTexto = new TextArea(mensaje);
        areaTexto.setEditable(false);
        areaTexto.setWrapText(true);
        areaTexto.setMaxHeight(100);
        
        info.getDialogPane().setContent(areaTexto);
        info.showAndWait();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setHeaderText(titulo);
        error.setContentText(mensaje);
        error.showAndWait();
    }
    
    private void cargarDatosTableroEnPantalla() {
        TableroDTO tableroDatos = tableroService.obtenerDatosTablero(miTableroId);
        
        if (lblNombreTablero != null) {
            String nombreCompleto = tableroDatos.getNombre();
            if (nombreCompleto.length() > 15) {
                lblNombreTablero.setText(nombreCompleto.substring(0, 15) + "...");
            } else {
                lblNombreTablero.setText(nombreCompleto);
            }
            lblNombreTablero.setTooltip(new Tooltip(nombreCompleto));
        }

        boolean estaBloqueado = tableroDatos.isBloqueado();
        
        if (btnBloquear != null) {
        	if (estaBloqueado) {
                btnBloquear.setText("Desbloquear Tablero");
                btnBloquear.setStyle("-fx-background-color: rgba(239, 83, 80, 0.9); " +
                                     "-fx-text-fill: white; " +
                                     "-fx-font-weight: bold; " +
                                     "-fx-cursor: hand; " +
                                     "-fx-font-size: 14px; " +
                                     "-fx-padding: 10 20 10 20;");
            } else {
                btnBloquear.setText("Bloquear Tablero");
                btnBloquear.setStyle("-fx-background-color: rgba(255, 255, 255, 0.15); " +
                                     "-fx-text-fill: white; " +
                                     "-fx-font-weight: bold; " +
                                     "-fx-cursor: hand; " +
                                     "-fx-font-size: 14px; " +
                                     "-fx-padding: 10 20 10 20;");
            }
        }

        for (ListaDTO lista : tableroDatos.getListas()) {
            VBox contenedorDeEstaLista = crearColumnaVisual(lista.getNombre(), estaBloqueado);

            if (lista.getTarjetas() != null) {
                for (TarjetaDTO tarjeta : lista.getTarjetas()) {
                    boolean pasaFiltroColor = false;
                    
                    if (colorFiltroActual == null) {
                        pasaFiltroColor = true; 
                    } else if (colorFiltroActual.equals("SIN_ETIQUETA")) {
                        pasaFiltroColor = tarjeta.getColoresEtiquetas().isEmpty();
                    } else {
                        pasaFiltroColor = tarjeta.getColoresEtiquetas().contains(colorFiltroActual); 
                    }

                    boolean pasaFiltroTexto = true;
                    if (textoFiltroActual != null && !textoFiltroActual.trim().isEmpty()) {
                        pasaFiltroTexto = tarjeta.getTitulo().toLowerCase().contains(textoFiltroActual.toLowerCase());
                    }

                    if (pasaFiltroColor && pasaFiltroTexto) {
                        contenedorDeEstaLista.getChildren().add(
                            crearTarjetaVisual(tarjeta, lista.getNombre(), contenedorDeEstaLista)
                        );
                    }
                }
            }
        }
        crearBotonAñadirLista(estaBloqueado);
    }

    @FXML
    public void alternarBloqueoUI() {
        tableroService.alternarBloqueo(miTableroId);
        recargarTablero();
    }

    private VBox crearColumnaVisual(String nombreLista, boolean estaBloqueado) {
        VBox columna = new VBox();
        columna.setStyle("-fx-background-color: #f4f5f7; -fx-padding: 10; -fx-background-radius: 5;");
        columna.setPrefWidth(250);
        columna.setSpacing(10);

        Label titulo = new Label(nombreLista);
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #172b4d;");

        Button btnBorrarLista = new Button("✕");
        btnBorrarLista.setStyle("-fx-background-color: transparent; -fx-text-fill: #a5adba; -fx-cursor: hand;");
        
        Button btnAjustes = new Button("⚙️");
        btnAjustes.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        btnAjustes.setOnAction(e -> abrirAjustesLista(nombreLista));
        
        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);
        
        HBox cabecera = new HBox(titulo, espaciador, btnAjustes, btnBorrarLista);
        cabecera.setAlignment(Pos.CENTER_LEFT);
        
        btnBorrarLista.setOnAction(e -> {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setHeaderText("¿Borrar la lista '" + nombreLista + "'?");
            confirmacion.showAndWait().ifPresent(respuesta -> {
                if (respuesta == ButtonType.OK) {
                    tableroService.eliminarLista(miTableroId, nombreLista);
                    tableroService.registrarAccionManual(miTableroId, "Se eliminó la lista '" + nombreLista + "'.");
                    contenedorListas.getChildren().remove(columna);
                    columnasVisuales.remove(nombreLista);
                }
            });
        });

        VBox contenedorTarjetas = new VBox();
        contenedorTarjetas.setSpacing(8);

        Button btnAñadir = new Button("+ Añadir tarjeta");
        btnAñadir.setStyle("-fx-background-color: transparent; -fx-text-fill: #5e6c84; -fx-cursor: hand;");
        
        btnAñadir.setOnAction(e -> {
            Dialog<ButtonType> dialogCrear = new Dialog<>();
            dialogCrear.setHeaderText("Añadir tarea a: " + nombreLista);
            TextField txtNombre = new TextField();
            ComboBox<String> comboTipo = new ComboBox<>();
            comboTipo.getItems().addAll("Tarea Simple", "Checklist");
            comboTipo.setValue("Tarea Simple");
            dialogCrear.getDialogPane().setContent(new VBox(10, new Label("Nombre:"), txtNombre, new Label("Tipo:"), comboTipo));
            dialogCrear.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            
            dialogCrear.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.OK && !txtNombre.getText().trim().isEmpty()) {
                    try {
                        String tipoSeleccionado = comboTipo.getValue().equals("Checklist") ? "CHECKLIST" : "TAREA";            	        
                        tableroService.crearTarjetaEnLista(miTableroId, nombreLista, txtNombre.getText(), tipoSeleccionado);
                        recargarTablero();
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
            contenedorListas.getChildren().add(columna);
        }
        columnasVisuales.put(nombreLista, contenedorTarjetas);
        return contenedorTarjetas; 
    }

    private void crearBotonAñadirLista(boolean estaBloqueado) {
        if (estaBloqueado) return;
        
        Button btnAñadirLista = new Button("+ Añadir otra lista");
        btnAñadirLista.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5); -fx-cursor: hand; -fx-padding: 10;");

        btnAñadirLista.setOnAction(e -> {
            TextInputDialog dialogo = new TextInputDialog();
            dialogo.setHeaderText("Nueva Lista");
            dialogo.showAndWait().ifPresent(nombre -> {
                if (!nombre.trim().isEmpty()) {
                    tableroService.añadirListaATablero(miTableroId, nombre);
                    recargarTablero();
                }
            });
        });

        if (contenedorListas != null) {
            contenedorListas.getChildren().add(btnAñadirLista);
        }
    }
    
    private VBox crearTarjetaVisual(TarjetaDTO datosTarjetaIniciales, String nombreListaOrigen, VBox contenedorActual) {
        VBox tarjeta = new VBox();
        tarjeta.getStyleClass().add("tarjeta");

        final String[] listaActual = {nombreListaOrigen};
        final VBox[] cajaActual = {contenedorActual};

        HBox contenedorEtiquetas = new HBox(5);
        for (String hexColor : datosTarjetaIniciales.getColoresEtiquetas()) {
            Region rectColor = new Region();
            rectColor.setStyle("-fx-background-color: " + hexColor + "; -fx-min-width: 40; -fx-min-height: 8; -fx-background-radius: 4;");
            contenedorEtiquetas.getChildren().add(rectColor);
        }

        Label contenido = new Label(datosTarjetaIniciales.getTitulo());
        contenido.setWrapText(true);
        contenido.getStyleClass().add("texto-tarjeta");
        
        tarjeta.getChildren().addAll(contenedorEtiquetas, contenido);

        tarjeta.setOnMouseClicked(event -> {
            TarjetaDTO datosActualizados = tarjetaService.obtenerDatosTarjeta(datosTarjetaIniciales.getId());

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Detalles");
            dialog.setHeaderText("Tarea: " + datosActualizados.getTitulo());
            dialog.getDialogPane().setPrefWidth(520);

            VBox contenidoDialogo = new VBox(20);
            contenidoDialogo.setStyle("-fx-padding: 20; -fx-background-color: #f4f5f7;");

            TextArea txtDesc = new TextArea(datosActualizados.getDescripcion());
            txtDesc.setPrefRowCount(3);
            contenidoDialogo.getChildren().add(new VBox(8, new Label("Descripción:"), txtDesc));

            VBox cajaChecklistCompleta = new VBox(8);
            if (datosActualizados.isEsChecklist()) {
                VBox listaSubtareas = new VBox(8);
                for (ItemChecklistDTO item : datosActualizados.getItemsChecklist()) {
                    listaSubtareas.getChildren().add(crearFilaSubtarea(item.getTexto(), item.isCompletado(), datosActualizados.getId(), datosActualizados.getTitulo(), listaSubtareas));
                }

                TextField txtNuevoItem = new TextField();
                Button btnAñadirItem = new Button("Añadir");
                btnAñadirItem.setOnAction(ev -> {
                    if (!txtNuevoItem.getText().trim().isEmpty()) {
                        tarjetaService.añadirItemChecklist(datosActualizados.getId(), txtNuevoItem.getText());
                        listaSubtareas.getChildren().add(crearFilaSubtarea(txtNuevoItem.getText(), false, datosActualizados.getId(), datosActualizados.getTitulo(), listaSubtareas));
                        txtNuevoItem.clear();
                    }
                });
                cajaChecklistCompleta.getChildren().addAll(new Label("Checklist:"), listaSubtareas, new HBox(10, txtNuevoItem, btnAñadirItem));
                contenidoDialogo.getChildren().add(cajaChecklistCompleta);
            }

            ColorPicker colorPicker = new ColorPicker(Color.web("#ef5350"));
            Button btnAñadirColor = new Button("Añadir Color");
            Button btnQuitarColor = new Button("Quitar Color");
            
            btnAñadirColor.setOnAction(e -> tarjetaService.añadirEtiqueta(datosActualizados.getId(), "Etiqueta", "#" + colorPicker.getValue().toString().substring(2, 8)));
            btnQuitarColor.setOnAction(e -> tarjetaService.quitarEtiqueta(datosActualizados.getId(), "#" + colorPicker.getValue().toString().substring(2, 8)));
            
            contenidoDialogo.getChildren().add(new VBox(8, new Label("Etiquetas:"), new HBox(10, colorPicker, btnAñadirColor, btnQuitarColor)));

            ComboBox<String> comboListas = new ComboBox<>();
            comboListas.getItems().addAll(tableroService.obtenerNombresListas(miTableroId)); 
            comboListas.setValue(listaActual[0]); 

            CheckBox chkCompletada = new CheckBox("Marcar tarjeta como COMPLETADA");
            chkCompletada.setSelected(datosActualizados.isCompletada());
           
            chkCompletada.selectedProperty().addListener((obs, old, isCompleted) -> {
                if (isCompleted) {
                    if (!comboListas.getItems().contains("Completadas")) comboListas.getItems().add("Completadas");
                    comboListas.setValue("Completadas");
                } else {
                    comboListas.setValue(listaActual[0]);
                }
            });
            
            contenidoDialogo.getChildren().addAll(new HBox(10, new Label("Mover a:"), comboListas), chkCompletada);
            
            ScrollPane scrollPane = new ScrollPane(contenidoDialogo);
            scrollPane.setFitToWidth(true); 
            dialog.getDialogPane().setContent(scrollPane);

            ButtonType btnGuardar = new ButtonType("Guardar Cambios", ButtonData.OK_DONE);
            ButtonType btnEliminar = new ButtonType("Eliminar", ButtonData.LEFT);
            dialog.getDialogPane().getButtonTypes().setAll(btnEliminar, btnGuardar, ButtonType.CANCEL);

            Button botonEliminarInterfaz = (Button) dialog.getDialogPane().lookupButton(btnEliminar);
            if (botonEliminarInterfaz != null) {
                botonEliminarInterfaz.addEventFilter(ActionEvent.ACTION, e -> {
                    tableroService.eliminarTarjetaDeLista(miTableroId, listaActual[0], datosActualizados.getId());
                    tarjetaService.eliminarTarjeta(datosActualizados.getId());
                    cajaActual[0].getChildren().remove(tarjeta); 
                    dialog.close();
                    e.consume();
                });
            }

            dialog.showAndWait().ifPresent(tipo -> {
                if (tipo == btnGuardar) {
                    tarjetaService.actualizarDescripcion(datosActualizados.getId(), txtDesc.getText());
                    if (datosActualizados.isCompletada() != chkCompletada.isSelected()) {
                        tarjetaService.cambiarEstadoCompletada(datosActualizados.getId(), chkCompletada.isSelected());
                    }
                  
                    String listaDestino = comboListas.getValue();
                    if (!listaDestino.equals(listaActual[0])) {
                        try {
                            tableroService.moverTarjeta(miTableroId, datosActualizados.getId(), listaActual[0], listaDestino);
                        } catch (IllegalStateException e) {
                            mostrarError("Movimiento no permitido", e.getMessage());
                        }
                    }
                    recargarTablero();
                }
            });
        });
        
        return tarjeta;
    }
    
    private HBox crearFilaSubtarea(String texto, boolean completado, String tarjetaId, String nombreTarjeta, VBox contenedor) {
        CheckBox cb = new CheckBox(texto);
        cb.setSelected(completado);
        cb.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(cb, Priority.ALWAYS);
        
        cb.selectedProperty().addListener((obs, old, newValue) -> {
            tarjetaService.alternarEstadoChecklist(tarjetaId, texto, newValue);
        });

        Button btnBorrar = new Button("✕");
        btnBorrar.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef5350; -fx-cursor: hand;");
        
        HBox fila = new HBox(5, cb, btnBorrar);
        btnBorrar.setOnAction(e -> {
            tarjetaService.eliminarItemChecklist(tarjetaId, texto);
            contenedor.getChildren().remove(fila);
        });

        return fila;
    }

    @FXML
    public void mostrarHistorial() {
        List<String> historial = tableroService.obtenerHistorialTextos(miTableroId);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setHeaderText("Historial del tablero");
        
        ListView<String> listaVisual = new ListView<>();
        Collections.reverse(historial);
        listaVisual.getItems().addAll(historial);
        dialog.getDialogPane().setContent(listaVisual);
        
        ButtonType btnBorrar = new ButtonType("Borrar Historial", ButtonData.LEFT);
        dialog.getDialogPane().getButtonTypes().addAll(btnBorrar, ButtonType.CLOSE);

        Button botonBorrarInterfaz = (Button) dialog.getDialogPane().lookupButton(btnBorrar);
        if (botonBorrarInterfaz != null) {
            botonBorrarInterfaz.addEventFilter(ActionEvent.ACTION, e -> {
                tableroService.limpiarHistorial(miTableroId);
                dialog.close();
                e.consume();
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
            recargarTablero();
        }
    }
    
    @FXML
    public void aplicarFiltroTexto(KeyEvent event) {
        if (txtFiltroTexto != null) {
            textoFiltroActual = txtFiltroTexto.getText();
            recargarTablero();
        }
    }

    @FXML
    public void filtrarSinEtiquetas() {
        colorFiltroActual = "SIN_ETIQUETA"; 
        recargarTablero();
    }

    @FXML
    public void limpiarFiltro() {
        colorFiltroActual = null; 
        textoFiltroActual = "";
        
        if (txtFiltroTexto != null) {
            txtFiltroTexto.clear();
        }
        recargarTablero();
    }
    
    @FXML
    private void cambiarColorEtiqueta() {
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.show();

        colorPicker.setOnAction(e -> {
            Color nuevoColor = colorPicker.getValue();
            String hexColor = toHexString(nuevoColor);
            
            if (etiquetaColor != null) {
                etiquetaColor.setStyle("-fx-background-color: #" + hexColor + "; -fx-background-radius: 4; -fx-min-width: 45; -fx-min-height: 8;");
            }
        });
    }
    
    private String toHexString(Color color) {
        return String.format("%02X%02X%02X",
            (int)(color.getRed() * 255),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255));
    }
    
    private void abrirAjustesLista(String nombreLista) {
        try {
            TableroDTO tablero = tableroService.obtenerDatosTablero(miTableroId);
            ListaDTO listaDatos = tablero.getListas().stream()
                    .filter(l -> l.getNombre().equals(nombreLista))
                    .findFirst().orElse(null);

            if (listaDatos == null) return;

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Ajustes de la lista: " + nombreLista);
            dialog.setHeaderText("Configurar reglas y límites Kanban");

            GridPane grid = new GridPane();
            grid.setHgap(10); 
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 20, 10, 10));

            TextField txtLimite = new TextField();
            if (listaDatos.getLimite() != null) {
                txtLimite.setText(String.valueOf(listaDatos.getLimite()));
            }
            txtLimite.setPromptText("Ej: 5 (Vacío = sin límite)");

            ComboBox<String> comboRequerida = new ComboBox<>();
            comboRequerida.getItems().add("Ninguna (Libre)");
            for (ListaDTO l : tablero.getListas()) {
                if (!l.getNombre().equals(nombreLista)) {
                    comboRequerida.getItems().add(l.getNombre());
                }
            }
            
            comboRequerida.setValue("Ninguna (Libre)");

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
                    recargarTablero();
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