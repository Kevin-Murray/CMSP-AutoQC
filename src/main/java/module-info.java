module cmsp.quickqc.visualizer {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;
    requires java.xml;
    requires org.apache.poi.ooxml;
    requires org.apache.commons.codec;

    opens cmsp.tool.box.gui to javafx.fxml;
    opens cmsp.tool.box.datamodel to javafx.fxml;

    exports cmsp.tool.box;
    exports cmsp.tool.box.datamodel;
    exports cmsp.tool.box.utils;
    opens cmsp.tool.box.utils to javafx.fxml;
    exports cmsp.tool.box.enums;
    opens cmsp.tool.box.enums to javafx.fxml;
    exports cmsp.tool.box.tasks;
    opens cmsp.tool.box.tasks to javafx.fxml;
}