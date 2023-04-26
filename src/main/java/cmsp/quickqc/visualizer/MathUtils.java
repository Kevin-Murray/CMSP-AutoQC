package cmsp.quickqc.visualizer;

import java.util.ArrayList;

public class MathUtils {

    public static Double calculateStandardDeviation(ArrayList<Double> array) {

        // get the sum of array
        double sum = 0.0;
        for (double i : array) {
            sum += i;
        }

        // get the mean of array
        int length = array.size();
        double mean = sum / length;

        // calculate the standard deviation
        double standardDeviation = 0.0;
        for (double num : array) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.sqrt(standardDeviation / length);
    }

    public static Double calculateAverage(ArrayList<Double> series) {
        return series.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0.0);
    }

    public static Double log2(Double N) {

        return (Math.log(N) / Math.log(2));
    }

}
