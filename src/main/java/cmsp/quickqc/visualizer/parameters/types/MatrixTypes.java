
package cmsp.quickqc.visualizer.parameters.types;

/**
 * Enumeration of QC report context matrix types.
 * Matrix types are standard mix that is monitored in QC report.
 * TODO - change matrix to standard mix-related name.
 */
public enum MatrixTypes {

    FUSION(new String[] {"HeLa Protein Digest", "iRT Standard Mix"}),
    ECLIPSE(new String[] {"HeLa Protein Digest", "iRT Standard Mix"}),
    QEXACTIVE(new String[] {"NIST Plasma"}),
    A6495C(new String[] {"iRT Mix - MRM"}),
    A7200(new String[] {"SST (SCFA)", "SST (Bleed)"}),
    SCIEX5500(new String[] {"BSA Digest - Sciex"}),
    SCIEX6500(new String[] {"BSA Digest - Sciex"});

    private final String[] label;

    /**
     * Constructor class for enum.
     */
    MatrixTypes(String[] matrices) {

        this.label = matrices;
    }

    /**
     * Get matrices from input instrument.
     */
    public static String[] getMatrix(String instrument) {

        for(MatrixTypes i : values()) {

            if(i.name().equals(instrument)) return i.label;
        }

        return null;
    }
}
