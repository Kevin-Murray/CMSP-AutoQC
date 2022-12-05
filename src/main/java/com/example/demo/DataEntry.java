package com.example.demo;

import java.util.*;

public class DataEntry {

    private LinkedHashMap<String, String> entries;

    public DataEntry(String[] header, String[] entry){

        this.entries = new LinkedHashMap<>();
        addItems(header, entry);
    }

    public void addItems(String[] header, String[] items){

        for(int i = 0; i < items.length; i++){

            entries.put(header[i], items[i]);

        }
    }

    public String getDate(){
        return entries.get("Date");
    }

    public Boolean excludeData(){
        return entries.get("Show").equals("Exclude");
    }

    public Double getItem(String key){

        return Double.parseDouble(entries.get(key));
    }

    public String getValue(String key){
        return entries.get(key);
    }

    public Set<String> getKeySet() {

        return entries.keySet();
    }

    public int size(){
        return this.entries.size();
    }

    public LinkedHashMap getEntry(){

        return this.entries;
    }

    public void setExcluded(){

        String exclusion = this.entries.get("Show");

        if(exclusion.equals("Include")){
            this.entries.put("Show", "Exclude");
        } else {
            this.entries.put("Show", "Include");
        }

    }

    public List<String> getValues() {

        List<String> values = new ArrayList<>();

        Set<String> keys = this.getKeySet();

        for(String key : keys) {

            values.add(this.getValue(key));

        }

        return(values);
    }

}
