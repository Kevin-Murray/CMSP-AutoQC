package cmsp.quickqc.visualizer.enums;

/**
 * Enumerator class to handle replicate detail export from Skyline.
 * Export includes five columns. Enum handles import names, assigned variable name, and final table names.
 */
public enum TargetedDetailsTypes {

    NAME("name", "Replicate", "Sample Name"),
    TYPE("type", "Sample Type", "Sample Type"),
    DILF("dilutionFactor", "Sample Dilution Factor", "Dilution Factor"),
    TIME("acqTime", "0 Acquired Time", "Acquired Time"),
    FILE("fileName", "0 File Name", "File Name");

    private final String varName;
    private final String importName;
    private final String tableName;

    /**
     * Constructor class.
     *
     * @param varName    Variable name in TargetedDetailEntry class
     * @param importName Column name in Skyline export
     * @param tableName  Exported column name of variable
     */
    TargetedDetailsTypes(String varName, String importName, String tableName) {
        this.varName = varName;
        this.importName = importName;
        this.tableName = tableName;
    }

    public String getVarName() {
        return varName;
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
    public static TargetedDetailsTypes fromImportName(String header) {

        for (TargetedDetailsTypes type : TargetedDetailsTypes.values()) {
            if (type.getImportName().equals(header)) {
                return type;
            }
        }
        return null;
    }
}
