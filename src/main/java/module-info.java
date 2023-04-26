module cmsp.quickqc.visualizer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;


    opens cmsp.quickqc.visualizer to javafx.fxml;
    exports cmsp.quickqc.visualizer;
}