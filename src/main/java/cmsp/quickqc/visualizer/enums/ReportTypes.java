
package cmsp.quickqc.visualizer.enums;

/**
 * Enum of various report context global values.
 */
public enum ReportTypes {

    DATABASE("Database"),
    INSTRUMENT("Instrument"),
    CONFIGURATION("Configuration"),
    MATRIX("Matrix"),
    STANDARD("Standard"),
    REPORT("Report"),
    RANGE("Range"),
    EXCLUDED("Excluded"),
    DATE("Date"),
    ANNOTATION("Type"),
    GUIDE("Guide"),
    COMMENT("Comment");

    private final String label;

    /**
     * Constructor class.
     *
     * @param label Global label string
     */
    ReportTypes(String label) {

        this.label = label;
    }

    /**
     * Get enum label string.
     *
     * @return String of label.
     */
    public String getLabel() {

        return this.label;
    }
}
