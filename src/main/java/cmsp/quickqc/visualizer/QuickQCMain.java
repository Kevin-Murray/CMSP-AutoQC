
package cmsp.quickqc.visualizer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

/**
 * QuickQC main class.
 */
public class QuickQCMain extends Application {

    /**
     * Starts QC application.
     *
     */
    @Override
    public void start(Stage stage) throws IOException {

        // Set application title.
        stage.setTitle("CMSP - QuickQC Visualizer (v. 1.3.0)");

        // Load main page design.
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainPageGui.fxml"));
        Parent mainPage = fxmlLoader.load();

        // Set application style guide.
        Scene main = new Scene(mainPage);
        main.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styleGuide.css")).toString());

        // Set application icon.
        Image image = new Image("cmsp/quickqc/visualizer/quickQA-icon.png");
        stage.getIcons().add(image);

        // Open gui.
        stage.setResizable(false);
        stage.setScene(main);
        stage.show();
    }

    /**
     *  Main method.
     *
     */
    public static void main(String[] args) {

        // Uses Launcher class as true main method class. Not really sure why it's designed like this...
        launch();
    }

}