package cmsp.autoqc.visualizer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class AutoQCMain extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        stage.setTitle("CMSP - QuickQC Visualizer (v. 1.1.0)");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainPageGui.fxml"));
        Parent mainPage = fxmlLoader.load();

        Scene main = new Scene(mainPage);
        main.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styleGuide.css")).toString());

        stage.setResizable(false);
        stage.setScene(main);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}