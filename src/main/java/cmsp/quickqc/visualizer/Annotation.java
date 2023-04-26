package cmsp.quickqc.visualizer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class Annotation {

    private String date;
    private String instrument;
    private String config;
    private String matrix;
    private String type;
    private String comment;

    final private LinkedHashMap<String, String> annotation;

    public Annotation(String date, String instrument, String config, String matrix,
                      String type, String comment) {
        this.date = date;
        this.instrument = instrument;
        this.config = config;
        this.matrix = matrix;
        this.type = type;
        this.comment = comment;

        this.annotation = new LinkedHashMap<>();
        annotation.put("Date", date);
        annotation.put("Instrument", instrument);
        annotation.put("Configuration", config);
        annotation.put("Matrix", matrix);
        annotation.put("Type", type);
        annotation.put("Comment", comment);
    }

    public String getDate(){
        return  this.date;
    }

    public String getInstrument(){
        return this.instrument;
    }

    public String getConfig(){
        return this.config;
    }

    public String getMatrix(){
        return this.matrix;
    }

    public String getType(){
        return this.type;
    }

    public String getComment(){
        return this.comment;
    }

    public int size(){
        return this.annotation.size();
    }

    public Set<String> getKeySet() {

        return annotation.keySet();
    }

    public String getValue(String key){
        return annotation.get(key);
    }

    public List<String> writeValues() {

        List<String> values = new ArrayList<>();

        Set<String> keys = this.getKeySet();

        for(String key : keys) {

            values.add("\"" + this.getValue(key) + "\"");

        }

        return(values);
    }

}
