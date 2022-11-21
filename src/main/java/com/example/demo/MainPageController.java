package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
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


    public void initialize() {

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

        Parameters AutoQCParameters = new Parameters(instrumentBox, configurationBox, matrixBox, reportBox,
                yAxisBox, leveyJenningsButton, movingRangeButton, cusummButton, cusumvButton, startDatePicker,
                endDatePicker, annotationCheckBox, groupXAxisCheckBox, showExcludedPointsCheckBox,
                showGuideSetCheckBox);

        AutoQCTask task = new AutoQCTask(AutoQCParameters);
        task.run();

        lineChart.getData().clear();
        lineChart.getData().add(task.getPlotData());

        valueTable.getColumns().clear();
        valueTable.getItems().clear();
        valueTable.getColumns().addAll(task.makeTable());
        valueTable.getItems().addAll(task.getTableData());

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
}