package exampleExtensions;

import java.util.Arrays;

public class Device {
    public void operation(Integer i, Integer j) {
        System.err.println(i + " + " + j);
    }

    public void operation(int i, int j) {
        System.err.println(i + j);
    }

    public void operation(int... numbers) {
        System.err.println(Arrays.stream(numbers).reduce(0, Integer::sum));
    }

    public void mix(Boolean b, String... arr) {
        System.err.println("Test mix parameter!");
    }

    public void mix(Boolean b, Object arr) {
        System.err.println("non varargs!");
    }

    public void test(Object o) {
        System.err.println("Dealing with null!");
    }

    public String test() {
        return "No arguments!";
    }

}
