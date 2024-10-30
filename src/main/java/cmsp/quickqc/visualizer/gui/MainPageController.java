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

    private Map<String, Boolean> annotationMap;
    private Boolean logScale;
    private Boolean showExcluded;
    private Boolean showLegend;
    private String varType;

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

        showLegend = true;
        showExcluded = false;
        logScale = false;
        varType = VariabilityTypes.RSD.toString();

        annotationMap = new HashMap<>();
        for(AnnotationTypes type : AnnotationTypes.values()){
            annotationMap.put(type.toString(), true);
        }

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

        Parameters selectParams = new Parameters(instrumentBox,
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
                annotationCheckBox,
                groupXAxisCheckBox,
                varType,
                annotationMap,
                logScale,
                showExcluded,
                showGuideSetCheckBox,
                databasePath);

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

        /*
        Remove symbol visibility of statistical series from plot
        TODO - Find better way to handle this.
         */
        for(int i = 1; i < plotData.size(); i++){
            XYChart.Series meanSeries = plotData.get(i);
            ObservableList<XYChart.Data> meanData = meanSeries.getData();
            for(XYChart.Data data : meanData) {
                data.getNode().setVisible(false);
            }
        }

        lineChart.setLegendVisible(showLegend);

        // Make main series clickable
        XYChart.Series mainSeries = plotData.get(0);
        ObservableList<XYChart.Data> seriesData = mainSeries.getData();
        for(XYChart.Data data : seriesData) {
            data.getNode().setOnMouseClicked(e -> showSampleInfo(data));
        }

        // TODO - Pull by main series name?
        Set<Node> node = lineChart.lookupAll(".default-color0.chart-line-symbol.series0.");
        ArrayList<Node> tmp = new ArrayList<>(node);

        for(int i = 0; i < tmp.size(); i++){

            if(mainTask.isAnnotation(i)){

                String type = AnnotationTypes.getAnnotationType(mainTask.getAnnotationType(i));
                tmp.get(i).setStyle(AnnotationStyles.valueOf(type).toString());

            } else {

                if(mainTask.isExcluded(i)){
                    if(mainTask.hasComment(i)){
                        tmp.get(i).setStyle("""
                                -fx-background-color: red, black;
                                    -fx-background-insets: 0, 2;
                                    -fx-background-radius: 0;
                                    -fx-padding: 5px;""");
                    } else {
                        tmp.get(i).setStyle("""
                                -fx-background-color: red, black;
                                    -fx-background-insets: 0, 2;
                                    -fx-background-radius: 5px;
                                    -fx-padding: 5px;""");
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

    /**
     * Disable submit button if report context not specified.
     */
    @FXML
    protected void reportBoxListener() {

        submitButton.setDisable(reportBox.getSelectionModel().isEmpty());
    }

    /**
     * Handles new selections to date range menu
     */
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
            LocalDate startDate = endDate.minusDays(DateRangeType.getDateRange(dateRange));

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
                LocalDate startDate = endDate.minusDays(DateRangeType.getDateRange(dateRange));

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
    protected void plotClickListener(MouseEvent event){

        // TODO - This is being handled wrong -  class input event is not being used. User has to right click twice.

        //Make LineChart context menu settings
        ContextMenu contextMenu = new ContextMenu();

        // Normal Menu Items
        MenuItem meanMenu = new MenuItem("Set Series Mean...");
        meanMenu.setDisable(true);

        MenuItem copyPlot = new MenuItem("Copy Plot");
        copyPlot.setOnAction((ActionEvent e) -> PlotUtils.copyChartToClipboard(lineChart));

        MenuItem copyData = new MenuItem("Copy Data");
        copyData.setOnAction((ActionEvent e) -> PlotUtils.copyChartDataToClipboard(lineChart));

        MenuItem saveImage = new MenuItem("Save Image As...");
        saveImage.setDisable(true);


        // Plot variability lines as Radio Buttons, default RSD selected
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


        // Plot annotation menu as CheckBox Menu, default all selected
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

        // Other check box items
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
        sxp.setSelected(false);
        sxp.setOnAction((ActionEvent e) -> {this.showExcluded = sxp.isSelected(); submitButtonClick();});
        sxp.setStyle("-fx-text-fill: -fx-text-base-color");
        CustomMenuItem excludedMenu = new CustomMenuItem(sxp);
        excludedMenu.setHideOnClick(false);


        // Make complete context menu
        contextMenu.getItems().add(meanMenu);
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(varGuides, annotMenu, logMenu, legendMenu, guideMenu, excludedMenu);
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(copyPlot, copyData, saveImage);

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

    @FXML
    protected void annotationClickListener(MouseEvent event){

        int index = annotationTable.getSelectionModel().selectedIndexProperty().get();

        if(event.getButton() == MouseButton.SECONDARY){

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


            menuItem1.setOnAction((ActionEvent e) -> System.out.println("Action 1"));
            menuItem2.setOnAction((ActionEvent e) -> addAnnotation());
            menuItem3.setOnAction((ActionEvent e) -> editAnnotation(selectedAnnotation));
            menuItem4.setOnAction((ActionEvent e) -> deleteAnnotation(selectedAnnotation));

            annotationTable.setContextMenu(contextMenu);
        }
    }

    @FXML
    private void addAnnotation(){

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("AnnotationPage.fxml"));
            Parent root = fxmlLoader.load();
            AnnotationPageController controller = fxmlLoader.getController();

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
            AnnotationPageController controller = fxmlLoader.getController();
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