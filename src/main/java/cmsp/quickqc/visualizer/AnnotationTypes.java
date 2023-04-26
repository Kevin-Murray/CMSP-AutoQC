package cmsp.quickqc.visualizer;

public enum AnnotationTypes {

    STANDARD("New Stock Solution"),
    COLUMN("Column Change"),
    MAINTENANCE("Instrument Maintenance"),
    MOBILE("Mobile Phase Change"),
    TUNE("New Tune / Calibration"),
    OTHER("Other");

    private final String label;

    AnnotationTypes(String s) {
        this.label = s;
    }

    public static String getAnnotationType(String type) {
        for(AnnotationTypes i : values()){
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
