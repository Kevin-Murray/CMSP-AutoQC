
package cmsp.quickqc.visualizer.gui;

import cmsp.quickqc.visualizer.*;
import cmsp.quickqc.visualizer.datamodel.DataEntry;
import cmsp.quickqc.visualizer.parameters.*;
import cmsp.quickqc.visualizer.parameters.types.*;
import cmsp.quickqc.visualizer.utils.annotations.Annotation;
import cmsp.quickqc.visualizer.utils.annotations.AnnotationStyles;
import cmsp.quickqc.visualizer.utils.annotations.AnnotationTypes;
import cmsp.quickqc.visualizer.utils.plotUtils.*;
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
import javafx.scene.input.MouseButton;
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
import java.util.*;
import java.util.prefs.Preferences;

/**
 * Controller class for Main Page GUI of QuickQC
 */
public class MainPageController {

    @FXML public ChoiceBox<String> instrumentBox;
    @FXML public ChoiceBox<String> configurationBox;
    @FXML public ChoiceBox<String> matrixBox;
    @FXML public ChoiceBox<String> reportBox;
    @FXML public ChoiceBox<String> dateRangeBox;

    @FXML public Button submitButton;
    @FXML public Button resetButton;

    @FXML public RadioButton leveyJenningsButton;
    @FXML public RadioButton movingRangeButton;
    @FXML public RadioButton cusummButton;
    @FXML public RadioButton cusumvButton;

    @FXML public DatePicker startDatePicker;
    @FXML public DatePicker endDatePicker;

    @FXML public LineChart<String, Number> lineChart;
    @FXML public CategoryAxis xAxis;
    @FXML public NumberAxis yAxis;

    @FXML public TableView<Map<Integer, String>> valueTable;
    @FXML public TableView<Map<Integer, String>>  annotationTable;

    private Parameters mainParameters;
    private QuickQCTask mainTask;
    private Path databasePath;

    private Map<String, Boolean> annotationMap;
    private Boolean logScale;
    private Boolean showExcluded;
    private Boolean showLegend;
    public Boolean showGuideSet;
    private String varType;

    private Preferences prefs;

    private Boolean reset = false;

    /**
     * Initialize controller class to default values.
     * This how the application will appear when used for the first time or reset.
     */
    public void initialize() {

        mainParameters = new Parameters();
        mainTask = new QuickQCTask(mainParameters);

        // Line chart starts empty.
        lineChart.getData().clear();
        lineChart.setLegendVisible(true);
        yAxis.setLabel("Value"); // TODO - Report context specific yAxis label

        // All ChoiceBox and DatePicker objects cleared or set to null.
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

        // By default, show legend, remove excluded points, and plot in linear scale with RSD variability guides.
        showLegend = true;
        showExcluded = false;
        logScale = false;
        varType = VariabilityTypes.RSD.toString();

        // All annotation types are visible.
        annotationMap = new HashMap<>();
        for(AnnotationTypes type : AnnotationTypes.values()){
            annotationMap.put(type.toString(), true);
        }

        // If user has used application previously, get last used configuration. Otherwise, set to defaults.
        if(!reset){
            getPreferences();
        } else {
            dateRangeBox.setValue("All Dates");
            reset = false;
        }

        // Add instrument and date ranges to selections. All other choice boxes are set dynamically.
        instrumentBox.getItems().addAll(InstrumentTypes.getInstrumentNames());
        dateRangeBox.getItems().addAll(DateRangeType.getDateRangeNames());
    }

    /**
     * Submit all user selections for plot and table generation.
     */
    @FXML
    protected void submitButtonClick() {

        // If database not specified, show error message.
        // TODO - Needs better error handling
        if(databasePath == null){
            showMissingDatabaseDialog();
            return;
        }

        // Clear line chart and replace with new parameters.
        lineChart.getData().clear();

        // Current user selected parameters.
        Parameters selectParams = new Parameters(
                instrumentBox,
                configurationBox,
                matrixBox,
                reportBox,
                dateRangeBox,
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
        if(mainParameters.diffReportSelection(selectParams)) {

            mainParameters = selectParams;
            mainTask = new QuickQCTask(mainParameters);

        } else {

            mainParameters = selectParams;
            mainTask.updateParams(mainParameters);
        }

        // Launch main task processing.
        mainTask.run();

        // Check if main task working entries list is empty.
        // TODO - better error handling.
        if(mainTask.getWorkingEntrySize() == 0) {

            showNoDataInRangeDialog();
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

            for(XYChart.Data<String, Number> data : seriesData) {

                data.getNode().setVisible(false);
            }
        }

        // Show or hide legend.
        lineChart.setLegendVisible(showLegend);

        // Handle if data entry from series is clicked on the plot. Only for main series.
        XYChart.Series<String, Number> mainSeries = plotData.get(0);
        ObservableList<XYChart.Data<String, Number>> seriesData = mainSeries.getData();

        for(XYChart.Data<String, Number> data : seriesData) {

            data.getNode().setOnMouseClicked(e -> showSampleInfo(data));
        }

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

        // Take all current context selections and add to user preferences.
        setPreferences();
    }

    /**
     * Reset all user selections to default.
     */
    @FXML
    protected void setResetButton(){
        this.reset = true;
        initialize();
    }

    /**
     * Listener method for changes in instrument box selection.
     * Dynamically updates configuration and matrix choice boxes based on selection.
     * TODO - remove matrix component, make new configuration-specific handling.
     */
    @FXML
    protected void instrumentBoxListener() {

        // Nothing set, ignore.
        if(instrumentBox.getSelectionModel().isEmpty()) return;

        String instrument = instrumentBox.getSelectionModel().getSelectedItem();

        // Update configuration and matrix selections based on instrument selection.
        instrument = InstrumentTypes.getInstrument(instrument);
        String[] configuration = ConfigurationTypes.getConfiguration(instrument);
        String[] matrix = MatrixTypes.getMatrix(instrument);

        // Clear existing selections and set items.
        configurationBox.getItems().clear();
        matrixBox.getItems().clear();
        reportBox.getItems().clear();

        // Add new options
        // TODO - Error handling if they are null? I don't know how they could be.
        if(configuration != null) configurationBox.getItems().addAll(Arrays.asList(configuration));
        if(matrix != null) matrixBox.getItems().addAll(Arrays.asList(matrix));
    }

    /**
     * Listener method for changes in the matrix choice box.
     * Dynamically updates report selections based on matrix selection.
     */
    @FXML
    protected void matrixBoxListener() {

        // Nothing set, ignore.
        if(matrixBox.getSelectionModel().isEmpty()) return;

        // Create ReportObject based on matrix selection.
        // ReportObject is HashMap with report context selections.
        String matrix = matrixBox.getSelectionModel().getSelectedItem();
        ReportObject reports = new ReportObject(matrix);

        // Clear existing selections and set items.
        reportBox.getItems().clear();
        reportBox.getItems().addAll(Arrays.asList(reports.getReports()));
    }

    /**
     * Disable submit button if report context not specified.
     */
    @FXML
    protected void reportBoxListener() {

        submitButton.setDisable(reportBox.getSelectionModel().isEmpty());
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

            if(!reportBox.getSelectionModel().isEmpty()){
                submitButton.setDisable(false);
            }

            // Remove all dates in DatePickers and disable them.
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);

            startDatePicker.setDisable(true);
            endDatePicker.setDisable(true);

        } else if (dateRange.equals("Custom Date Range")) {

            // Disable submit button until date range specified.
            submitButton.setDisable(true);

            // Reset Date Pickers and enable them.
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);

            startDatePicker.setDisable(false);
            endDatePicker.setDisable(false);

        } else {

            if(!reportBox.getSelectionModel().isEmpty()){
                submitButton.setDisable(false);
            }

            // Replace Date Picker values with appropriate start and end dates, then enable them.
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(DateRangeType.getDateRange(dateRange));

            startDatePicker.setValue(startDate);
            endDatePicker.setValue(endDate);

            startDatePicker.setDisable(true);
            endDatePicker.setDisable(true);
        }
    }

    /**
     * Listener method for changes in date picker boxes.
     * TODO - need to handle custom date range interaction with default parameters.
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
                submitButton.setDisable(true);

            } else {

                startDatePicker.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(0), Insets.EMPTY)));

                if(!reportBox.getSelectionModel().isEmpty()){
                    submitButton.setDisable(false);
                }
            }
        }
    }

    /**
     * Handle primary mouse click on main series data point on line chart.
     * Launch pop-up window with sample information for selected entry.
     *
     * @param data User selected data entry
     */
    protected void showSampleInfo(XYChart.Data<String, Number> data){

        DataEntry selectedEntry = this.mainTask.getDataEntry(data);

        if(selectedEntry == null) return;

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
            if(controller.getChangedInclusion()) {

                // Update entry in main task series. Update change in master database. Update the plot.
                this.mainTask.setDataEntryInclusion(selectedEntry);
                this.mainTask.writeReport();
                submitButtonClick();
            }

            // Secondary user change - user comment on QC point
            if(controller.changedComment()) {

                // Update comment in main task series. Update master database. Update the plot.
                this.mainTask.setDataEntryComment(selectedEntry, controller.getComment());
                this.mainTask.writeReport();
                submitButtonClick();
            }

            // TODO - Guide set designation handling.

        } catch(Exception e) {

            // TODO - better error handling.
            e.printStackTrace();
        }
    }

    /**
     * Handle application set-up menu.
     * Currently only used to designate directory of QC databases.
     */
    @FXML
    protected void menuSetUpListener(){

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

            // Update database location.
            this.databasePath = controller.getDatabaseFolder();

        } catch(Exception e) {

            // TODO - better error handling.
            e.printStackTrace();
        }
    }

    /**
     * Handle empty database path.
     * User must specify database in set-up window.
     * TODO - only handles missing location. Needs to handle wrong location.
     *
     * @see #menuSetUpListener()
     */
    @FXML
    protected void showMissingDatabaseDialog() {

        try {

            // Get error page window design.
            FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("ErrorPage.fxml"));
            Parent root = fxmlLoader.load();

            // Initialize error window with message.
            ErrorPageController controller = fxmlLoader.<ErrorPageController>getController();
            controller.setErrorMessage("Database path not set properly.\nPlease set in - Files > Set Up...");

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
     * Handle empty series in user specified data range.
     * TODO - merge all error page handling into single method?
     *
     * @see #submitButtonClick()
     */
    @FXML
    protected void showNoDataInRangeDialog() {

        try {

            // Get error page design.
            FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("ErrorPage.fxml"));
            Parent root = fxmlLoader.load();

            // Initialize error page with message.
            ErrorPageController controller = fxmlLoader.<ErrorPageController>getController();
            controller.setErrorMessage("No QC entries in date range.\nPlease adjust the range and try again.");

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
     * TODO - expand preferences stored - date picker, annotation map, variability guides...
     *
     * @see #submitButtonClick()
     */
    public void setPreferences() {

        // This will define a node in which the preferences can be stored.
        prefs = Preferences.userRoot().node(this.getClass().getName());

        // TODO - specify preference IDs using Enum?
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

    /**
     * Retrieve user application preferences.
     * TODO - expand preferences stored - date picker, annotation map, variability guides...
     *
     * @see #setPreferences()
     * @see #submitButtonClick()
     */
    @FXML
    protected void getPreferences() {

        // This will retrieve the node where the user preferences are stored.
        prefs = Preferences.userRoot().node(this.getClass().getName());

        // TODO - specify preference IDs using Enum?
        String ID1 = "Database Path";
        String ID2 = "Instrument";
        String ID3 = "Configuration";
        String ID4 = "Matrix";
        String ID5 = "Report";
        String ID6 = "Date Range";
        String ID7 = "Show Excluded";

        // Get preference, return default value if none found.
        String databasePath = prefs.get(ID1, null);
        String instrument = prefs.get(ID2, null);
        String config = prefs.get(ID3, null);
        String matrix = prefs.get(ID4, null);
        String report = prefs.get(ID5, null);
        String dateRange = prefs.get(ID6, null);
        showExcluded = prefs.getBoolean(ID7, true);

        // Update database path.
        if(databasePath != null) {

            this.databasePath = Paths.get(databasePath);
        }

        // Handle choice box options based on instrument setting.
        // TODO - can I just call #instrumentBoxListener()?
        if(instrument != null) {

            instrumentBox.setValue(instrument);

            instrument = InstrumentTypes.getInstrument(instrument);

            String[] configurationOptions = ConfigurationTypes.getConfiguration(instrument);
            String[] matrixOptions = MatrixTypes.getMatrix(instrument);

            // Add new options
            // TODO - Error handling if they are null?
            if(configurationOptions != null) configurationBox.getItems().addAll(Arrays.asList(configurationOptions));
            if(matrixOptions != null) matrixBox.getItems().addAll(Arrays.asList(matrixOptions));
        }

        // Set user configuration selection.
        // TODO - configuration-specific matrix selections update planned.
        if(config != null) {

            configurationBox.setValue(config);
        }

        // Set matrix choice. Update options in matrix choice box.
        if(matrix != null) {

            matrixBox.setValue(matrix);
            matrixBoxListener();
        }

        // Set report choice. Update listener.
        // TODO - what does #reportBoxListener() method call do here? Redundant?
        if(report != null) {

            reportBox.setValue(report);
            reportBoxListener();
        }

        // Set date range.
        //TODO - Merge this with dateRangeListener function
        if(dateRange != null) {

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
                LocalDate startDate = endDate.minusDays(DateRangeType.getDateRange(dateRange));

                startDatePicker.setDisable(true);
                endDatePicker.setDisable(true);

                startDatePicker.setValue(null);
                endDatePicker.setValue(null);

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

        CheckBox am5 = new CheckBox("Instrument Maintenance");
        am5.setSelected(annotationMap.get(am5.getText()));
        am5.setOnAction((ActionEvent e) -> {annotationMap.put(am5.getText(), am5.isSelected()); submitButtonClick();});
        am5.setStyle("-fx-text-fill: -fx-text-base-color; selected-box-color: #C8A2C8");
        CustomMenuItem ann5 = new CustomMenuItem(am5);
        ann5.setHideOnClick(false);

        CheckBox am6 = new CheckBox("Other");
        am6.setSelected(annotationMap.get(am6.getText()));
        am6.setOnAction((ActionEvent e) -> {annotationMap.put(am6.getText(), am6.isSelected()); submitButtonClick();});
        am6.setStyle("-fx-text-fill: -fx-text-base-color; selected-box-color: #E6AB02");
        CustomMenuItem ann6 = new CustomMenuItem(am6);
        ann6.setHideOnClick(false);

        Menu annotMenu = new Menu("Show Annotations");
        annotMenu.getItems().addAll(ann1, ann2, ann3, ann4, ann5, ann6);

        // Other check box items.
        // TODO - convert all context menu strings into Enum?
        CheckBox lg2 = new CheckBox("Log2 Values");
        lg2.setSelected(false);
        lg2.setOnAction((ActionEvent e) -> {logScale = lg2.isSelected(); submitButtonClick();});
        lg2.setStyle("-fx-text-fill: -fx-text-base-color");
        CustomMenuItem logMenu = new CustomMenuItem(lg2);
        logMenu.setHideOnClick(false);

        CheckBox slg = new CheckBox("Show Legend");
        slg.setSelected(true);
        slg.setOnAction((ActionEvent e) -> {showLegend = slg.isSelected(); submitButtonClick();});
        slg.setStyle("-fx-text-fill: -fx-text-base-color");
        CustomMenuItem legendMenu = new CustomMenuItem(slg);
        legendMenu.setHideOnClick(false);

        CheckBox sgd = new CheckBox("Show Guide");
        sgd.setSelected(false);
        sgd.setDisable(true);
        sgd.setStyle("-fx-text-fill: -fx-text-base-color");
        CustomMenuItem guideMenu = new CustomMenuItem(sgd);
        guideMenu.setHideOnClick(false);
        guideMenu.setDisable(true);

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
                        if (mouseEvent.getButton() == MouseButton.PRIMARY & mouseEvent.getClickCount() >= 2) {
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

            // Launch pop-up window.
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Add Annotation");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Handle user input.
            mainTask.addAnnotation(controller.getAnnotation());
            mainTask.sortAnnotations();
            mainTask.writeAnnotationReport();

            // Update application.
            submitButtonClick();

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
            controller.setAnnotation(annotation);

            // Launch pop-up window.
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Edit Annotation");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Update annotation entry with new input and update master annotation database.
            mainTask.editAnnotation(annotation, controller.getAnnotation());
            mainTask.sortAnnotations();
            mainTask.writeAnnotationReport();

            // Refresh application.
            submitButtonClick();

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
}