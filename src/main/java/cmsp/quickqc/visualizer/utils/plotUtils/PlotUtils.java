package cmsp.quickqc.visualizer.utils.plotUtils;

import cmsp.quickqc.visualizer.datamodel.DataEntry;
import cmsp.quickqc.visualizer.utils.MathUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class PlotUtils {

    public static ObservableList<XYChart.Series> getLeveyData(List<DataEntry> reportItems, String reportType, String plotScale){

        ObservableList<XYChart.Series> plotData = FXCollections.observableArrayList();

        ArrayList<String> dateList = new ArrayList<>();
        ArrayList<Double> itemList = new ArrayList<>();
        XYChart.Series mainSeries = new XYChart.Series();

        for(DataEntry entry : reportItems){
            if(!entry.isAnnotation()){
                dateList.add(entry.getDate());
                itemList.add(entry.getItem(reportType, plotScale));
            }
            mainSeries.getData().add(new XYChart.Data(entry.getDate(), entry.getItem(reportType, plotScale)));
        }

        double mean = MathUtils.calculateAverage(itemList);
        Double standardDev = MathUtils.calculateStandardDeviation(itemList);

        XYChart.Series meanSeries = new XYChart.Series();
        XYChart.Series dev1pSeries = new XYChart.Series();
        XYChart.Series dev2pSeries = new XYChart.Series();
        XYChart.Series dev3pSeries = new XYChart.Series();
        XYChart.Series dev1nSeries = new XYChart.Series();
        XYChart.Series dev2nSeries = new XYChart.Series();
        XYChart.Series dev3nSeries = new XYChart.Series();

        for(String date : dateList){

            meanSeries.getData().add(new XYChart.Data(date, mean));

            dev1pSeries.getData().add(new XYChart.Data(date, mean + 1 * standardDev));
            dev2pSeries.getData().add(new XYChart.Data(date, mean + 2 * standardDev));
            dev3pSeries.getData().add(new XYChart.Data(date, mean + 3 * standardDev));

            dev1nSeries.getData().add(new XYChart.Data(date, mean - 1 * standardDev));
            dev2nSeries.getData().add(new XYChart.Data(date, mean - 2 * standardDev));
            dev3nSeries.getData().add(new XYChart.Data(date, mean - 3 * standardDev));
        }

        plotData.add(mainSeries);
        plotData.add(meanSeries);
        plotData.add(dev1pSeries);
        plotData.add(dev1nSeries);
        plotData.add(dev2pSeries);
        plotData.add(dev2nSeries);
        plotData.add(dev3pSeries);
        plotData.add(dev3nSeries);

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

        Double mean = MathUtils.calculateAverage(itemList);

        XYChart.Series meanSeries = new XYChart.Series();

        for(String date : dateList){

            meanSeries.getData().add(new XYChart.Data(date, mean));
        }

        plotData.add(mainSeries);
        plotData.add(meanSeries);

        return plotData;
    }
}
