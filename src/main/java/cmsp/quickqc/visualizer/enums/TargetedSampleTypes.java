package cmsp.quickqc.visualizer.enums;

/**
 * Enum of sample type abbreviations and their full names.
 */
public enum TargetedSampleTypes {

    UNKNOWN("Unknown", "S"),
    QC("Quality Control", "QC"),
    STANDARD("Standard", "Std"),
    SOLVENT("Solvent", "Solv"),
    BLANK("Blank", "B"),
    DBLBLANK("Double Blank", "DB");

    private final String label;
    private final String abrv;

    /**
     * Constructor class.
     *
     * @param label Sample types
     * @param abrv Sample type abbreviations
     */
    TargetedSampleTypes(String label, String abrv) {
        this.label = label;
        this.abrv = abrv;
    }

    public String getLabel() {
        return this.label;
    }

    public String getAbrv() {
        return this.abrv;
    }

    /**
     * Get Enum type abbreviation from sample type
     *
     * @param code String of sample type
     * @return String of type abbreviation
     */
    public static String getEnumByString(String code){
        for(TargetedSampleTypes e : TargetedSampleTypes.values()){
            if(e.label.equals(code)) return e.getAbrv();
        }
        return null;
    }
}
