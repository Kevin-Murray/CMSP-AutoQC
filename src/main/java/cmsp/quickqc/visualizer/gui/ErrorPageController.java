
package cmsp.quickqc.visualizer.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * Controller class for error page.
 */
public class ErrorPageController {

    @FXML private Button closeButton;
    @FXML private TextArea errorMessageField;

    /**
     * Handle close button click from error page.
     */
    @FXML
    void closeButtonClicked() {

        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Set error message with input message.
     *
     * @param message String describing error.
     */
    @FXML
    public void setErrorMessage(String message) {

        errorMessageField.setText(message);
    }
}
