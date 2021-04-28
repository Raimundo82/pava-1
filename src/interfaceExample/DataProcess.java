package interfaceExample;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataProcess  {
    public void process(Set set) {
        System.err.println("Processing a data set");
    }

    public void process(List list) {
        System.err.println("Processing a data list");
    }

    public void process(Collection list) {
        System.err.println("Processing a data list");
    }






    public void test(DataStructure struct) {
        System.err.println("Processing a data structure");
    }




}
