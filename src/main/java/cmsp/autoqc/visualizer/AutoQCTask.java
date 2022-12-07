package cmsp.autoqc.visualizer;


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

import static cmsp.autoqc.visualizer.PlotUtils.getLeveyData;
import static cmsp.autoqc.visualizer.PlotUtils.getMovingData;
import static cmsp.autoqc.visualizer.ReportFiles.getPath;;


public class AutoQCTask {

    private Parameters parameters;
    private ObservableList<XYChart.Series> chart;
    private List<DataEntry> globalEntries;
    private List<DataEntry> workingEntries;

    private Path databasePath;


    public AutoQCTask(Parameters parameters) {

        this.parameters = parameters;

        if(parameters.validSelection()){
            this.databasePath = getPath(this.parameters);
            this.globalEntries = readReport();
        }
    }

    public void run() {

        this.workingEntries = getFilteredData();

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

                line = line.replace("\"", "");
                String[] attributes = line.split(",");
                DataEntry entry = new DataEntry(header, attributes);

                dataEntries.add(entry);

                line = br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return dataEntries;
    }

    public void writeReport() {
        try {

            BufferedWriter writer = Files.newBufferedWriter(this.databasePath);

            List<String> header = new ArrayList<>(this.globalEntries.get(0).getKeySet());

            writer.write(String.join(",", header));
            writer.newLine();

            // write all records
            for (DataEntry entry: this.globalEntries) {

                writer.write(String.join(",", entry.getValues()));
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

            if (ReportFilteringUtils.isWithinDateRange(dateTime, parameters.startDate, parameters.endDate) &
                    ReportFilteringUtils.isShowable(parameters.showExcluded, entry.excludeData())) {
                newList.add(entry);
            }
        }

        return newList;
    }

    private void makePlotData() {

        this.chart = FXCollections.observableArrayList();

        XYChart.Series series = new XYChart.Series();

        for (DataEntry entry : this.workingEntries) {

            series.getData().add(new XYChart.Data(entry.getDate(), entry.getItem(parameters.report)));
        }

        switch (parameters.plotType) {
            case 1 -> this.chart.addAll(getLeveyData(this.workingEntries, parameters.report));
            case 2 -> this.chart.addAll(getMovingData(this.workingEntries, parameters.report));
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
}
