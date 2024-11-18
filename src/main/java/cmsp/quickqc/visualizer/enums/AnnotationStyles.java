
package cmsp.quickqc.visualizer.enums;

/**
 * Enumeration of annotation styles for QC plot.
 */
public enum AnnotationStyles {

    //TODO - Make into Annotation colors only. Move point style elsewhere.

    STANDARD("""
            -fx-background-color: #1B9E77, white;
             -fx-background-insets: 0, 4;
             -fx-background-radius: 10px;
             -fx-padding: 10px;"""),
    COLUMN("""
            -fx-background-color: #D95F02, white;
             -fx-background-insets: 0, 4;
             -fx-background-radius: 10px;
             -fx-padding: 10px;"""),
    MAINTENANCE("""
            -fx-background-color: #C8A2C8, white;
             -fx-background-insets: 0, 4;
             -fx-background-radius: 10px;
             -fx-padding: 10px;"""),
    MOBILE("""
            -fx-background-color: #A6CAEC, white;
             -fx-background-insets: 0, 4;
             -fx-background-radius: 10px;
             -fx-padding: 10px;"""),
    TUNE("""
            -fx-background-color: #E7298A, white;
             -fx-background-insets: 0, 4;
             -fx-background-radius: 10px;
             -fx-padding: 10px;"""),
    OTHER("""
            -fx-background-color: #E6AB02, white;
             -fx-background-insets: 0, 4;
             -fx-background-radius: 10px;
             -fx-padding: 10px;""");

    private final String label;

    /**
     * Enum constructor
     */
    AnnotationStyles(String style) {

        this.label = style;
    }

    /**
     * Cast label to string.
     */
    @Override
    public String toString() {

        return this.label;
    }
}
