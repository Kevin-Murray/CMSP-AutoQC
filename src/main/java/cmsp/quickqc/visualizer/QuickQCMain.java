package cmsp.quickqc.visualizer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class QuickQCMain extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        stage.setTitle("CMSP - QuickQC Visualizer (v. 1.3.0)");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainPageGui.fxml"));
        Parent mainPage = fxmlLoader.load();

        Scene main = new Scene(mainPage);
        main.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styleGuide.css")).toString());

        Image image = new Image("cmsp/quickqc/visualizer/quickQA-icon.png");
        stage.getIcons().add(image);

        stage.setResizable(false);
        stage.setScene(main);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}