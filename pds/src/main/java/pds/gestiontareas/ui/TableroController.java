package pds.gestiontareas.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import pds.gestiontareas.application.TableroService;
import pds.gestiontareas.application.TarjetaService;
import pds.gestiontareas.domain.model.tablero.id.TableroId;
import pds.gestiontareas.domain.model.tarjeta.id.TarjetaId;

@Controller
public class TableroController {

    @Autowired
    private TableroService tableroService;
    
    @Autowired
    private TarjetaService tarjetaService;

    private TableroId miTableroId;
    
    private java.util.Map<String, VBox> columnasVisuales = new java.util.HashMap<>();
    
    private VBox tarjetasPorHacer;
    private VBox tarjetasEnProgreso;
    private VBox tarjetasCompletadas;
    
    @FXML
    private HBox contenedorListas;

    @FXML
    private Region etiquetaColor;

    @FXML
    public void initialize() {
        System.out.println("Iniciando la interfaz y conectando con el dominio...");

        miTableroId = tableroService.crearTablero("Mi Proyecto PDS", "alumno@um.es");
        tableroService.añadirListaATablero(miTableroId, "Por Hacer");
        tableroService.añadirListaATablero(miTableroId, "En Progreso");

        tarjetasPorHacer = crearColumnaVisual("Por Hacer");
        tarjetasEnProgreso = crearColumnaVisual("En Progreso");
        tarjetasCompletadas = crearColumnaVisual("Completadas");

        crearBotonAñadirLista();
    }

    private VBox crearColumnaVisual(String nombreLista) {
        VBox columna = new VBox();
        columna.setStyle("-fx-background-color: #ebecf0; -fx-padding: 10; -fx-background-radius: 5;");
        columna.setPrefWidth(250);
        
        columna.setMinHeight(Region.USE_PREF_SIZE); 
        columna.setMaxHeight(Region.USE_PREF_SIZE);
        
        columna.setSpacing(10);

        Label titulo = new Label(nombreLista);
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 0 0 10 0;");

        VBox contenedorTarjetas = new VBox();
        contenedorTarjetas.setSpacing(8);

        contenedorTarjetas.setMinHeight(Region.USE_PREF_SIZE);

        Button btnAñadir = new Button("+ Añadir tarjeta");
        btnAñadir.setStyle("-fx-background-color: transparent; -fx-text-fill: #5e6c84; -fx-cursor: hand;");
        btnAñadir.setMaxWidth(Double.MAX_VALUE);
        
        btnAñadir.setOnAction(e -> {
            javafx.scene.control.TextInputDialog dialogo = new javafx.scene.control.TextInputDialog();
            dialogo.setHeaderText("Nueva tarea para " + nombreLista);
            dialogo.showAndWait().ifPresent(texto -> {
                if(!texto.trim().isEmpty()) {

                    TarjetaId nuevaId = tarjetaService.crearTarjeta(texto);

                    tableroService.añadirTarjetaAListaPorNombre(miTableroId, nombreLista, nuevaId.getValor());

                    contenedorTarjetas.getChildren().add(crearTarjetaVisual(texto, nuevaId.getValor(), nombreLista, contenedorTarjetas));
                    
                    System.out.println("Tarjeta enlazada al tablero en la lista: " + nombreLista);
                }
            });
        });

        columna.getChildren().addAll(titulo, contenedorTarjetas, btnAñadir);
        
        if (contenedorListas != null) {
            contenedorListas.setAlignment(javafx.geometry.Pos.TOP_LEFT);
            contenedorListas.getChildren().add(columna);
        }
        
        columnasVisuales.put(nombreLista, contenedorTarjetas);
        
        return contenedorTarjetas; 
    }
    

    private javafx.scene.layout.VBox crearTarjetaVisual(String textoTarea, String tarjetaId, String nombreListaOrigen, javafx.scene.layout.VBox contenedorActual) {
        javafx.scene.layout.VBox tarjeta = new javafx.scene.layout.VBox();
        tarjeta.getStyleClass().add("tarjeta");

        final String[] listaActual = {nombreListaOrigen};
        final javafx.scene.layout.VBox[] cajaActual = {contenedorActual};

        javafx.scene.layout.HBox contenedorEtiquetas = new javafx.scene.layout.HBox(5);
        pds.gestiontareas.domain.model.tarjeta.model.Tarjeta datosTarjeta = tarjetaService.obtenerTarjeta(tarjetaId);
        for (pds.gestiontareas.domain.model.tarjeta.model.Etiqueta etiqueta : datosTarjeta.getEtiquetas()) {
            javafx.scene.layout.Region rectColor = new javafx.scene.layout.Region();
            rectColor.setStyle("-fx-background-color: " + etiqueta.getColor() + "; -fx-min-width: 40; -fx-min-height: 8; -fx-background-radius: 4;");
            contenedorEtiquetas.getChildren().add(rectColor);
        }

        javafx.scene.control.Label contenido = new javafx.scene.control.Label(textoTarea);
        contenido.setWrapText(true);
        contenido.getStyleClass().add("texto-tarjeta");
        
        tarjeta.getChildren().addAll(contenedorEtiquetas, contenido);

        tarjeta.setOnMouseClicked(event -> {
            pds.gestiontareas.domain.model.tarjeta.model.Tarjeta datosActualizados = tarjetaService.obtenerTarjeta(tarjetaId);

            javafx.scene.control.Dialog<javafx.scene.control.ButtonType> dialog = new javafx.scene.control.Dialog<>();
            dialog.setTitle("Detalles de la Tarjeta");
            dialog.setHeaderText("Tarea: " + textoTarea);

            javafx.scene.layout.VBox contenidoDialogo = new javafx.scene.layout.VBox(10);

            javafx.scene.control.Label lblDesc = new javafx.scene.control.Label("Descripción:");
            javafx.scene.control.TextArea txtDesc = new javafx.scene.control.TextArea(datosActualizados.getDescripcion());
            txtDesc.setWrapText(true);
            txtDesc.setPrefRowCount(3);

            javafx.scene.control.Label lblColor = new javafx.scene.control.Label("Gestionar Etiquetas:");
            javafx.scene.control.ColorPicker colorPicker = new javafx.scene.control.ColorPicker(javafx.scene.paint.Color.web("#ef5350"));
            javafx.scene.control.Button btnAñadirColor = new javafx.scene.control.Button("Añadir Color");
            javafx.scene.control.Button btnQuitarColor = new javafx.scene.control.Button("Quitar Color");
            javafx.scene.layout.HBox cajaColor = new javafx.scene.layout.HBox(10, colorPicker, btnAñadirColor, btnQuitarColor);

            btnAñadirColor.setOnAction(e -> {
                String hexColor = "#" + colorPicker.getValue().toString().substring(2, 8);
                if (!tarjetaService.obtenerTarjeta(tarjetaId).tieneEtiqueta(hexColor)) {
                    tarjetaService.añadirEtiqueta(tarjetaId, "Etiqueta", hexColor);
                    javafx.scene.layout.Region nuevaEtiqueta = new javafx.scene.layout.Region();
                    nuevaEtiqueta.setStyle("-fx-background-color: " + hexColor + "; -fx-min-width: 40; -fx-min-height: 8; -fx-background-radius: 4;");
                    contenedorEtiquetas.getChildren().add(nuevaEtiqueta);
                }
            });

            btnQuitarColor.setOnAction(e -> {
                String hexColor = "#" + colorPicker.getValue().toString().substring(2, 8);
                tarjetaService.quitarEtiqueta(tarjetaId, hexColor);
                contenedorEtiquetas.getChildren().clear();
                for (pds.gestiontareas.domain.model.tarjeta.model.Etiqueta etiqueta : tarjetaService.obtenerTarjeta(tarjetaId).getEtiquetas()) {
                    javafx.scene.layout.Region rectColor = new javafx.scene.layout.Region();
                    rectColor.setStyle("-fx-background-color: " + etiqueta.getColor() + "; -fx-min-width: 40; -fx-min-height: 8; -fx-background-radius: 4;");
                    contenedorEtiquetas.getChildren().add(rectColor);
                }
            });

            javafx.scene.control.Label lblMover = new javafx.scene.control.Label("Mover a lista:");
            javafx.scene.control.ComboBox<String> comboListas = new javafx.scene.control.ComboBox<>();
            comboListas.getItems().addAll(tableroService.obtenerNombresListas(miTableroId)); 
            comboListas.setValue(listaActual[0]); 
            
            javafx.scene.layout.HBox cajaMover = new javafx.scene.layout.HBox(10, lblMover, comboListas);
            cajaMover.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            contenidoDialogo.getChildren().addAll(lblDesc, txtDesc, lblColor, cajaColor, cajaMover);
            dialog.getDialogPane().setContent(contenidoDialogo);

            javafx.scene.control.ButtonType btnGuardar = new javafx.scene.control.ButtonType("Guardar Cambios", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            javafx.scene.control.ButtonType btnCancelar = new javafx.scene.control.ButtonType("Cancelar", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);

            dialog.getDialogPane().getButtonTypes().setAll(btnGuardar, btnCancelar);

            dialog.showAndWait().ifPresent(tipo -> {
                if (tipo == btnGuardar) {
                    tarjetaService.actualizarDescripcion(tarjetaId, txtDesc.getText());

                    String listaDestino = comboListas.getValue();
                    if (!listaDestino.equals(listaActual[0])) {
                        
                        tableroService.moverTarjeta(miTableroId, tarjetaId, listaActual[0], listaDestino);
                        
                        cajaActual[0].getChildren().remove(tarjeta); 
                        javafx.scene.layout.VBox nuevaCaja = columnasVisuales.get(listaDestino);
                        
                        if (nuevaCaja != null) {
                            nuevaCaja.getChildren().add(tarjeta); 
                            listaActual[0] = listaDestino;
                            cajaActual[0] = nuevaCaja;
                            System.out.println("✅ Tarjeta movida a: " + listaDestino);
                        }
                    }
                }
            });
        });
        
        return tarjeta;
    }
    
    
    private void crearBotonAñadirLista() {
        javafx.scene.control.Button btnAñadirLista = new javafx.scene.control.Button("+ Añadir otra lista");
        btnAñadirLista.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5); -fx-text-fill: #172b4d; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 10;");
        btnAñadirLista.setPrefWidth(250);
        btnAñadirLista.setMinHeight(40);

        btnAñadirLista.setOnAction(e -> {
            javafx.scene.control.TextInputDialog dialogo = new javafx.scene.control.TextInputDialog();
            dialogo.setTitle("Nueva Lista");
            dialogo.setHeaderText("Crear una nueva lista de tareas");
            dialogo.setContentText("Nombre de la lista:");

            dialogo.showAndWait().ifPresent(nombre -> {
                if (!nombre.trim().isEmpty()) {
                    tableroService.añadirListaATablero(miTableroId, nombre);

                    contenedorListas.getChildren().remove(btnAñadirLista);

                    crearColumnaVisual(nombre);

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

    private String toHexString(Color color) {
        return String.format("%02X%02X%02X",
            (int)(color.getRed() * 255),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255));
    }
}