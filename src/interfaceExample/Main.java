package interfaceExample;

import ist.meic.pava.multipledispatch.UsingMultipleDispatch;
import ist.meic.pava.multipledispatchextended.UsingMultipleDispatchExtended;

public class Main {
    public static void main(String[] args) {

        DataProcess dp = new DataProcess();
        DataStructure ds = new DataStructure();
        UsingMultipleDispatchExtended.invoke(dp, "process", new DataStructure());
        UsingMultipleDispatchExtended.invoke(dp, "test", ds);


    }
}
