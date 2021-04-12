package example3;

import ist.meic.pava.multipledispatchextended.UsingMultipleDispatchExtended;

import java.util.ArrayList;
import java.util.HashSet;

public class Example {

    public static void main(String[] args) {
        System.out.println("example1");
        new Device().test("1", "2", "3");
        UsingMultipleDispatchExtended.invoke(new Device(), "test", "1", "2", "3");

        System.out.println("example2");
        new Device().test(true, "1", "2", "3");
        UsingMultipleDispatchExtended.invoke(new Device(), "test", true, "1", "2", "3");

        System.out.println("example3");
        new Device().test(true, 1, "1", "2", "3");
        UsingMultipleDispatchExtended.invoke(new Device(), "test", true, 1, "1", "2", "3");


        System.out.println("example4");
        new Device().test(new ArrayList<Boolean>(), new HashSet<Integer>(), "1", "2", "3");
        UsingMultipleDispatchExtended.invoke(new Device(), "test", new ArrayList<>(), new HashSet<>(), "1", "2", "3");


        new Device().test2("1","","");
        UsingMultipleDispatchExtended.invoke(new Device(), "test2","","","");

    }

}
