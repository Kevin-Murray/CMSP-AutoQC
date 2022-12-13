package cmsp.autoqc.visualizer;

import java.util.HashMap;

public class ReportObject {

    private String matrix;

    private static HashMap<String, String[]> reportMap = new HashMap<>();

    static {
        reportMap.put("HeLa Protein Digest",
                new String[] {"Protein Counts", "Peptide Counts", "PSM Counts"});
        reportMap.put("iRT Standard Mix",
                new String[] {"Area - ELGQSGVDTYLQTK", "Area - GILFVGSGVSGGEEGAR", "Area - HVLTSIGEK",
                        "Area - SSAAPPPPPR", "Area - TASEFDSAIAQDK", "FWHM - ELGQSGVDTYLQTK",
                        "FWHM - GILFVGSGVSGGEEGAR", "FWHM - HVLTSIGEK", "FWHM - SSAAPPPPPR",
                        "FWHM - TASEFDSAIAQDK", "Mass Error (PPM) - ELGQSGVDTYLQTK",
                        "Mass Error (PPM) - GILFVGSGVSGGEEGAR", "Mass Error (PPM) - HVLTSIGEK",
                        "Mass Error (PPM) - SSAAPPPPPR", "Mass Error (PPM) - TASEFDSAIAQDK",
                        "RT - ELGQSGVDTYLQTK", "RT - GILFVGSGVSGGEEGAR", "RT - HVLTSIGEK",
                        "RT - SSAAPPPPPR", "RT - TASEFDSAIAQDK"});
    }

    ReportObject(String matrix){

        this.matrix = matrix;
    }

    public String[] getReports(){

        return reportMap.get(matrix);
    }
}
