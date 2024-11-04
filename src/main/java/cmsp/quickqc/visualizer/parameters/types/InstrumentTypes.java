
package cmsp.quickqc.visualizer.parameters.types;

import java.util.ArrayList;

/**
 * Enumeration of QC report context instrument types.
 * Instrument types represent literal analytical instrumentation equipment.
 */
public enum InstrumentTypes {

    FUSION("Thermo Fusion"),
    ECLIPSE("Thermo Eclipse"),
    QEXACTIVE("Thermo Q Exactive"),
    A6495C("Agilent 6495C"),
    A7200("Agilent 7200"),
    SCIEX5500("Sciex 5500"),
    SCIEX6500("Sciex 6500");

    private final String label;

    /**
     * Constructor for enum
     */
    InstrumentTypes(String instrumentName) {

        this.label = instrumentName;
    }

    /**
     * Get instrument name.
     */
    public static String getInstrument(String instrument) {

        for(InstrumentTypes i : values()) {

            if(i.label.equals(instrument)) return i.name();
        }

        return null;
    }

    /**
     * Cast enum label to string.
     */
    @Override
    public String toString() {

        return this.label;
    }

    /**
     * Get ArrayList of instrument names
     */
    public static ArrayList<String> getInstrumentNames() {

        ArrayList<String> values = new ArrayList<>();

        for(InstrumentTypes i : InstrumentTypes.values()) values.add(i.label);

        return values;
    }
}
