
package cmsp.quickqc.visualizer.enums;

/**
 * Database type file names.
 */
public enum DatabaseTypes {

    REPORT("CMSP_QC_ReportContexts.csv"),
    ANNOTATION("CMSP_QC_Annotations.csv");

    private final String fileName;

    /**
     * Constructor method
     *
     * @param fileName Filename as string
     */
    DatabaseTypes (String fileName) {

        this.fileName = fileName;
    }

    /**
     * Get file name string.
     *
     * @return String of file name
     */
    public String getFileName() {

        return  this.fileName;
    }
}
