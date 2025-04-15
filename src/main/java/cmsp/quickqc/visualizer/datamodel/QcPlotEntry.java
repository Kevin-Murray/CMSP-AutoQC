package cmsp.quickqc.visualizer.datamodel;

/**
 * Line Chart data point class.
 * Stores X-value (String date) and Y-Value (String to be cast to Double)
 */
public class QcPlotEntry {

    private final String field;
    private final String value;

    /**
     * Constructor class
     * @param field Date string
     * @param value Value string that is a numerical value
     */
    public QcPlotEntry(String field, String value){

        this.field = field;
        this.value = value;
    }

    /**
     * Get entry date field.
     *
     * @return String of field
     */
    public String getField(){
        return this.field;
    }

    /**
     * Get value numeric string
     *
     * @return String of value
     */
    public String getValue(){
        return this.value;
    }
}
