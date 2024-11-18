
package cmsp.quickqc.visualizer.gui;

import cmsp.quickqc.visualizer.enums.ErrorTypes;
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
     * @param error Specified error type
     */
    @FXML
    public void setErrorMessage(ErrorTypes error) {

        errorMessageField.setText(error.getErrorMessage());
    }
}
