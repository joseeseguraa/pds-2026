package pds.gestiontareas.ui;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import pds.gestiontareas.application.TableroService;
import pds.gestiontareas.application.TarjetaService;
import pds.gestiontareas.domain.model.tablero.id.TableroId;
import pds.gestiontareas.domain.model.tarjeta.model.Etiqueta;
import pds.gestiontareas.domain.model.tarjeta.model.ItemChecklist;
import pds.gestiontareas.domain.model.tarjeta.model.Tarjeta;
import pds.gestiontareas.domain.model.tarjeta.model.TarjetaChecklist;
import pds.gestiontareas.domain.model.tablero.model.Tablero;

public class TarjetaUIComponent extends VBox {

    private String tarjetaId;
    private String textoTarea;
    private String nombreListaOrigen;
    private TableroId miTableroId;
    
    private TarjetaService tarjetaService;
    private TableroService tableroService;
    private TableroController mainController;

    public TarjetaUIComponent(String textoTarea, String tarjetaId, String nombreListaOrigen, 
                              TableroId miTableroId, TarjetaService tarjetaService, 
                              TableroService tableroService, TableroController mainController) {
        
        this.textoTarea = textoTarea;
        this.tarjetaId = tarjetaId;
        this.nombreListaOrigen = nombreListaOrigen;
        this.miTableroId = miTableroId;
        this.tarjetaService = tarjetaService;
        this.tableroService = tableroService;
        this.mainController = mainController;

        this.getStyleClass().add("tarjeta");

        construirVistaBasica();

        this.setOnMouseClicked(event -> abrirMenuEdicion());
    }

    private void construirVistaBasica() {
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
        
        this.getChildren().addAll(contenedorEtiquetas, contenido);
    }

    private void abrirMenuEdicion() {
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
                    listaSubtareas.getChildren().add(crearFilaSubtarea(item.getTexto(), item.isCompletado(), listaSubtareas));
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
                    listaSubtareas.getChildren().add(crearFilaSubtarea(texto, false, listaSubtareas));
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
            tarjetaService.añadirEtiqueta(tarjetaId, "Etiqueta", hexColor);
            mainController.recargarTablero();
        });

        btnQuitarColor.setOnAction(e -> {
            String hexColor = "#" + colorPicker.getValue().toString().substring(2, 8);
            tarjetaService.quitarEtiqueta(tarjetaId, hexColor);
            mainController.recargarTablero();
        });

        Label lblMover = new Label("Mover a lista:");
        lblMover.setStyle("-fx-font-weight: bold; -fx-text-fill: #172b4d;");
        ComboBox<String> comboListas = new ComboBox<>();
        comboListas.getItems().addAll(tableroService.obtenerNombresListas(miTableroId)); 
        comboListas.setValue(nombreListaOrigen); 
        HBox cajaMover = new HBox(10, lblMover, comboListas);
        cajaMover.setAlignment(Pos.CENTER_LEFT);
        
        VBox cajaPermisos = new VBox(8);
        Label lblPermisos = new Label("Gestionar Permisos de Usuario:");
        lblPermisos.setStyle("-fx-font-weight: bold; -fx-text-fill: #172b4d;");

        Tablero tableroActual = tableroService.obtenerTablero(miTableroId);
        if (tableroActual.getUsuariosCompartidos() != null && !tableroActual.getUsuariosCompartidos().isEmpty()) {
            ComboBox<String> comboUsuarios = new ComboBox<>();
            comboUsuarios.getItems().addAll(tableroActual.getUsuariosCompartidos());
            comboUsuarios.setPromptText("Usuario invitado");

            ComboBox<String> comboNiveles = new ComboBox<>();
            comboNiveles.getItems().addAll("LECTURA", "ESCRITURA");
            comboNiveles.setPromptText("Nivel");

            TextField txtEmailDueño = new TextField();
            txtEmailDueño.setPromptText("Tu email (Dueño)");
            txtEmailDueño.setPrefWidth(120);

            Button btnAsignarPermiso = new Button("Asignar");
            btnAsignarPermiso.setStyle("-fx-background-color: #0052cc; -fx-text-fill: white; -fx-cursor: hand;");
            
            HBox controlesPermisos = new HBox(10, comboUsuarios, comboNiveles, txtEmailDueño, btnAsignarPermiso);
            cajaPermisos.getChildren().addAll(lblPermisos, controlesPermisos);

            btnAsignarPermiso.setOnAction(e -> {
                if (comboUsuarios.getValue() == null || comboNiveles.getValue() == null || txtEmailDueño.getText().trim().isEmpty()) {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setHeaderText("Faltan datos");
                    error.setContentText("Rellena todos los campos para asignar el permiso.");
                    error.showAndWait();
                    return;
                }
                try {
                    // LLamada al servicio usando las variables de esta clase
                    tableroService.asignarPermisoTarjeta(miTableroId, tarjetaId, txtEmailDueño.getText().trim(), comboUsuarios.getValue(), comboNiveles.getValue());
                    
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setHeaderText("Éxito");
                    info.setContentText("Permiso de " + comboNiveles.getValue() + " asignado a " + comboUsuarios.getValue());
                    info.showAndWait();
                } catch (Exception ex) {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setHeaderText("Acceso Denegado");
                    error.setContentText(ex.getMessage());
                    error.showAndWait();
                }
            });
        } else {
            Label lblNoCompartido = new Label("El tablero no está compartido. Usa el botón 👤+ del tablero principal.");
            lblNoCompartido.setStyle("-fx-text-fill: #7a869a; -fx-font-style: italic;");
            cajaPermisos.getChildren().addAll(lblPermisos, lblNoCompartido);
        }

        CheckBox chkCompletada = new CheckBox("Marcar tarjeta como COMPLETADA");
        chkCompletada.setSelected(datosActualizados.isCompletada());
        chkCompletada.setStyle("-fx-font-weight: bold; -fx-text-fill: #2e7d32; -fx-font-size: 14px; -fx-padding: 0 0 10 0;");
       
        chkCompletada.selectedProperty().addListener((obs, old, isCompleted) -> {
            if (isCompleted) {
                if (!comboListas.getItems().contains("Completadas")) {
                    comboListas.getItems().add("Completadas");
                }
                comboListas.setValue("Completadas");
            } else {
                comboListas.setValue(nombreListaOrigen);
            }
        });
        
        comboListas.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                chkCompletada.setSelected(newVal.equals("Completadas"));
            }
        });
        
        contenidoDialogo.getChildren().addAll(chkCompletada, cajaDesc, cajaChecklistCompleta, cajaEtiquetas, cajaMover, cajaPermisos);
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
                tableroService.eliminarTarjetaDeLista(miTableroId, nombreListaOrigen, tarjetaId);
                tarjetaService.eliminarTarjeta(tarjetaId);
                tableroService.eliminarTarjetaCompletamente(miTableroId, nombreListaOrigen, tarjetaId, textoTarea);
                dialog.close();
                mainController.recargarTablero();
                e.consume();
            });
        }

        dialog.showAndWait().ifPresent(tipo -> {
            if (tipo == btnGuardar) {
                tarjetaService.actualizarDescripcion(tarjetaId, txtDesc.getText());

                boolean estadoAnterior = datosActualizados.isCompletada();
                boolean estadoNuevo = chkCompletada.isSelected();
                String listaDestino = comboListas.getValue();

                try {
                    if (!estadoAnterior && estadoNuevo) {
                        tableroService.completarTarjetaYBuscarDestino(miTableroId, tarjetaId, nombreListaOrigen);
                    } else {
                        if (!estadoNuevo && estadoAnterior) {
                            tarjetaService.cambiarEstadoCompletada(tarjetaId, false);
                        }
                        if (!listaDestino.equals(nombreListaOrigen)) {
                            tableroService.moverTarjeta(miTableroId, tarjetaId, nombreListaOrigen, listaDestino);
                        }
                    }
                    mainController.recargarTablero(); 
                } catch (Exception e) {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setHeaderText("Error al mover/completar");
                    error.setContentText(e.getMessage());
                    error.showAndWait();
                }
            }
        });
    }

    private HBox crearFilaSubtarea(String texto, boolean completado, VBox contenedor) {
        CheckBox cb = new CheckBox(texto);
        cb.setSelected(completado);
        cb.setStyle("-fx-text-fill: #172b4d;");
        cb.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(cb, Priority.ALWAYS);
        
        cb.selectedProperty().addListener((obs, old, newValue) -> {
            tarjetaService.alternarEstadoChecklist(tarjetaId, texto, newValue);
        });

        Button btnBorrar = new Button("✕");
        btnBorrar.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef5350; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 0 5;");
        
        HBox fila = new HBox(5, cb, btnBorrar);
        fila.setAlignment(Pos.CENTER_LEFT);

        btnBorrar.setOnAction(e -> {
            tarjetaService.eliminarItemChecklist(tarjetaId, texto);
            contenedor.getChildren().remove(fila);
        });

        return fila;
    }
}