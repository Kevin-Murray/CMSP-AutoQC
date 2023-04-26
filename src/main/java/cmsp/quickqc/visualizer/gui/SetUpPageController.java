package cmsp.quickqc.visualizer.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SetUpPageController {

    @FXML private Button cancelButton;
    @FXML private Button openButton;
    @FXML private TextField pathField;
    @FXML private Button pathSelectorButton;

    @FXML
    void cancelButtonClicked(ActionEvent event) {

        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();

    }

    @FXML
    void openButtonClicked(ActionEvent event) {

        Stage stage = (Stage) openButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void pathSelectorButtonClicked(ActionEvent event) {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Folder");
        File file = directoryChooser.showDialog(null);

        if(file != null){
            pathField.setText(file.getAbsolutePath());
        }

    }

    public Path getDatabaseFolder() {

        return Paths.get(pathField.getText());

    }

    public void setDatabaseFolder(Path databaseFolder) {

        if(databaseFolder != null){
            this.pathField.setText(databaseFolder.toString());
        }

    }

}
