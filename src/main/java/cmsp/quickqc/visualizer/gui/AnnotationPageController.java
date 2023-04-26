package cmsp.quickqc.visualizer.gui;

import cmsp.quickqc.visualizer.*;
import cmsp.quickqc.visualizer.parameters.types.ConfigurationTypes;
import cmsp.quickqc.visualizer.parameters.types.InstrumentTypes;
import cmsp.quickqc.visualizer.parameters.types.MatrixTypes;
import cmsp.quickqc.visualizer.utils.annotations.Annotation;
import cmsp.quickqc.visualizer.utils.annotations.AnnotationTypes;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AnnotationPageController {

    @FXML private DatePicker annotationDate;
    @FXML private TextField annotationTimeField;
    @FXML private ChoiceBox annotationTimePicker;
    @FXML private ChoiceBox annotationInstrument;
    @FXML private ChoiceBox annotationConfig;
    @FXML private ChoiceBox annotationMatrix;
    @FXML private ChoiceBox annotationType;
    @FXML private TextArea annotationComment;

    @FXML private Button annotationSubmit;
    @FXML private Button annotationCancel;

    private Boolean validSubmission;
    private String errorMessage;
    private Annotation annotation;

    public void initialize(){

        annotationInstrument.getItems().clear();
        annotationInstrument.valueProperty().setValue(null);
        annotationInstrument.getItems().addAll(Arrays.asList(InstrumentTypes.values()));

        annotationType.getItems().clear();
        annotationType.valueProperty().setValue(null);
        annotationType.getItems().addAll(Arrays.asList(AnnotationTypes.values()));

        String[] timeChoice = {"AM", "PM"};
        annotationTimePicker.getItems().clear();
        annotationTimePicker.valueProperty().setValue(null);
        annotationTimePicker.getItems().addAll(Arrays.asList(timeChoice));

    }

    public void setAnnotation(Annotation annotation) throws ParseException {

        this.annotation = annotation;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(annotation.getDate(), formatter);

        int hour = dateTime.getHour();
        int min = dateTime.getMinute();

        if(hour < 12){
            annotationTimePicker.valueProperty().setValue("AM");
        } else if (hour == 12){
            annotationTimePicker.valueProperty().setValue("PM");
        } else if (hour > 12 && hour < 24){
            hour = hour - 12;
            annotationTimePicker.valueProperty().setValue("PM");
        } else {
            hour = 12;
            annotationTimePicker.valueProperty().setValue("PM");
        }

        annotationTimeField.setText(hour + ":" + min);
        annotationDate.valueProperty().setValue(dateTime.toLocalDate());

        String instrument = InstrumentTypes.getInstrument(annotation.getInstrument());

        ArrayList<String> config = new ArrayList<>();
        config.add("All");
        config.addAll(Arrays.asList(ConfigurationTypes.getConfiguration(instrument)));

        ArrayList<String> matrix = new ArrayList<>();
        matrix.add("All");
        matrix.addAll(Arrays.asList(MatrixTypes.getMatrix(instrument)));

        annotationConfig.getItems().clear();
        annotationConfig.getItems().addAll(config);

        annotationMatrix.getItems().clear();
        annotationMatrix.getItems().addAll(matrix);

        annotationInstrument.valueProperty().setValue(annotation.getInstrument());
        annotationConfig.valueProperty().setValue(annotation.getConfig());
        annotationMatrix.valueProperty().setValue(annotation.getMatrix());
        annotationType.valueProperty().setValue(annotation.getType());
        annotationComment.setText(annotation.getComment());

    }

    @FXML
    protected void annotationInstrumentListener(){

        if(annotationInstrument.getSelectionModel().isEmpty()){ return; }

        String instrument = annotationInstrument.getSelectionModel().getSelectedItem().toString();

        instrument = InstrumentTypes.getInstrument(instrument);

        ArrayList<String> config = new ArrayList<>();
        config.add("All");
        config.addAll(Arrays.asList(ConfigurationTypes.getConfiguration(instrument)));

        ArrayList<String> matrix = new ArrayList<>();
        matrix.add("All");
        matrix.addAll(Arrays.asList(MatrixTypes.getMatrix(instrument)));

        annotationConfig.getItems().clear();
        annotationConfig.getItems().addAll(config);

        annotationMatrix.getItems().clear();
        annotationMatrix.getItems().addAll(matrix);
    }

    @FXML
    protected void annotationSubmitListener(){

        validSubmission = false;
        errorMessage = "";

        if(annotationDate.getValue() == null){
            errorMessage = "Annotation Date not properly set.";
        } else if (annotationTimeField.getText().equals("")){
            errorMessage = "Annotation Time not properly set.";
        } else if (annotationTimePicker.getSelectionModel().isEmpty()){
            errorMessage = "Annotation Time not properly set. Select AM or PM.";
        } else if (annotationInstrument.valueProperty().isNull().getValue()){
            errorMessage = "Annotation Instrument not properly set.";
        } else if (annotationConfig.getSelectionModel().isEmpty()){
            errorMessage = "Annotation Configuration not properly set.";
        } else if (annotationMatrix.getSelectionModel().isEmpty()){
            errorMessage = "Annotation Matrix not properly set.";
        } else if (annotationType.valueProperty().isNull().getValue()){
            errorMessage = "Annotation Event Type not properly set.";
        } else {
            validSubmission = true;
            this.annotation = makeNewAnnotation();
        }

        System.out.println(errorMessage);

        Stage stage = (Stage) annotationSubmit.getScene().getWindow();
        stage.close();

    }

    @FXML
    protected void annotationCancelListener(){

        Stage stage = (Stage) annotationCancel.getScene().getWindow();
        stage.close();

    }

    private Annotation makeNewAnnotation(){

        String date = annotationDate.getValue().toString() + " " +
                annotationTimeField.getText() + " " +
                annotationTimePicker.getSelectionModel().getSelectedItem().toString();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d h:mm a", Locale.US);
        LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
        DateTimeFormatter outFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        String comment = annotationComment.getText();

        if(comment.equals("")){
            comment = "NA";
        }

        Annotation newAnnotation = new Annotation(dateTime.format(outFormat),
                annotationInstrument.getSelectionModel().getSelectedItem().toString(),
                annotationConfig.getSelectionModel().getSelectedItem().toString(),
                annotationMatrix.getSelectionModel().getSelectedItem().toString(),
                annotationType.getSelectionModel().getSelectedItem().toString(),
                comment);

        return newAnnotation;
    }

    public Annotation getAnnotation(){
        return this.annotation;
    }

}
