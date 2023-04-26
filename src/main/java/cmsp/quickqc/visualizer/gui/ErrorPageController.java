package cmsp.quickqc.visualizer.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ErrorPageController {

    @FXML private Button closeButton;

    @FXML private TextArea errorMessageField;

    @FXML
    void closeButtonClicked() {

        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();

    }

    @FXML
    public void setErrorMessage(String message) {

        errorMessageField.setText(message);

    }

}
