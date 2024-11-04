
package cmsp.quickqc.visualizer.utils.annotations;

import java.util.ArrayList;

/**
 * Enumeration of annotation types.
 * Annotations types are descriptors of instrument events, such as replacing a column.
 */
public enum AnnotationTypes {

    STANDARD("New Stock Solution"),
    COLUMN("Column Change"),
    MAINTENANCE("Instrument Maintenance"),
    MOBILE("Mobile Phase Change"),
    TUNE("New Tune / Calibration"),
    OTHER("Other");

    private final String label;

    /**
     * Enum constructor.
     */
    AnnotationTypes(String type) {

        this.label = type;
    }

    /**
     * Get annotation type name.
     */
    public static String getAnnotationType(String type) {

        for(AnnotationTypes i : values()) {

            if(i.label.equals(type)) return i.name();
        }

        return null;
    }

    /**
     * Cast annotation type to string.
     */
    @Override
    public String toString() {

        return this.label;
    }

    /**
     * Get annotation types as string list.
     */
    public static ArrayList<String> getAnnotationTypes() {

        ArrayList<String> values = new ArrayList<>();

        for(AnnotationTypes i : AnnotationTypes.values()) values.add(i.label);

        return values;
    }
}
