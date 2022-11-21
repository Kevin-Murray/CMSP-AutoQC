package com.example.demo;

public enum DateRangeType {

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

    DateRangeType(String s, int n) {
        this.label = s;
        this.range = n;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public static int getDateRange(String dateRange) {

        for(DateRangeType i : values()){
            if(i.label.equals(dateRange)){
                return i.range;
            }
        }

        return 0;
    }
}
