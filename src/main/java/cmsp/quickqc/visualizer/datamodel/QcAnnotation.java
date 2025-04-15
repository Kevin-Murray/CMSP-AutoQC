
package cmsp.quickqc.visualizer.datamodel;

import cmsp.quickqc.visualizer.enums.QcReportTypes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * QcAnnotation class. Stores instrument event information to be displayed along QC data.
 * Annotations include events such as column changes, instrument maintenance, or other potentially impactful events.
 */
public class QcAnnotation {

    final private LinkedHashMap<String, String> annotation;

    /**
     * QcAnnotation constructor.
     * Makes new LinkedHashMap to store all information.
     */
    public QcAnnotation(String date, String instrument, String config, String matrix,
                        String type, String comment) {

        this.annotation = new LinkedHashMap<>();

        annotation.put(QcReportTypes.DATE.getLabel(), date);
        annotation.put(QcReportTypes.INSTRUMENT.getLabel(), instrument);
        annotation.put(QcReportTypes.CONFIGURATION.getLabel(), config);
        annotation.put(QcReportTypes.MATRIX.getLabel(), matrix);
        annotation.put(QcReportTypes.ANNOTATION.getLabel(), type);
        annotation.put(QcReportTypes.COMMENT.getLabel(), comment);
    }

    /**
     * Get annotation date.
     */
    public String getDate() {

        return this.annotation.get(QcReportTypes.DATE.getLabel());
    }

    /**
     * Get annotation instrument.
     */
    public String getInstrument() {

        return this.annotation.get(QcReportTypes.INSTRUMENT.getLabel());
    }

    /**
     * Get annotation configuration.
     */
    public String getConfig() {

        return this.annotation.get(QcReportTypes.CONFIGURATION.getLabel());
    }

    /**
     * Get annotation matrix.
     */
    public String getMatrix() {

        return this.annotation.get(QcReportTypes.MATRIX.getLabel());
    }

    /**
     * Get annotation type.
     */
    public String getType() {

        return this.annotation.get(QcReportTypes.ANNOTATION.getLabel());
    }

    /**
     * Get annotation comment.
     */
    public String getComment() {

        return this.annotation.get(QcReportTypes.COMMENT.getLabel());
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
