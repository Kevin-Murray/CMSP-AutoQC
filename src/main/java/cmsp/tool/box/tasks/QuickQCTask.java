
package cmsp.tool.box.tasks;

import cmsp.tool.box.datamodel.QcAnnotation;
import cmsp.tool.box.datamodel.QcDataEntry;
import cmsp.tool.box.datamodel.QcParameters;
import cmsp.tool.box.utils.MathUtils;
import cmsp.tool.box.utils.ReportFilteringUtils;
import cmsp.tool.box.utils.PlotUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * QuickQC main processing class.
 * Handles user selections for QC plotting and generates associated line chart and tables of data.
 *
 */
public class QuickQCTask {

    // Currently using CSV databases
    private Path databasePath;
    private Path annotationPath;

    // User specified selections
    private QcParameters qcParameters;

    // Plotting data
    private List<QcDataEntry> globalEntries;
    private List<QcDataEntry> workingEntries;
    private List<QcDataEntry> mergedEntries;
    private List<QcAnnotation> qcAnnotationDatabase;
    private List<QcAnnotation> workingQcAnnotations;
    private ObservableList<XYChart.Series<String, Number>> chart;

    /**
     * QuickQC main processing method.
     *
     * @param qcParameters User input qcParameters class
     */
    public QuickQCTask(QcParameters qcParameters) {

        this.qcParameters = qcParameters;

        // If qcParameters valid, assign global variables
        if(qcParameters.validSelection()) {

            this.databasePath = this.qcParameters.getPath();
            this.annotationPath = this.qcParameters.getAnnotationPath();
            this.globalEntries = readReport();
            this.qcAnnotationDatabase = readAnnotation();
        }
    }

    /**
     * Main task run method.
     * Gets filtered data and annotations to make line chart and data tables.
     */
    public void run() {

        // Get appropriate QC data and filter based on user selections.
        this.workingEntries = getFilteredData();
        this.workingQcAnnotations = getFilteredAnnotation();

        // Merge annotations into QC data series.
        this.mergedEntries = mergePlotData();

        // Format QC data into line chart.
        makePlotData();
    }

    /**
     * Read QC database CSV file and format into list.
     *
     * @return List of data entries.
     */
    private List<QcDataEntry> readReport() {

        List<QcDataEntry> dataEntries = new ArrayList<>();

        // Create an instance of BufferedReader
        try (BufferedReader br = Files.newBufferedReader(this.databasePath, StandardCharsets.US_ASCII)) {

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

                // Make data entry object and add to list.
                QcDataEntry entry = new QcDataEntry(header, attributes, "Sample");
                dataEntries.add(entry);

                line = br.readLine();
            }

        } catch (IOException ioe) {

            // TODO - Error handling
            ioe.printStackTrace();
        }

        return dataEntries;
    }

    /**
     * Read annotation database entries into list.
     *
     * @return List of QcAnnotation objects.
     */
    private ObservableList<QcAnnotation> readAnnotation() {

        ObservableList<QcAnnotation> qcAnnotationList = FXCollections.observableArrayList();

        // Create an instance of BufferedReader.
        try (BufferedReader br = Files.newBufferedReader(this.annotationPath, StandardCharsets.US_ASCII)) {

            // Skip header line.
            br.readLine();

            String line = br.readLine();

            // Loop through annotation database.
            while (line != null) {

                // TODO - evaluate a more efficient way to handle commas in comment field.
                // Parse entry strings.
                String[] attributes = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                for(int i = 0; i < attributes.length; i++) {
                    attributes[i] = attributes[i].replace("\"", "");
                }

                // Create new qcAnnotation object and add to list.
                QcAnnotation qcAnnotation = new QcAnnotation(attributes[0], // Date string
                        attributes[1], // Instrument string
                        attributes[2], // Configuration string
                        attributes[3], // Matrix string
                        attributes[4], // Type string
                        attributes[5]); // Comment string
                qcAnnotationList.add(qcAnnotation);

                line = br.readLine();
            }

        } catch (IOException ioe) {

            // TODO - error handling.
            ioe.printStackTrace();
        }

        return qcAnnotationList;
    }

    /**
     * Write QC data back into database CSV file.
     * Primarily used to update exclusion and guide fields for individual entries.
     */
    public void writeReport() {

        try {

            // Create instance of BufferedWriter.
            BufferedWriter writer = Files.newBufferedWriter(this.databasePath);

            // Write header row.
            List<String> header = new ArrayList<>(this.globalEntries.get(0).getKeySet());
            writer.write(String.join(",", header));
            writer.newLine();

            // Write all remaining entries.
            for (QcDataEntry entry: this.globalEntries) {

                writer.write(String.join(",", entry.writeValues()));
                writer.newLine();
            }

            // Close the writer.
            writer.close();

        } catch (IOException ex) {

            // TODO - error handling.
            ex.printStackTrace();
        }
    }

    /**
     * Write annotation database to CSV file.
     * Primarily used to add new annotations or modifications to existing records into database.
     */
    public void writeAnnotationReport() {

        try {

            // Create new instance of BufferedWriter.
            BufferedWriter writer = Files.newBufferedWriter(this.annotationPath);

            // Write header row from database keys.
            List<String> header = new ArrayList<>(this.qcAnnotationDatabase.get(0).getKeySet());
            writer.write(String.join(",", header));
            writer.newLine();

            // Write all remaining records.
            for (QcAnnotation qcAnnotation : this.qcAnnotationDatabase) {

                writer.write(String.join(",", qcAnnotation.writeValues()));
                writer.newLine();
            }

            // Close the writer.
            writer.close();

        } catch (IOException ex) {

            // TODO - Error handling.
            ex.printStackTrace();
        }
    }

    /**
     * Get QC data in specified date range.
     * Remove data marked as excluded if context menu parameter selected.
     *
     * @return List of filtered data entries.
     */
    private List<QcDataEntry> getFilteredData() {

        List<QcDataEntry> newList = new ArrayList<>();

        // Loop through list, checking if each entry within specified date range and showable.
        for (QcDataEntry entry : this.globalEntries) {

            String date = entry.getDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDate dateTime = LocalDate.parse(date, formatter);

            if ((ReportFilteringUtils.isWithinDateRange(dateTime, qcParameters.startDate, qcParameters.endDate) &&
                    ReportFilteringUtils.isShowable(qcParameters.showExcluded, entry.isExcluded()) &&
                    ReportFilteringUtils.isMonitoredLogNumber(entry.getLogNumber(), qcParameters.logNumbers)) ||
                    ReportFilteringUtils.isGuideSet(qcParameters.showGuide, entry.isGuide())) {

                newList.add(entry);
            }
        }

        return newList;
    }

    /**
     * Get annotations in specified date range.
     *
     * @return List of filtered annotations.
     */
    private List<QcAnnotation> getFilteredAnnotation() {

        List<QcAnnotation> newList = new ArrayList<>();

        // Loop through annotation database, check if annotation in date range.
        for (QcAnnotation qcAnnotation : this.qcAnnotationDatabase) {

            String date = qcAnnotation.getDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDate dateTime = LocalDate.parse(date, formatter);

            if (ReportFilteringUtils.isWithinDateRange(dateTime, qcParameters.startDate, qcParameters.endDate) &&
                    ReportFilteringUtils.filteredAnnotation(qcParameters, qcAnnotation)) {

                newList.add(qcAnnotation);
            }
        }

        return newList;
    }

    /**
     * Get QC data for plotting.
     * User may specify if the line chart is a Levey-Jennings plot or Moving-Difference plots.
     */
    private void makePlotData() {

        this.chart = FXCollections.observableArrayList();

        // Sort entries by date
        this.mergedEntries.sort(Comparator.comparing(QcDataEntry::getDate));

        // Designate which type of plot to generate.
        switch (qcParameters.plotType) {
            case 1 -> this.chart.addAll(PlotUtils.getLeveyData(this.mergedEntries, qcParameters.selection, qcParameters.logScale, qcParameters.varType, qcParameters.showGuide));
            case 2 -> this.chart.addAll(PlotUtils.getMovingData(this.mergedEntries, qcParameters.selection));
        }
    }

    /**
     * Merge annotation entries into QC data entries.
     * Merged working entries sorted by date.
     */
    private List<QcDataEntry> mergePlotData() {

        List<QcDataEntry> dataEntries = new ArrayList<>(this.workingEntries);

        // Add annotations to entry list, set report value to zero.
        for(QcAnnotation qcAnnotation : this.workingQcAnnotations){

            String[] header = {"Date", qcParameters.selection, "Comment"};
            String[] entry = {qcAnnotation.getDate(), "0.0", qcAnnotation.getComment()};

            dataEntries.add(new QcDataEntry(header, entry, qcAnnotation.getType()));
        }

        // Sort entries by date, annotations should appear between sample entries.
        dataEntries.sort(Comparator.comparing(QcDataEntry::getDate));

        // Loop through working entries, set annotation entries value to be identical to first valid previous data entry.
        // If no valid previous entry exists, replace with first valid older entry.
        for (int i = 0; i < dataEntries.size(); i++){

            // Is annotation.
            if(dataEntries.get(i).getItem(qcParameters.selection) == 0.0){

                Double firstValue = 0.0;
                Double nextValue = 0.0;

                int j = i - 1;
                int k = i + 1;

                // Find first earlier non-zero value entry
                while(j >= 0){

                    // Set value to previous entry.
                    if(dataEntries.get(j).getItem(qcParameters.selection) != 0.0) {
                        firstValue = dataEntries.get(j).getItem(qcParameters.selection);
                        break;
                    }

                    j = j - 1;
                }

                // Find first older non-zero value entry.
                while(k < dataEntries.size()){

                    // Set value to older entry.
                    if(dataEntries.get(k).getItem(qcParameters.selection) != 0.0) {

                        nextValue = dataEntries.get(k).getItem(qcParameters.selection);
                        break;
                    }

                    k = k + 1;
                }

                // Replace annotation zero value with nearest non-zero value.
                if(firstValue == 0.0 ){
                    dataEntries.get(i).replaceItem(qcParameters.selection, String.valueOf(nextValue));
                }  else {
                    dataEntries.get(i).replaceItem(qcParameters.selection, String.valueOf(firstValue));
                }
            }
        }

        return dataEntries;
    }

    /**
     * Make table columns of all entries in current QC context
     *
     * @return List of table columns.
     */
    public ArrayList<TableColumn<Map<Integer, String>, String>> makeTable() {

        ArrayList<TableColumn<Map<Integer, String>, String>> columns = new ArrayList<>();

        // Get key set from the data entry list.
        QcDataEntry entry = this.workingEntries.get(0);
        int col = entry.size();
        String[] keys = entry.getKeySet().toArray(new String[0]);

        // Add a column for each key in the key list.
        for (int i = 0; i < col; i++) {

            String name = keys[i];
            int index = i;

            TableColumn<Map<Integer, String>, String> tableColumn = new TableColumn<>(name);
            tableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(index)));
            //tableColumn.setCellValueFactory(new MapValueFactory(i)); // This was working, but I couldn't figure out the correct parameterization.
            columns.add(tableColumn);
        }

        return columns;
    }

    /**
     * Get data associated with the QC plot.
     *
     * @return List of chart data.
     */
    public ObservableList<XYChart.Series<String, Number>> getPlotData() {

        return this.chart;
    }

    /**
     * Get data associated with the summary table.
     *
     * @return List of mapped table entries.
     */
    public ObservableList<Map<Integer, String>> getTableData() {

        ObservableList<Map<Integer, String>> allData = FXCollections.observableArrayList();

        // Get number of columns and key set.
        QcDataEntry entry = this.workingEntries.get(0);
        int col = entry.size();
        String[] keys = entry.getKeySet().toArray(new String[0]);

        // Add each data entry into mapped table row object.
        for (QcDataEntry data : this.workingEntries) {

            // Table rows are hash maps.
            Map<Integer, String> dataRow = new HashMap<>();
            for (int i = 0; i < col; i++) {

                dataRow.put(i, data.getValue(keys[i]));
            }

            allData.add(dataRow);
        }

        return allData;
    }

    /**
     * Make table column of all annotations in current context.
     *
     * @return List of table columns
     */
    public ArrayList<TableColumn<Map<Integer, String>, String>> makeAnnotationTable() {

        ArrayList<TableColumn<Map<Integer, String>, String>> columns = new ArrayList<>();

        // Get number of columns and key set.
        QcAnnotation qcAnnotation = this.qcAnnotationDatabase.get(0);
        int col = qcAnnotation.size();
        String[] keys = qcAnnotation.getKeySet().toArray(new String[0]);

        // Make table column for each key in set.
        for (int i = 0; i < col; i++) {

            String name = keys[i];
            int index = i;

            TableColumn<Map<Integer, String>, String> tableColumn = new TableColumn<>(name);
            tableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(index)));
            //tableColumn.setCellValueFactory(new MapValueFactory(i)); // This was working but didn't like raw use.

            // Bigger column width for qcAnnotation comments.
            if(name.equals("Comment")) {

                tableColumn.setMinWidth(400);
            }

            columns.add(tableColumn);
        }

        return columns;
    }

    /**
     * Get table data from the annotation summary table.
     *
     * @return List of mapped table data.
     */
    public ObservableList<Map<Integer, String>> getAnnotationData() {

        ObservableList<Map<Integer, String>> allData = FXCollections.observableArrayList();

        // Get column number and key set.
        QcAnnotation qcAnnotation = this.workingQcAnnotations.get(0);
        int col = qcAnnotation.size();
        String[] keys = qcAnnotation.getKeySet().toArray(new String[0]);

        // Add each qcAnnotation entry as a mapped table row.
        for (QcAnnotation entry : this.workingQcAnnotations) {

            // Table rows are hash maps.
            Map<Integer, String> dataRow = new HashMap<>();
            for (int i = 0; i < col; i++) {

                dataRow.put(i, entry.getValue(keys[i]));
            }

            allData.add(dataRow);
        }

        return allData;
    }

    /**
     * Replace existing QcParameters with new QcParameters.
     *
     * @param newParams New user QcParameters.
     */
    public void updateParams(QcParameters newParams) {

        this.qcParameters = newParams;
    }

    /**
     * Get QcDataEntry from current chart data point.
     *
     * @param data Selected chart data.
     * @return Selected QcDataEntry or null if not found.
     */
    public QcDataEntry getDataEntry(XYChart.Data<String, Number> data) {

        String date = data.getXValue();

        // Get QcAnnotation by selected data point data
        // Assumption that it is impossible for to entries to have the same date
        // TODO - give entries unique identifiers.
        for(QcDataEntry entry : this.mergedEntries) {

            if(entry.getDate().equals(date)) return entry;
        }

        return null;
    }

    /**
     * Get QcAnnotation from QcDataEntry-QcAnnotation entry.
     *
     * @param entry User selected entry from line chart.
     * @return QcAnnotation object of selection.
     */
    public QcAnnotation getDataEntryAnnotation(QcDataEntry entry) {

        String date = entry.getDate();
        String type = entry.getType();

        // Get QcAnnotation by selected data point data and type
        // Assumption that it is impossible for to annotations to have the same date and type (not true)
        // TODO - give annotations unique identifiers.
        for(QcAnnotation qcAnnotation : this.workingQcAnnotations) {

            if(qcAnnotation.getDate().equals(date) && qcAnnotation.getType().equals(type)) return qcAnnotation;
        }

        return null;
    }

    /**
     * Update Inclusion-Exclusion setting of selected data entry.
     * Update QC report following change.
     *
     * @param selectedEntry Selected data entry from line chart.
     */
    public void setDataEntryInclusion(QcDataEntry selectedEntry) {

        String date = selectedEntry.getDate();

        for(QcDataEntry entry : this.globalEntries) {

            if(entry.getDate().equals(date)) entry.changeExclusionStatus();
        }

        // Update report.
        writeReport();
    }

    /**
     * Update Guide setting of selected data entry.
     * Update QC report following change.
     *
     * @param selectedEntry Selected data entry from line chart.
     */
    public void setDataEntryGuide(QcDataEntry selectedEntry) {

        String date = selectedEntry.getDate();

        for(QcDataEntry entry : this.globalEntries) {

            if(entry.getDate().equals(date)) entry.changeGuideStatus();
        }

        // Update report.
        writeReport();
    }

    /**
     * Update data entry comment for selected data entry.
     * Update QC report following change.
     *
     * @param selectedEntry Selected data entry from line chart.
     * @param comment New user comment.
     */
    public void setDataEntryComment(QcDataEntry selectedEntry, String comment) {

        String date = selectedEntry.getDate();

        for(QcDataEntry entry : this.globalEntries) {

            if(entry.getDate().equals(date)) entry.setComment(comment);
        }

        // Update report.
        writeReport();
    }

    /**
     * Get date of first QC entry in selected series.
     *
     * @return String of date
     */
    public String getFirstEntryDate() {

        return this.workingEntries.get(0).getDate();
    }

    /**
     * Get date of last QC entry in selected series.
     *
     * @return String of date
     */
    public String getLastEntryDate() {

        return this.workingEntries.get(this.workingEntries.size() - 1).getDate();
    }

    /**
     * Get working data entry list size.
     *
     * @return int size of list.
     */
    public int getWorkingEntrySize() {

        return this.workingEntries.size();
    }

    /**
     * Get working annotations list size.
     *
     * @return int size of list.
     */
    public int getWorkingAnnotationSize() {

        return this.workingQcAnnotations.size();
    }

    /**
     * Compute frequency of QC acquisition in selected series. Average frequency calculated as median time difference
     * between filtered QC entries.
     *
     * @return String of acquisition frequency
     */
    public String getEntryFrequency() {

        ArrayList<LocalDateTime> dateList = new ArrayList<>();

        // Get dates.
        for(QcDataEntry entry : this.workingEntries){

            String date = entry.getDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            dateList.add(LocalDateTime.parse(date, formatter));
        }

        ArrayList<Double> freq = new ArrayList<>();

        // Compute duration between entries.
        for(int i = 0; i < dateList.size() - 1; i++) {

            freq.add(Duration.between(dateList.get(i), dateList.get(i+1)).toMinutes() / 60.0);
        }

        return String.format("%,.1f", MathUtils.calculateMedian(freq)) + " hours";
    }

    /**
     * Get selected QC value from user input.
     *
     * @return Double ArrayList of values.
     */
    private ArrayList<Double> getSeriesValues() {

        ArrayList<Double> valueList = new ArrayList<>();

        for(QcDataEntry entry : this.workingEntries) {

            valueList.add(entry.getItem(qcParameters.selection));
        }

        return valueList;
    }

    /**
     * Format input string to desired format.
     * TODO - not working with some entries, e.g. Peptide areas.
     *
     * @param value double value to format.
     * @return String of formatted value
     */
    private String formatSummaryValue(double value) {

        String text = Double.toString(value);

        // If more than 4 digits, format as scientific.
        if(text.indexOf('.') > 4) {

            return String.format("%.3E", value);
        } else {

            return String.format("%,.3f", value);
        }
    }

    /**
     * Get series mean as string.
     */
    public String getSeriesMeanString() {

        return formatSummaryValue(MathUtils.calculateAverage(getSeriesValues()));
    }

    /**
     * Get series minimum as string.
     */
    public String getSeriesMin() {

        return formatSummaryValue(Collections.min(getSeriesValues()));
    }

    /**
     * Get series max as string.
     */
    public String getSeriesMax() {

        return formatSummaryValue(Collections.max(getSeriesValues()));
    }

    /**
     * Get series standard deviation as string.
     */
    public String getSeriesSD() {

        return formatSummaryValue(MathUtils.calculateStandardDeviation(getSeriesValues()));
    }

    /**
     * Get series relative deviation as string.
     */
    public String getSeriesRsd() {

        return formatSummaryValue(MathUtils.calculateRelativeStandardDeviation(getSeriesValues()));
    }

    /**
     * Get unique log numbers of selected QC entries.
     *
     * @return List of sorted unique log number strings.
     */
    public List<String> getSeriesLogNumbers() {

        HashSet<String> logNumbers = new HashSet<>(this.workingEntries.stream().map(QcDataEntry::getLogNumber).collect(Collectors.toList()));

        return logNumbers.stream().sorted().toList();
    }

    /**
     * Add new qcAnnotation to master qcAnnotation database.
     *
     * @param qcAnnotation New user qcAnnotation.
     */
    public void addAnnotation(QcAnnotation qcAnnotation) {

        this.qcAnnotationDatabase.add(qcAnnotation);
    }

    /**
     * Get user selected annotation from working annotation list.
     *
     * @param index index of selected entry
     * @return Selected annotation
     */
    public QcAnnotation getSelectedAnnotation(int index) {

        return this.workingQcAnnotations.get(index);
    }

    /**
     * Edit selected annotation and replace with user modified version in master annotation database.
     *
     * @param oldQcAnnotation Original selected annotation
     * @param newQcAnnotation New user modified annotation
     */
    public void editAnnotation(QcAnnotation oldQcAnnotation, QcAnnotation newQcAnnotation) {

        // Loop through master annotation database and replace with modified annotation
        for(int i = 0; i < qcAnnotationDatabase.size(); i++) {

            if(qcAnnotationDatabase.get(i).equals(oldQcAnnotation)) {

                qcAnnotationDatabase.set(i, newQcAnnotation);
                break;
            }
        }
    }

    /**
     * Sort annotations by date.
     */
    public void sortAnnotations() {

        this.qcAnnotationDatabase.sort(Comparator.comparing(QcAnnotation::getDate));
    }

    /**
     * Delete qcAnnotation from master qcAnnotation database.
     *
     * @param qcAnnotation Selected qcAnnotation.
     */
    public void deleteAnnotation(QcAnnotation qcAnnotation) {

        this.qcAnnotationDatabase.remove(qcAnnotation);
    }

    /**
     * Check if selected annotation in data entry list is annotation-like entry.
     *
     * @param index index from data entry list.
     */
    public Boolean isAnnotation(int index) {

        return this.mergedEntries.get(index).isAnnotation();
    }

    /**
     * Check if the data entry is designated as excluded from the series.
     *
     * @param index index from data entry list
     */
    public Boolean isExcluded(int index) {

        return this.mergedEntries.get(index).isExcluded();
    }

    /**
     * Check if data entry has a comment that doesn't equal "NA"
     *
     * @param index index from data entry list
     */
    public Boolean hasComment(int index) {

        return !this.mergedEntries.get(index).getComment().equals("NA");
    }

    /**
     * Check if entry at index is part of the guide set.
     *
     * @param index index in merged QC-annotation series
     * @return boolean true if part of guide set
     */
    public Boolean isGuideSet(int index) {

        return this.mergedEntries.get(index).isGuide();
    }

    /**
     * Get QcAnnotation type of current selection.
     *
     * @param index index from data entry list
     * @return QcAnnotation type string
     */
    public String getAnnotationType(int index) {

        return this.mergedEntries.get(index).getType();
    }
}
