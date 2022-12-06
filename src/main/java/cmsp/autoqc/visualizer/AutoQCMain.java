package cmsp.autoqc.visualizer;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class AutoQCMain extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        stage.setTitle("CMSP - AutoQC Visualizer");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainPageGui.fxml"));
        Parent mainPage = fxmlLoader.load();
        MainPageController controller = fxmlLoader.<MainPageController>getController();

        Scene main = new Scene(mainPage);
        main.getStylesheets().add(getClass().getResource("styleGuide.css").toString());

        stage.setResizable(false);
        stage.setScene(main);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}