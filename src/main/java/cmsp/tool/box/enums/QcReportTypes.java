
package cmsp.tool.box.enums;

/**
 * Enum of various report context global values.
 */
public enum QcReportTypes {

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
    QcReportTypes(String label) {

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
