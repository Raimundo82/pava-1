package interfaceExample;

import ist.meic.pava.MultipleDispatchExtended.UsingMultipleDispatchExtended;

public class Main {
    public static void main(String[] args) {

        UsingMultipleDispatchExtended.invoke(new DataProcess(), "process", new DataStructure());
    }
}