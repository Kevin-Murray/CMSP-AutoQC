package cmsp.quickqc.visualizer.utils;

import cmsp.quickqc.visualizer.parameters.*;
import cmsp.quickqc.visualizer.utils.annotations.Annotation;

import java.time.LocalDate;

public class ReportFilteringUtils {

    public static boolean isWithinDateRange(LocalDate testDate, LocalDate startDate, LocalDate endDate) {

        if(startDate == null && endDate == null) {
            return true;
        } else {
            return !(testDate.isBefore(startDate) || testDate.isAfter(endDate));
        }
    }

    public static boolean isShowable(Boolean showExcluded, Boolean exclude){

        return !showExcluded ? !exclude : showExcluded;
    }

    public static boolean filteredAnnotation(Parameters parameters, Annotation annotation){

        return (parameters.instrument.equals(annotation.getInstrument()) &&
                (parameters.configuration.equals(annotation.getConfig()) || annotation.getConfig().equals("All")) &&
                (parameters.matrix.equals(annotation.getMatrix()) || annotation.getMatrix().equals("All")) &&
                (parameters.annotationMap.get(annotation.getType())));

    }

}
