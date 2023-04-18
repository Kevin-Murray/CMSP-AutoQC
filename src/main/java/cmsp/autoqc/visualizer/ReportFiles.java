package cmsp.autoqc.visualizer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class ReportFiles {

    private static Path annotationPath = Paths.get("test_annotation.csv");
    private static HashMap<Integer, Path> reportMap = new HashMap<>();

    static {
        reportMap.put(hashCode("Thermo Fusion", "N/A", "HeLa Protein Digest"), Paths.get("CMSP_Fusion_HeLa_QC-Report.csv"));
        reportMap.put(hashCode("Thermo Eclipse", "FAIMS", "HeLa Protein Digest"), Paths.get("CMSP_Eclipse_HeLa_QC-Report.csv"));
        reportMap.put(hashCode("Thermo Velos", "N/A", "HeLa Protein Digest"), Paths.get("CMSP_Velos_HeLa_QC-Report.csv"));
        reportMap.put(hashCode("Thermo Eclipse", "Non-FAIMS", "HeLa Protein Digest"), Paths.get("CMSP_Eclipse-nonFAIMS_HeLa_QC-Report.csv"));

        reportMap.put(hashCode("Thermo Fusion", "N/A", "iRT Standard Mix"), Paths.get("CMSP_Fusion_iRT_QC-Report.csv"));
        reportMap.put(hashCode("Thermo Eclipse", "FAIMS", "iRT Standard Mix"), Paths.get("CMSP_Eclipse_iRT_QC-Report.csv"));
        reportMap.put(hashCode("Thermo Eclipse", "Non-FAIMS", "iRT Standard Mix"), Paths.get("CMSP_Eclipse-nonFAIMS_iRT_QC-Report.csv"));
        reportMap.put(hashCode("Thermo Velos", "N/A", "BSA Digest - DDA"), Paths.get("CMSP_Velos_BSA_QC-Report.csv"));

        reportMap.put(hashCode("Agilent 6495C", "C18 Column", "iRT Mix - MRM"), Paths.get("CMSP_6495C_iRT_QC-Report.csv"));

        reportMap.put(hashCode("Agilent 7200 GC-QTOF", "HP-5 MS UI", "Retention Index (n-Alkane)"), Paths.get("CMSP_7200_RI-alkane_QC-Report.csv"));
    }

    private static int hashCode(String instrument, String config, String matrix){

        return (instrument + config + matrix).hashCode();
    }

    public static Path getPath(Parameters parameters) {

        int hash = hashCode(parameters.instrument, parameters.configuration, parameters.matrix);

        return parameters.databasePath.resolve(reportMap.get(hash));

    }

    public static Path getAnnotationPath(Parameters parameters) {

        return parameters.databasePath.resolve(annotationPath);
    }
}
