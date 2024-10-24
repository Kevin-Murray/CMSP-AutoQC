package cmsp.quickqc.visualizer.parameters.types;

import cmsp.quickqc.visualizer.parameters.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class ReportFiles {

    private final static Path annotationPath = Paths.get("test_annotation.csv");
    private final static HashMap<Integer, Path> reportMap = new HashMap<>();

    static {
        reportMap.put(hashCode("Thermo Fusion", "N/A", "HeLa Protein Digest"), Paths.get("CMSP_Fusion_HeLa_QC-Report.csv"));
        reportMap.put(hashCode("Thermo Fusion", "N/A", "iRT Standard Mix"), Paths.get("CMSP_Fusion_iRT_QC-Report.csv"));

        reportMap.put(hashCode("Thermo Eclipse", "FAIMS", "HeLa Protein Digest"), Paths.get("CMSP_Eclipse_HeLa_QC-Report.csv"));
        reportMap.put(hashCode("Thermo Eclipse", "FAIMS", "iRT Standard Mix"), Paths.get("CMSP_Eclipse_iRT_QC-Report.csv"));
        reportMap.put(hashCode("Thermo Eclipse", "Non-FAIMS", "HeLa Protein Digest"), Paths.get("CMSP_Eclipse-nonFAIMS_HeLa_QC-Report.csv"));
        reportMap.put(hashCode("Thermo Eclipse", "Non-FAIMS", "iRT Standard Mix"), Paths.get("CMSP_Eclipse-nonFAIMS_iRT_QC-Report.csv"));

        reportMap.put(hashCode("Thermo Q Exactive", "HILIC", "NIST Plasma"), Paths.get("CMSP_Eclipse_HeLa_QC-Report.csv"));

        reportMap.put(hashCode("Agilent 6495C", "C18 Column", "iRT Mix - MRM"), Paths.get("CMSP_6495C_iRT_QC-Report.csv"));

        reportMap.put(hashCode("Agilent 7200", "FATWAX UI", "SST (SCFA)"), Paths.get("CMSP_A7200_FATWAX_SST_SCFA-Report.csv"));
        reportMap.put(hashCode("Agilent 7200", "FATWAX UI", "SST (Bleed)"), Paths.get("CMSP_A7200_FATWAX_SST_Bleed-Report.csv"));

        reportMap.put(hashCode("Sciex 5500", "BEH18 50 mm (186002350)", "BSA Digest - Sciex"), Paths.get("CMSP_Sciex5500_BehC18-50mm_BSA_QC-Report.csv"));
        reportMap.put(hashCode("Sciex 6500", "BEH18 50 mm (186002350)", "BSA Digest - Sciex"), Paths.get("CMSP_Sciex6500_BehC18-50mm_BSA_QC-Report.csv"));

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
