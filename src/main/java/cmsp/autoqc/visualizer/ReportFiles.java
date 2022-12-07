package cmsp.autoqc.visualizer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class ReportFiles {

    private static HashMap<Integer, Path> reportMap = new HashMap<>();

    static {
        reportMap.put(hashCode("Thermo Fusion", "N/A", "HeLa Protein Digest"), Paths.get("CMSP_Fusion_HeLa_QC-Report.csv"));
        reportMap.put(hashCode("Thermo Eclipse", "FAIMS", "HeLa Protein Digest"), Paths.get("CMSP_Eclipse_HeLa_QC-Report.csv"));
    }

    private static int hashCode(String instrument, String config, String matrix){

        return (instrument + config + matrix).hashCode();
    }

    public static Path getPath(Parameters parameters) {

        int hash = hashCode(parameters.instrument, parameters.configuration, parameters.matrix);

        return parameters.databasePath.resolve(reportMap.get(hash));

        //return reportMap.get(hash);
    }
}