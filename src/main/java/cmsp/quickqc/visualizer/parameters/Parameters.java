
package cmsp.quickqc.visualizer.parameters;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;

/**
 * Parameter class that stores all user input selections to control the QC report context.
 */
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
    public Boolean logScale;
    public Boolean showExcluded;
    private Boolean showGuide;

    public Path databasePath;

    /**
     * Null-constructor for parameters class.
     */
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
        logScale = null;
        showExcluded = null;
        showGuide = null;
    }

    /**
     * Constructor for parameters class. Formats user input selections or preferences.
     */
    public Parameters(ChoiceBox<String> instrumentBox,
                      ChoiceBox<String> configurationBox,
                      ChoiceBox<String> matrixBox,
                      ChoiceBox<String> reportBox,
                      ChoiceBox<String> dateRangeBox,
                      RadioButton leveyJenningsButton,
                      RadioButton movingRangeButton,
                      RadioButton cusummButton,
                      RadioButton cusumvButton,
                      DatePicker startDatePicker,
                      DatePicker endDatePicker,
                      String varType,
                      Map<String, Boolean> annotationMap,
                      Boolean logScale,
                      Boolean showExcluded,
                      Boolean showGuideSet,
                      Path databasePath) {

        this.instrument = instrumentBox.getSelectionModel().getSelectedItem();
        this.configuration = configurationBox.getSelectionModel().getSelectedItem();
        this.matrix = matrixBox.getSelectionModel().getSelectedItem();
        this.report = reportBox.getSelectionModel().getSelectedItem();
        this.dateRange = dateRangeBox.getSelectionModel().getSelectedItem();

        // TODO - this needs to be handled better.
        if(leveyJenningsButton.isSelected()) {

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

        this.logScale = logScale;
        this.showExcluded = showExcluded;
        this.showGuide = false;

        this.databasePath = databasePath;
    }

    /**
     * All critical report context options are set.
     *
     * @return true if everything has value.
     */
    public boolean validSelection() {

        return !(this.instrument == null &&
                this.configuration == null &&
                this.matrix == null &&
                this.report == null &&
                this.dateRange == null);
    }

    /**
     * Check if new parameters differ in critical report context choices
     *
     * @param newParam Changed user selections
     * @return true if either instrument, configuration, or matrix is different
     */
    public boolean diffReportSelection(Parameters newParam) {

        if(this.validSelection()) {

            return !(this.instrument.equals(newParam.instrument) &&
                    this.configuration.equals(newParam.configuration) &&
                    this.matrix.equals(newParam.matrix));

        } else {

            return true;
        }
    }
}
