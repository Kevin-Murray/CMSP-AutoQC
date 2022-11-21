package com.example.demo;

public enum yAxisScaleTypes {

    LINEAR("Linear"),
    LOG2("Log2"),
    PERCENT("Percent of Mean"),
    DEVIATIONS("Standard Deviations");

    private final String label;

    yAxisScaleTypes(String s) {
        this.label = s;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
