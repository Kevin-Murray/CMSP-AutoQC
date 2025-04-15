
package cmsp.quickqc.visualizer.datamodel;

import cmsp.quickqc.visualizer.enums.QcDatabaseTypes;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Parameter class that stores all user input selections to control the QC report context.
 */
public class QcParameters {

    public String instrument;
    public String configuration;
    public String matrix;
    public String standard;
    public String report;
    public String selection;
    public String dateRange;
    public String varType; // Variability guides
    public int plotType; // Type of Line chart, e.g. - LeveyJennings
    public LocalDate startDate;
    public LocalDate endDate;
    public Boolean logScale;
    public Boolean showExcluded;
    public Boolean showGuide;
    public Path databasePath;
    public Path reportPath;
    public List<String> logNumbers;
    public Map<String, Boolean> annotationMap;

    /**
     * Null-constructor for parameters class.
     */
    public QcParameters() {

    }

    /**
     * Constructor for parameters class. Formats user input selections or preferences.
     */
    public QcParameters(QcReportContext qcReportContext,
                        ChoiceBox<String> reportBox,
                        ChoiceBox<String> dateRangeBox,
                        List<String> logNumbers,
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

        // Set user input parameters.
        this.instrument = qcReportContext.instrument();
        this.configuration = qcReportContext.config();
        this.matrix = qcReportContext.matrix();
        this.standard = qcReportContext.standard();
        this.report = reportBox.getSelectionModel().getSelectedItem();
        this.dateRange = dateRangeBox.getSelectionModel().getSelectedItem();
        this.logNumbers = logNumbers;

        // User variable selection, expects column headers to be in form: Standard - Variable
        this.selection = standard + " - " + report;

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

        // Variability guides and annotation map assignment
        this.varType = varType;
        this.annotationMap = annotationMap;
        this.startDate = startDatePicker.getValue();
        this.endDate = endDatePicker.getValue();
        this.logScale = logScale;
        this.showExcluded = showExcluded;
        this.showGuide = showGuideSet;
        this.databasePath = databasePath;
        this.reportPath = qcReportContext.database();
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
                this.standard == null &&
                this.report == null &&
                this.dateRange == null);
    }

    /**
     * Check if new parameters differ in critical report context choices
     *
     * @param newParam Changed user selections
     * @return true if either instrument, configuration, or matrix is different
     */
    public boolean diffReportSelection(QcParameters newParam) {

        if(this.validSelection()) {

            return !(this.instrument.equals(newParam.instrument) &&
                    this.configuration.equals(newParam.configuration) &&
                    this.matrix.equals(newParam.matrix));

        } else {

            return true;
        }
    }

    /**
     * Get database file path based on user selected Instrument-Configuration-Matrix combination.
     *
     * @return File path of context-specific database CSV
     */
    public Path getPath() {

        //return parameters.databasePath.resolve(reportMap.get(hash));
        return this.databasePath.resolve(this.reportPath);
    }

    /**
     * Get path of annotation database CSV
     *
     * @return QcAnnotation database CSV path
     */
    public Path getAnnotationPath() {

        // TODO - annotation database path should be handled by enum
        return this.databasePath.resolve(Paths.get(QcDatabaseTypes.ANNOTATION.getFileName()));
    }
}
