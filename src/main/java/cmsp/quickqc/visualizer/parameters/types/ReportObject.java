
package cmsp.quickqc.visualizer.parameters.types;

import java.util.HashMap;

/**
 * Report context variables to be monitored in application.
 * Each matrix type has designated string of variables to monitor.
 * Class is effectively an inefficient hash map.
 */
public class ReportObject {

    private final String matrix;

    private final static HashMap<String, String[]> reportMap = new HashMap<>();

    // TODO - plan on turing this into a database.
    static {

        // Fusion-Eclipse
        reportMap.put("HeLa Protein Digest",
                new String[] {"Protein Counts",
                        "Peptide Counts",
                        "PSM Counts"});

        // Fusion-Eclipse
        reportMap.put("iRT Standard Mix",
                new String[] {"Area - ELGQSGVDTYLQTK",
                        "Area - GILFVGSGVSGGEEGAR",
                        "Area - HVLTSIGEK",
                        "Area - SSAAPPPPPR",
                        "Area - TASEFDSAIAQDK",
                        "FWHM - ELGQSGVDTYLQTK",
                        "FWHM - GILFVGSGVSGGEEGAR",
                        "FWHM - HVLTSIGEK",
                        "FWHM - SSAAPPPPPR",
                        "FWHM - TASEFDSAIAQDK",
                        "Mass Error (PPM) - ELGQSGVDTYLQTK",
                        "Mass Error (PPM) - GILFVGSGVSGGEEGAR",
                        "Mass Error (PPM) - HVLTSIGEK",
                        "Mass Error (PPM) - SSAAPPPPPR",
                        "Mass Error (PPM) - TASEFDSAIAQDK",
                        "RT - ELGQSGVDTYLQTK",
                        "RT - GILFVGSGVSGGEEGAR",
                        "RT - HVLTSIGEK",
                        "RT - SSAAPPPPPR",
                        "RT - TASEFDSAIAQDK"});

        // Unused - Formerly Velos
        reportMap.put("BSA Digest - DDA",
                new String[] {"Area - HADICTLPDTEK",
                        "Area - HLVDEPQNLIK",
                        "Area - LVNELTEFAK",
                        "Area - TCVADESHAGCEK",
                        "Area - YICDNQDTISSK",
                        "FWHM - HADICTLPDTEK",
                        "FWHM - HLVDEPQNLIK",
                        "FWHM - LVNELTEFAK",
                        "FWHM - TCVADESHAGCEK",
                        "FWHM - YICDNQDTISSK",
                        "Mass Error (PPM) - HADICTLPDTEK",
                        "Mass Error (PPM) - HLVDEPQNLIK",
                        "Mass Error (PPM) - LVNELTEFAK",
                        "Mass Error (PPM) - TCVADESHAGCEK",
                        "Mass Error (PPM) - YICDNQDTISSK",
                        "RT - HADICTLPDTEK",
                        "RT - HLVDEPQNLIK",
                        "RT - LVNELTEFAK",
                        "RT - TCVADESHAGCEK",
                        "RT - YICDNQDTISSK"});

        // Unused - Sciex 5500, Sciex 6500, Agilent 6495C
        reportMap.put("iRT Mix - MRM",
                new String[] {"Area - ELGQSGVDTYLQTK",
                        "Area - GISNEGQNASIK",
                        "Area - IGDYAGIK",
                        "Area - LTILEELR",
                        "Area - SFANQPLEVVYSK",
                        "FWHM - ELGQSGVDTYLQTK",
                        "FWHM - GISNEGQNASIK",
                        "FWHM - IGDYAGIK",
                        "FWHM - LTILEELR",
                        "FWHM - SFANQPLEVVYSK",
                        "Height - ELGQSGVDTYLQTK",
                        "Height - GISNEGQNASIK",
                        "Height - IGDYAGIK",
                        "Height - LTILEELR",
                        "Height - SFANQPLEVVYSK",
                        "RT - ELGQSGVDTYLQTK",
                        "RT - GISNEGQNASIK",
                        "RT - IGDYAGIK",
                        "RT - LTILEELR",
                        "RT - SFANQPLEVVYSK",
                        "Pressure - Max",
                        "Pressure - Min",
                        "Pressure - Avg"});

        // Unused - formerly Agilent 7200
        reportMap.put("Retention Index (n-Alkane)",
                new String[] {"Area - Decane",
                        "Area - Dodecane",
                        "Area - Pentadecane",
                        "Area - Nonadecane",
                        "Area - Docosane",
                        "FWHM - Decane",
                        "FWHM - Dodecane",
                        "FWHM - Pentadecane",
                        "FWHM - Nonadecane",
                        "FWHM - Docosane",
                        "Height - Decane",
                        "Height - Dodecane",
                        "Height - Pentadecane",
                        "Height - Nonadecane",
                        "Height - Docosane",
                        "RT - Decane",
                        "RT - Dodecane",
                        "RT - Pentadecane",
                        "RT - Nonadecane",
                        "RT - Docosane",
                        "Mass Error (PPM) - Decane",
                        "Mass Error (PPM) - Dodecane",
                        "Mass Error (PPM) - Pentadecane",
                        "Mass Error (PPM) - Nonadecane",
                        "Mass Error (PPM) - Docosane",
                        });


        // Unused - formerly Sciex 5500, Sciex 6500
        reportMap.put("BSA Digest - Sciex",
                new String[] {"Area - HADICTLPDTEK",
                        "Area - HLVDEPQNLIK",
                        "Area - LVNELTEFAK",
                        "Area - TCVADESHAGCEK",
                        "Area - YICDNQDTISSK",
                        "FWHM - HADICTLPDTEK",
                        "FWHM - HLVDEPQNLIK",
                        "FWHM - LVNELTEFAK",
                        "FWHM - TCVADESHAGCEK",
                        "FWHM - YICDNQDTISSK",
                        "Height - HADICTLPDTEK",
                        "Height - HLVDEPQNLIK",
                        "Height - LVNELTEFAK",
                        "Height - TCVADESHAGCEK",
                        "Height - YICDNQDTISSK",
                        "RT - HADICTLPDTEK",
                        "RT - HLVDEPQNLIK",
                        "RT - LVNELTEFAK",
                        "RT - TCVADESHAGCEK",
                        "RT - YICDNQDTISSK"});

        // Agilent 7200
        reportMap.put("SST (SCFA)",
                new String[] {
                        "Area (Raw) - Acetic Acid",
                        "Area (Raw) - Propionic Acid",
                        "Area (Raw) - Butyric Acid",
                        "Area (Raw) - Valeric Acid",
                        "Area (Raw) - 2-Ethylbutyric Acid",
                        "Area (Norm) - Acetic Acid",
                        "Area (Norm) - Propionic Acid",
                        "Area (Norm) - Butyric Acid",
                        "Area (Norm) - Valeric Acid",
                        "Area (Norm) - 2-Ethylbutyric Acid",
                        "Height - Acetic Acid",
                        "Height - Propionic Acid",
                        "Height - Butyric Acid",
                        "Height - Valeric Acid",
                        "Height - 2-Ethylbutyric Acid",
                        "RT - Acetic Acid",
                        "RT - Propionic Acid",
                        "RT - Butyric Acid",
                        "RT - Valeric Acid",
                        "RT - 2-Ethylbutyric Acid",
                        "FWHM - Acetic Acid",
                        "FWHM - Propionic Acid",
                        "FWHM - Butyric Acid",
                        "FWHM - Valeric Acid",
                        "FWHM - 2-Ethylbutyric Acid",
                        "Dot Product - Acetic Acid",
                        "Dot Product - Propionic Acid",
                        "Dot Product - Butyric Acid",
                        "Dot Product - Valeric Acid",
                        "Dot Product - 2-Ethylbutyric Acid",
                        "Mass Error (PPM) - Acetic Acid",
                        "Mass Error (PPM) - Propionic Acid",
                        "Mass Error (PPM) - Butyric Acid",
                        "Mass Error (PPM) - Valeric Acid",
                        "Mass Error (PPM) - 2-Ethylbutyric Acid"});
    }

    /**
     * Class constructor.
     *
     * @param matrix Input matrix of report context
     */
    public ReportObject(String matrix) {

        this.matrix = matrix;
    }

    /**
     * Get report variables of constructed matrix.
     *
     * @return array of report variables
     */
    public String[] getReports() {

        return reportMap.get(matrix);
    }
}
