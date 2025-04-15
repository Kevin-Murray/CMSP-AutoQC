
package cmsp.tool.box.gui;

import cmsp.tool.box.datamodel.QcDataEntry;
import cmsp.tool.box.datamodel.QcPlotEntry;
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
public class QcEntryPageController {

    @FXML private RadioButton guideExcludeButton;
    @FXML private RadioButton guideIncludeButton;
    @FXML private RadioButton excludeButton;
    @FXML private RadioButton includeButton;
    @FXML private TextArea commentBox;
    @FXML private TableView sampleTable;

    private QcDataEntry selectedEntry;
    private boolean changedInclusion;
    private boolean changedGuide;

    /**
     * Listener method for changes in inclusion-exclusion status of selected data entry point.
     */
    @FXML
    protected void inclusionGroupListener() {

        changedInclusion = true;
    }

    public void guideGroupListener() {


        changedGuide = true;
    }

    /**
     * Set sample page selection using information from selected data entry point.
     *
     * @param selectedEntry
     */
    public void setDataEntry(QcDataEntry selectedEntry){

        this.selectedEntry = selectedEntry;

        // Is there more elegant way of doing this?
        if(selectedEntry.isExcluded()) {

            excludeButton.setSelected(true);

        } else {

            includeButton.setSelected(true);
        }

        if(selectedEntry.isGuide()) {

            guideIncludeButton.setSelected(true);

        } else {

            guideExcludeButton.setSelected(true);
        }

        // Make table with values from selected data entry point.
        // TODO - raw use of TableView. I don't know how to fix this with current implementation.
        sampleTable.getColumns().clear();

        TableColumn<List<String>, String> fieldColumn = new TableColumn<>("Field");
        fieldColumn.setCellValueFactory(new PropertyValueFactory<>("field"));

        TableColumn<List<String>, String> valueColumn = new TableColumn<>("Value");
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        sampleTable.getColumns().addAll(fieldColumn, valueColumn);

        ObservableList<QcPlotEntry> sampleEntries = makeSampleTable(selectedEntry);

        sampleTable.getItems().addAll(sampleEntries);

        this.commentBox.setText(this.selectedEntry.getComment());
    }

    /**
     * Make TableView compatible entry from selected data entry point.
     *
     * @param entry Selected data entry
     * @return ObservableList of Sample Entries
     */
    private ObservableList<QcPlotEntry> makeSampleTable(QcDataEntry entry){

        ObservableList<QcPlotEntry> sampleTableList = FXCollections.observableArrayList();

        Set<String> keySet = entry.getKeySet();

        // Make new sample entry object for each key:value pair in selected date entry point.
        for(String key : keySet) {

            sampleTableList.add(new QcPlotEntry(key, entry.getValue(key)));
        }

        return sampleTableList;
    }

    /**
     * Determine if the inclusion-exclusion status of the selected data entry point has changed.
     */
    public boolean getChangedInclusion() {

        // TODO - This logic is confusing. I need to make this more clear to understand.
        if(this.changedInclusion) {

            return this.selectedEntry.isExcluded() != this.excludeButton.isSelected();

        } else {

            return false;
        }
    }

    /**
     * Was guide set inclusion status changed.
     *
     * @return true if guide set status changed and guide set marked as showable.
     */
    public boolean getChangedGuide() {

        // TODO - This logic is confusing. I need to make this more clear to understand.
        if(this.changedGuide) {

            return this.selectedEntry.isGuide() != this.guideIncludeButton.isSelected();

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