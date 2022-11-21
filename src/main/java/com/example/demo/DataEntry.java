package com.example.demo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataEntry {

    private LinkedHashMap<String, String> entries;

    public DataEntry(String[] header, String[] entry){

        this.entries = new LinkedHashMap<>();
        addItems(header, entry);
    }

    public void addItems(String[] header, String[] items){

        for(int i = 0; i < items.length; i++){

            if(header[i].equals("Date")){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                LocalDateTime dateTime = LocalDateTime.parse(items[i], formatter);
                entries.put(header[i], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(dateTime));
            } else {
                entries.put(header[i], items[i]);
            }
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

}
