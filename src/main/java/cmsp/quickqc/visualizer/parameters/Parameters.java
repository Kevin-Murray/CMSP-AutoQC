package cmsp.quickqc.visualizer.parameters;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;

public class Parameters {

    public String instrument;
    public String configuration;
    public String matrix;
    public String report;
    public String plotScale;
    public String dateRange;

    public int plotType;

    public LocalDate startDate;
    public LocalDate endDate;

    public Map<String, Boolean> annotationMap;

    public String varType;
    public Boolean showAnnotation;
    private Boolean groupAxis;
    public Boolean logScale;
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
        varType = null;
        annotationMap = null;
        showAnnotation = null;
        groupAxis = null;
        logScale = null;
        showExcluded = null;
        showGuide = null;

    }

    public Parameters(ChoiceBox instrumentBox,
                      ChoiceBox configurationBox,
                      ChoiceBox matrixBox,
                      ChoiceBox reportBox,
                      ChoiceBox dateRangeBox,
                      RadioButton leveyJenningsButton,
                      RadioButton movingRangeButton,
                      RadioButton cusummButton,
                      RadioButton cusumvButton,
                      DatePicker startDatePicker,
                      DatePicker endDatePicker,
                      CheckBox annotationCheckBox,
                      CheckBox groupXAxisCheckBox,
                      String varType,
                      Map<String, Boolean> annotationMap,
                      Boolean logScale,
                      Boolean showExcluded,
                      CheckBox showGuideSetCheckBox,
                      Path databasePath) {

        this.instrument = instrumentBox.getSelectionModel().getSelectedItem().toString();
        this.configuration = configurationBox.getSelectionModel().getSelectedItem().toString();
        this.matrix = matrixBox.getSelectionModel().getSelectedItem().toString();
        this.report = reportBox.getSelectionModel().getSelectedItem().toString();
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

        this.varType = varType;
        this.annotationMap = annotationMap;

        this.startDate = startDatePicker.getValue();
        this.endDate = endDatePicker.getValue();

        this.showAnnotation = annotationCheckBox.isSelected();
        this.groupAxis = groupXAxisCheckBox.isSelected();
        this.logScale = logScale;
        this.showExcluded = showExcluded;
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
