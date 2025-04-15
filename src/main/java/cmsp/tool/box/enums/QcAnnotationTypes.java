
package cmsp.tool.box.enums;

import java.util.ArrayList;

/**
 * Enumeration of annotation types.
 * Annotations types are descriptors of instrument events, such as replacing a column.
 */
public enum QcAnnotationTypes {

    STANDARD("New Stock Solution"),
    COLUMN("Column Change"),
    MOBILE("Mobile Phase Change"),
    TUNE("New Tune / Calibration"),
    PART("New Part / Consumable"),
    MAINTENANCE("Instrument Maintenance"),
    OTHER("Other");

    private final String label;

    /**
     * Enum constructor.
     */
    QcAnnotationTypes(String type) {

        this.label = type;
    }

    /**
     * Get annotation type name.
     */
    public static String getAnnotationType(String type) {

        for(QcAnnotationTypes i : values()) {

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

        for(QcAnnotationTypes i : QcAnnotationTypes.values()) values.add(i.label);

        return values;
    }
}
