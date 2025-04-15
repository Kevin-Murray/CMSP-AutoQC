
package cmsp.tool.box.utils;

import java.util.HashSet;
import java.util.List;

import cmsp.tool.box.datamodel.QcReportContext;

/**
 * Utility class for filtering Lists of QcReportContext.
 * TODO - this is all very messy. Can this be implemented better?
 */
public class ContextFilteringUtils {

    /**
     * Get unique instruments from List of QcReportContext
     *
     * @param contextList Application contexts to from database
     * @return String List of unique instruments
     */
    public static List<String> getUniqueInstruments(List<QcReportContext> contextList) {

        HashSet<String> context = new HashSet<>(contextList.stream().map(QcReportContext::instrument).toList());

        return context.stream().sorted().toList();
    }

    /**
     * Filter Report Context List to only include objects with specified instrument
     *
     * @param contextList List of Report Contexts from main database
     * @param instrument String of specified instrument
     * @return List of Report Context that only include the specified instrument
     */
    private static List<QcReportContext> filterInstrumentContext(List<QcReportContext> contextList, String instrument) {

        return contextList.stream().filter(context -> context.instrument().equals(instrument)).toList();
    }

    /**
     * Get unique configurations from Report Context list of specified instrument
     *
     * @param contextList List of Report Contexts from main database
     * @param instrument String of specified instrument
     * @return List of unique configuration strings
     */
    public static List<String> getUniqueConfigurations(List<QcReportContext> contextList, String instrument) {

        List<QcReportContext> instrumentContext = filterInstrumentContext(contextList, instrument);

        HashSet<String> context = new HashSet<>(instrumentContext.stream().map(QcReportContext::config).toList());

        return context.stream().sorted().toList();
    }

    /**
     * Filter List of Report Context to only include those with the specified instrument and configuration.
     *
     * @param contextList List of Report Contexts from main database
     * @param instrument String of specified instrument
     * @param config String of specified configuration
     * @return List of Report Contexts of specified instrument and configuration
     */
    private static List<QcReportContext> filterConfigContext(List<QcReportContext> contextList, String instrument, String config) {

        List<QcReportContext> instrumentContext = filterInstrumentContext(contextList, instrument);

        return instrumentContext.stream().filter(context -> context.config().equals(config)).toList();
    }

    /**
     * Get unique matrices from List of Report Context with specified instrument and configuration.
     *
     * @param contextList List of Report Contexts from main database
     * @param instrument String of specified instrument
     * @param config String of specified configuration
     * @return List of unique matrix strings
     */
    public static List<String> getUniqueMatrices(List<QcReportContext> contextList, String instrument, String config) {

        List<QcReportContext> instrumentContext = filterInstrumentContext(contextList, instrument);
        List<QcReportContext> configContext = filterConfigContext(instrumentContext, instrument, config);

        HashSet<String> context = new HashSet<>(configContext.stream().map(QcReportContext::matrix).toList());

        return context.stream().sorted().toList();
    }

    /**
     * Filter List of Report Contexts with specified instrument, configuration, and matrix.
     *
     * @param contextList List of Report Contexts from main database
     * @param instrument String of specified instrument
     * @param config String of specified configuration
     * @param matrix String of specified matrix
     * @return List of filtered Report Contexts
     */
    private static List<QcReportContext> filterMatrixContext(List<QcReportContext> contextList, String instrument, String config, String matrix) {

        List<QcReportContext> instrumentContext = filterInstrumentContext(contextList, instrument);
        List<QcReportContext> configContext = filterConfigContext(instrumentContext, instrument, config);

        return configContext.stream().filter(context -> context.matrix().equals(matrix)).toList();
    }

    /**
     * Get unique standards from List of Report Contexts with specified instrument, configuration, and matrix.
     *
     * @param contextList List of Report Contexts from main database
     * @param instrument String of specified instrument
     * @param config String of specified configuration
     * @param matrix String of specified matrix
     * @return List of unique standard strings
     */
    public static List<String> getUniqueStandards(List<QcReportContext> contextList, String instrument, String config, String matrix) {

        List<QcReportContext> instrumentContext = filterInstrumentContext(contextList, instrument);
        List<QcReportContext> configContext = filterConfigContext(instrumentContext, instrument, config);
        List<QcReportContext> matrixContext = filterMatrixContext(configContext, instrument, config, matrix);

        HashSet<String> context = new HashSet<>(matrixContext.stream().map(QcReportContext::standard).toList());

        return context.stream().sorted().toList();
    }

    /**
     * Get Report Context with all specified filters.
     *
     * @param contextList List of Report Contexts from main database
     * @param instrument String of specified instrument
     * @param config String of specified configuration
     * @param matrix String of specified matrix
     * @param standard String of specified standard
     * @return Unique Report Context object.
     */
    public static QcReportContext getSelectedReportContext(List<QcReportContext> contextList, String instrument, String config, String matrix, String standard) {

        List<QcReportContext> instrumentContext = filterInstrumentContext(contextList, instrument);
        List<QcReportContext> configContext = filterConfigContext(instrumentContext, instrument, config);
        List<QcReportContext> matrixContext = filterMatrixContext(configContext, instrument, config, matrix);
        return matrixContext.stream().filter(context -> context.standard().equals(standard)).toList().get(0);
    }
}
