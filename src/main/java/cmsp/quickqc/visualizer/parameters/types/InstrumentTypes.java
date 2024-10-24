package cmsp.quickqc.visualizer.parameters.types;

public enum InstrumentTypes {

    FUSION("Thermo Fusion"),
    ECLIPSE("Thermo Eclipse"),
    QEXACTIVE("Thermo Q Exactive"),
    A6495C("Agilent 6495C"),
    A7200("Agilent 7200"),
    SCIEX5500("Sciex 5500"),
    SCIEX6500("Sciex 6500");

    private final String label;

    InstrumentTypes(String s) {
        this.label = s;
    }

    public static String getInstrument(String instrument) {
        for(InstrumentTypes i : values()){
            if(i.label.equals(instrument)){
                return i.name();
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
