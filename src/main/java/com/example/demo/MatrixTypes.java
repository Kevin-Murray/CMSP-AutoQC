package com.example.demo;

public enum MatrixTypes {

    VELOS(new String[] {"HeLa Protein Digest", "iRT Standard Mix"}),
    FUSION(new String[] {"HeLa Protein Digest", "iRT Standard Mix"}),
    ECLIPSE(new String[] {"HeLa Protein Digest", "iRT Standard Mix"});

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
