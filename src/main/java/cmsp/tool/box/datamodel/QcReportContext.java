package cmsp.tool.box.datamodel;

import java.nio.file.Path;
import java.util.List;

/**
 * QcReportContext record class. Includes all the QC report contexts for monitoring.
 *
 * @param instrument Instrument type string
 * @param config Instrument-Configuration string
 * @param matrix Instrument-Configuration-Matrix string
 * @param standard Instrument-Configuration-Matrix-Standard string
 * @param database Path to QC database
 * @param variables String list of all variables monitored in QC database
 */
public record QcReportContext(String instrument, String config, String matrix, String standard, Path database,
                              List<String> variables) {

}
