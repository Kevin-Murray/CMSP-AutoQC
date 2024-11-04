
package cmsp.quickqc.visualizer.utils.annotations;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Annotation class. Stores instrument event information to be displayed along QC data.
 * Annotations include events such as column changes, instrument maintenance, or other potentially impactful events.
 */
public class Annotation {

    private final String date;
    private final String instrument;
    private final String config;
    private final String matrix;
    private final String type;
    private final String comment;

    final private LinkedHashMap<String, String> annotation;

    /**
     * Annotation constructor.
     * Makes new LinkedHashMap to store all information.
     */
    public Annotation(String date, String instrument, String config, String matrix,
                      String type, String comment) {

        this.date = date;
        this.instrument = instrument;
        this.config = config;
        this.matrix = matrix;
        this.type = type;
        this.comment = comment;

        this.annotation = new LinkedHashMap<>();
        annotation.put("Date", date);
        annotation.put("Instrument", instrument);
        annotation.put("Configuration", config);
        annotation.put("Matrix", matrix);
        annotation.put("Type", type);
        annotation.put("Comment", comment);
    }

    /**
     * Get annotation date.
     */
    public String getDate() {

        return  this.date;
    }

    /**
     * Get annotation instrument.
     */
    public String getInstrument() {

        return this.instrument;
    }

    /**
     * Get annotation configuration.
     */
    public String getConfig() {

        return this.config;
    }

    /**
     * Get annotation matrix.
     */
    public String getMatrix() {

        return this.matrix;
    }

    /**
     * Get annotation type.
     */
    public String getType() {

        return this.type;
    }

    /**
     * Get annotation comment.
     */
    public String getComment() {

        return this.comment;
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

        for(String key : this.getKeySet()) values.add("\"" + this.getValue(key) + "\"");

        return(values);
    }
}
