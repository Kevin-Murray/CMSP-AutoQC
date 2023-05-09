module cmsp.quickqc.visualizer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;

    opens cmsp.quickqc.visualizer.gui to javafx.fxml;
    opens cmsp.quickqc.visualizer.datamodel to javafx.fxml;

    exports cmsp.quickqc.visualizer;
    exports cmsp.quickqc.visualizer.datamodel;
}