package interfaceExample;

import ist.meic.pava.MultipleDispatch.UsingMultipleDispatch;
import ist.meic.pava.MultipleDispatchExtended.UsingMultipleDispatchExtended;

public class Main {
    public static void main(String[] args) {

        DataProcess dp = new DataProcess();
        DataStructure ds = new DataStructure();
        UsingMultipleDispatchExtended.invoke(dp, "process", new DataStructure());
        UsingMultipleDispatch.invoke(dp, "process", new DataStructure());

        dp.test(ds);
        UsingMultipleDispatchExtended.invoke(dp, "test", ds);
        UsingMultipleDispatch.invoke(dp, "test",  ds);


    }
}
