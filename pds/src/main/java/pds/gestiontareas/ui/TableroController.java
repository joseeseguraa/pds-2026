package pds.gestiontareas.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
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
import pds.gestiontareas.domain.model.tarjeta.model.Tarjeta;

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
    public void initialize() {
        System.out.println("Iniciando la interfaz y comprobando la Base de Datos...");

        Tablero tableroReal = null;
        
        List<Tablero> tablerosGuardados = tableroService.obtenerTodos(); 
        
        if (!tablerosGuardados.isEmpty()) {
            tableroReal = tablerosGuardados.get(0);
            miTableroId = tableroReal.getId();
            System.out.println("¡Tablero recuperado del disco duro!");
        } else {
            System.out.println("Creando tablero nuevo por primera vez...");
            miTableroId = tableroService.crearTablero("Mi Proyecto PDS", "alumno@um.es");
            tableroService.añadirListaATablero(miTableroId, "Por Hacer");
            tableroService.añadirListaATablero(miTableroId, "En Progreso");
            tableroReal = tableroService.obtenerTablero(miTableroId);
        }

        for (ListaTareas lista : tableroReal.getListas()) {
            VBox contenedorDeEstaLista = crearColumnaVisual(lista.getTitulo());
            
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
            TextInputDialog dialogo = new TextInputDialog();
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

            VBox contenidoDialogo = new VBox(10);

            Label lblDesc = new Label("Descripción:");
            TextArea txtDesc = new TextArea(datosActualizados.getDescripcion());
            txtDesc.setWrapText(true);
            txtDesc.setPrefRowCount(3);

            Label lblColor = new Label("Gestionar Etiquetas:");
            ColorPicker colorPicker = new ColorPicker(Color.web("#ef5350"));
            Button btnAñadirColor = new Button("Añadir Color");
            Button btnQuitarColor = new Button("Quitar Color");
            HBox cajaColor = new HBox(10, colorPicker, btnAñadirColor, btnQuitarColor);

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
            ComboBox<String> comboListas = new ComboBox<>();
            comboListas.getItems().addAll(tableroService.obtenerNombresListas(miTableroId)); 
            comboListas.setValue(listaActual[0]); 
            
            HBox cajaMover = new HBox(10, lblMover, comboListas);
            cajaMover.setAlignment(Pos.CENTER_LEFT);

            contenidoDialogo.getChildren().addAll(lblDesc, txtDesc, lblColor, cajaColor, cajaMover);
            dialog.getDialogPane().setContent(contenidoDialogo);

            ButtonType btnGuardar = new ButtonType("Guardar Cambios", ButtonData.OK_DONE);
            ButtonType btnCancelar = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
            ButtonType btnEliminar = new ButtonType("Eliminar Tarjeta", ButtonData.LEFT);

            dialog.getDialogPane().getButtonTypes().setAll(btnEliminar, btnGuardar, btnCancelar);

            Button botonEliminarInterfaz = (Button) dialog.getDialogPane().lookupButton(btnEliminar);
            if (botonEliminarInterfaz != null) {
                
                botonEliminarInterfaz.addEventFilter(ActionEvent.ACTION, e -> {
                    
                    tableroService.eliminarTarjetaDeLista(miTableroId, listaActual[0], tarjetaId);
                    tarjetaService.eliminarTarjeta(tarjetaId);
                    
                    cajaActual[0].getChildren().remove(tarjeta); 
                    System.out.println("🗑️ Tarjeta eliminada con éxito");
                    
                    dialog.close();
                    
                    e.consume();
                });
            }

            dialog.showAndWait().ifPresent(tipo -> {
                if (tipo == btnGuardar) {
                    tarjetaService.actualizarDescripcion(tarjetaId, txtDesc.getText());

                    String listaDestino = comboListas.getValue();
                    if (!listaDestino.equals(listaActual[0])) {
                        
                        tableroService.moverTarjeta(miTableroId, tarjetaId, listaActual[0], listaDestino);
                        
                        cajaActual[0].getChildren().remove(tarjeta); 
                        VBox nuevaCaja = columnasVisuales.get(listaDestino);
                        
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