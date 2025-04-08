package cmsp.quickqc.visualizer.datamodel;

/**
 * Information Class to store output of Skyline Molecule Quantification export.
 */
public class TargetedMoleculeEntry {

    // Values imported from Skyline Export.
    private String list;
    private String name;
    private String formula;
    private String cas;
    private Double retentionTime;
    private String standardType;
    private String normalizationMethod;
    private Double standardConcentration;
    private Double concentrationMultiplier;
    private String note;
    private String calibrationCurve;
    private Double calibrationFit;

    // Computed values.
    private String unit;
    private Boolean internalStandard;
    private Double limitOfDetection;
    private Double lowerLimitOfQuant;
    private Double upperLimitOfQuant;

    /**
     * Constructor class. Matches header name to variable parameter.
     *
     * @param header Array of header strings from external export
     * @param entry  Array of values strings from external export
     */
    public TargetedMoleculeEntry(String[] header, String[] entry) {

        // Loop through array and match each result with corresponding variable.
        for(int i = 0; i < header.length; i++) {

            switch (header[i]) {
                case "Molecule List":
                    list = entry[i];
                    internalStandard = entry[i].toLowerCase().matches("internal standard|internal standards|istd|istds|standard|standards");
                    break;
                case "Molecule":
                    name = entry[i];
                    break;
                case "Molecule Formula":
                    formula = entry[i];
                    break;
                case "CAS":
                    cas = entry[i];
                    break;
                case "Average Measured Retention Time":
                    retentionTime = Double.parseDouble(entry[i]);
                    break;
                case "Standard Type":
                    standardType = entry[i];
                    break;
                case "Normalization Method":
                    normalizationMethod = entry[i];
                    break;
                case "Internal Standard Concentration":
                    standardConcentration = (entry[i].isEmpty()) ? null : Double.parseDouble(entry[i]);
                    break;
                case "Concentration Multiplier":
                    // If user did not specify concentration multiplier in Skyline, software default is 1.0x
                    concentrationMultiplier = (entry[i].isEmpty()) ? 1.0 : Double.parseDouble(entry[i]);
                    break;
                case "Molecule Note":
                    note = entry[i];
                    break;
                case "Calibration Curve":
                    calibrationCurve = entry[i];
                    break;
                case "R Squared":
                    calibrationFit = (entry[i].equals("#N/A")) ? null : Double.parseDouble(String.format("%.3f", Double.parseDouble(entry[i])));
            }
        }

        limitOfDetection = null;
        lowerLimitOfQuant = null;
        upperLimitOfQuant = null;
    }

    public String getList() {
        return list;
    }

    public String getName() {
        return name;
    }

    public String getFormula() {
        return formula;
    }

    public String getCas() {
        return cas;
    }

    public Double getRetentionTime() {
        return retentionTime;
    }

    public String getStandardType() {
        return standardType;
    }

    public String getNormalizationMethod() {
        return normalizationMethod;
    }

    public Double getStandardConcentration() {
        return standardConcentration;
    }

    public Double getConcentrationMultiplier() {
        return concentrationMultiplier;
    }

    public void setConcentrationMultiplier(Double value) {
        concentrationMultiplier = value;
    }

    public String getNote() {
        return note;
    }

    public String getCalibrationCurve() {
        return calibrationCurve;
    }

    public void setCalibrationCurve(String value) {
        calibrationCurve = value;
    }

    public Double getCalibrationFit() {
        return calibrationFit;
    }

    public void setCalibrationFit(Double value) {
        calibrationFit = value;
    }

    public Double getLimitOfDetection() {
        return limitOfDetection;
    }

    public void setLimitOfDetection(Double limitOfDetection) {
        this.limitOfDetection = limitOfDetection;
    }

    public Double getLowerLimitOfQuant() {
        return lowerLimitOfQuant;
    }

    public void setLowerLimitOfQuant(Double lowerLimitOfQuant) {
        this.lowerLimitOfQuant = lowerLimitOfQuant;
    }

    public Double getUpperLimitOfQuant() {
        return upperLimitOfQuant;
    }

    public void setUpperLimitOfQuant(Double upperLimitOfQuant) {
        this.upperLimitOfQuant = upperLimitOfQuant;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Boolean getInternalStandard() {
        return internalStandard;
    }
}
