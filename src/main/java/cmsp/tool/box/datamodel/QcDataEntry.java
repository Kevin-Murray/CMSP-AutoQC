
package cmsp.tool.box.datamodel;

import java.util.*;

import static cmsp.tool.box.utils.MathUtils.log2;

/**
 * QC data class. Stores all tabular entries as a LinkedHashMap.
 * For plotting purposes, Annotations may be stored as QcDataEntry
 */
public class QcDataEntry {

    final private LinkedHashMap<String, String> entries;
    final private String type;

    /**
     * Constructor method.
     *
     * @param header Column headers from report table
     * @param entry Variable values that user intends to monitor
     * @param type Indicator whether entry is faux-annotation entry
     */
    public QcDataEntry(String[] header, String[] entry, String type) {

        this.entries = new LinkedHashMap<>();
        addItems(header, entry);

        this.type = type;
    }

    /**
     * Put each value into designated bucket.
     *
     * @param header Column names from QC report
     * @param items Variable values
     */
    public void addItems(String[] header, String[] items) {

        for(int i = 0; i < items.length; i++) {

            entries.put(header[i], items[i]);
        }
    }

    /**
     * Replace value at key with new value.
     *
     * @param key Column name key
     * @param value New value
     */
    public void replaceItem(String key, String value) {

        entries.put(key, value);
    }

    /**
     * Get date of entry acquisition.
     *
     * @return Date as string
     */
    public String getDate() {

        return entries.get("Date");
    }

    /**
     * Is QcDataEntry marked as excluded from the QC line chart.
     *
     * @return Boolean exclusion status
     */
    public Boolean isExcluded() {

        return entries.get("Show").equals("Exclude");
    }

    /**
     * Get value at key as Double.
     *
     * @param key Column header key
     * @return Double of value string
     */
    public Double getItem(String key) {

        return Double.parseDouble(entries.get(key));
    }

    /**
     * Get value at key as Log2 transformed Double.
     *
     * @param key Column header key
     * @param logScale Boolean logScale transformation indicator
     * @return Double of log2 transformed value string
     */
    public Double getItem(String key, Boolean logScale) {

        return (logScale) ? log2(this.getItem(key)) : this.getItem(key);
    }

    /**
     * Get value at key as string
     *
     * @param key Column header key
     * @return String of value
     */
    public String getValue(String key) {

        return entries.get(key);
    }

    /**
     * Get column header keys as Set
     *
     * @return Set of column header strings
     */
    public Set<String> getKeySet() {

        return entries.keySet();
    }

    /**
     * Size of this QcDataEntry LinkedHashSet variable
     *
     * @return int this LinkedHashSet size
     */
    public int size(){
        return this.entries.size();
    }

    /**
     *  Change QcDataEntry exclusion status to opposite status
     */
    public void changeExclusionStatus() {

        String exclusion = this.entries.get("Show");

        if(exclusion.equals("Include")) {

            this.entries.put("Show", "Exclude");

        } else {

            this.entries.put("Show", "Include");
        }
    }

    /**
     * Change QcDataEntry guide set status to opposite status
     */
    public void changeGuideStatus() {

        String exclusion = this.entries.get("Guide");

        // This is a little wonky because all entries are stored as strings.
        if(exclusion.equals("TRUE")) {

            this.entries.put("Guide", "FALSE");

        } else {

            this.entries.put("Guide", "TRUE");
        }
    }

    /**
     * Set QcDataEntry comment
     *
     * @param comment User input comment
     */
    public void setComment(String comment) {

        this.entries.put("Comment", comment.replace("\n", "\\n"));
    }

    /**
     * Get QcDataEntry comment
     *
     * @return Comment string
     */
    public String getComment() {

        return(this.entries.get("Comment").replace("\\n", "\n"));
    }

    /**
     * Get QcDataEntry experimental log number
     *
     * @return Log number string
     */
    public String getLogNumber() {

        return this.entries.get("Log");
    }

    /**
     * Write as QcDataEntry values as string wrapped with quotation marks for database writing.
     *
     * @return List of strings of each value in quotation marks
     */
    public List<String> writeValues() {

        List<String> values = new ArrayList<>();
        Set<String> keys = this.getKeySet();

        for(String key : keys) {

            values.add("\"" + this.getValue(key) + "\"");
        }

        return(values);
    }

    /**
     * Get type of QcDataEntry - QC report value or annotation type.
     *
     * @return String of entry type
     */
    public String getType() {

        return this.type;
    }

    /**
     * Is QcDataEntry an annotation or QC report value.
     *
     * @return Boolean true if annotation
     */
    public Boolean isAnnotation() {

        return !this.type.equals("Sample");
    }

    /**
     * Is QcDataEntry part of report guide set.
     *
     * @return Boolean true if part of guide set
     */
    public Boolean isGuide() {

        return Boolean.valueOf(this.entries.get("Guide"));
    }
}
