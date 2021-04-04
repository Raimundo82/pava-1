package interfaceExample;

import java.util.List;
import java.util.Set;

public class DataProcess extends CollectionDataProcess {
    public void process(Set set) {
        System.err.println("Processing a data set");
    }

    public void process(List list) {
        System.err.println("Processing a data list");
    }



}
