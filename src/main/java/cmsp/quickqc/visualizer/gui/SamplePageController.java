
package cmsp.quickqc.visualizer.gui;

import cmsp.quickqc.visualizer.datamodel.DataEntry;
import cmsp.quickqc.visualizer.datamodel.SampleEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;
import java.util.Set;

/**
 * Controller class for sample page.
 */
public class SamplePageController {

    @FXML private TextArea commentBox;
    @FXML private RadioButton excludeButton;
    @FXML private RadioButton includeButton;
    @FXML private TableView sampleTable;

    private DataEntry selectedEntry;
    private boolean changedInclusion;

    /**
     * Listener method for changes in inclusion-exclusion status of selected data entry point.
     */
    @FXML
    protected void inclusionGroupListener() {

        changedInclusion = true;
    }

    /**
     * Set sample page selection using information from selected data entry point.
     *
     * @param selectedEntry
     */
    public void setDataEntry(DataEntry selectedEntry){

        this.selectedEntry = selectedEntry;

        // Is there more elegant way of doing this?
        if(selectedEntry.excludeData()) {

            excludeButton.setSelected(true);

        } else {

            includeButton.setSelected(true);
        }

        // Make table with values from selected data entry point.
        // TODO - raw use of TableView. I don't know how to fix this with current implementation.
        sampleTable.getColumns().clear();

        TableColumn<List<String>, String> fieldColumn = new TableColumn<>("Field");
        fieldColumn.setCellValueFactory(new PropertyValueFactory<>("field"));

        TableColumn<List<String>, String> valueColumn = new TableColumn<>("Value");
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        sampleTable.getColumns().addAll(fieldColumn, valueColumn);

        ObservableList<SampleEntry> sampleEntries = makeSampleTable(selectedEntry);

        sampleTable.getItems().addAll(sampleEntries);

        this.commentBox.setText(this.selectedEntry.getComment());
    }

    /**
     * Make TableView compatible entry from selected data entry point.
     *
     * @param entry Selected data entry
     * @return ObservableList of Sample Entries
     */
    private ObservableList<SampleEntry> makeSampleTable(DataEntry entry){

        ObservableList<SampleEntry> sampleTableList = FXCollections.observableArrayList();

        Set<String> keySet = entry.getKeySet();

        // Make new sample entry object for each key:value pair in selected date entry point.
        for(String key : keySet) {

            sampleTableList.add(new SampleEntry(key, entry.getValue(key)));
        }

        return sampleTableList;
    }

    /**
     * Determine if the inclusion-exclusion status of the selected data entry point has changed.
     */
    public boolean getChangedInclusion() {

        // TODO - This logic is confusing. I need to make this more clear to understand.
        if(this.changedInclusion) {

            return this.selectedEntry.excludeData() != this.excludeButton.isSelected();

        } else {

            return false;
        }
    }

    /**
     * Comment of data entry point has been changed.
     */
    public boolean changedComment() {

        return !(this.selectedEntry.getValue("Comment").equals(this.commentBox.getText()));
    }

    /**
     * Get comment from comment box.
     */
    public String getComment() {

        return this.commentBox.getText();
    }
}