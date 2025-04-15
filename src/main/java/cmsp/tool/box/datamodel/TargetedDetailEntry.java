package cmsp.tool.box.datamodel;

import cmsp.tool.box.enums.TargetedDetailsTypes;

import java.util.Objects;

/**
 * Information Class to store output of Skyline Sample Details export.
 */
public class TargetedDetailEntry {

    private String name;
    private String type;
    private String dilutionFactor;
    private String acqTime;
    private String fileName;

    /**
     * Constructor class. Matches header name to variable parameter.
     *
     * @param header Array of header strings from external export
     * @param entry  Array of values strings from external export
     */
    public TargetedDetailEntry(String[] header, String[] entry) {

        // Loop through array and match each result with corresponding variable.
        for (int i = 0; i < header.length; i++) {

            switch (Objects.requireNonNull(TargetedDetailsTypes.fromImportName(header[i]))) {
                case NAME:
                    name = entry[i];
                    break;
                case TYPE:
                    type = entry[i];
                    break;
                case DILF:
                    dilutionFactor = entry[i];
                    break;
                case TIME:
                    acqTime = entry[i];
                    break;
                case FILE:
                    fileName = entry[i];
                    break;
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDilutionFactor() {
        return dilutionFactor;
    }

    public String getAcqTime() {
        return acqTime;
    }

    public String getFileName() {
        return fileName;
    }
}
