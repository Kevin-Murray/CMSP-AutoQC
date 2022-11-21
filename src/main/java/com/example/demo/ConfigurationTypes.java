package com.example.demo;

import java.util.Arrays;

public enum ConfigurationTypes {

    VELOS(new String[] {"N/A"}),
    FUSION(new String[] {"N/A"}),
    ECLIPSE(new String[] {"FAIMS", "Non-FAIMS"});

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
