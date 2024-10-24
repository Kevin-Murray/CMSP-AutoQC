package cmsp.quickqc.visualizer.parameters.types;

public enum ConfigurationTypes {

    FUSION(new String[] {"N/A"}),
    ECLIPSE(new String[] {"FAIMS", "Non-FAIMS"}),
    QEXACTIVE(new String[] {"HILIC"}),
    A6495C(new String[] {"C18 Column"}),
    A7200(new String[] {"FATWAX UI"}),
    SCIEX5500(new String[] {"BEH18 50 mm (186002350)"}),
    SCIEX6500(new String[] {"BEH18 50 mm (186002350)"});

    private final String[] label;

    ConfigurationTypes(String[] configs) {
        this.label = configs;
    }

    public static String[] getConfiguration(String instrument) {
        for(ConfigurationTypes i : values()){
            if(i.name().equals(instrument)){
                return i.label;
            }
        }

        return null;
    }
}
