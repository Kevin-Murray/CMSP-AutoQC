package com.example.demo;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.MapValueFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.example.demo.ReportFiles.getPath;
//import static com.example.demo.ReportFilteringUtils.isShowable;
import static com.example.demo.ReportFilteringUtils.isShowable;
import static com.example.demo.ReportFilteringUtils.isWithinDateRange;


public class AutoQCTask {

    private Parameters parameters;
    private XYChart.Series chart;
    private List<DataEntry> globalEntries;
    private List<DataEntry> workingEntries;


    public AutoQCTask(Parameters parameters) {

        this.parameters = parameters;
    }

    public void run() {

        Path path = getPath(this.parameters);
        this.globalEntries = readReport(path);
        this.workingEntries = getFilteredData();

        getFilteredData();
        makePlotData();
        //makeTableData();
    }

    private List<DataEntry> readReport(Path path) {

        List<DataEntry> dataEntries = new ArrayList<>();

        // Create an instance of BufferedReader
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.US_ASCII)) {

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

    private List<DataEntry> getFilteredData() {

        List<DataEntry> newList = new ArrayList<>();

        for (DataEntry entry : this.globalEntries) {

            String date = entry.getDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDate dateTime = LocalDate.parse(date, formatter);

            if (isWithinDateRange(dateTime, parameters.startDate, parameters.endDate) &
                    isShowable(parameters.showExcluded, entry.excludeData())) {
                newList.add(entry);
            }
        }

        return newList;
    }

    private void makePlotData() {

        XYChart.Series series = new XYChart.Series();

        for (DataEntry entry : this.workingEntries) {

            series.getData().add(new XYChart.Data(entry.getDate(), entry.getItem(parameters.report)));
        }

        this.chart = series;
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

    public XYChart.Series getPlotData() {

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
}
