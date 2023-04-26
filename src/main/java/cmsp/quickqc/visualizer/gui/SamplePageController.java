package cmsp.quickqc.visualizer.gui;

import cmsp.quickqc.visualizer.DataEntry;
import cmsp.quickqc.visualizer.SampleEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Set;

public class SamplePageController {

    @FXML private TextArea commentBox;
    @FXML private ToggleGroup exclude;
    @FXML private RadioButton excludeButton;
    @FXML private RadioButton includeButton;
    @FXML private TableView sampleTable;

    private DataEntry selectedEntry;

    private boolean changedInclusion;

    @FXML
    protected void inclusionGroupListener() {

        changedInclusion = true;
    }

    public void setDataEntry(DataEntry selectedEntry){

        this.selectedEntry = selectedEntry;

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

        this.commentBox.setText(this.selectedEntry.getComment());
    }

    private ObservableList<SampleEntry> makeSampleTable(DataEntry entry){

        ObservableList<SampleEntry> sampleTableList = FXCollections.observableArrayList();

        Set<String> keySet = entry.getKeySet();

        for(String key : keySet){
            sampleTableList.add(new SampleEntry(key, entry.getValue(key)));
        }

        return sampleTableList;
    }

    public boolean getChangedInclusion() {

        if(this.changedInclusion){
            return this.selectedEntry.excludeData() != this.excludeButton.isSelected();
        } else {
            return false;
        }
    }

    public boolean getChangedComment(){

        return !(this.selectedEntry.getValue("Comment").equals(this.commentBox.getText()));

    }

    public String getComment() {
        return this.commentBox.getText();
    }

}