package cmsp.quickqc.visualizer;

import cmsp.quickqc.visualizer.datamodel.DataEntry;
import cmsp.quickqc.visualizer.utils.ReportFilteringUtils;
import cmsp.quickqc.visualizer.utils.annotations.Annotation;
import cmsp.quickqc.visualizer.parameters.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.MapValueFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static cmsp.quickqc.visualizer.utils.plotUtils.PlotUtils.getLeveyData;
import static cmsp.quickqc.visualizer.utils.plotUtils.PlotUtils.getMovingData;
import static cmsp.quickqc.visualizer.parameters.types.ReportFiles.getAnnotationPath;
import static cmsp.quickqc.visualizer.parameters.types.ReportFiles.getPath;

public class QuickQCTask {

    private Parameters parameters;
    private ObservableList<XYChart.Series> chart;
    private List<DataEntry> globalEntries;
    private List<DataEntry> workingEntries;
    private List<Annotation> annotationDatabase;
    private List<Annotation> workingAnnotations;

    private Path databasePath;
    private Path annotationPath;


    public QuickQCTask(Parameters parameters) {

        this.parameters = parameters;

        if(parameters.validSelection()){
            this.databasePath = getPath(this.parameters);
            this.annotationPath = getAnnotationPath(this.parameters);
            this.globalEntries = readReport();
            this.annotationDatabase = readAnnotation();
        }
    }

    public void run() {

        this.workingEntries = getFilteredData();
        this.workingAnnotations = getFilteredAnnotation();

        if(parameters.showAnnotation){
            mergePlotData();
        }

        makePlotData();
    }

    private List<DataEntry> readReport() {

        List<DataEntry> dataEntries = new ArrayList<>();

        // Create an instance of BufferedReader
        try (BufferedReader br = Files.newBufferedReader(this.databasePath, StandardCharsets.US_ASCII)) {

            String line = br.readLine();
            line = line.replace("\"", "");
            String[] header = line.split(",");

            line = br.readLine();

            while (line != null) {

                //line = line.replace("\"", "");
                //String[] attributes = line.split(",");

                // TODO - evaluate a more efficient way to handle commas in comment field.
                String[] attributes = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                for(int i = 0; i < attributes.length; i++) {
                    attributes[i] = attributes[i].replace("\"", "");
                }

                DataEntry entry = new DataEntry(header, attributes, "Sample");

                dataEntries.add(entry);

                line = br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return dataEntries;
    }

    private ObservableList<Annotation> readAnnotation() {

        ObservableList<Annotation> annotationList = FXCollections.observableArrayList();

        // Create an instance of BufferedReader
        try (BufferedReader br = Files.newBufferedReader(this.annotationPath, StandardCharsets.US_ASCII)) {

            String line = br.readLine();
            line = line.replace("\"", "");
            String[] header = line.split(",");

            line = br.readLine();

            while (line != null) {

                // TODO - evaluate a more efficient way to handle commas in comment field.
                String[] attributes = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                for(int i = 0; i < attributes.length; i++) {
                    attributes[i] = attributes[i].replace("\"", "");
                }

                Annotation annotation = new Annotation(attributes[0], attributes[1], attributes[2],
                        attributes[3], attributes[4], attributes[5]);

                annotationList.add(annotation);

                line = br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return annotationList;
    }

    public void writeReport() {
        try {

            BufferedWriter writer = Files.newBufferedWriter(this.databasePath);

            List<String> header = new ArrayList<>(this.globalEntries.get(0).getKeySet());

            writer.write(String.join(",", header));
            writer.newLine();

            // write all records
            for (DataEntry entry: this.globalEntries) {

                writer.write(String.join(",", entry.writeValues()));
                writer.newLine();
            }

            //close the writer
            writer.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void writeAnnotationReport() {

        try {

            BufferedWriter writer = Files.newBufferedWriter(this.annotationPath);

            List<String> header = new ArrayList<>(this.annotationDatabase.get(0).getKeySet());

            writer.write(String.join(",", header));
            writer.newLine();

            // write all records
            for (Annotation annotation: this.annotationDatabase) {

                writer.write(String.join(",", annotation.writeValues()));
                writer.newLine();
            }

            //close the writer
            writer.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private List<DataEntry> getFilteredData() {

        List<DataEntry> newList = new ArrayList<>();

        for (DataEntry entry : this.globalEntries) {

            String date = entry.getDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDate dateTime = LocalDate.parse(date, formatter);

            if (ReportFilteringUtils.isWithinDateRange(dateTime, parameters.startDate, parameters.endDate) &&
                    ReportFilteringUtils.isShowable(parameters.showExcluded, entry.excludeData())) {
                newList.add(entry);
            }
        }

        return newList;
    }

    private List<Annotation> getFilteredAnnotation() {

        List<Annotation> newList = new ArrayList<>();

        for (Annotation annotation : this.annotationDatabase) {

            String date = annotation.getDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDate dateTime = LocalDate.parse(date, formatter);

            if (ReportFilteringUtils.isWithinDateRange(dateTime, parameters.startDate, parameters.endDate) &&
                    ReportFilteringUtils.filteredAnnotation(parameters, annotation)) {
                newList.add(annotation);
            }
        }

        return newList;
    }

    private void makePlotData() {

        this.chart = FXCollections.observableArrayList();

        // Sort entries by date
        Collections.sort(this.workingEntries, Comparator.comparing(p -> p.getDate()));

        XYChart.Series series = new XYChart.Series();

        for (DataEntry entry : this.workingEntries) {

            series.getData().add(new XYChart.Data(entry.getDate(), entry.getItem(parameters.report)));
        }

        switch (parameters.plotType) {
            case 1 -> this.chart.addAll(getLeveyData(this.workingEntries, parameters.report, parameters.plotScale));
            case 2 -> this.chart.addAll(getMovingData(this.workingEntries, parameters.report));
        }
    }

    private void mergePlotData() {

        // Add annotations to entry list, set report value to zero.
        for(Annotation annotation : this.workingAnnotations){

            String[] header = {"Date", parameters.report, "Comment"};
            String[] entry = {annotation.getDate(), "0.0", annotation.getComment()};

            this.workingEntries.add(new DataEntry(header, entry, annotation.getType()));
        }

        // Sort entries by date, annotations should appear between sample entries.
        Collections.sort(this.workingEntries, Comparator.comparing(p -> p.getDate()));

        for (int i = 0; i < this.workingEntries.size(); i++){

            if(this.workingEntries.get(i).getItem(parameters.report) == 0.0){

                Double firstValue = 0.0;
                Double nextValue = 0.0;

                int j = i - 1;
                int k = i + 1;

                // Find first earlier non-zero value entry
                while(j >= 0){

                    if(this.workingEntries.get(j).getItem(parameters.report) != 0.0) {
                        firstValue = this.workingEntries.get(j).getItem(parameters.report);
                        break;
                    }

                    j = j - 1;
                }

                // Find first older non-zero value entry
                while(k < this.workingEntries.size()){

                    if(this.workingEntries.get(k).getItem(parameters.report) != 0.0) {
                        nextValue = this.workingEntries.get(k).getItem(parameters.report);
                        break;
                    }

                    k = k + 1;
                }

                // Replace annotation zero value with mean or nearest non-zero value.
                if(firstValue == 0.0 ){
                    this.workingEntries.get(i).replaceItem(parameters.report, String.valueOf(nextValue));
                }  else {
                    this.workingEntries.get(i).replaceItem(parameters.report, String.valueOf(firstValue));
                }
            }
        }
    }

    public ArrayList<TableColumn> makeTable() {

        ArrayList<TableColumn> columns = new ArrayList<>();

        DataEntry entry = this.workingEntries.get(0);
        int col = entry.size();
        String[] keys = entry.getKeySet().toArray(new String[0]);

        for (int i = 0; i < col; i++) {
            String name = keys[i];
            TableColumn<Map<Integer, String>, String> tableColumn = new TableColumn<>(name);
            tableColumn.setCellValueFactory(new MapValueFactory(i));
            columns.add(tableColumn);
        }

        return columns;
    }

    public ObservableList<XYChart.Series> getPlotData() {

        return this.chart;

    }

    public ObservableList<Map<Integer, String>> getTableData() {

        ObservableList<Map<Integer, String>> alldata = FXCollections.observableArrayList();

        DataEntry entry = this.workingEntries.get(0);
        int col = entry.size();
        String[] keys = entry.getKeySet().toArray(new String[0]);

        for (DataEntry data : this.workingEntries) {

            Map<Integer, String> dataRow = new HashMap<>();
            for (int i = 0; i < col; i++) {
                dataRow.put(i, data.getValue(keys[i]));
            }

            alldata.add(dataRow);
        }

        return alldata;
    }

    public ArrayList<TableColumn> makeAnnotationTable() {

        ArrayList<TableColumn> columns = new ArrayList<>();

        Annotation annotation = this.annotationDatabase.get(0);
        int col = annotation.size();
        String[] keys = annotation.getKeySet().toArray(new String[0]);

        for (int i = 0; i < col; i++) {
            String name = keys[i];
            TableColumn<Map<Integer, String>, String> tableColumn = new TableColumn<>(name);
            tableColumn.setCellValueFactory(new MapValueFactory(i));

            if(name == "Comment"){
                tableColumn.setMinWidth(400);
            }

            columns.add(tableColumn);
        }

        return columns;
    }

    public ObservableList<Map<Integer, String>> getAnnotationData() {

        ObservableList<Map<Integer, String>> alldata = FXCollections.observableArrayList();

        Annotation annotation = this.workingAnnotations.get(0);
        int col = annotation.size();
        String[] keys = annotation.getKeySet().toArray(new String[0]);

        for (Annotation entry : this.workingAnnotations) {

            Map<Integer, String> dataRow = new HashMap<>();
            for (int i = 0; i < col; i++) {

                dataRow.put(i, entry.getValue(keys[i]));
            }

            alldata.add(dataRow);
        }

        return alldata;
    }

    public void updateParams(Parameters newParams){
        this.parameters = newParams;
    }

    public DataEntry getDataEntry(XYChart.Data data) {

        String date = data.getXValue().toString();

        for(DataEntry entry : this.workingEntries){

            if(entry.getDate().equals(date)){
                return entry;
            }
        }

        return null;
    }

    public void setDataEntryInclusion(DataEntry selectedEntry) {

        String date = selectedEntry.getDate();

        for(DataEntry entry : this.globalEntries){

            if(entry.getDate().equals(date)){

                entry.setExcluded();
            }

        }

        writeReport();
    }

    public void setDataEntryComment(DataEntry selectedEntry, String comment) {

        String date = selectedEntry.getDate();

        for(DataEntry entry : this.globalEntries){

            if(entry.getDate().equals(date)){

                entry.setComment(comment);
            }

        }

        writeReport();

    }

    public int getWorkingEntrySize(){
        return this.workingEntries.size();
    }

    public int getWorkingAnnotationSize(){ return this.workingAnnotations.size();}

    public List<Annotation> getAnnotationDatabase(){ return this.annotationDatabase;}

    public List<Annotation> getWorkingAnnotations(){ return this.workingAnnotations;}

    public void addAnnotation(Annotation annotation){

        this.annotationDatabase.add(annotation);

    }

    public Annotation getSelectedAnnotation(int index){
        return this.workingAnnotations.get(index);
    }

    public void editAnnotation(Annotation oldAnnotation, Annotation newAnnotation){

        for(int i = 0; i < annotationDatabase.size(); i++){

            if(annotationDatabase.get(i).equals(oldAnnotation)){
                annotationDatabase.set(i, newAnnotation);
                break;
            }
        }
    }

    public void sortAnnotations(){

        Collections.sort(this.annotationDatabase, Comparator.comparing(Annotation::getDate));

    }

    public void deleteAnnotation(Annotation annotation){
        this.annotationDatabase.remove(annotation);
    }

    public Boolean isAnnotation(int index){
        return this.workingEntries.get(index).isAnnotation();
    }

    public Boolean isExcluded(int index){
        return this.workingEntries.get(index).excludeData();
    }

    public Boolean hasComment(int index){
        return !this.workingEntries.get(index).getComment().equals("NA");
    }

    public String getAnnotationType(int index){
        return this.workingEntries.get(index).getType();
    }

}
