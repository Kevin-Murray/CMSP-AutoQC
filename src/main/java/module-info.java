module cmsp.autoqc.visualizer {
    requires javafx.controls;
    requires javafx.fxml;


    opens cmsp.autoqc.visualizer to javafx.fxml;
    exports cmsp.autoqc.visualizer;
}