package cmsp.quickqc.visualizer.utils.annotations;

public enum AnnotationStyles {

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

    AnnotationStyles(String s) {
        this.label = s;
    }

    public static String getAnnotationName(String type) {
        for(AnnotationStyles i : values()){
            if(i.label.equals(type)){
                return i.name();
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
