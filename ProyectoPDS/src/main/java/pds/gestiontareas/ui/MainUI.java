package pds.gestiontareas.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class MainUI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TableroView.fxml"));
        
        Parent root = loader.load();
        
        Scene scene = new Scene(root, 1200, 800); 

        primaryStage.setMinWidth(900);  
        primaryStage.setMinHeight(650); 

        primaryStage.setResizable(true);
        
        primaryStage.setTitle("Gestión de Tareas PDS - Tablero Principal");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}