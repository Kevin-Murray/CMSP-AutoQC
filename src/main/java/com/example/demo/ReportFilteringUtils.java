package com.example.demo;

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

}
