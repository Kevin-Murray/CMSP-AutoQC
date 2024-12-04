
package cmsp.quickqc.visualizer.gui;

import cmsp.quickqc.visualizer.*;
import cmsp.quickqc.visualizer.datamodel.DataEntry;
import cmsp.quickqc.visualizer.datamodel.Parameters;
import cmsp.quickqc.visualizer.datamodel.ReportContext;
import cmsp.quickqc.visualizer.enums.*;
import cmsp.quickqc.visualizer.utils.ContextFilteringUtils;
import cmsp.quickqc.visualizer.utils.PlotUtils;
import cmsp.quickqc.visualizer.datamodel.Annotation;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.prefs.Preferences;

/**
 * Controller class for Main Page GUI of QuickQC
 */
public class MainPageController {

    @FXML public ChoiceBox<String> instrumentBox;
    @FXML public ChoiceBox<String> configurationBox;
    @FXML public ChoiceBox<String> matrixBox;
    @FXML public ChoiceBox<String> standardBox;
    @FXML public ChoiceBox<String> reportBox;
    @FXML public ChoiceBox<String> dateRangeBox;

    @FXML public RadioButton leveyJenningsButton;
    @FXML public RadioButton movingRangeButton;
    @FXML public RadioButton cusummButton;
    @FXML public RadioButton cusumvButton;

    @FXML public DatePicker startDatePicker;
    @FXML public DatePicker endDatePicker;

    @FXML public ComboBox<String> logNumberBox;

    @FXML public LineChart<String, Number> lineChart;
    @FXML public CategoryAxis xAxis;
    @FXML public NumberAxis yAxis;

    @FXML public TableView<Map<Integer, String>> valueTable;
    @FXML public TableView<Map<Integer, String>>  annotationTable;
    @FXML public Label nEntriesLabel;
    @FXML public Label firstEntryLabel;
    @FXML public Label seriesMeanLabel;
    @FXML public Label nAnnotLabel;
    @FXML public Label lastEntryLabel;
    @FXML public Label avgFreqLabel;
    @FXML public Label seriesMinLabel;
    @FXML public Label seriesMaxLabel;
    @FXML public Label seriesSDLabel;
    @FXML public Label seriesRsdLabel;
    @FXML public Label lastRefreshLabel;

    private Parameters mainParameters;
    private QuickQCTask mainTask;

    private Path databasePath;
    private Path reportConfigPath;

    private List<ReportContext> reportContexts;
    private ReportContext selectedContext;
    private Map<String, Boolean> annotationMap;

    private Boolean logScale;
    private Boolean showExcluded;
    private Boolean showLegend;
    private Boolean showGuideSet;
    private Boolean refresh = false;

    private String varType;
    private List<String> logNumbers;

    private Preferences prefs;


    /**
     * Initialize controller class to default values.
     * This how the application will appear when used for the first time or reset.
     */
    public void initialize() {

        mainParameters = new Parameters();
        mainTask = new QuickQCTask(mainParameters);

        // Set all gui options to default
        setApplicationDefaults();

        // If user has used application previously, get last used configuration.
        getPreferences();

        // Add instrument and date ranges to selections. All other choice boxes are set dynamically.
        if(reportContexts != null) instrumentBox.getItems().addAll(ContextFilteringUtils.getUniqueInstruments(reportContexts));
        dateRangeBox.getItems().addAll(DateRangeTypes.getDateRangeNames());

        // Submit parameters for GUI render
        if(validReportSelection()) submitButtonClick();

        // Refresh application every ten minutes
        Timeline timeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(600), event -> {

            this.refresh = true;
            if(validReportSelection()) submitButtonClick();

            // Force garbage collection to minimize memory usage.
            System.gc();
        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Default values for all GUI selection boxes
     */
    public void setApplicationDefaults() {

        // Line chart starts empty.
        lineChart.getData().clear();
        lineChart.setLegendVisible(true);
        yAxis.setLabel("Value");

        // All ChoiceBox and DatePicker objects cleared or set to null.
        instrumentBox.getItems().clear();
        instrumentBox.valueProperty().setValue(null);
        configurationBox.getItems().clear();
        configurationBox.valueProperty().setValue(null);
        matrixBox.getItems().clear();
        matrixBox.valueProperty().setValue(null);
        standardBox.getItems().clear();
        standardBox.valueProperty().setValue(null);
        reportBox.getItems().clear();
        reportBox.valueProperty().setValue(null);
        dateRangeBox.getItems().clear();
        dateRangeBox.valueProperty().setValue(null);
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        startDatePicker.setDisable(true);
        endDatePicker.setDisable(true);
        logNumberBox.getItems().clear();

        // All QC summary labels set to null
        nEntriesLabel.textProperty().setValue(null);
        nAnnotLabel.textProperty().setValue(null);
        firstEntryLabel.textProperty().setValue(null);
        lastEntryLabel.textProperty().setValue(null);
        avgFreqLabel.textProperty().setValue(null);
        seriesMeanLabel.textProperty().setValue(null);
        seriesMinLabel.textProperty().setValue(null);
        seriesMaxLabel.textProperty().setValue(null);
        seriesSDLabel.textProperty().setValue(null);
        seriesRsdLabel.textProperty().setValue(null);

        // Last database refresh
        lastRefreshLabel.textProperty().setValue("Last Refresh:");

        // By default, show legend, remove excluded points, and plot in linear scale with RSD variability guides.
        showLegend = true;
        showExcluded = false;
        showGuideSet = false;
        logScale = false;
        varType = VariabilityTypes.RSD.getLabel();

        // No log numbers monitored.
        logNumbers = null;

        // All annotation types are visible.
        annotationMap = new HashMap<>();
        for(AnnotationTypes type : AnnotationTypes.values()) annotationMap.put(type.toString(), true);

        // On reset, re-initialize values available to user.
        if(reportContexts != null) {

            instrumentBox.getItems().addAll(ContextFilteringUtils.getUniqueInstruments(reportContexts));
            dateRangeBox.getItems().addAll(DateRangeTypes.getDateRangeNames());
            dateRangeBox.valueProperty().setValue(DateRangeTypes.ALL.toString());
        }

    }

    /**
     * Submit all user selections for plot and table generation.
     */
    @FXML
    protected void submitButtonClick() {

        // If database not specified, show error message.
        if(databasePath == null) {

            showErrorMessage(ErrorTypes.DATABASE);
            return;
        }

        // Clear line chart and replace with new parameters.
        lineChart.getData().clear();

        // Current user selected parameters.
        Parameters selectParams = new Parameters(
                selectedContext,
                reportBox,
                dateRangeBox,
                logNumbers,
                leveyJenningsButton,
                movingRangeButton,
                cusummButton,
                cusumvButton,
                startDatePicker,
                endDatePicker,
                varType,
                annotationMap,
                logScale,
                showExcluded,
                showGuideSet,
                databasePath);

        // If user selected to visualize a new report, reprocess task. Else, update parameters in existing task.
        if(mainParameters.diffReportSelection(selectParams) || refresh) {

            mainParameters = selectParams;
            mainTask = new QuickQCTask(mainParameters);
            updateLastRefresh();
            refresh = false;

        } else {

            mainParameters = selectParams;
            mainTask.updateParams(mainParameters);
        }

        // Launch main task processing.
        mainTask.run();

        // Check if main task working entries list is empty.
        if(mainTask.getWorkingEntrySize() == 0) {

            showErrorMessage(ErrorTypes.RANGE);
            return;
        }

        // Get plot data from task and add to line chart.
        ObservableList<XYChart.Series<String, Number>> plotData = mainTask.getPlotData();
        lineChart.getData().addAll(plotData);

        /*
        Remove symbol visibility of variability guides from plot
        Series at index zero is main series.
        TODO - Find better way to handle this.
         */
        for(int i = 1; i < plotData.size(); i++) {

            XYChart.Series<String, Number> series = plotData.get(i);
            ObservableList<XYChart.Data<String, Number>> seriesData = series.getData();

            for(XYChart.Data<String, Number> data : seriesData) data.getNode().setVisible(false);
        }

        // Show or hide legend.
        lineChart.setLegendVisible(showLegend);

        // Handle if data entry from series is clicked on the plot. Only for main series.
        XYChart.Series<String, Number> mainSeries = plotData.get(0);
        ObservableList<XYChart.Data<String, Number>> seriesData = mainSeries.getData();
        for(XYChart.Data<String, Number> data : seriesData) data.getNode().setOnMouseClicked(e -> showSampleInfo(data));

        // Get node of main series.
        // TODO - Pull by main series name?
        Set<Node> node = lineChart.lookupAll(".default-color0.chart-line-symbol.series0.");
        ArrayList<Node> nodes = new ArrayList<>(node);

        // Handle annotations and excluded points in main task working entry list.
        // TODO - Does this need be handled here?
        for(int i = 0; i < nodes.size(); i++) {

            // Is entry an annotation-like entry.
            if(mainTask.isAnnotation(i)) {

                // Set color and point style.
                // TODO - Change how styling is handled.
                String type = AnnotationTypes.getAnnotationType(mainTask.getAnnotationType(i));
                nodes.get(i).setStyle(AnnotationStyles.valueOf(type).toString());

            } else {


                // Is entry marked excluded and is showable.
                // TODO - Find better way to handle all of this.
                if(mainTask.isExcluded(i)) {

                    // If entry has comment, make point square with red boundary.
                    if(mainTask.hasComment(i)) {

                        nodes.get(i).setStyle("""
                                -fx-background-color: red, black;
                                    -fx-background-insets: 0, 2;
                                    -fx-background-radius: 0;
                                    -fx-padding: 5px;""");

                    } else {

                        // Point is circle with red boundary.
                        nodes.get(i).setStyle("""
                                -fx-background-color: red, black;
                                    -fx-background-insets: 0, 2;
                                    -fx-background-radius: 5px;
                                    -fx-padding: 5px;""");
                    }

                } else {

                    // If entry has comment, make point black square.
                    if(mainTask.hasComment(i)) {

                        nodes.get(i).setStyle("""
                                -fx-background-color: black, black;
                                    -fx-background-insets: 0, 2;
                                    -fx-background-radius: 0;
                                    -fx-padding: 5px;""");

                    } else {

                        // Make point black circle.
                        nodes.get(i).setStyle("""
                                -fx-background-color: black, black;
                                    -fx-background-insets: 0, 2;
                                    -fx-background-radius: 5px;
                                    -fx-padding: 5px;""");
                    }
                }

                if(mainTask.isGuideSet(i) && showGuideSet) {

                    nodes.get(i).setStyle("""
                                -fx-background-color: blue, blue;
                                    -fx-background-insets: 0, 2;
                                    -fx-background-radius: 8px;
                                    -fx-padding: 8px;""");
                }
            }
        }

        // Clear QC data table
        valueTable.getColumns().clear();
        valueTable.getItems().clear();

        // Add QC data table columns and data.
        valueTable.getColumns().addAll(mainTask.makeTable());
        valueTable.getItems().addAll(mainTask.getTableData());

        // Clear annotation table.
        annotationTable.getColumns().clear();
        annotationTable.getItems().clear();

        // Add annotation table columns and data.
        annotationTable.getColumns().addAll(mainTask.makeAnnotationTable());
        if(mainTask.getWorkingAnnotationSize() != 0) annotationTable.getItems().addAll(mainTask.getAnnotationData());

        // Update QC Summary Area
        nEntriesLabel.textProperty().setValue(String.valueOf(mainTask.getWorkingEntrySize()));
        nAnnotLabel.textProperty().setValue(String.valueOf(mainTask.getWorkingAnnotationSize()));
        firstEntryLabel.textProperty().setValue(mainTask.getFirstEntryDate());
        lastEntryLabel.textProperty().setValue(mainTask.getLastEntryDate());
        avgFreqLabel.textProperty().setValue(mainTask.getEntryFrequency());
        seriesMeanLabel.textProperty().setValue(mainTask.getSeriesMeanString());
        seriesMinLabel.textProperty().setValue(mainTask.getSeriesMin());
        seriesMaxLabel.textProperty().setValue(mainTask.getSeriesMax());
        seriesSDLabel.textProperty().setValue(mainTask.getSeriesSD());
        seriesRsdLabel.textProperty().setValue(mainTask.getSeriesRsd());

        // Update log number options
        String entry = logNumberBox.getEditor().getText();
        logNumberBox.getItems().clear();
        logNumberBox.getItems().addAll(mainTask.getSeriesLogNumbers());
        logNumberBox.setValue(entry);

        // Take all current context selections and add to user preferences.
        setPreferences();
    }

    /**
     * Listener method for changes in instrument box selection.
     * Dynamically updates configuration selections based on instrument selection.
     */
    @FXML
    protected void instrumentBoxListener() {

        // Nothing set, ignore.
        if(instrumentBox.getSelectionModel().isEmpty()) return;

        // Get selected item
        String instrument = instrumentBox.getSelectionModel().getSelectedItem();

        // Clear existing selections
        configurationBox.getItems().clear();
        configurationBox.valueProperty().setValue(null);
        matrixBox.getItems().clear();
        matrixBox.valueProperty().setValue(null);
        standardBox.getItems().clear();
        standardBox.valueProperty().setValue(null);
        reportBox.getItems().clear();
        reportBox.valueProperty().setValue(null);

        // Set new selections
        if(reportContexts != null) configurationBox.getItems().addAll(ContextFilteringUtils.getUniqueConfigurations(reportContexts, instrument));
    }

    /**
     * Listener method for changes in the configuration choice box.
     * Dynamically updates matrix selections based on configuration selection.
     */
    @FXML
    protected void configBoxListener() {

        // Nothing set, ignore.
        if(configurationBox.getSelectionModel().isEmpty()) return;

        // Get selected item
        String instrument = instrumentBox.getSelectionModel().getSelectedItem();
        String config = configurationBox.getSelectionModel().getSelectedItem();

        // Clear existing selections
        matrixBox.getItems().clear();
        matrixBox.valueProperty().setValue(null);
        reportBox.getItems().clear();
        reportBox.valueProperty().setValue(null);

        // Set new selections
        if(reportContexts != null) matrixBox.getItems().addAll(ContextFilteringUtils.getUniqueMatrices(reportContexts, instrument, config));
    }


    /**
     * Listener method for changes in the matrix choice box.
     * Dynamically updates standard selections based on matrix selection.
     */
    @FXML
    protected void matrixBoxListener() {

        // Nothing set, ignore.
        if(matrixBox.getSelectionModel().isEmpty()) return;

        // Get Selected item
        String instrument = instrumentBox.getSelectionModel().getSelectedItem();
        String config = configurationBox.getSelectionModel().getSelectedItem();
        String matrix = matrixBox.getSelectionModel().getSelectedItem();

        // Clear existing selections
        standardBox.getItems().clear();
        standardBox.valueProperty().setValue(null);
        reportBox.getItems().clear();
        reportBox.valueProperty().setValue(null);

        // Set new selections
        if(reportContexts != null) {
            standardBox.getItems().addAll(ContextFilteringUtils.getUniqueStandards(reportContexts, instrument, config, matrix));
        }
    }

    /**
     * Listener method for changes in the standard choice box.
     * Dynamically updates report selections based on matrix selection.
     */
    @FXML
    protected void standardBoxListener() {

        // Nothing set, ignore.
        if(standardBox.getSelectionModel().isEmpty()) return;

        // Get Selected item
        String instrument = instrumentBox.getSelectionModel().getSelectedItem();
        String config = configurationBox.getSelectionModel().getSelectedItem();
        String matrix = matrixBox.getSelectionModel().getSelectedItem();
        String standard = standardBox.getSelectionModel().getSelectedItem();

        // Clear existing selections, but don't set to null for easy switching of standard-report
        if(reportContexts != null) {

            selectedContext = ContextFilteringUtils.getSelectedReportContext(reportContexts, instrument, config, matrix, standard);
            List<String> items = selectedContext.variables();
            List<String> setItems = reportBox.getItems();

            if(!setItems.equals(items)) {

                reportBox.getItems().clear();
                reportBox.valueProperty().setValue(null);

                reportBox.getItems().addAll(items);
            }

            if(validReportSelection()) submitButtonClick();
        }
    }

    /**
     * Submit user selections if date range, standard, and report properly set.
     */
    @FXML
    protected void reportBoxListener() {

        if(validReportSelection()) submitButtonClick();
    }

    /**
     * Listener method for changes in date range choice box.
     * Dynamically updates DatePicker box selections based on date range selection.
     * Enables DatePicker boxes if "Custom Date Range" selected.
     */
    @FXML
    protected void dataRangeListener() {

        // Nothing set, ignore.
        if(dateRangeBox.getSelectionModel().isEmpty()) return;

        String dateRange = dateRangeBox.getSelectionModel().getSelectedItem();

        if(dateRange.equals("All Dates")) {

            // Remove all dates in DatePickers and disable them.
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);

            startDatePicker.setDisable(true);
            endDatePicker.setDisable(true);

        } else if (dateRange.equals("Custom Date Range")) {

            // Reset Date Pickers and enable them.
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);

            startDatePicker.setDisable(false);
            endDatePicker.setDisable(false);

        } else {

            // Replace Date Picker values with appropriate start and end dates, then enable them.
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(DateRangeTypes.getDateRange(dateRange));

            startDatePicker.setValue(startDate);
            endDatePicker.setValue(endDate);

            startDatePicker.setDisable(true);
            endDatePicker.setDisable(true);
        }

        if(validReportSelection()) submitButtonClick();
    }

    /**
     * Listener method for changes in date picker boxes.
     */
    @FXML
    protected void datePickerListener(){

        // Nothing set, ignore.
        if(dateRangeBox.getSelectionModel().isEmpty()) return;

        String dateRange = dateRangeBox.getSelectionModel().getSelectedItem();

        if(!dateRange.equals("Custom Date Range")) return;

        // Handle selections in DatePickers
        if(startDatePicker.getValue() != null && endDatePicker.getValue() != null){

            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            // Invalid start date.
            if(startDate.isAfter(endDate)){

                // Highlight DatePicker in red and disable submit button.
                startDatePicker.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(0), Insets.EMPTY)));

            } else {

                startDatePicker.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(0), Insets.EMPTY)));
            }
        }

        if(validReportSelection()) submitButtonClick();
    }

    /**
     * Listener method for changes in the plot type radio buttons.
     */
    @FXML
    protected void plotTypeListener() {

        if(validReportSelection()) submitButtonClick();
    }

    /**
     * Listener method for key entries in log number combo box.
     *
     * @param keyEvent If enter key pressed, handle input.
     */
    @FXML
    protected void logNumberBoxKeyListener(KeyEvent keyEvent) {

        // User input enter key
        if(keyEvent.getCode().equals(KeyCode.ENTER)) {

            String input = logNumberBox.getEditor().getText().trim();

            if(input.isEmpty()) {

                this.logNumbers = null;
                submitButtonClick();

            } else {

                logNumberBox.setValue(input);
                logNumberBox.getEditor().setText(input);

                // Split input string and trim whitespace.
                String[] logNumbers = input.split(",");
                Arrays.stream(logNumbers).map(String::trim).toArray(unused -> logNumbers);

                // Check if each log number is 5-digit number
                for(String log : logNumbers) {

                    if(!log.matches("\\d{5}")) {

                        showErrorMessage(ErrorTypes.LOG);
                        return;
                    }
                }

                this.logNumbers = Arrays.asList(logNumbers);

                if(validReportSelection()) submitButtonClick();
            }
        }
    }

    /**
     * Update the last refresh label to show current time.
     */
    @FXML
    protected void updateLastRefresh() {

        Date now = new Date();
        String label = "Last Refresh: " + now;
        this.lastRefreshLabel.textProperty().setValue(label);
    }

    /**
     * Handle primary mouse click on main series data point on the line chart.
     * Launch pop-up window with sample information for selected entry.
     *
     * @param data User selected data entry
     */
    protected void showSampleInfo(XYChart.Data<String, Number> data) {

        DataEntry selectedEntry = this.mainTask.getDataEntry(data);

        if(selectedEntry == null) return;

        if(selectedEntry.isAnnotation()) {

            // Bring up edit annotation page for selected annotation.
            editAnnotation(this.mainTask.getDataEntryAnnotation(selectedEntry));

        } else {

            // Open pop-up window with data entry point information.
            try {

                // Get the window design for pop-up.
                FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("SamplePage.fxml"));
                Parent root = fxmlLoader.load();

                // Set sample page controller class.
                SamplePageController controller = fxmlLoader.<SamplePageController>getController();
                controller.setDataEntry(selectedEntry);

                // Launch pop-up window.
                Stage stage = new Stage();
                stage.setTitle("QC Information");
                stage.setResizable(false);
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);

                // Handle all user designated changes after the window is closed.
                stage.showAndWait();

                // Primary user change - point marked from inclusion/exclusion from the series.
                if (controller.getChangedInclusion()) {

                    // Update entry in main task series. Update change in master database. Update the plot.
                    this.mainTask.setDataEntryInclusion(selectedEntry);
                    this.mainTask.writeReport();
                    submitButtonClick();
                }

                // Primary user change - point marked from inclusion/exclusion from the series.
                if (controller.getChangedGuide()) {

                    // Update entry in main task series. Update change in master database. Update the plot.
                    this.mainTask.setDataEntryGuide(selectedEntry);
                    this.mainTask.writeReport();
                    submitButtonClick();
                }

                // Secondary user change - user comment on QC point
                if (controller.changedComment()) {

                    // Update comment in main task series. Update master database. Update the plot.
                    this.mainTask.setDataEntryComment(selectedEntry, controller.getComment());
                    this.mainTask.writeReport();
                    submitButtonClick();
                }

            } catch (Exception e) {

                // TODO - better error handling.
                e.printStackTrace();
            }
        }
    }

    /**
     * Handle application set-up menu.
     * Currently only used to designate directory of QC databases.
     */
    @FXML
    protected void menuSetUpListener() {

        try {

            // Get window design for Set-up page.
            FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("SetUpPage.fxml"));
            Parent root = fxmlLoader.load();

            // Update controller class with current database location.
            SetUpPageController controller = fxmlLoader.<SetUpPageController>getController();
            controller.setDatabaseFolder(this.databasePath);

            // Launch pop-up window.
            Stage stage = new Stage();
            stage.setTitle("AutoQC Set Up...");
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

            // Update database location and set application defaults.
            this.databasePath = controller.getDatabaseFolder();

            if(!Files.exists(this.databasePath)) {

                showErrorMessage(ErrorTypes.DATABASE);

            } else {

                this.reportConfigPath = this.databasePath.resolve(DatabaseTypes.REPORT.getFileName());
                this.reportContexts = readReportConfigs();
                setApplicationDefaults();
            }
        } catch(Exception e) {

            // TODO - better error handling.
            e.printStackTrace();
        }
    }

    /**
     * Reset all user selections to default.
     */
    @FXML
    protected void menuEditDefaultListener() {

        setApplicationDefaults();
    }

    /**
     * Refresh application to update QC database
     */
    @FXML
    protected void menuEditRefreshListener() {

        this.refresh = true;
        submitButtonClick();
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

    /**
     * Set application preference setting with current user selections.
     *
     * @see #submitButtonClick()
     */
    public void setPreferences() {

        // This will define a node in which the preferences can be stored.
        prefs = Preferences.userRoot().node(this.getClass().getName());

        // To simply preferences, don't allow Custom date range to be a preference.
        String date = (this.mainParameters.dateRange.equals(DateRangeTypes.CUSTOM.toString())) ?
                DateRangeTypes.ALL.toString() : this.mainParameters.dateRange;

        // Assign preferences.
        prefs.put(ReportTypes.DATABASE.name(), this.mainParameters.databasePath.toString());
        prefs.put(ReportTypes.INSTRUMENT.name(), this.mainParameters.instrument);
        prefs.put(ReportTypes.CONFIGURATION.name(), this.mainParameters.configuration);
        prefs.put(ReportTypes.MATRIX.name(), this.mainParameters.matrix);
        prefs.put(ReportTypes.STANDARD.name(), this.mainParameters.standard);
        prefs.put(ReportTypes.REPORT.name(), this.mainParameters.report);
        prefs.put(ReportTypes.RANGE.name(), date);
        prefs.put(ReportTypes.GUIDE.name(), this.mainParameters.showGuide.toString());
        prefs.putBoolean(ReportTypes.EXCLUDED.name(), this.mainParameters.showExcluded);
    }

    /**
     * Retrieve user application preferences.
     *
     * @see #setPreferences()
     * @see #submitButtonClick()
     */
    @FXML
    protected void getPreferences() {

        // This will retrieve the node where the user preferences are stored.
        prefs = Preferences.userRoot().node(this.getClass().getName());

        // Get preference, return default value if none found.
        String databasePath = prefs.get(ReportTypes.DATABASE.name(), null);
        String instrument = prefs.get(ReportTypes.INSTRUMENT.name(), null);
        String config = prefs.get(ReportTypes.CONFIGURATION.name(), null);
        String matrix = prefs.get(ReportTypes.MATRIX.name(), null);
        String standard = prefs.get(ReportTypes.STANDARD.name(), null);
        String report = prefs.get(ReportTypes.REPORT.name(), null);
        String dateRange = prefs.get(ReportTypes.RANGE.name(), null);
        showExcluded = prefs.getBoolean(ReportTypes.EXCLUDED.name(), true);
        showGuideSet = prefs.getBoolean(ReportTypes.GUIDE.name(), false);

        // Update database path.
        if(databasePath != null) {

            this.databasePath = Paths.get(databasePath);

            // Preference database location no longer exists.
            if(!Files.exists(this.databasePath)) {

                showErrorMessage(ErrorTypes.DATABASE);
                return;
            }

            // Format report configs database
            this.reportConfigPath = this.databasePath.resolve(DatabaseTypes.REPORT.getFileName());
            this.reportContexts = readReportConfigs();
        }

        // Handle choice box options based on instrument setting.
        if(instrument != null && config != null && matrix != null && standard != null && report != null && reportContexts != null) {

            instrumentBox.valueProperty().setValue(instrument);
            configurationBox.getItems().clear();
            configurationBox.getItems().addAll(ContextFilteringUtils.getUniqueConfigurations(reportContexts, instrument));
            configurationBox.valueProperty().setValue(config);
            matrixBox.getItems().clear();
            matrixBox.getItems().addAll(ContextFilteringUtils.getUniqueMatrices(reportContexts, instrument, config));
            matrixBox.valueProperty().setValue(matrix);
            standardBox.getItems().clear();
            standardBox.getItems().addAll(ContextFilteringUtils.getUniqueStandards(reportContexts, instrument, config, matrix));
            standardBox.valueProperty().setValue(standard);
            reportBox.getItems().clear();
            reportBox.getItems().addAll(ContextFilteringUtils.getSelectedReportContext(reportContexts, instrument, config, matrix, standard).variables());
            reportBox.valueProperty().setValue(report);
        }

        // Set date range.
        //TODO - Merge this with dateRangeListener function
        if(dateRange != null) {

            dateRangeBox.setValue(dateRange);

            if(dateRange.equals(DateRangeTypes.ALL.toString())) {

                startDatePicker.setValue(null);
                endDatePicker.setValue(null);

                startDatePicker.setDisable(true);
                endDatePicker.setDisable(true);

            } else if (dateRange.equals(DateRangeTypes.CUSTOM.toString())) {

                startDatePicker.setValue(null);
                endDatePicker.setValue(null);

                startDatePicker.setDisable(false);
                endDatePicker.setDisable(false);

            } else {

                LocalDate endDate = LocalDate.now();
                LocalDate startDate = endDate.minusDays(DateRangeTypes.getDateRange(dateRange));

                startDatePicker.setDisable(true);
                endDatePicker.setDisable(true);

                startDatePicker.setValue(startDate);
                endDatePicker.setValue(endDate);
            }
        }
    }

    /**
     * Handle context menu request for line chart.
     * Not to be confused with #showSampleInfo() event handling.
     * TODO - This is being handled wrong -  class input event is not being used. User has to right click twice.
     *
     * @param event Mouse event not currently used.
     */
    @FXML
    protected void plotClickListener(MouseEvent event) {

        //Make LineChart context menu settings
        ContextMenu contextMenu = new ContextMenu();

        // Normal Menu Items and action handling.
        // TODO - implement missing action requests.
        MenuItem meanMenu = new MenuItem("Set Series Mean...");
        meanMenu.setDisable(true);

        MenuItem copyPlot = new MenuItem("Copy Plot");
        copyPlot.setOnAction((ActionEvent e) -> PlotUtils.copyChartToClipboard(lineChart));

        MenuItem copyData = new MenuItem("Copy Data");
        copyData.setOnAction((ActionEvent e) -> PlotUtils.copyChartDataToClipboard(lineChart));

        MenuItem saveImage = new MenuItem("Save Image As...");
        saveImage.setDisable(true);

        // Plot variability lines as Radio Buttons, default RSD selected
        // TODO - custom selection handling.
        // TODO - change options into Enum? Loop toggle button creation?
        ToggleGroup varToggle = new ToggleGroup();

        RadioButton vg1 = new RadioButton("Standard Deviations");
        vg1.setToggleGroup(varToggle);
        vg1.setSelected(varType.equals(vg1.getText()));
        vg1.setOnAction((ActionEvent e) -> {this.varType = vg1.getText(); submitButtonClick();});
        vg1.setStyle("-fx-text-fill: -fx-text-base-color");
        CustomMenuItem var1 = new CustomMenuItem(vg1);
        var1.setHideOnClick(false);

        RadioButton vg2 = new RadioButton("Relative Std. Dev. (RSD)");
        vg2.setToggleGroup(varToggle);
        vg2.setSelected(varType.equals(vg2.getText()));
        vg2.setOnAction((ActionEvent e) -> {this.varType = vg2.getText(); submitButtonClick();});
        vg2.setStyle("-fx-text-fill: -fx-text-base-color");
        CustomMenuItem var2 = new CustomMenuItem(vg2);
        var2.setHideOnClick(false);

        RadioButton vg3 = new RadioButton("Custom...");
        vg3.setToggleGroup(varToggle);
        vg3.setDisable(true);
        vg3.setStyle("-fx-text-fill: -fx-text-base-color");
        CustomMenuItem var3 = new CustomMenuItem(vg3);
        var3.setHideOnClick(false);

        Menu varGuides = new Menu("Variability Guides");
        varGuides.getItems().addAll(var1, var2, var3);

        // Plot annotation menu as CheckBox Menu, default all selected.
        // TODO - change annotation types into Enum. Loop CheckBox creation.
        CheckBox am1 = new CheckBox("New Stock Solution");
        am1.setSelected(annotationMap.get(am1.getText()));
        am1.setOnAction((ActionEvent e) -> {annotationMap.put(am1.getText(), am1.isSelected()); submitButtonClick();});
        am1.setStyle("-fx-text-fill: -fx-text-base-color; selected-box-color: #1B9E77");
        CustomMenuItem ann1 = new CustomMenuItem(am1);
        ann1.setHideOnClick(false);

        CheckBox am2 = new CheckBox("Column Change");
        am2.setSelected(annotationMap.get(am2.getText()));
        am2.setOnAction((ActionEvent e) -> {annotationMap.put(am2.getText(), am2.isSelected()); submitButtonClick();});
        am2.setStyle("-fx-text-fill: -fx-text-base-color; selected-box-color: #D95F02");
        CustomMenuItem ann2 = new CustomMenuItem(am2);
        ann2.setHideOnClick(false);

        CheckBox am3 = new CheckBox("Mobile Phase Change");
        am3.setSelected(annotationMap.get(am3.getText()));
        am3.setOnAction((ActionEvent e) -> {annotationMap.put(am3.getText(), am3.isSelected()); submitButtonClick();});
        am3.setStyle("-fx-text-fill: -fx-text-base-color; selected-box-color: #A6CAEC");
        CustomMenuItem ann3 = new CustomMenuItem(am3);
        ann3.setHideOnClick(false);

        CheckBox am4 = new CheckBox("New Tune / Calibration");
        am4.setSelected(annotationMap.get(am4.getText()));
        am4.setOnAction((ActionEvent e) -> {annotationMap.put(am4.getText(), am4.isSelected()); submitButtonClick();});
        am4.setStyle("-fx-text-fill: -fx-text-base-color; selected-box-color: #E7298A");
        CustomMenuItem ann4 = new CustomMenuItem(am4);
        ann4.setHideOnClick(false);

        CheckBox am5 = new CheckBox("New Part / Consumable");
        am5.setSelected(annotationMap.get(am5.getText()));
        am5.setOnAction((ActionEvent e) -> {annotationMap.put(am5.getText(), am5.isSelected()); submitButtonClick();});
        am5.setStyle("-fx-text-fill: -fx-text-base-color; selected-box-color: #97EB75");
        CustomMenuItem ann5 = new CustomMenuItem(am5);
        ann5.setHideOnClick(false);

        CheckBox am6 = new CheckBox("Instrument Maintenance");
        am6.setSelected(annotationMap.get(am6.getText()));
        am6.setOnAction((ActionEvent e) -> {annotationMap.put(am6.getText(), am6.isSelected()); submitButtonClick();});
        am6.setStyle("-fx-text-fill: -fx-text-base-color; selected-box-color: #C8A2C8");
        CustomMenuItem ann6 = new CustomMenuItem(am6);
        ann6.setHideOnClick(false);

        CheckBox am7 = new CheckBox("Other");
        am7.setSelected(annotationMap.get(am6.getText()));
        am7.setOnAction((ActionEvent e) -> {annotationMap.put(am7.getText(), am7.isSelected()); submitButtonClick();});
        am7.setStyle("-fx-text-fill: -fx-text-base-color; selected-box-color: #E6AB02");
        CustomMenuItem ann7 = new CustomMenuItem(am7);
        ann7.setHideOnClick(false);

        Menu annotMenu = new Menu("Show Annotations");
        annotMenu.getItems().addAll(ann1, ann2, ann3, ann4, ann5, ann6, ann7);

        // Other check box items.
        // TODO - convert all context menu strings into Enum?
        CheckBox lg2 = new CheckBox("Log2 Values");
        lg2.setSelected(logScale);
        lg2.setOnAction((ActionEvent e) -> {logScale = lg2.isSelected(); submitButtonClick();});
        lg2.setStyle("-fx-text-fill: -fx-text-base-color");
        CustomMenuItem logMenu = new CustomMenuItem(lg2);
        logMenu.setHideOnClick(false);

        CheckBox slg = new CheckBox("Show Legend");
        slg.setSelected(showLegend);
        slg.setOnAction((ActionEvent e) -> {showLegend = slg.isSelected(); submitButtonClick();});
        slg.setStyle("-fx-text-fill: -fx-text-base-color");
        CustomMenuItem legendMenu = new CustomMenuItem(slg);
        legendMenu.setHideOnClick(false);

        CheckBox sgd = new CheckBox("Show Guide");
        sgd.setSelected(showGuideSet);
        sgd.setOnAction((ActionEvent e) -> {showGuideSet = sgd.isSelected(); submitButtonClick();});
        sgd.setStyle("-fx-text-fill: -fx-text-base-color");
        CustomMenuItem guideMenu = new CustomMenuItem(sgd);
        guideMenu.setHideOnClick(false);

        CheckBox sxp = new CheckBox("Show Excluded Points");
        sxp.setSelected(showExcluded);
        sxp.setOnAction((ActionEvent e) -> {this.showExcluded = sxp.isSelected(); submitButtonClick();});
        sxp.setStyle("-fx-text-fill: -fx-text-base-color");
        CustomMenuItem excludedMenu = new CustomMenuItem(sxp);
        excludedMenu.setHideOnClick(false);

        // Make complete context menu.
        contextMenu.getItems().add(meanMenu);
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(varGuides, annotMenu, logMenu, legendMenu, guideMenu, excludedMenu);
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(copyPlot, copyData, saveImage);

        // This isn't doing anything based on my testing.
        contextMenu.setAutoHide(true);

        // Handle LineChart Mouse Click
        lineChart.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                            varGuides.hide();
                            annotMenu.hide();
                            contextMenu.show(lineChart, mouseEvent.getScreenX(), mouseEvent.getScreenY());

                        }
                        if (mouseEvent.getButton() == MouseButton.PRIMARY & mouseEvent.getClickCount() >= 1) {
                            contextMenu.hide();
                        }

                    }
                });
    }

    /**
     * Handle context menu request for annotation table.
     * TODO - export annotation table able not implemented.
     */
    @FXML
    protected void annotationClickListener(MouseEvent event) {

        // Selected row index.
        int index = annotationTable.getSelectionModel().selectedIndexProperty().get();

        // Right click request for context menu.
        if(event.getButton() == MouseButton.SECONDARY) {

            ContextMenu contextMenu = new ContextMenu();
            SeparatorMenuItem sep1 = new SeparatorMenuItem();
            SeparatorMenuItem sep2 = new SeparatorMenuItem();

            // Context menu options.
            // TODO - change to Enum?
            MenuItem menuItem1 = new MenuItem("Export Annotation Table");
            MenuItem menuItem2 = new MenuItem("Add Annotation");
            MenuItem menuItem3 = new MenuItem("Edit Annotation");
            MenuItem menuItem4 = new MenuItem("Delete Annotation");

            // Get selected annotation. If no row selected, returns -1 -> set to null.
            Annotation selectedAnnotation = (index < 0) ? null : mainTask.getSelectedAnnotation(index);

            // If selected row or table is empty, disable edit and delete options.
            if(annotationTable.getSelectionModel().isEmpty()) {

                menuItem3.setDisable(true);
                menuItem4.setDisable(true);
            }

            // Design context menu appearance.
            contextMenu.getItems().add(menuItem1);
            contextMenu.getItems().add(sep1);
            contextMenu.getItems().addAll(menuItem2, menuItem3);
            contextMenu.getItems().add(sep2);
            contextMenu.getItems().add(menuItem4);

            // Handle context menu actions.
            // TODO - handle export action.
            menuItem1.setOnAction((ActionEvent e) -> System.out.println("Action 1"));
            menuItem2.setOnAction((ActionEvent e) -> addAnnotation());
            menuItem3.setOnAction((ActionEvent e) -> editAnnotation(selectedAnnotation));
            menuItem4.setOnAction((ActionEvent e) -> deleteAnnotation(selectedAnnotation));

            annotationTable.setContextMenu(contextMenu);
        }
    }

    /**
     * Handle request to add new annotation to QC plot context.
     * Create new annotation based on user input, add to mainTask working entries, and update master database.
     */
    @FXML
    private void addAnnotation() {

        try {

            // Get annotation page window design.
            FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("AnnotationPage.fxml"));
            Parent root = fxmlLoader.load();
            AnnotationPageController controller = fxmlLoader.getController();
            controller.setContext(reportContexts, selectedContext);

            // Launch pop-up window.
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Add Annotation");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // If page wasn't canceled, handle new annotation.
            if(!controller.wasCanceled()) {

                // Handle user input.
                mainTask.addAnnotation(controller.getAnnotation());
                mainTask.sortAnnotations();
                mainTask.writeAnnotationReport();

                // Update application.
                submitButtonClick();
            }

        } catch(Exception e) {

            // TODO - better error handling.
            e.printStackTrace();
        }
    }

    /**
     * Handle request to edit existing annotation.
     * Replaces existing values with new values and updates master annotation database.
     *
     * @param annotation selected annotation.
     */
    @FXML
    private void editAnnotation(Annotation annotation) {

        try {

            // Get annotation page window design.
            FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("AnnotationPage.fxml"));
            Parent root = fxmlLoader.load();

            // Initialize page with selected annotation.
            AnnotationPageController controller = fxmlLoader.getController();
            controller.setAnnotation(reportContexts, annotation);

            // Launch pop-up window.
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Edit Annotation");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if(!controller.wasCanceled()) {

                // Update annotation entry with new input and update master annotation database.
                mainTask.editAnnotation(annotation, controller.getAnnotation());
                mainTask.sortAnnotations();
                mainTask.writeAnnotationReport();

                // Refresh application.
                submitButtonClick();
            }

        } catch(Exception e) {

            // TODO - better error handling.
            e.printStackTrace();
        }
    }

    /**
     * Handle request to delete selected annotation.
     * Deletes annotation from series and updates master annotation database.
     *
     * @param annotation Selected annotation.
     */
    @FXML
    private void deleteAnnotation(Annotation annotation){

        // Delete annotation from annotation list and overwrite master database file.
        mainTask.deleteAnnotation(annotation);
        mainTask.writeAnnotationReport();

        // Refresh application.
        submitButtonClick();
    }

    /**
     *
     */
    private List<ReportContext> readReportConfigs() {

        List<ReportContext> contextList = new ArrayList<>();

        // Create an instance of BufferedReader
        try (BufferedReader br = Files.newBufferedReader(this.reportConfigPath, StandardCharsets.US_ASCII)) {

            String line = br.readLine();
            line = line.replace("\"", "");
            String[] header = line.split(",");

            line = br.readLine();

            // Loop through database line by line.
            while (line != null) {

                // TODO - evaluate a more efficient way to handle commas in comment field.
                // Parse strings.
                String[] attributes = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                for(int i = 0; i < attributes.length; i++) {

                    attributes[i] = attributes[i].replace("\"", "");
                }

                // Handle variable string in fifth column. Entries separated by semicolon.
                String[] variables = attributes[4].split(";");
                Arrays.stream(variables).map(String::trim).toArray(unused -> variables);
                List<String> varArray = Arrays.asList(variables);

                Path reportPath = this.databasePath.resolve(attributes[5]);

                // Make ReportContext Object
                contextList.add(new ReportContext(attributes[0], attributes[1], attributes[2], attributes[3], reportPath, varArray));

                line = br.readLine();
            }

        } catch (IOException ioe) {

            // TODO - Error handling
            ioe.printStackTrace();
        }

        return contextList;
    }

    /**
     * Check if all report context are properly set by user.
     *
     * @return true if all context set
     */
    private Boolean validReportSelection() {

        return (!reportBox.getSelectionModel().isEmpty() &&
                !standardBox.getSelectionModel().isEmpty() &&
                (!dateRangeBox.getSelectionModel().isEmpty() && !dateRangeBox.getSelectionModel().getSelectedItem().equals("Custom Date Range") ||
                        (startDatePicker.getValue() != null && endDatePicker.getValue() != null)));

    }
}