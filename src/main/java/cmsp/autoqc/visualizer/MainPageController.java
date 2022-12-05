package cmsp.autoqc.visualizer;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.ResourceBundle;

public class MainPageController {

    @FXML private URL location;
    @FXML private ResourceBundle resources;

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
    private AutoQCTask mainTask;
    private Path databasePath;


    public void initialize() {

        mainParameters = new Parameters();
        mainTask = new AutoQCTask(mainParameters);

        lineChart.getData().clear();
        lineChart.setLegendVisible(false);
        yAxis.setLabel("Value");

        instrumentBox.getItems().clear();
        configurationBox.getItems().clear();
        matrixBox.getItems().clear();
        reportBox.getItems().clear();
        dateRangeBox.getItems().clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        startDatePicker.setDisable(true);
        endDatePicker.setDisable(true);

        instrumentBox.getItems().addAll(Arrays.asList(InstrumentTypes.values())); // Init instrument types
        dateRangeBox.getItems().addAll(Arrays.asList(DateRangeType.values()));
        dateRangeBox.setValue("All Dates");
        yAxisBox.getItems().addAll(Arrays.asList(yAxisScaleTypes.values()));
        yAxisBox.setValue("Linear");
    }

    @FXML
    protected void submitButtonClick() {

        if(databasePath == null){
            showMissingDatabaseDialog();
        }

        Parameters selectParams = new Parameters(instrumentBox, configurationBox, matrixBox, reportBox,
                yAxisBox, leveyJenningsButton, movingRangeButton, cusummButton, cusumvButton, startDatePicker,
                endDatePicker, annotationCheckBox, groupXAxisCheckBox, showExcludedPointsCheckBox,
                showGuideSetCheckBox, databasePath);

        if(mainParameters.diffReportSelection(selectParams)){
            mainParameters = selectParams;
            mainTask = new AutoQCTask(mainParameters);
        } else {
            mainParameters = selectParams;
            mainTask.updateParams(mainParameters);
        }

        mainTask.run();

        lineChart.getData().clear();

        ObservableList<XYChart.Series> plotData = mainTask.getPlotData();
        lineChart.getData().addAll(plotData);

        XYChart.Series mainSeries = plotData.get(0);
        ObservableList<XYChart.Data> seriesData = mainSeries.getData();
        for(XYChart.Data data : seriesData){
            data.getNode().setOnMouseClicked(e -> showSampleInfo(data));
        }

        valueTable.getColumns().clear();
        valueTable.getItems().clear();
        valueTable.getColumns().addAll(mainTask.makeTable());
        valueTable.getItems().addAll(mainTask.getTableData());
    }

    @FXML
    protected void setResetButton(){ initialize(); }

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

        if(dateRange == "All Dates"){

            startDatePicker.setDisable(true);
            endDatePicker.setDisable(true);

            startDatePicker.getEditor().clear();
            endDatePicker.getEditor().clear();

        } else if (dateRange == "Custom Date Range") {

            startDatePicker.getEditor().clear();
            endDatePicker.getEditor().clear();

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

        if(dateRange != "Custom Date Range"){ return; }

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

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SamplePage.fxml"));
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

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    protected void menuSetUpListener(){

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SetUpPage.fxml"));
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ErrorPage.fxml"));
            Parent root = fxmlLoader.load();
            ErrorPageController controller = fxmlLoader.<ErrorPageController>getController();
            controller.setErrorMessage("Database path not set properly.\n Please set in - Files > Set Up...");

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
}