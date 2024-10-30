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

public class PlotUtils {

    public static ObservableList<XYChart.Series> getLeveyData(List<DataEntry> reportItems, String reportType, Boolean logScale, String varType){

        ObservableList<XYChart.Series> plotData = FXCollections.observableArrayList();

        ArrayList<String> dateList = new ArrayList<>();
        ArrayList<Double> itemList = new ArrayList<>();
        XYChart.Series mainSeries = new XYChart.Series();

        for(DataEntry entry : reportItems){
            if(!entry.isAnnotation()){
                dateList.add(entry.getDate());
                itemList.add(entry.getItem(reportType, logScale));
            }
            mainSeries.getData().add(new XYChart.Data(entry.getDate(), entry.getItem(reportType, logScale)));
        }

        mainSeries.setName("Main Series");

        double mean = MathUtils.calculateAverage(itemList);

        XYChart.Series meanSeries = new XYChart.Series();
        XYChart.Series var1pSeries = new XYChart.Series();
        XYChart.Series var2pSeries = new XYChart.Series();
        XYChart.Series var3pSeries = new XYChart.Series();
        XYChart.Series var1nSeries = new XYChart.Series();
        XYChart.Series var2nSeries = new XYChart.Series();
        XYChart.Series var3nSeries = new XYChart.Series();

        double var1 = mean;
        double var2 = mean;
        double var3 = mean;

        if(varType.equals(VariabilityTypes.STD.toString())){
            double sd = MathUtils.calculateStandardDeviation(itemList);
            var1 = 1 * sd;
            var2 = 2 * sd;
            var3 = 3 * sd;
            var1pSeries.setName("1 Std Dev");
            var2pSeries.setName("2 Std Dev");
            var3pSeries.setName("3 Std Dev");
        }

        if(varType.equals(VariabilityTypes.RSD.toString())){
            var1 = 0.05 * mean;
            var2 = 0.10 * mean;
            var3 = 0.20 * mean;
            var1pSeries.setName("RSD 5%");
            var2pSeries.setName("RSD 10%");
            var3pSeries.setName("RSD 20%");
        }

        for(String date : dateList){

            meanSeries.getData().add(new XYChart.Data(date, mean));

            var1pSeries.getData().add(new XYChart.Data(date, mean + var1));
            var2pSeries.getData().add(new XYChart.Data(date, mean + var2));
            var3pSeries.getData().add(new XYChart.Data(date, mean + var3));

            var1nSeries.getData().add(new XYChart.Data(date, mean - var1));
            var2nSeries.getData().add(new XYChart.Data(date, mean - var2));
            var3nSeries.getData().add(new XYChart.Data(date, mean - var3));

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

    public static ObservableList<XYChart.Series> getMovingData(List<DataEntry> reportItems, String reportType){

        ObservableList<XYChart.Series> plotData = FXCollections.observableArrayList();

        ArrayList<String> dateList = new ArrayList<>();
        ArrayList<Double> itemList = new ArrayList<>();
        XYChart.Series mainSeries = new XYChart.Series();

        Double previousItem = reportItems.get(0).getItem(reportType);

        for(DataEntry entry : reportItems){
            dateList.add(entry.getDate());

            Double newItem = Math.abs(entry.getItem(reportType) - previousItem);
            itemList.add(newItem);
            previousItem = entry.getItem(reportType);

            mainSeries.getData().add(new XYChart.Data(entry.getDate(), newItem));
        }

        mainSeries.setName("Main Series");

        Double mean = MathUtils.calculateAverage(itemList);

        XYChart.Series meanSeries = new XYChart.Series();

        for(String date : dateList){

            meanSeries.getData().add(new XYChart.Data(date, mean));
        }

        plotData.add(mainSeries);
        plotData.add(meanSeries);

        return plotData;
    }

    public static void copyChartToClipboard(LineChart lineChart) {

        WritableImage image = lineChart.snapshot(new SnapshotParameters(), null);

        ClipboardContent cc = new ClipboardContent();
        cc.putImage(image);

        Clipboard.getSystemClipboard().setContent(cc);
    }

    public static void copyChartDataToClipboard(LineChart lineChart) {

        // Get the data series from the chart
        ObservableList<XYChart.Series<Number, Number>> dataSeries = lineChart.getData();
        XYChart.Series<Number, Number> series = dataSeries.get(0);

        StringBuilder dataToCopy = new StringBuilder();
        for (XYChart.Data<Number, Number> data : series.getData()) {
            dataToCopy.append(data.getXValue()).append("\t").append(data.getYValue()).append("\n");
        }

        ClipboardContent content = new ClipboardContent();
        content.putString(dataToCopy.toString());

        Clipboard.getSystemClipboard().setContent(content);
    }

}
