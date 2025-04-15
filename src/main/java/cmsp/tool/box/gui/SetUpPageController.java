
package cmsp.tool.box.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Controller class for application set-up page.
 */
public class SetUpPageController {

    @FXML private Button cancelButton;
    @FXML private Button openButton;
    @FXML private TextField pathField;

    /**
     * Handle cancel button click - Close window.
     */
    @FXML
    void cancelButtonClicked() {

        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Handle open button click - Close window.
     */
    @FXML
    void openButtonClicked() {

        Stage stage = (Stage) openButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles path selector button clicked.
     * Allow user to navigate to location of QC databases.
     */
    @FXML
    void pathSelectorButtonClicked() {

        // Initiate new DirectoryChooser object.
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Folder");
        File file = directoryChooser.showDialog(null);

        if(file != null) pathField.setText(file.getAbsolutePath());
    }

    /**
     * Get selected database directory path.
     */
    public Path getDatabaseFolder() {

        return Paths.get(pathField.getText());
    }

    /**
     * Set controller database location field.
     */
    public void setDatabaseFolder(Path databaseFolder) {

        if(databaseFolder != null) this.pathField.setText(databaseFolder.toString());
    }
}
