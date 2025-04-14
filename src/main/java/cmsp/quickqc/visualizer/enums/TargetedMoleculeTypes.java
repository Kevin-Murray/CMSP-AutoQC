package cmsp.quickqc.visualizer.enums;

/**
 * Enumerator class to handle molecule quantification export from Skyline.
 * Export includes 13 columns. Three compute variables are created to summarize analytical constraints.
 */
public enum TargetedMoleculeTypes {

    NAME("name", "Molecule", "Molecule"),
    UNIT("unit", "NOT EXPORTED", "Units"),
    LIST("list", "Molecule List", "Classification"),
    FORMULA("formula", "Molecule Formula", "Formula"),
    CAS("cas", "CAS", "CAS"),
    RT("retentionTime", "Average Measured Retention Time", "Retention Time"),
    TYPE("standardType", "Standard Type", "Standard Type"),
    STDCONC("standardConcentration", "Internal Standard Concentration", "Internal Standard Concentration"),
    NORM("normalizationMethod", "Normalization Method", "Normalization Method"),
    MULT("concentrationMultiplier", "Concentration Multiplier", "Concentration Multiplier"),
    NOTE("note", "Molecule Note", "Molecule Note"),
    CURVE("calibrationCurve", "Calibration Curve", "Calibration Curve"),
    R2("calibrationFit", "R Squared", "Calibration Fit (R2)"),
    LOD("limitOfDetection", "NOT EXPORTED", "Limit of Detection (LOD)"),
    LLOQ("lowerLimitOfQuant", "NOT EXPORTED", "Lower Limit of Quantitation (LLOQ)"),
    ULOQ("upperLimitOfQuant", "NOT EXPORTED", "Upper Limit of Quantitation (ULOQ)");

    private final String variableName;
    private final String importName;
    private final String tableName;

    /**
     * Constructor class.
     * @param variableName Name of variable in TargetedMoleculeEntry class
     * @param importName Column name in Skyline export
     * @param tableName Name for display in TableView and Excel export
     */
    TargetedMoleculeTypes(String variableName, String importName, String tableName) {

        this.variableName = variableName;
        this.importName = importName;
        this.tableName = tableName;
    }

    public String getVariableName() {
        return variableName;
    }

    public String getImportName() {
        return importName;
    }

    public String getTableName() {
        return tableName;
    }

    /**
     * Get Enum type from export header string. Match by import name field.
     *
     * @param header String from Skyline Sample Details Export.
     * @return Enum type of match.
     */
    public static TargetedMoleculeTypes fromImportName(String header) {

        for (TargetedMoleculeTypes type : TargetedMoleculeTypes.values()) {
            if (type.getImportName().equals(header)) {
                return type;
            }
        }
        return null;
    }
}
