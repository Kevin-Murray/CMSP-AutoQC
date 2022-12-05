package cmsp.autoqc.visualizer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AutoQCMain extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        stage.setTitle("CMSP - AutoQC Visualizer");

        Parent mainPage = FXMLLoader.load(getClass().getResource("MainPageGui.fxml"));
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