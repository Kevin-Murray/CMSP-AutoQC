package cmsp.quickqc.visualizer.datamodel;

/**
 * Information Class to store output of Skyline Ratio Results export.
 */
public class TargetedRatioEntry {

    private String name;
    private String list;
    private String sampleName;
    private String sampleType;
    private String unit;
    private Double dilutionFactor;
    private Double expectedConcentration;
    private Double measuredConcentration;
    private Double accuracy;
    private Boolean excludedFromCalibration;
    private Boolean internalStandard;

    /**
     * Constructor class. Matches header name to variable parameter.
     *
     * @param header Array of header strings from external export
     * @param entry  Array of values strings from external export
     */
    public TargetedRatioEntry(String[] header, String[] entry) {

        // Loop through each header and assign entry to appropriate variable.
        // Compute context specific variables as needed.
        for (int i = 0; i < header.length; i++) {

            switch (header[i]) {

                case ("Molecule"):
                    name = entry[i];
                    break;
                case ("Molecule List"):
                    list = entry[i];
                    internalStandard = entry[i].toLowerCase().matches("internal standard|internal standards|istd|istds|standard|standards");
                    break;
                case ("Replicate"):
                    sampleName = entry[i];
                    break;
                case ("Sample Type"):
                    sampleType = entry[i];
                    break;
                case ("Sample Dilution Factor"):
                    dilutionFactor = Double.parseDouble(entry[i]);
                    break;
                case ("Analyte Concentration"):
                    expectedConcentration = (entry[i].isEmpty()) ? null : Double.parseDouble(entry[i]);
                    break;
                case ("Quantification"):
                    // Quantification value is stored as "Value <Space> Unit". Parse by space and store value and units.
                    measuredConcentration = (entry[i].equals("#N/A")) ? null : Double.parseDouble(entry[i].split(" ")[0]);
                    unit = (measuredConcentration == null) ? null : entry[i].split(" ", 2)[1];
                    break;
                case ("Accuracy"):
                    // Remove percent sign, replace NA entries with null
                    accuracy = (entry[i].equals("#N/A")) ? null : Double.parseDouble(entry[i].split("%")[0]);
                    break;
                case ("Exclude From Calibration"):
                    excludedFromCalibration = Boolean.parseBoolean(entry[i]);
                    break;
            }
        }
    }

    public String getMoleculeName() {
        return name;
    }

    public String getMoleculeList() {
        return list;
    }

    public String getSampleName() {
        return sampleName;
    }

    public String getSampleType() {
        return sampleType;
    }

    public Double getDilutionFactor() {
        return dilutionFactor;
    }

    public Double getExpectedConcentration() {
        return expectedConcentration;
    }

    public void setExpectedConcentration(Double value) {
        expectedConcentration = value;
    }

    public Double getMeasuredConcentration() {
        return measuredConcentration;
    }

    public String getUnit() {
        return unit;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public Boolean getExcludedFromCalibration() {
        return excludedFromCalibration;
    }

    public Boolean getInternalStandard() {
        return internalStandard;
    }
}
