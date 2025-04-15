
package cmsp.tool.box.enums;

/**
 * Database type file names.
 */
public enum QcDatabaseTypes {

    REPORT("CMSP_QC_ReportContexts.csv"),
    ANNOTATION("CMSP_QC_Annotations.csv");

    private final String fileName;

    /**
     * Constructor method
     *
     * @param fileName Filename as string
     */
    QcDatabaseTypes(String fileName) {

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
