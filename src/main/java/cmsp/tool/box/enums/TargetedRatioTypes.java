package cmsp.tool.box.enums;

/**
 * Enumerator class to handle ratio export from Skyline.
 * Export includes nine columns.
 */
public enum TargetedRatioTypes {

    MOLECULE( "Molecule"),
    LIST("Molecule List"),
    SAMPLE("Replicate"),
    TYPE("Sample Type"),
    DILF("Sample Dilution Factor"),
    CONC("Analyte Concentration"),
    QUANT("Quantification"),
    ACCURACY("Accuracy"),
    EXCLUDE("Exclude From Calibration");

    private final String  importName;

    /**
     * Constructor class.
     * @param importName Column name in Skyline export
     */
    TargetedRatioTypes(String importName) {
        this.importName = importName;
    }

    public String getImportName() {
        return importName;
    }

    /**
     * Get Enum type from export header string. Match by import name field.
     *
     * @param header String from Skyline Sample Details Export.
     * @return Enum type of match.
     */
    public static TargetedRatioTypes fromImportName(String header) {

        for (TargetedRatioTypes type : TargetedRatioTypes.values()) {
            if (type.getImportName().equals(header)) {
                return type;
            }
        }
        return null;
    }
}
