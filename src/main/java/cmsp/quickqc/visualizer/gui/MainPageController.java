package cmsp.quickqc.visualizer.gui;

import cmsp.quickqc.visualizer.*;
import cmsp.quickqc.visualizer.datamodel.DataEntry;
import cmsp.quickqc.visualizer.parameters.*;

import cmsp.quickqc.visualizer.parameters.types.*;
import cmsp.quickqc.visualizer.utils.annotations.Annotation;
import cmsp.quickqc.visualizer.utils.annotations.AnnotationStyles;
import cmsp.quickqc.visualizer.utils.annotations.AnnotationTypes;
import cmsp.quickqc.visualizer.utils.plotUtils.yAxisScaleTypes;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Set;
import java.util.prefs.Preferences;

public class MainPageController {

    @FXML public ChoiceBox instrumentBox;
    @FXML public ChoiceBox configurationBox;
    @FXML public ChoiceBox matrixBox;
    @FXML public ChoiceBox reportBox;
    @FXML public ChoiceBox dateRangeBox;
    @FXML public ChoiceBox yAxisBox;

    @FXML public Button submitButton;
    @FXML public Button resetButton;

    @FXML public RadioButton leveyJenningsButton;
    @FXML public RadioButton movingRangeButton;
    @FXML public RadioButton cusummButton;
    @FXML public RadioButton cusumvButton;

    @FXML public DatePicker startDatePicker;
    @FXML public DatePicker endDatePicker;

    @FXML public CheckBox annotationCheckBox;
    @FXML public CheckBox groupXAxisCheckBox;
    @FXML public CheckBox showExcludedPointsCheckBox;
    @FXML public CheckBox showGuideSetCheckBox;

    @FXML public LineChart lineChart;
    @FXML public CategoryAxis xAxis;
    @FXML public NumberAxis yAxis;

    @FXML public TableView valueTable;
    @FXML public TableView annotationTable;

    private Parameters mainParameters;
    private QuickQCTask mainTask;
    private Path databasePath;

    private Preferences prefs;

    private Boolean reset = false;

    public void initialize() {

        mainParameters = new Parameters();
        mainTask = new QuickQCTask(mainParameters);

        lineChart.getData().clear();
        lineChart.setLegendVisible(false);
        yAxis.setLabel("Value");

        instrumentBox.getItems().clear();
        instrumentBox.valueProperty().setValue(null);
        configurationBox.getItems().clear();
        matrixBox.getItems().clear();
        reportBox.getItems().clear();
        dateRangeBox.getItems().clear();
        dateRangeBox.valueProperty().setValue(null);
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        startDatePicker.setDisable(true);
        endDatePicker.setDisable(true);

        if(!reset){
            getPreferences();
        } else {
            dateRangeBox.setValue("All Dates");
            reset = false;
        }

        instrumentBox.getItems().addAll(Arrays.asList(InstrumentTypes.values())); // Init instrument types
        dateRangeBox.getItems().addAll(Arrays.asList(DateRangeType.values()));

        yAxisBox.getItems().addAll(Arrays.asList(yAxisScaleTypes.values()));
        yAxisBox.setValue("Linear");


    }

    @FXML
    protected void submitButtonClick() {

        if(databasePath == null){
            showMissingDatabaseDialog();
            return;
        }

        lineChart.getData().clear();

        Parameters selectParams = new Parameters(instrumentBox, configurationBox, matrixBox, reportBox,
                yAxisBox, dateRangeBox, leveyJenningsButton, movingRangeButton, cusummButton, cusumvButton, startDatePicker,
                endDatePicker, annotationCheckBox, groupXAxisCheckBox, showExcludedPointsCheckBox,
                showGuideSetCheckBox, databasePath);

        if(mainParameters.diffReportSelection(selectParams)){
            mainParameters = selectParams;
            mainTask = new QuickQCTask(mainParameters);
        } else {
            mainParameters = selectParams;
            mainTask.updateParams(mainParameters);
        }

        mainTask.run();

        // No entries in date range
        if(mainTask.getWorkingEntrySize() == 0){
            showNoDataInRangeDialog();
            return;
        }

        ObservableList<XYChart.Series> plotData = mainTask.getPlotData();
        lineChart.getData().addAll(plotData);

        XYChart.Series mainSeries = plotData.get(0);
        ObservableList<XYChart.Data> seriesData = mainSeries.getData();
        for(XYChart.Data data : seriesData) {
            data.getNode().setOnMouseClicked(e -> showSampleInfo(data));
        }

        Set<Node> node = lineChart.lookupAll(".default-color0.chart-line-symbol.series0.");
        ArrayList<Node> tmp = new ArrayList<>(node);

        for(int i = 0; i < tmp.size(); i++){

            if(mainTask.isAnnotation(i)){

                String type = AnnotationTypes.getAnnotationType(mainTask.getAnnotationType(i));
                tmp.get(i).setStyle(AnnotationStyles.valueOf(type).toString());

            } else {

                if(mainTask.isExcluded(i)){
                    if(mainTask.hasComment(i)){
                        tmp.get(i).setStyle("-fx-background-color: red, black;\n"
                                + "    -fx-background-insets: 0, 2;\n"
                                + "    -fx-background-radius: 0;\n"
                                + "    -fx-padding: 5px;");
                    } else {
                        tmp.get(i).setStyle("-fx-background-color: red, black;\n"
                                + "    -fx-background-insets: 0, 2;\n"
                                + "    -fx-background-radius: 5px;\n"
                                + "    -fx-padding: 5px;");
                    }

                }
            }
        }

        valueTable.getColumns().clear();
        valueTable.getItems().clear();
        valueTable.getColumns().addAll(mainTask.makeTable());
        valueTable.getItems().addAll(mainTask.getTableData());

        annotationTable.getColumns().clear();
        annotationTable.getItems().clear();
        annotationTable.getColumns().addAll(mainTask.makeAnnotationTable());

        if(mainTask.getWorkingAnnotationSize() != 0){
            annotationTable.getItems().addAll(mainTask.getAnnotationData());
        }

        setPreferences();
    }

    @FXML
    protected void setResetButton(){
        this.reset = true;
        initialize();
    }

    @FXML
    protected void instrumentBoxListener() {

        // Nothing set, ignore
        if(instrumentBox.getSelectionModel().isEmpty()){ return; }

        String instrument = instrumentBox.getSelectionModel().getSelectedItem().toString();

        // Get corresponding inputs
        instrument = InstrumentTypes.getInstrument(instrument);
        String[] configuration = ConfigurationTypes.getConfiguration(instrument);
        String[] matrix = MatrixTypes.getMatrix(instrument);

        // Clear existing selections and set items
        configurationBox.getItems().clear();
        matrixBox.getItems().clear();
        reportBox.getItems().clear();

        // Add new options
        configurationBox.getItems().addAll(Arrays.asList(configuration));
        matrixBox.getItems().addAll(Arrays.asList(matrix));
    }

    @FXML
    protected void matrixBoxListener() {

        // Nothing set, ignore
        if(matrixBox.getSelectionModel().isEmpty()){ return; }

        // Get corresponding inputs
        String matrix = matrixBox.getSelectionModel().getSelectedItem().toString();
        ReportObject reports = new ReportObject(matrix);

        // Clear existing selections and set items
        reportBox.getItems().clear();
        reportBox.getItems().addAll(Arrays.asList(reports.getReports()));
    }

    @FXML
    protected void reportBoxListener() {

        if(reportBox.getSelectionModel().isEmpty()){
            submitButton.setDisable(true);
        } else {
            submitButton.setDisable(false);
        }
    }

    @FXML
    protected void dataRangeListener() {

        // Nothing set, ignore
        if(dateRangeBox.getSelectionModel().isEmpty()){ return; }

        String dateRange = dateRangeBox.getSelectionModel().getSelectedItem().toString();

        if(dateRange.equals("All Dates")) {

            startDatePicker.setDisable(true);
            endDatePicker.setDisable(true);

            startDatePicker.setValue(null);
            endDatePicker.setValue(null);

        } else if (dateRange.equals("Custom Date Range")) {

            startDatePicker.setValue(null);
            endDatePicker.setValue(null);

            startDatePicker.setDisable(false);
            endDatePicker.setDisable(false);

        } else {

            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minus(DateRangeType.getDateRange(dateRange), ChronoUnit.DAYS);

            startDatePicker.setDisable(true);
            endDatePicker.setDisable(true);

            startDatePicker.getEditor().clear();
            endDatePicker.getEditor().clear();

            startDatePicker.setValue(startDate);
            endDatePicker.setValue(endDate);
        }
    }


    @FXML
    protected void datePickerListener(){

        if(dateRangeBox.getSelectionModel().isEmpty()){ return; }

        String dateRange = dateRangeBox.getSelectionModel().getSelectedItem().toString();

        if(!dateRange.equals("Custom Date Range")) { return; }

        if(startDatePicker.getValue() != null && endDatePicker.getValue() != null){

            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if(startDate.isAfter(endDate)){

                startDatePicker.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(0), Insets.EMPTY)));
                submitButton.setDisable(true);

            } else {

                startDatePicker.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(0), Insets.EMPTY)));

                if(!reportBox.getSelectionModel().isEmpty()){
                    submitButton.setDisable(false);
                }
            }
        }
    }

    protected void showSampleInfo(XYChart.Data data){

        DataEntry selectedEntry = this.mainTask.getDataEntry(data);

        if(selectedEntry == null) return;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("SamplePage.fxml"));
            Parent root = fxmlLoader.load();
            SamplePageController controller = fxmlLoader.<SamplePageController>getController();
            controller.setDataEntry(selectedEntry);

            Stage stage = new Stage();
            stage.setTitle("QC Information");
            stage.setResizable(false);

            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

            if(controller.getChangedInclusion()){

                this.mainTask.setDataEntryInclusion(selectedEntry);
                this.mainTask.writeReport();
                submitButtonClick();
            }

            if(controller.getChangedComment()) {

                this.mainTask.setDataEntryComment(selectedEntry, controller.getComment());
                this.mainTask.writeReport();
                submitButtonClick();
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    protected void menuSetUpListener(){

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("SetUpPage.fxml"));
            Parent root = fxmlLoader.load();
            SetUpPageController controller = fxmlLoader.<SetUpPageController>getController();
            controller.setDatabaseFolder(this.databasePath);

            Stage stage = new Stage();
            stage.setTitle("AutoQC Set Up...");
            stage.setResizable(false);

            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

            this.databasePath = controller.getDatabaseFolder();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML protected void showMissingDatabaseDialog() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("ErrorPage.fxml"));
            Parent root = fxmlLoader.load();
            ErrorPageController controller = fxmlLoader.<ErrorPageController>getController();
            controller.setErrorMessage("Database path not set properly.\nPlease set in - Files > Set Up...");

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(false);

            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    @FXML protected void showNoDataInRangeDialog() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("ErrorPage.fxml"));
            Parent root = fxmlLoader.load();
            ErrorPageController controller = fxmlLoader.<ErrorPageController>getController();
            controller.setErrorMessage("No QC entries in date range.\nPlease adjust the range and try again.");

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(false);

            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void setPreferences() {

        // This will define a node in which the preferences can be stored
        prefs = Preferences.userRoot().node(this.getClass().getName());

        String ID1 = "Database Path";
        String ID2 = "Instrument";
        String ID3 = "Configuration";
        String ID4 = "Matrix";
        String ID5 = "Report";
        String ID6 = "Date Range";
        String ID7 = "Show Excluded";

        prefs.put(ID1, this.mainParameters.databasePath.toString());
        prefs.put(ID2, this.mainParameters.instrument);
        prefs.put(ID3, this.mainParameters.configuration);
        prefs.put(ID4, this.mainParameters.matrix);
        prefs.put(ID5, this.mainParameters.report);
        prefs.put(ID6, this.mainParameters.dateRange);
        prefs.putBoolean(ID7, this.mainParameters.showExcluded);
    }

    @FXML
    protected void getPreferences() {

        prefs = Preferences.userRoot().node(this.getClass().getName());

        String ID1 = "Database Path";
        String ID2 = "Instrument";
        String ID3 = "Configuration";
        String ID4 = "Matrix";
        String ID5 = "Report";
        String ID6 = "Date Range";
        String ID7 = "Show Excluded";

        String databasePath = prefs.get(ID1, null);
        String instrument = prefs.get(ID2, null);
        String config = prefs.get(ID3, null);
        String matrix = prefs.get(ID4, null);
        String report = prefs.get(ID5, null);
        String dateRange = prefs.get(ID6, null);
        boolean showExcluded = prefs.getBoolean(ID7, true);

        if(databasePath != null) {
            this.databasePath = Paths.get(databasePath);
        }

        if(instrument != null) {

            instrumentBox.setValue(instrument);

            instrument = InstrumentTypes.getInstrument(instrument);

            String[] configurationOptions = ConfigurationTypes.getConfiguration(instrument);
            String[] matrixOptions = MatrixTypes.getMatrix(instrument);

            // Add new options
            configurationBox.getItems().addAll(Arrays.asList(configurationOptions));
            matrixBox.getItems().addAll(Arrays.asList(matrixOptions ));
        }

        if(config != null) {
            configurationBox.setValue(config);
        }

        if(matrix != null) {
            matrixBox.setValue(matrix);
            matrixBoxListener();
        }

        if(report != null) {
            reportBox.setValue(report);
            reportBoxListener();
        }

        if(dateRange != null) {

            //TODO - Merge this with dateRangeListener function

            dateRangeBox.setValue(dateRange);

            if(dateRange.equals("All Dates")) {

                startDatePicker.setDisable(true);
                endDatePicker.setDisable(true);

                startDatePicker.setValue(null);
                endDatePicker.setValue(null);

            } else if (dateRange.equals("Custom Date Range")) {

                startDatePicker.setValue(null);
                endDatePicker.setValue(null);

                startDatePicker.setDisable(false);
                endDatePicker.setDisable(false);

            } else {

                LocalDate endDate = LocalDate.now();
                LocalDate startDate = endDate.minus(DateRangeType.getDateRange(dateRange), ChronoUnit.DAYS);

                startDatePicker.setDisable(true);
                endDatePicker.setDisable(true);

                startDatePicker.setValue(null);
                endDatePicker.setValue(null);

                startDatePicker.setValue(startDate);
                endDatePicker.setValue(endDate);
            }
        }

        showExcludedPointsCheckBox.setSelected(showExcluded);
    }

    @FXML
    protected void annotationClickListener(MouseEvent event){

        int index = annotationTable.getSelectionModel().selectedIndexProperty().get();

        if(event.getButton().toString() == "SECONDARY"){

            ContextMenu contextMenu = new ContextMenu();
            SeparatorMenuItem sep1 = new SeparatorMenuItem();
            SeparatorMenuItem sep2 = new SeparatorMenuItem();

            MenuItem menuItem1 = new MenuItem("Export Annotation Table");
            MenuItem menuItem2 = new MenuItem("Add Annotation");
            MenuItem menuItem3 = new MenuItem("Edit Annotation");
            MenuItem menuItem4 = new MenuItem("Delete Annotation");

            Annotation annotation = null;

            if(annotationTable.getSelectionModel().isEmpty()){
                menuItem3.setDisable(true);
                menuItem4.setDisable(true);
            } else {
                annotation = mainTask.getSelectedAnnotation(index);
            }

            Annotation selectedAnnotation = annotation;

            contextMenu.getItems().add(menuItem1);
            contextMenu.getItems().add(sep1);
            contextMenu.getItems().add(menuItem2);
            contextMenu.getItems().add(menuItem3);
            contextMenu.getItems().add(sep2);
            contextMenu.getItems().add(menuItem4);


            menuItem1.setOnAction((ActionEvent e) -> {
                System.out.println("Action 1");
            });
            menuItem2.setOnAction((ActionEvent e) -> {
                addAnnotation();
            });
            menuItem3.setOnAction((ActionEvent e) -> {
                editAnnotation(selectedAnnotation);
            });
            menuItem4.setOnAction((ActionEvent e) -> {
                deleteAnnotation(selectedAnnotation);
            });

            annotationTable.setContextMenu(contextMenu);
        }
    }

    @FXML
    private void addAnnotation(){

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("AnnotationPage.fxml"));
            Parent root = fxmlLoader.load();
            AnnotationPageController controller = fxmlLoader.<AnnotationPageController>getController();

            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Add Annotation");

            stage.setScene(new Scene(root));
            stage.showAndWait();

            mainTask.addAnnotation(controller.getAnnotation());
            mainTask.sortAnnotations();
            mainTask.writeAnnotationReport();

            submitButtonClick();

        } catch(Exception e) {
            e.printStackTrace();
        }


    }

    @FXML
    private void editAnnotation(Annotation annotation){

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("AnnotationPage.fxml"));
            Parent root = fxmlLoader.load();
            AnnotationPageController controller = fxmlLoader.<AnnotationPageController>getController();
            controller.setAnnotation(annotation);

            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Edit Annotation");

            stage.setScene(new Scene(root));
            stage.showAndWait();

            mainTask.editAnnotation(annotation, controller.getAnnotation());
            mainTask.sortAnnotations();
            mainTask.writeAnnotationReport();

            submitButtonClick();

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void deleteAnnotation(Annotation annotation){

        mainTask.deleteAnnotation(annotation);
        mainTask.writeAnnotationReport();

        submitButtonClick();
    }

}