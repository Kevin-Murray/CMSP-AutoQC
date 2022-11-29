package com.example.demo;

public class SampleEntry {

    private String field;
    private String value;

    public SampleEntry(String field, String value){

        this.field = field;
        this.value = value;
    }

    public String getField(){
        return this.field;
    }

    public String getValue(){
        return this.value;
    }
}
