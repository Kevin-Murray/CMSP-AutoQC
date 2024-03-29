package cmsp.quickqc.visualizer.parameters.types;

public enum InstrumentTypes {

    VELOS("Thermo Velos"),
    FUSION("Thermo Fusion"),
    ECLIPSE("Thermo Eclipse"),
    A6495C("Agilent 6495C"),
    A7200("Agilent 7200 GC-QTOF"),
    SCIEX5500("Sciex 5500");

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
