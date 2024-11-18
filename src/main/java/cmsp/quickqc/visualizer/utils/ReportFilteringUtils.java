
package cmsp.quickqc.visualizer.utils;

import cmsp.quickqc.visualizer.datamodel.Parameters;
import cmsp.quickqc.visualizer.datamodel.Annotation;
import java.time.LocalDate;
import java.util.List;

/**
 * Utility class for static methods for filtering the mainTask data entry lists.
 */
public class ReportFilteringUtils {

    /**
     * Check if date is within range.
     *
     * @return true if date within range.
     */
    public static boolean isWithinDateRange(LocalDate testDate, LocalDate startDate, LocalDate endDate) {

        // Return true if start and end date unspecified.
        if(startDate == null && endDate == null) {

            return true;

        } else {

            return !(testDate.isBefore(startDate) || testDate.isAfter(endDate));
        }
    }

    /**
     * Check if the plot is parameterized to show this point if it is marked excluded.
     *
     * @return true is the point is not marked as excluded or the plot is currently showing excluded points
     */
    public static boolean isShowable(Boolean showExcluded, Boolean exclude) {

        return showExcluded || !exclude;
    }

    /**
     * Check if DataEntry log number is in user monitored log number list.
     * If no log numbers monitored, return true.
     */
    public static boolean isMonitoredLogNumber(String log, List<String> logNumbers) {

        return logNumbers == null || logNumbers.contains(log);
    }

    public static boolean isGuideSet(Boolean showGuide, Boolean isGuide) {

        return showGuide && isGuide;
    }

    /**
     * Filter annotations by current report context and line chart context menu settings.
     *
     * @param parameters Main page parameters
     * @param annotation Input annotation object
     * @return true if annotation matches current context
     */
    public static boolean filteredAnnotation(Parameters parameters, Annotation annotation){

        return (parameters.instrument.equals(annotation.getInstrument()) &&
                (parameters.configuration.equals(annotation.getConfig()) || annotation.getConfig().equals("All")) &&
                (parameters.matrix.equals(annotation.getMatrix()) || annotation.getMatrix().equals("All")) &&
                (parameters.annotationMap.get(annotation.getType())));
    }
}
