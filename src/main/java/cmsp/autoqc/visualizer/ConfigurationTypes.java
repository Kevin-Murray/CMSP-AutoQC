package cmsp.autoqc.visualizer;

public enum ConfigurationTypes {

    VELOS(new String[] {"N/A"}),
    FUSION(new String[] {"N/A"}),
    ECLIPSE(new String[] {"FAIMS", "Non-FAIMS"}),
    A6495C(new String[] {"C18 Column"});

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
