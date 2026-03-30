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
import pds.gestiontareas.domain.model.tablero.id.TableroId;

@Controller
public class TableroController {

    @Autowired
    private TableroService tableroService;

    @FXML
    private HBox contenedorListas;

    @FXML
    private Region etiquetaColor;

    @FXML
    public void initialize() {
        System.out.println("Iniciando la interfaz y conectando con el dominio...");

        TableroId miTableroId = tableroService.crearTablero("Mi Proyecto PDS", "alumno@um.es");
        tableroService.añadirListaATablero(miTableroId, "Por Hacer");
        tableroService.añadirListaATablero(miTableroId, "En Progreso");

        VBox tarjetasPorHacer = crearColumnaVisual("Por Hacer");
        VBox tarjetasEnProgreso = crearColumnaVisual("En Progreso");
        VBox tarjetasCompletadas = crearColumnaVisual("Completadas");

        tarjetasPorHacer.getChildren().add(crearTarjetaVisual("Diseñar el modelo de dominio"));
        tarjetasEnProgreso.getChildren().add(crearTarjetaVisual("Mostrar tarjetas en la interfaz"));
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
            contenedorTarjetas.getChildren().add(crearTarjetaVisual("Nueva tarea de ejemplo"));
        });

        columna.getChildren().addAll(titulo, contenedorTarjetas, btnAñadir);
        
        if (contenedorListas != null) {
            contenedorListas.setAlignment(javafx.geometry.Pos.TOP_LEFT);
            contenedorListas.getChildren().add(columna);
        }
        
        return contenedorTarjetas; 
    }
    
    private VBox crearTarjetaVisual(String textoTarea) {
        VBox tarjeta = new VBox();
        tarjeta.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 3; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        
        Label contenido = new Label(textoTarea);
        contenido.setWrapText(true);
        
        tarjeta.getChildren().add(contenido);
        
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