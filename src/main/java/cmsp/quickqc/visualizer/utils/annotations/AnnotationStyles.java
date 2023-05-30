package cmsp.quickqc.visualizer.utils.annotations;

public enum AnnotationStyles {

    STANDARD("-fx-background-color: #1B9E77, white;\n"
            + " -fx-background-insets: 0, 4;\n"
            + " -fx-background-radius: 10px;\n"
            + " -fx-padding: 10px;"),
    COLUMN("-fx-background-color: #D95F02, white;\n"
            + " -fx-background-insets: 0, 4;\n"
            + " -fx-background-radius: 10px;\n"
            + " -fx-padding: 10px;"),
    MAINTENANCE("-fx-background-color: #7570B3, white;\n"
            + " -fx-background-insets: 0, 4;\n"
            + " -fx-background-radius: 10px;\n"
            + " -fx-padding: 10px;"),
    MOBILE("-fx-background-color: #E7298A, white;\n"
            + " -fx-background-insets: 0, 4;\n"
            + " -fx-background-radius: 10px;\n"
            + " -fx-padding: 10px;"),
    TUNE("-fx-background-color: #A6761D, white;\n"
            + " -fx-background-insets: 0, 4;\n"
            + " -fx-background-radius: 10px;\n"
            + " -fx-padding: 10px;"),
    OTHER("-fx-background-color: #E6AB02, white;\n"
            + " -fx-background-insets: 0, 4;\n"
            + " -fx-background-radius: 10px;\n"
            + " -fx-padding: 10px;");

    private final String label;

    AnnotationStyles(String s) {
        this.label = s;
    }

    public static String getAnnotationStyle(String type) {
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
