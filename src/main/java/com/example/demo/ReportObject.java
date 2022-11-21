package com.example.demo;

import java.util.HashMap;

public class ReportObject {

    private String matrix;

    private static HashMap<String, String[]> reportMap = new HashMap<>();

    static {
        reportMap.put("HeLa Protein Digest",
                new String[] {"Protein Counts", "Peptide Counts", "PSM Counts"});
        reportMap.put("iRT Standard Mix",
                new String[] {"Area", "RT"});
    }


    ReportObject(String matrix){

        this.matrix = matrix;
    }

    public String[] getReports(){

        return reportMap.get(matrix);
    }
}
