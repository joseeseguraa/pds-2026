package pds.gestiontareas.ui;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import javafx.application.Application;

@SpringBootApplication(scanBasePackages = "pds.gestiontareas") 
public class MainUI {

    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}