package cmsp.quickqc.visualizer.utils.plotUtils;

public enum VariabilityTypes {

    STD("Standard Deviations"),
    RSD("Relative Std. Dev. (RSD)"),
    CUSTOM("Custom...");

    private final String label;

    VariabilityTypes(String s) {
        this.label = s;
    }

    @Override
    public String toString() {
        return this.label;
    }

}
