package exampleExtensions;

import ist.meic.pava.multipledispatchextended.UsingMultipleDispatchExtended;

public class Main {
    public static void main(String[] args) {
/*        new Device().operation(1, 2, 3, 4, 5);
        UsingMultipleDispatchExtended.invoke(new Device(), "operation", 1, 2, 3, 4, 5);*/

        new Device().mix(true, "1");
        UsingMultipleDispatchExtended.invoke(new Device(), "mix", true, "1");

/*        new Device().operation(1,2);
        UsingMultipleDispatch.invoke(new Device(), "operation", 1,2);
        UsingMultipleDispatchExtended.invoke(new Device(), "operation", 1,2);*/


    }
}
