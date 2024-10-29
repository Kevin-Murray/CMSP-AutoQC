package cmsp.quickqc.visualizer.utils.plotUtils;

import cmsp.quickqc.visualizer.datamodel.DataEntry;
import cmsp.quickqc.visualizer.utils.MathUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;


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

        mainSeries.setName("Main Series");

        double mean = MathUtils.calculateAverage(itemList);
        double rsd05 = 0.05 * mean;
        double rsd10 = 0.10 * mean;
        double rsd20 = 0.20 * mean;

        XYChart.Series meanSeries = new XYChart.Series();
        XYChart.Series rsd05pSeries = new XYChart.Series();
        XYChart.Series rsd10pSeries = new XYChart.Series();
        XYChart.Series rsd20pSeries = new XYChart.Series();
        XYChart.Series rsd05nSeries = new XYChart.Series();
        XYChart.Series rsd10nSeries = new XYChart.Series();
        XYChart.Series rsd20nSeries = new XYChart.Series();


        for(String date : dateList){

            meanSeries.getData().add(new XYChart.Data(date, mean));

            rsd05pSeries.getData().add(new XYChart.Data(date, mean + rsd05));
            rsd10pSeries.getData().add(new XYChart.Data(date, mean + rsd10));
            rsd20pSeries.getData().add(new XYChart.Data(date, mean + rsd20));

            rsd05nSeries.getData().add(new XYChart.Data(date, mean - rsd05));
            rsd10nSeries.getData().add(new XYChart.Data(date, mean - rsd10));
            rsd20nSeries.getData().add(new XYChart.Data(date, mean - rsd20));

            rsd05pSeries.setName("RSD 5%");
            rsd10pSeries.setName("RSD 10%");
            rsd20pSeries.setName("RSD 20%");

        }

        plotData.add(mainSeries);
        plotData.add(meanSeries);
        plotData.add(rsd05pSeries);
        plotData.add(rsd05nSeries);
        plotData.add(rsd10pSeries);
        plotData.add(rsd10nSeries);
        plotData.add(rsd20pSeries);
        plotData.add(rsd20nSeries);

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
}
