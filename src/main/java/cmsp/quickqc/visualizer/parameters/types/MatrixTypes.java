package cmsp.quickqc.visualizer.parameters.types;

public enum MatrixTypes {

    VELOS(new String[] {"HeLa Protein Digest", "BSA Digest - DDA"}),
    FUSION(new String[] {"HeLa Protein Digest", "iRT Standard Mix"}),
    ECLIPSE(new String[] {"HeLa Protein Digest", "iRT Standard Mix"}),
    A6495C(new String[] {"iRT Mix - MRM"}),
    A7200(new String[] {"Retention Index (n-Alkane)"}),
    SCIEX5500(new String[] {"BSA Digest - Sciex"});

    private final String[] label;

    MatrixTypes(String[] configs) {
        this.label = configs;
    }

    public static String[] getMatrix(String instrument) {
        for(MatrixTypes i : values()){
            if(i.name().equals(instrument)){
                return i.label;
            }
        }

        return null;
    }
}
