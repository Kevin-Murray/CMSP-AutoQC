
package cmsp.tool.box.enums;

/**
 * Variability guides enum. Types of variability measures that may be used to assess process control.
 */
public enum QcVariabilityTypes {

    STD("Standard Deviations"),
    RSD("Relative Std. Dev. (RSD)"),
    CUSTOM("Custom...");

    private final String label;

    /**
     * Constructor class
     *
     * @param label String label
     */
    QcVariabilityTypes(String label) {

        this.label = label;
    }

    /**
     * Get enum label for enum constant.
     *
     * @return String label of variability type
     */
    public String getLabel() {

        return this.label;
    }
}
