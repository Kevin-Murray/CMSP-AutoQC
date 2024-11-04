
package cmsp.quickqc.visualizer.utils.plotUtils;

import cmsp.quickqc.visualizer.datamodel.DataEntry;
import cmsp.quickqc.visualizer.utils.MathUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for plotting related method to be used in static context.
 */
public class PlotUtils {

    /**
     * Format array of data entry into data series for plotting in main page line chart in Levey-Jennings style.
     * Pull specified report value (type) from data entry and plots over data entry date.
     * Values are plotted with variability guides to help interpretation of process control.
     * Values may be log2 transformed for values with log-normal distributions, such as raw peak areas.
     *
     * @param reportItems List of data entries of QC values and annotations
     * @param reportType Data entry value to plot
     * @param logScale Log2 transform data value
     * @param varType Type of variability guides
     * @return List of chart series data
     */
    public static ObservableList<XYChart.Series<String, Number>> getLeveyData(List<DataEntry> reportItems, String reportType, Boolean logScale, String varType){

        ObservableList<XYChart.Series<String, Number>> plotData = FXCollections.observableArrayList();

        ArrayList<String> dateList = new ArrayList<>();
        ArrayList<Double> itemList = new ArrayList<>();

        // Main series contains all date strings and values of report context to be plotted.
        XYChart.Series<String, Number> mainSeries = new XYChart.Series<>();
        mainSeries.setName("Main Series");

        // Loop through data entries and add them to main series.
        for(DataEntry entry : reportItems) {

            // Get data values for descriptive statistic calculation and dates for variability guides.
            // Exclude annotations.
            if(!entry.isAnnotation()) {

                dateList.add(entry.getDate());
                itemList.add(entry.getItem(reportType, logScale));
            }

            mainSeries.getData().add(new XYChart.Data<>(entry.getDate(), entry.getItem(reportType, logScale)));
        }

        // Calculate series mean
        double mean = MathUtils.calculateAverage(itemList);

        // Create series for mean and variability guides.
        XYChart.Series<String, Number> meanSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> var1pSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> var2pSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> var3pSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> var1nSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> var2nSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> var3nSeries = new XYChart.Series<>();

        // Variability measures. 3-levels.
        double var1 = mean;
        double var2 = mean;
        double var3 = mean;

        // Use series standard deviations as guides.
        // Levels set as 1, 2, 3 standard deviations from mean.
        if(varType.equals(VariabilityTypes.STD.toString())) {

            double sd = MathUtils.calculateStandardDeviation(itemList);
            var1 = 1 * sd;
            var2 = 2 * sd;
            var3 = 3 * sd;
            var1pSeries.setName("1 Std Dev");
            var2pSeries.setName("2 Std Dev");
            var3pSeries.setName("3 Std Dev");
        }

        // Use relative standard deviation of mean as guides.
        // Levels set as standard deviation equivalent to 5%, 10%, and 20% RSD.
        if(varType.equals(VariabilityTypes.RSD.toString())) {

            var1 = 0.05 * mean;
            var2 = 0.10 * mean;
            var3 = 0.20 * mean;
            var1pSeries.setName("RSD 5%");
            var2pSeries.setName("RSD 10%");
            var3pSeries.setName("RSD 20%");
        }

        // Create mean and variability series with identical values over the date range to make horizontal lines.
        for(String date : dateList){

            meanSeries.getData().add(new XYChart.Data<>(date, mean));

            // Positive variability guides.
            var1pSeries.getData().add(new XYChart.Data<>(date, mean + var1));
            var2pSeries.getData().add(new XYChart.Data<>(date, mean + var2));
            var3pSeries.getData().add(new XYChart.Data<>(date, mean + var3));

            // Negative variability guides.
            var1nSeries.getData().add(new XYChart.Data<>(date, mean - var1));
            var2nSeries.getData().add(new XYChart.Data<>(date, mean - var2));
            var3nSeries.getData().add(new XYChart.Data<>(date, mean - var3));
        }

        plotData.add(mainSeries);
        plotData.add(meanSeries);
        plotData.add(var1pSeries);
        plotData.add(var1nSeries);
        plotData.add(var2pSeries);
        plotData.add(var2nSeries);
        plotData.add(var3pSeries);
        plotData.add(var3nSeries);

        return(plotData);
    }

    /**
     * Format array of data entry into data series for plotting in main page line chart in Moving Data Difference style.
     * Pull specified report value (type) from data entry and plots over data entry date.
     * Specifically plots difference between each point and the next previous entry.
     *
     * @param reportItems List of data entry points
     * @param reportType Report variable to plot
     * @return List of series to be plotted on main page line chart
     */
    public static ObservableList<XYChart.Series<String, Number>> getMovingData(List<DataEntry> reportItems, String reportType){

        ObservableList<XYChart.Series<String, Number>> plotData = FXCollections.observableArrayList();

        // Array list to collect data entry dates and values for mean series.
        ArrayList<String> dateList = new ArrayList<>();
        ArrayList<Double> itemList = new ArrayList<>();

        // Main series to be plotted.
        XYChart.Series<String, Number> mainSeries = new XYChart.Series<>();
        mainSeries.setName("Main Series");

        // Initialize first series item to be zero.
        Double previousItem = reportItems.get(0).getItem(reportType);

        // Loop through data entries and subtract value of current entry with previous entry.
        for(DataEntry entry : reportItems) {

            dateList.add(entry.getDate());

            Double newItem = Math.abs(entry.getItem(reportType) - previousItem);
            itemList.add(newItem);

            // Update previous item.
            previousItem = entry.getItem(reportType);

            mainSeries.getData().add(new XYChart.Data<>(entry.getDate(), newItem));
        }

        // Calculate mean of moving data difference.
        Double mean = MathUtils.calculateAverage(itemList);

        // Create series for difference mean.
        XYChart.Series<String, Number> meanSeries = new XYChart.Series<>();

        // Add mean series item for each date in main series to create horizontal line.
        for(String date : dateList) meanSeries.getData().add(new XYChart.Data<>(date, mean));

        plotData.add(mainSeries);
        plotData.add(meanSeries);

        return plotData;
    }

    /**
     * Handle context menu action to copy chart image to clipboard.
     *
     * @param lineChart Line chart from the main page.
     */
    public static void copyChartToClipboard(LineChart<String, Number> lineChart) {

        // Create new WritableImage instance using snapshot of line chart.
        WritableImage image = lineChart.snapshot(new SnapshotParameters(), null);

        // Set image to system clipboard.
        ClipboardContent cc = new ClipboardContent();
        cc.putImage(image);
        Clipboard.getSystemClipboard().setContent(cc);
    }

    /**
     * Handle context menu action to copy chart data to clipboard.
     *
     * @param lineChart Line chart from the main page.
     */
    public static void copyChartDataToClipboard(LineChart<String, Number> lineChart) {

        // Get the main data series from the chart.
        // TODO - get series by name rather than index.
        ObservableList<XYChart.Series<String, Number>> dataSeries = lineChart.getData();
        XYChart.Series<String, Number> series = dataSeries.get(0);

        // Concatenate x and y data into tab-separated string
        StringBuilder dataToCopy = new StringBuilder();
        for (XYChart.Data<String, Number> data : series.getData()) {

            dataToCopy.append(data.getXValue()).append("\t").append(data.getYValue()).append("\n");
        }

        // Set data to system clipboard for copying.
        ClipboardContent content = new ClipboardContent();
        content.putString(dataToCopy.toString());
        Clipboard.getSystemClipboard().setContent(content);
    }
}
