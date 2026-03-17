package pds.gestiontareas.ui;

import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

public class TarjetaController {

    @FXML
    private Region etiquetaColor;

    @FXML
    private void cambiarColorEtiqueta() {
        ColorPicker colorPicker = new ColorPicker();

        colorPicker.show();

        colorPicker.setOnAction(e -> {
            Color nuevoColor = colorPicker.getValue();
            String hexColor = toHexString(nuevoColor);
            etiquetaColor.setStyle("-fx-background-color: #" + hexColor + ";");
        });
    }

    private String toHexString(Color color) {
        return String.format("%02X%02X%02X",
            (int)(color.getRed() * 255),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255));
    }
}