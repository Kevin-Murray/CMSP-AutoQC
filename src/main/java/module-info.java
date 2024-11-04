module cmsp.quickqc.visualizer {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;

    opens cmsp.quickqc.visualizer.gui to javafx.fxml;
    opens cmsp.quickqc.visualizer.datamodel to javafx.fxml;
    opens cmsp.quickqc.visualizer.utils.plotUtils to javafx.fxml;

    exports cmsp.quickqc.visualizer;
    exports cmsp.quickqc.visualizer.parameters;
    exports cmsp.quickqc.visualizer.datamodel;
    exports cmsp.quickqc.visualizer.utils.plotUtils;
    exports cmsp.quickqc.visualizer.utils.annotations;
}