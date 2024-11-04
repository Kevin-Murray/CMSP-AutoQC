package cmsp.quickqc.visualizer.datamodel;

public class SampleEntry {

    private final String field;
    private final String value;

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
