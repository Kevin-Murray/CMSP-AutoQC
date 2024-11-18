
package cmsp.quickqc.visualizer.gui;

import cmsp.quickqc.visualizer.Launcher;
import cmsp.quickqc.visualizer.datamodel.ReportContext;
import cmsp.quickqc.visualizer.enums.ErrorTypes;
import cmsp.quickqc.visualizer.utils.ContextFilteringUtils;
import cmsp.quickqc.visualizer.datamodel.Annotation;
import cmsp.quickqc.visualizer.enums.AnnotationTypes;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    private ReportContext selectedContext;
    private List<ReportContext> reportContext;
    private Annotation annotation;
    private Boolean canceled;

    /**
     * Initialize window with default options.
     */
    public void initialize() {

        this.canceled = true;

        // Instrument types. All other configuration options set dynamically.
        annotationInstrument.getItems().clear();
        annotationInstrument.valueProperty().setValue(null);

        // Annotation types.
        annotationType.getItems().clear();
        annotationType.valueProperty().setValue(null);
        annotationType.getItems().addAll(AnnotationTypes.getAnnotationTypes());

        // Initialize with current date.
        Date date = new Date();
        annotationDate.setValue(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        annotationDate.getEditor().setStyle("-fx-alignment: center;");

        // Initialize with current time.
        SimpleDateFormat formatDate = new SimpleDateFormat("hh:mm");
        annotationTimeField.setText(formatDate.format(date));

        // Initialize AM-PM setting and current time selection.
        String[] timeChoice = {"AM", "PM"};
        annotationTimePicker.getItems().clear();
        annotationTimePicker.valueProperty().setValue(null);
        annotationTimePicker.getItems().addAll(Arrays.asList(timeChoice));
        SimpleDateFormat formatAMPM = new SimpleDateFormat("a");
        annotationTimePicker.valueProperty().setValue(formatAMPM.format(date));

        // Initialize with current selections
        if(reportContext != null) {

            annotationInstrument.getItems().addAll(ContextFilteringUtils.getUniqueInstruments(reportContext));
        }

        // Update fields with selected entry
        if(selectedContext != null) {

            // Set instrument selection
            annotationInstrument.valueProperty().setValue(selectedContext.instrument());

            // Get and update configuration options.
            List<String> config = new ArrayList<>();
            config.add("All");
            config.addAll(ContextFilteringUtils.getUniqueConfigurations(reportContext, selectedContext.instrument()));
            annotationConfig.getItems().clear();
            annotationConfig.getItems().addAll(config);
            annotationConfig.valueProperty().setValue(selectedContext.config());

            // Get and update matrix options.
            List<String> matrix = new ArrayList<>();
            matrix.add("All");
            matrix.addAll(ContextFilteringUtils.getUniqueMatrices(reportContext, selectedContext.instrument(), selectedContext.config()));
            annotationMatrix.getItems().clear();
            annotationMatrix.getItems().addAll(matrix);
            annotationMatrix.valueProperty().setValue(selectedContext.matrix());
        }
    }

    /**
     * Set application report context and current selected context.
     * Re-initialize controller to update values.
     */
    public void setContext(List<ReportContext> reportContext, ReportContext selectedContext) {

        this.reportContext = reportContext;
        this.selectedContext = selectedContext;

        initialize();
    }

    /**
     * Fill window with selected annotation information.
     *
     * @param annotation User selected annotation.
     */
    public void setAnnotation(List<ReportContext> reportContext, Annotation annotation) {

        this.reportContext = reportContext;
        initialize();

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
        String instrument = annotation.getInstrument();

        // All non-instrument selections may specify "All" to indicate every context should include this annotation.
        // Get configurations options for selected instrument.
        List<String> config = new ArrayList<>();
        config.add("All");
        config.addAll(ContextFilteringUtils.getUniqueConfigurations(reportContext, instrument));
        annotationConfig.getItems().clear();
        annotationConfig.getItems().addAll(config);

        // Get matrix options for selected instrument.
        List<String> matrix = new ArrayList<>();
        matrix.add("All");
        matrix.addAll(ContextFilteringUtils.getUniqueMatrices(reportContext, instrument, annotation.getConfig()));
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
     * Dynamically updates configuration choice boxes based on user selection.
     */
    @FXML
    protected void annotationInstrumentListener() {

        if(annotationInstrument.getSelectionModel().isEmpty()) return;

        String instrument = annotationInstrument.getSelectionModel().getSelectedItem();

        // Get and update configuration options.
        List<String> config = new ArrayList<>();
        config.add("All");
        config.addAll(ContextFilteringUtils.getUniqueConfigurations(reportContext, instrument));
        annotationConfig.getItems().clear();
        annotationConfig.getItems().addAll(config);
    }

    /**
     * Annotation configuration box listener.
     * Dynamically updates matrix choice boxed based on user selection.
     */
    @FXML
    protected void annotationConfigurationListener() {

        if(annotationConfig.getSelectionModel().isEmpty()) return;

        String instrument = annotationInstrument.getSelectionModel().getSelectedItem();
        String config = annotationConfig.getSelectionModel().getSelectedItem();

        // Get and update matrix options.
        List<String> matrix = new ArrayList<>();
        matrix.add("All");
        matrix.addAll(ContextFilteringUtils.getUniqueMatrices(reportContext, instrument, config));
        annotationMatrix.getItems().clear();
        annotationMatrix.getItems().addAll(matrix);
    }

    /**
     * Handles annotation submit button click.
     * If selection valid, make new annotation.
     */
    @FXML
    protected void annotationSubmitListener() {

        // TODO - Is this the best way to handle this?
        if(annotationDate.getValue() == null) {
            showErrorMessage(ErrorTypes.DATE);
        } else if (annotationTimeField.getText().isEmpty()) {
           showErrorMessage(ErrorTypes.TIME);
        } else if (annotationTimePicker.getSelectionModel().isEmpty()) {
            showErrorMessage(ErrorTypes.TIMEDAY);
        } else if (annotationInstrument.valueProperty().isNull().getValue()) {
            showErrorMessage(ErrorTypes.INSTRUMENT);
        } else if (annotationConfig.getSelectionModel().isEmpty()) {
            showErrorMessage(ErrorTypes.CONFIGURATION);
        } else if (annotationMatrix.getSelectionModel().isEmpty()) {
            showErrorMessage(ErrorTypes.MATRIX);
        } else if (annotationType.valueProperty().isNull().getValue()) {
            showErrorMessage(ErrorTypes.TYPE);
        } else {
            annotation = makeNewAnnotation();
            canceled = false;
        }

        // Close window.
        if(!canceled) {
            Stage stage = (Stage) annotationSubmit.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Handles annotation page cancel button click.
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

    /**
     * Determine if window was closed through submit button
     *
     * @return true if cancel or close window selected
     */
    public Boolean wasCanceled() {

        return this.canceled;
    }

    /**
     * Launches error window with input error message.
     */
    @FXML
    protected  void showErrorMessage(ErrorTypes error) {

        try {

            // Get error page window design.
            FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("ErrorPage.fxml"));
            Parent root = fxmlLoader.load();

            // Initialize error window with message.
            ErrorPageController controller = fxmlLoader.getController();
            controller.setErrorMessage(error);

            // Launch pop-up window.
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

        } catch(Exception e) {

            // TODO - better error handling.
            e.printStackTrace();
        }
    }
}
