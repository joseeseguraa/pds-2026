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
        
        return contenedorTarjetas; 
    }
    
    private VBox crearTarjetaVisual(String textoTarea, String tarjetaId, String nombreListaOrigen, VBox contenedorActual) {
        VBox tarjeta = new VBox();
        tarjeta.getStyleClass().add("tarjeta");
        
        Label contenido = new Label(textoTarea);
        contenido.setWrapText(true);
        contenido.getStyleClass().add("texto-tarjeta");
        
        tarjeta.getChildren().add(contenido);
        
        tarjeta.setOnMouseClicked(event -> {
            
            if (nombreListaOrigen.equals("Completadas")) {
                return; 
            }

            javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
            alerta.setTitle("Opciones de la Tarea");
            alerta.setHeaderText(textoTarea);
            alerta.setContentText("¿Qué deseas hacer con esta tarjeta?");

            javafx.scene.control.ButtonType btnCompletar = new javafx.scene.control.ButtonType("Completar Tarea");
            javafx.scene.control.ButtonType btnCancelar = new javafx.scene.control.ButtonType("Cancelar", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);

            alerta.getButtonTypes().setAll(btnCompletar, btnCancelar);

            alerta.showAndWait().ifPresent(tipo -> {
                if (tipo == btnCompletar) {
                    tableroService.moverTarjetaACompletadas(miTableroId, tarjetaId, nombreListaOrigen);
                    
                    contenedorActual.getChildren().remove(tarjeta); 
                    tarjetasCompletadas.getChildren().add(tarjeta); 
                    
                    System.out.println("Tarjeta movida a Completadas");
                }
            });
        });
        
        return tarjeta;
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