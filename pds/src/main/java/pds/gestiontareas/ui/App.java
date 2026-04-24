package pds.gestiontareas.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "pds.gestiontareas")
@EnableJpaRepositories(basePackages = "pds.gestiontareas.infrastructure.jpa")
@EntityScan(basePackages = "pds.gestiontareas.infrastructure.jpa.entity")
public class App extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() throws Exception {
        springContext = SpringApplication.run(App.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/TableroView.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);

        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 1500, 800); 

        primaryStage.setMinWidth(900);  
        primaryStage.setMinHeight(650); 
        primaryStage.setResizable(true);
        
        primaryStage.setTitle("Gestión de Tareas PDS - Tablero Principal");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        springContext.close();
        Platform.exit();
    }
}