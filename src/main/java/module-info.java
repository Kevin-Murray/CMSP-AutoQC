module cmsp.quickqc.visualizer {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;
    requires java.xml;
    requires org.apache.poi.ooxml;
    requires org.apache.commons.codec;

    opens cmsp.quickqc.visualizer.gui to javafx.fxml;
    opens cmsp.quickqc.visualizer.datamodel to javafx.fxml;

    exports cmsp.quickqc.visualizer;
    exports cmsp.quickqc.visualizer.datamodel;
    exports cmsp.quickqc.visualizer.utils;
    opens cmsp.quickqc.visualizer.utils to javafx.fxml;
    exports cmsp.quickqc.visualizer.enums;
    opens cmsp.quickqc.visualizer.enums to javafx.fxml;
    exports cmsp.quickqc.visualizer.tasks;
    opens cmsp.quickqc.visualizer.tasks to javafx.fxml;
}