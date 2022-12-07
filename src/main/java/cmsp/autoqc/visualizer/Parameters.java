package cmsp.autoqc.visualizer;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;

import java.nio.file.Path;
import java.time.LocalDate;

public class Parameters {

    public String instrument;
    public String configuration;
    public String matrix;
    public String report;
    private String plotScale;
    public String dateRange;

    public int plotType;

    public LocalDate startDate;
    public LocalDate endDate;

    private Boolean showAnnotation;
    private Boolean groupAxis;
    public Boolean showExcluded;
    private Boolean showGuide;

    public Path databasePath;

    public Parameters() {

        instrument = null;
        configuration = null;
        matrix = null;
        report = null;
        plotScale = null;
        plotType = 0;
        startDate = null;
        endDate = null;
        showAnnotation = null;
        groupAxis = null;
        showExcluded = null;
        showGuide = null;

    }

    public Parameters(ChoiceBox instrumentBox,
                      ChoiceBox configurationBox,
                      ChoiceBox matrixBox,
                      ChoiceBox reportBox,
                      ChoiceBox yAxisBox,
                      ChoiceBox dateRangeBox,
                      RadioButton leveyJenningsButton,
                      RadioButton movingRangeButton,
                      RadioButton cusummButton,
                      RadioButton cusumvButton,
                      DatePicker startDatePicker,
                      DatePicker endDatePicker,
                      CheckBox annotationCheckBox,
                      CheckBox groupXAxisCheckBox,
                      CheckBox showExcludedCheckBox,
                      CheckBox showGuideSetCheckBox,
                      Path databasePath) {

        this.instrument = instrumentBox.getSelectionModel().getSelectedItem().toString();
        this.configuration = configurationBox.getSelectionModel().getSelectedItem().toString();
        this.matrix = matrixBox.getSelectionModel().getSelectedItem().toString();
        this.report = reportBox.getSelectionModel().getSelectedItem().toString();
        this.plotScale = yAxisBox.getSelectionModel().getSelectedItem().toString();
        this.dateRange = dateRangeBox.getSelectionModel().getSelectedItem().toString();

        if(leveyJenningsButton.isSelected()){
            this.plotType = 1;
        } else if (movingRangeButton.isSelected()) {
            this.plotType = 2;
        } else if (cusummButton.isSelected()) {
            this.plotType = 3;
        } else {
            this.plotType = 4;
        }

        this.startDate = startDatePicker.getValue();
        this.endDate = endDatePicker.getValue();

        this.showAnnotation = annotationCheckBox.isSelected();
        this.groupAxis = groupXAxisCheckBox.isSelected();
        this.showExcluded = showExcludedCheckBox.isSelected();
        this.showGuide = showGuideSetCheckBox.isSelected();

        this.databasePath = databasePath;
    }

    public boolean validSelection() {
        return !(this.instrument == null &&
                this.configuration == null &&
                this.matrix == null &&
                this.report == null);
    }

    public boolean diffReportSelection(Parameters newParam){

        if(this.validSelection()) {
            return !(this.instrument.equals(newParam.instrument) &&
                    this.configuration.equals(newParam.configuration) &&
                    this.matrix.equals(newParam.matrix));
        } else {
            return true;
        }
    }
}
