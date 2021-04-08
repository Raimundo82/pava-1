package example3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Device {

    public void test(String... text) {
        Arrays.stream(text).forEach(t -> System.out.println(t));
    }

    public void test(boolean b, String... text) {
        System.out.println(b);
        Arrays.stream(text).forEach(t -> System.out.println(t));
    }

    public void test(boolean b, int i, String... text) {
        System.out.println(b + " " + i);
        Arrays.stream(text).forEach(t -> System.out.println(t));
    }

    public void test(ArrayList<Boolean> b, HashSet<Integer> i, String... text) {
       // System.out.println(b + " " + i);
        Arrays.stream(text).forEach(t -> System.out.println(t));
    }


    public void test2(String a, String b, String ...text) {
        System.out.println("varargs");
    }

    public void test2(Object a, Object b) {
        System.out.println("non varargs");
    }


}
