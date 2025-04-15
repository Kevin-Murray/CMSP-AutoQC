
package cmsp.quickqc.visualizer.enums;

import java.util.ArrayList;

/**
 * Enumeration of QC report context date range types.
 * Date ranges correspond to specific number of day since now.
 */
public enum QcDateRangeTypes {

    ALL("All Dates", 0),
    LAST7("Last 7 Days", 7),
    LAST15("Last 15 Days", 15),
    LAST30("Last 30 Days", 30),
    LAST90("Last 90 Days", 90),
    LAST180("Last 180 Days", 180),
    LAST365("Last 365 Days", 365),
    CUSTOM("Custom Date Range", -1);

    private final String label;
    private final int range;

    /**
     * Constructor for enum.
     */
    QcDateRangeTypes(String rangeName, int daysInt) {

        this.label = rangeName;
        this.range = daysInt;
    }

    /**
     * Cast label to string.
     */
    @Override
    public String toString() {
        return this.label;
    }

    /**
     * Get date range integer for date range.
     *
     * @return Number of days integer
     */
    public static int getDateRange(String dateRange) {

        for(QcDateRangeTypes i : values()) {

            if(i.label.equals(dateRange)) return i.range;
        }

        return 0;
    }

    /**
     * Get labels of enum class.
     */
    public static ArrayList<String> getDateRangeNames() {

        ArrayList<String> values = new ArrayList<>();

        for(QcDateRangeTypes i : QcDateRangeTypes.values()) values.add(i.label);

        return values;
    }
}
