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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import pds.gestiontareas.application.TableroService;
import pds.gestiontareas.application.TarjetaService;
import pds.gestiontareas.domain.model.tablero.id.TableroId;
import pds.gestiontareas.domain.model.tablero.model.ListaTareas;
import pds.gestiontareas.domain.model.tablero.model.Tablero;
import pds.gestiontareas.domain.model.tarjeta.id.TarjetaId;
import pds.gestiontareas.domain.model.tarjeta.model.Etiqueta;
import pds.gestiontareas.domain.model.tarjeta.model.ItemChecklist;
import pds.gestiontareas.domain.model.tarjeta.model.Tarjeta;
import pds.gestiontareas.domain.model.tarjeta.model.TarjetaChecklist;

@Controller
public class TableroController {

    @Autowired
    private TableroService tableroService;
    
    @Autowired
    private TarjetaService tarjetaService;

    private TableroId miTableroId;
    
    private Map<String, VBox> columnasVisuales = new HashMap<>();
    
    private VBox tarjetasPorHacer;
    private VBox tarjetasEnProgreso;
    private VBox tarjetasCompletadas;
    
    @FXML
    private HBox contenedorListas;

    @FXML
    private Region etiquetaColor;
    
    @FXML
    private Button btnBloquear;
    
    @FXML
    public void initialize() {
        boolean tableroCargado = false;
        
        while (!tableroCargado) {
            Alert inicioDialog = new Alert(Alert.AlertType.CONFIRMATION);
            inicioDialog.setTitle("Bienvenido a Gestión de Tareas PDS");
            inicioDialog.setHeaderText("Elige una opción para comenzar");
            
            ButtonType btnCrear = new ButtonType("Crear Nuevo Tablero");
            ButtonType btnAcceder = new ButtonType("Acceder con ID");
            ButtonType btnRecuperar = new ButtonType("Recuperar por Email");
            ButtonType btnSalir = new ButtonType("Salir", ButtonData.CANCEL_CLOSE);
            
            inicioDialog.getButtonTypes().setAll(btnCrear, btnAcceder, btnRecuperar, btnSalir);
            
            Optional<ButtonType> resultado = inicioDialog.showAndWait();
            
            if (resultado.isPresent() && resultado.get() == btnCrear) {
                TextInputDialog dialogEmail = new TextInputDialog();
                dialogEmail.setTitle("Nuevo Tablero");
                dialogEmail.setHeaderText("Introduce tu correo electrónico para ser el dueño:");
                dialogEmail.setContentText("Email:");
                Optional<String> emailOpt = dialogEmail.showAndWait();
                
                if (emailOpt.isPresent() && !emailOpt.get().trim().isEmpty()) {
                    String email = emailOpt.get().trim();
                    
                    if(!email.contains("@")) {
                        mostrarError("Email inválido", "Por favor, introduce un correo real.");
                        continue;
                    }

                    miTableroId = tableroService.crearTablero("Mi Proyecto PDS", email);
                    tableroService.añadirListaATablero(miTableroId, "Por Hacer");
                    tableroService.añadirListaATablero(miTableroId, "En Progreso");
                    
                    Alert infoUrl = new Alert(Alert.AlertType.INFORMATION);
                    infoUrl.setTitle("Tablero Creado");
                    infoUrl.setHeaderText("¡Tablero creado con éxito!");
                    
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
        Tablero tableroReal = tableroService.obtenerTablero(miTableroId);
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

        for (ListaTareas lista : tableroReal.getListas()) {
            VBox contenedorDeEstaLista = crearColumnaVisual(lista.getTitulo(), estaBloqueado);
            
            if (lista.getTitulo().equals("Por Hacer")) tarjetasPorHacer = contenedorDeEstaLista;
            if (lista.getTitulo().equals("En Progreso")) tarjetasEnProgreso = contenedorDeEstaLista;
            if (lista.getTitulo().equals("Completadas")) tarjetasCompletadas = contenedorDeEstaLista;

            if (lista.getTarjetasIds() != null) {
                for (String idTarjeta : lista.getTarjetasIds()) {
                    String tituloGuardado = tarjetaService.obtenerTituloTarjeta(idTarjeta);
                    contenedorDeEstaLista.getChildren().add(
                        crearTarjetaVisual(tituloGuardado, idTarjeta, lista.getTitulo(), contenedorDeEstaLista)
                    );
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
        
        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);
        
        HBox cabecera = new HBox(titulo, espaciador, btnBorrarLista);
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
                    tableroService.registrarAccionManual(miTableroId, "Se eliminó la lista completa '" + nombreLista + "'.");
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
                    String tipoSeleccionado = comboTipo.getValue().equals("Checklist") ? "CHECKLIST" : "TAREA";
                    TarjetaId nuevaId = tarjetaService.crearTarjeta(txtNombre.getText(), tipoSeleccionado);
                    
                    tableroService.añadirTarjetaAListaPorNombre(miTableroId, nombreLista, nuevaId.getValor());
                    tableroService.registrarAccionManual(miTableroId, "Se creó la tarjeta '" + txtNombre.getText() + "' de tipo " + tipoSeleccionado + " en la lista '" + nombreLista + "'.");
                    contenedorTarjetas.getChildren().add(crearTarjetaVisual(txtNombre.getText(), nuevaId.getValor(), nombreLista, contenedorTarjetas));
                }
            });
        });

        if (!estaBloqueado) {
            columna.getChildren().addAll(cabecera, contenedorTarjetas, btnAñadir);
        } else {
            btnBorrarLista.setVisible(false);
            columna.getChildren().addAll(cabecera, contenedorTarjetas);
        }
        
        if (contenedorListas != null) {
            contenedorListas.setAlignment(Pos.TOP_LEFT);
            contenedorListas.getChildren().add(columna);
        }
        
        columnasVisuales.put(nombreLista, contenedorTarjetas);
        return contenedorTarjetas; 
    }
    

    private VBox crearTarjetaVisual(String textoTarea, String tarjetaId, String nombreListaOrigen, VBox contenedorActual) {
        VBox tarjeta = new VBox();
        tarjeta.getStyleClass().add("tarjeta");

        final String[] listaActual = {nombreListaOrigen};
        final VBox[] cajaActual = {contenedorActual};

        HBox contenedorEtiquetas = new HBox(5);
        Tarjeta datosTarjeta = tarjetaService.obtenerTarjeta(tarjetaId);
        for (Etiqueta etiqueta : datosTarjeta.getEtiquetas()) {
            Region rectColor = new Region();
            rectColor.setStyle("-fx-background-color: " + etiqueta.getColor() + "; -fx-min-width: 40; -fx-min-height: 8; -fx-background-radius: 4;");
            contenedorEtiquetas.getChildren().add(rectColor);
        }

        Label contenido = new Label(textoTarea);
        contenido.setWrapText(true);
        contenido.getStyleClass().add("texto-tarjeta");
        
        tarjeta.getChildren().addAll(contenedorEtiquetas, contenido);

        tarjeta.setOnMouseClicked(event -> {
            Tarjeta datosActualizados = tarjetaService.obtenerTarjeta(tarjetaId);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Detalles de la Tarjeta");
            dialog.setHeaderText("Tarea: " + textoTarea);

            dialog.setResizable(true);
            dialog.getDialogPane().setMinWidth(450);
            dialog.getDialogPane().setPrefWidth(520);

            VBox contenidoDialogo = new VBox(20);
            contenidoDialogo.setStyle("-fx-padding: 20; -fx-background-color: #f4f5f7;");

            VBox cajaDesc = new VBox(8);
            Label lblDesc = new Label("Descripción:");
            lblDesc.setStyle("-fx-font-weight: bold; -fx-text-fill: #172b4d;");
            TextArea txtDesc = new TextArea(datosActualizados.getDescripcion());
            txtDesc.setWrapText(true);
            txtDesc.setPrefRowCount(3);
            cajaDesc.getChildren().addAll(lblDesc, txtDesc);

            VBox cajaChecklistCompleta = new VBox(8);
            
            if (datosActualizados instanceof TarjetaChecklist) {
                TarjetaChecklist tarjetaChecklist = (TarjetaChecklist) datosActualizados;
                
                Label lblChecklist = new Label("Checklist (Subtareas):");
                lblChecklist.setStyle("-fx-font-weight: bold; -fx-text-fill: #172b4d;");
                
                VBox listaSubtareas = new VBox(8);
                if (tarjetaChecklist.getChecklist() != null) {
                    for (ItemChecklist item : tarjetaChecklist.getChecklist()) {
                        listaSubtareas.getChildren().add(crearFilaSubtarea(item.getTexto(), item.isCompletado(), tarjetaId, textoTarea, listaSubtareas));
                    }
                }

                TextField txtNuevoItem = new TextField();
                txtNuevoItem.setPromptText("Añadir un elemento...");
                Button btnAñadirItem = new Button("Añadir");
                HBox controlesChecklist = new HBox(10, txtNuevoItem, btnAñadirItem);
                
                btnAñadirItem.setOnAction(ev -> {
                    String texto = txtNuevoItem.getText();
                    if (texto != null && !texto.trim().isEmpty()) {
                        tarjetaService.añadirItemChecklist(tarjetaId, texto);
                        tableroService.registrarAccionManual(miTableroId, "Se añadió la subtarea '" + texto + "' a la tarjeta '" + textoTarea + "'.");
                        listaSubtareas.getChildren().add(crearFilaSubtarea(texto, false, tarjetaId, textoTarea, listaSubtareas));
                        txtNuevoItem.clear();
                    }
                });
                cajaChecklistCompleta.getChildren().addAll(lblChecklist, listaSubtareas, controlesChecklist);
            }

            VBox cajaEtiquetas = new VBox(8);
            Label lblColor = new Label("Gestionar Etiquetas:");
            lblColor.setStyle("-fx-font-weight: bold; -fx-text-fill: #172b4d;");
            ColorPicker colorPicker = new ColorPicker(Color.web("#ef5350"));
            Button btnAñadirColor = new Button("Añadir Color");
            Button btnQuitarColor = new Button("Quitar Color");
            HBox controlesColor = new HBox(10, colorPicker, btnAñadirColor, btnQuitarColor);
            cajaEtiquetas.getChildren().addAll(lblColor, controlesColor);

            btnAñadirColor.setOnAction(e -> {
                String hexColor = "#" + colorPicker.getValue().toString().substring(2, 8);
                if (!tarjetaService.obtenerTarjeta(tarjetaId).tieneEtiqueta(hexColor)) {
                    tarjetaService.añadirEtiqueta(tarjetaId, "Etiqueta", hexColor);
                    Region nuevaEtiqueta = new Region();
                    nuevaEtiqueta.setStyle("-fx-background-color: " + hexColor + "; -fx-min-width: 40; -fx-min-height: 8; -fx-background-radius: 4;");
                    contenedorEtiquetas.getChildren().add(nuevaEtiqueta);
                }
            });

            btnQuitarColor.setOnAction(e -> {
                String hexColor = "#" + colorPicker.getValue().toString().substring(2, 8);
                tarjetaService.quitarEtiqueta(tarjetaId, hexColor);
                contenedorEtiquetas.getChildren().clear();
                for (Etiqueta etiqueta : tarjetaService.obtenerTarjeta(tarjetaId).getEtiquetas()) {
                    Region rectColor = new Region();
                    rectColor.setStyle("-fx-background-color: " + etiqueta.getColor() + "; -fx-min-width: 40; -fx-min-height: 8; -fx-background-radius: 4;");
                    contenedorEtiquetas.getChildren().add(rectColor);
                }
            });

            Label lblMover = new Label("Mover a lista:");
            lblMover.setStyle("-fx-font-weight: bold; -fx-text-fill: #172b4d;");
            ComboBox<String> comboListas = new ComboBox<>();
            comboListas.getItems().addAll(tableroService.obtenerNombresListas(miTableroId)); 
            comboListas.setValue(listaActual[0]); 
            HBox cajaMover = new HBox(10, lblMover, comboListas);
            cajaMover.setAlignment(Pos.CENTER_LEFT);

            CheckBox chkCompletada = new CheckBox("✅ Marcar tarjeta como COMPLETADA");
            chkCompletada.setSelected(datosActualizados.isCompletada());
            chkCompletada.setStyle("-fx-font-weight: bold; -fx-text-fill: #2e7d32; -fx-font-size: 14px; -fx-padding: 0 0 10 0;");
            
            chkCompletada.selectedProperty().addListener((obs, old, isCompleted) -> {
                if (isCompleted) {
                    if (!comboListas.getItems().contains("Completadas")) {
                        comboListas.getItems().add("Completadas");
                    }
                    comboListas.setValue("Completadas");
                } else {
                    comboListas.setValue(listaActual[0]);
                }
            });

            comboListas.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    if (newVal.equals("Completadas")) {
                        chkCompletada.setSelected(true);
                    } else {
                        chkCompletada.setSelected(false);
                    }
                }
            });
            
            contenidoDialogo.getChildren().addAll(chkCompletada, cajaDesc, cajaChecklistCompleta, cajaEtiquetas, cajaMover);
            
            ScrollPane scrollPane = new ScrollPane(contenidoDialogo);
            scrollPane.setFitToWidth(true); 
            scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #f4f5f7; -fx-border-color: transparent;");
            
            dialog.getDialogPane().setContent(scrollPane);

            ButtonType btnGuardar = new ButtonType("Guardar Cambios", ButtonData.OK_DONE);
            ButtonType btnCancelar = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
            ButtonType btnEliminar = new ButtonType("Eliminar Tarjeta", ButtonData.LEFT);

            dialog.getDialogPane().getButtonTypes().setAll(btnEliminar, btnGuardar, btnCancelar);

            Button botonEliminarInterfaz = (Button) dialog.getDialogPane().lookupButton(btnEliminar);
            if (botonEliminarInterfaz != null) {
                botonEliminarInterfaz.addEventFilter(ActionEvent.ACTION, e -> {
                    tableroService.registrarAccionManual(miTableroId, "Se eliminó la tarjeta '" + textoTarea + "' de la lista '" + listaActual[0] + "'.");
                    tableroService.eliminarTarjetaDeLista(miTableroId, listaActual[0], tarjetaId);
                    tarjetaService.eliminarTarjeta(tarjetaId);
                    cajaActual[0].getChildren().remove(tarjeta); 
                    dialog.close();
                    e.consume();
                });
            }

            dialog.showAndWait().ifPresent(tipo -> {
                if (tipo == btnGuardar) {
                    tarjetaService.actualizarDescripcion(tarjetaId, txtDesc.getText());

                    boolean estadoAnterior = datosActualizados.isCompletada();
                    boolean estadoNuevo = chkCompletada.isSelected();
                    
                    if (estadoAnterior != estadoNuevo) {
                        tarjetaService.cambiarEstadoCompletada(tarjetaId, estadoNuevo);
                        if (estadoNuevo) {
                            tableroService.registrarAccionManual(miTableroId, "Se marcó como completada la tarjeta '" + textoTarea + "'.");
                        } else {
                            tableroService.registrarAccionManual(miTableroId, "Se desmarcó como completada la tarjeta '" + textoTarea + "'.");
                        }
                    }

                    String listaDestino = comboListas.getValue();
                    if (!listaDestino.equals(listaActual[0])) {
                        
                        if (listaDestino.equals("Completadas") && !columnasVisuales.containsKey("Completadas")) {
                            tableroService.añadirListaATablero(miTableroId, "Completadas");
                            boolean estaBloqueado = tableroService.obtenerTablero(miTableroId).isBloqueado();
                            crearColumnaVisual("Completadas", estaBloqueado);
                        }

                        tableroService.moverTarjeta(miTableroId, tarjetaId, listaActual[0], listaDestino);
                        cajaActual[0].getChildren().remove(tarjeta); 
                        VBox nuevaCaja = columnasVisuales.get(listaDestino);
                        
                        if (nuevaCaja != null) {
                            nuevaCaja.getChildren().add(tarjeta); 
                            listaActual[0] = listaDestino;
                            cajaActual[0] = nuevaCaja;
                        }
                    }
                }
            });
        });
        
        return tarjeta;
    }
    
    private HBox crearFilaSubtarea(String texto, boolean completado, String tarjetaId, String nombreTarjeta, VBox contenedor) {
        CheckBox cb = new CheckBox(texto);
        cb.setSelected(completado);
        cb.setStyle("-fx-text-fill: #172b4d;");
        cb.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(cb, Priority.ALWAYS);
        
        cb.selectedProperty().addListener((obs, old, newValue) -> {
            tarjetaService.alternarEstadoChecklist(tarjetaId, texto, newValue);
            String estado = newValue ? "completó" : "desmarcó";
            tableroService.registrarAccionManual(miTableroId, "Se " + estado + " la subtarea '" + texto + "' de la tarjeta '" + nombreTarjeta + "'.");
        });

        Button btnBorrar = new Button("✕");
        btnBorrar.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef5350; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 0 5;");
        
        HBox fila = new HBox(5, cb, btnBorrar);
        fila.setAlignment(Pos.CENTER_LEFT);

        btnBorrar.setOnAction(e -> {
            tarjetaService.eliminarItemChecklist(tarjetaId, texto);
            tableroService.registrarAccionManual(miTableroId, "Se eliminó la subtarea '" + texto + "' de la tarjeta '" + nombreTarjeta + "'.");
            contenedor.getChildren().remove(fila);
        });

        return fila;
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

        Dialog<Void> dialog = new Dialog<>();
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
        
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.showAndWait();
    }
    
    private String toHexString(Color color) {
        return String.format("%02X%02X%02X",
            (int)(color.getRed() * 255),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255));
    }
}