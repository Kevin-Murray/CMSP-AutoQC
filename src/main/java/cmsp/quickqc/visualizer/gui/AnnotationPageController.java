
package cmsp.quickqc.visualizer.gui;

import cmsp.quickqc.visualizer.parameters.types.ConfigurationTypes;
import cmsp.quickqc.visualizer.parameters.types.InstrumentTypes;
import cmsp.quickqc.visualizer.parameters.types.MatrixTypes;
import cmsp.quickqc.visualizer.utils.annotations.Annotation;
import cmsp.quickqc.visualizer.utils.annotations.AnnotationTypes;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Controller class for annotation page pop-up window.
 */
public class AnnotationPageController {

    @FXML private DatePicker annotationDate;
    @FXML private TextField annotationTimeField;
    @FXML private ChoiceBox<String> annotationTimePicker;
    @FXML private ChoiceBox<String> annotationInstrument;
    @FXML private ChoiceBox<String> annotationConfig;
    @FXML private ChoiceBox<String> annotationMatrix;
    @FXML private ChoiceBox<String> annotationType;
    @FXML private TextArea annotationComment;
    @FXML private Button annotationSubmit;
    @FXML private Button annotationCancel;

    private Annotation annotation;

    /**
     * Initialize window with default options.
     */
    public void initialize(){

        // Instrument types. All other configuration options set dynamically.
        annotationInstrument.getItems().clear();
        annotationInstrument.valueProperty().setValue(null);
        annotationInstrument.getItems().addAll(InstrumentTypes.getInstrumentNames());

        // Annotation types.
        annotationType.getItems().clear();
        annotationType.valueProperty().setValue(null);
        annotationType.getItems().addAll(AnnotationTypes.getAnnotationTypes());

        // Time settings.
        // TODO - better parameterization of timeChoice? Enum or global parameter?
        // TODO - initialize annotation page with current time?
        String[] timeChoice = {"AM", "PM"};
        annotationTimePicker.getItems().clear();
        annotationTimePicker.valueProperty().setValue(null);
        annotationTimePicker.getItems().addAll(Arrays.asList(timeChoice));
    }

    /**
     * Fill window with selected annotation information.
     *
     * @param annotation User selected annotation.
     */
    public void setAnnotation(Annotation annotation) {

        // Time format specifications.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(annotation.getDate(), formatter);

        int hour = dateTime.getHour();
        int min = dateTime.getMinute();

        // Handle AM / PM formatting.
        if(hour < 12){
            annotationTimePicker.valueProperty().setValue("AM");
        } else if (hour > 12){
            hour = hour - 12;
            annotationTimePicker.valueProperty().setValue("PM");
        } else {
            annotationTimePicker.valueProperty().setValue("PM");
        }

        annotationTimeField.setText(hour + ":" + String.format("%02d", min));
        annotationDate.valueProperty().setValue(dateTime.toLocalDate());

        // Set instrument selection.
        String instrument = InstrumentTypes.getInstrument(annotation.getInstrument());

        // All non-instrument selections may specify "All" to indicate every context should include this annotation.
        // Get configurations options for selected instrument.
        // TODO - Configuration specific matrix selections.
        ArrayList<String> config = new ArrayList<>();
        config.add("All");
        String[] configurations = ConfigurationTypes.getConfiguration(instrument);
        if(configurations != null) config.addAll(Arrays.asList(configurations));

        annotationConfig.getItems().clear();
        annotationConfig.getItems().addAll(config);

        // Get matrix options for selected instrument.
        ArrayList<String> matrix = new ArrayList<>();
        matrix.add("All");
        String[] matrices = MatrixTypes.getMatrix(instrument);
        if(matrices != null) matrix.addAll(Arrays.asList(matrices));

        annotationMatrix.getItems().clear();
        annotationMatrix.getItems().addAll(matrix);

        // Set all fields with annotations values.
        annotationInstrument.valueProperty().setValue(annotation.getInstrument());
        annotationConfig.valueProperty().setValue(annotation.getConfig());
        annotationMatrix.valueProperty().setValue(annotation.getMatrix());
        annotationType.valueProperty().setValue(annotation.getType());
        annotationComment.setText(annotation.getComment());
    }

    /**
     * Annotation instrument choice box listener.
     * Dynamically updates configuration and matrix choice boxes based on user selection.
     * TODO - configuration-specific matrix choices.
     */
    @FXML
    protected void annotationInstrumentListener() {

        if(annotationInstrument.getSelectionModel().isEmpty()) return;

        String instrument = annotationInstrument.getSelectionModel().getSelectedItem();

        instrument = InstrumentTypes.getInstrument(instrument);

        // Get and update configuration options.
        ArrayList<String> config = new ArrayList<>();
        config.add("All");
        String[] configurations = ConfigurationTypes.getConfiguration(instrument);
        if(configurations != null) config.addAll(Arrays.asList(configurations));

        annotationConfig.getItems().clear();
        annotationConfig.getItems().addAll(config);

        // Get and update matrix options.
        ArrayList<String> matrix = new ArrayList<>();
        matrix.add("All");
        String[] matrices = MatrixTypes.getMatrix(instrument);
        if(matrices != null) matrix.addAll(Arrays.asList(matrices));

        annotationMatrix.getItems().clear();
        annotationMatrix.getItems().addAll(matrix);
    }

    /**
     * Handles annotation submit button click.
     * If selection valid, make new annotation.
     * TODO - no error handling currently!!! Whoops... need to get on that ASAP.
     */
    @FXML
    protected void annotationSubmitListener() {

        String errorMessage = "";

        // TODO - Is this the best way to handle this?
        if(annotationDate.getValue() == null) {
            errorMessage = "Date not properly set.";
        } else if (annotationTimeField.getText().isEmpty()) {
            errorMessage = "Time not properly set.";
        } else if (annotationTimePicker.getSelectionModel().isEmpty()) {
            errorMessage = "Time not properly set. Select AM or PM.";
        } else if (annotationInstrument.valueProperty().isNull().getValue()) {
            errorMessage = "Instrument not properly set.";
        } else if (annotationConfig.getSelectionModel().isEmpty()) {
            errorMessage = "Configuration not properly set.";
        } else if (annotationMatrix.getSelectionModel().isEmpty()) {
            errorMessage = "Matrix not properly set.";
        } else if (annotationType.valueProperty().isNull().getValue()) {
            errorMessage = "Event Type not properly set.";
        } else {
            this.annotation = makeNewAnnotation();
        }

        // TODO - replace with error page call.
        System.out.println(errorMessage);

        // Close window.
        Stage stage = (Stage) annotationSubmit.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles annotation page cancel button click.
     * TODO - this doesn't seem to handled correctly. I am seeing big NullPointerException in Runtime window.
     */
    @FXML
    protected void annotationCancelListener() {

        Stage stage = (Stage) annotationCancel.getScene().getWindow();
        stage.close();
    }

    /**
     * Make new annotation object with user selected context options.
     *
     * @return Annotation object.
     */
    private Annotation makeNewAnnotation() {

        // Format date and time selection.
        // I hate date-time handling...
        String date = annotationDate.getValue().toString() + " " +
                annotationTimeField.getText() + " " +
                annotationTimePicker.getSelectionModel().getSelectedItem();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d h:mm a", Locale.US);
        LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
        DateTimeFormatter outFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // Handle comment selection. Replace empty comment with "NA".
        String comment = annotationComment.getText();
        if(comment.isEmpty()) comment = "NA";

        // Return new annotation object.
        return new Annotation(dateTime.format(outFormat),
                annotationInstrument.getSelectionModel().getSelectedItem(),
                annotationConfig.getSelectionModel().getSelectedItem(),
                annotationMatrix.getSelectionModel().getSelectedItem(),
                annotationType.getSelectionModel().getSelectedItem(),
                comment);
    }

    /**
     * Get selected annotation object.
     */
    public Annotation getAnnotation() {

        return this.annotation;
    }
}
