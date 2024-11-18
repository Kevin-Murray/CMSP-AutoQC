
package cmsp.quickqc.visualizer.datamodel;

import cmsp.quickqc.visualizer.enums.ReportTypes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Annotation class. Stores instrument event information to be displayed along QC data.
 * Annotations include events such as column changes, instrument maintenance, or other potentially impactful events.
 */
public class Annotation {

    final private LinkedHashMap<String, String> annotation;

    /**
     * Annotation constructor.
     * Makes new LinkedHashMap to store all information.
     */
    public Annotation(String date, String instrument, String config, String matrix,
                      String type, String comment) {

        this.annotation = new LinkedHashMap<>();

        annotation.put(ReportTypes.DATE.getLabel(), date);
        annotation.put(ReportTypes.INSTRUMENT.getLabel(), instrument);
        annotation.put(ReportTypes.CONFIGURATION.getLabel(), config);
        annotation.put(ReportTypes.MATRIX.getLabel(), matrix);
        annotation.put(ReportTypes.ANNOTATION.getLabel(), type);
        annotation.put(ReportTypes.COMMENT.getLabel(), comment);
    }

    /**
     * Get annotation date.
     */
    public String getDate() {

        return this.annotation.get(ReportTypes.DATE.getLabel());
    }

    /**
     * Get annotation instrument.
     */
    public String getInstrument() {

        return this.annotation.get(ReportTypes.INSTRUMENT.getLabel());
    }

    /**
     * Get annotation configuration.
     */
    public String getConfig() {

        return this.annotation.get(ReportTypes.CONFIGURATION.getLabel());
    }

    /**
     * Get annotation matrix.
     */
    public String getMatrix() {

        return this.annotation.get(ReportTypes.MATRIX.getLabel());
    }

    /**
     * Get annotation type.
     */
    public String getType() {

        return this.annotation.get(ReportTypes.ANNOTATION.getLabel());
    }

    /**
     * Get annotation comment.
     */
    public String getComment() {

        return this.annotation.get(ReportTypes.COMMENT.getLabel());
    }

    /**
     * Get size of annotation hashmap.
     */
    public int size() {

        return this.annotation.size();
    }

    /**
     * Get key set of annotation hash map.
     */
    public Set<String> getKeySet() {

        return annotation.keySet();
    }

    /**
     * Get value of annotation hashmap at key.
     */
    public String getValue(String key) {

        return annotation.get(key);
    }

    /**
     * Write annotation to string list.
     */
    public List<String> writeValues() {

        List<String> values = new ArrayList<>();

        for (String key : this.getKeySet()) values.add("\"" + this.getValue(key) + "\"");

        return (values);
    }
}
