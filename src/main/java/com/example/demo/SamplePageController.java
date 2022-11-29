package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.LinkedHashMap;
import java.util.Set;

public class SamplePageController {

    @FXML private TextArea commentBox;
    @FXML private ToggleGroup exclude;
    @FXML private RadioButton excludeButton;
    @FXML private RadioButton includeButton;
    @FXML private TableView sampleTable;

    public void setDataEntry(DataEntry selectedEntry){

        if(selectedEntry.excludeData()){
            excludeButton.setSelected(true);
        } else {
            includeButton.setSelected(true);
        }

        sampleTable.getColumns().clear();

        TableColumn fieldColumn = new TableColumn("Field");
        fieldColumn.setCellValueFactory(new PropertyValueFactory<>("field"));

        TableColumn valueColumn = new TableColumn("Value");
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        sampleTable.getColumns().addAll(fieldColumn, valueColumn);

        ObservableList<SampleEntry> sampleEntries = makeSampleTable(selectedEntry);

        sampleTable.getItems().addAll(sampleEntries);
    }

    private ObservableList<SampleEntry> makeSampleTable(DataEntry entry){

        ObservableList<SampleEntry> sampleTableList = FXCollections.observableArrayList();

        Set<String> keySet = entry.getKeySet();

        for(String key : keySet){
            sampleTableList.add(new SampleEntry(key, entry.getValue(key)));
        }

        return sampleTableList;
    }

}