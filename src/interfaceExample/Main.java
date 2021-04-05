package interfaceExample;

import ist.meic.pava.MultipleDispatch.UsingMultipleDispatch;
import ist.meic.pava.MultipleDispatchExtended.UsingMultipleDispatchExtended;

public class Main {
    public static void main(String[] args) {

        UsingMultipleDispatch.invoke(new DataProcess(), "asd", new DataStructure());
    }
}
