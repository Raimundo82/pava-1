package example;

public class Device {

    public void draw(Object...o) {
        System.out.println("test");
    }

    public void draw(Shape s, Brush b) {
        System.err.println("draw what where and with what?");
    }

    public void draw(Line l, Brush b) {
        System.err.println("draw a line where and with what?");
    }

    public void draw(Circle c, Brush b) {
        System.err.println("draw a circle where and with what?");
    }

/*
    public void draw(Shape s, Crayon c) {
        System.err.println("draw what where and with crayon?");
    }


    public void draw(Line l, Brush b) {
        System.err.println("draw a line where and with what?");
    }

    public void draw(Circle c, Brush b) {
        System.err.println("draw a circle where and with what?");
    }

    public void draw(Shape ...shapes) {
        System.err.println("draw what shapes where?");
    }

    public void print(String... text) {
        System.err.println("Varargs");
    }

    public void print(String s1, String s2) {
        System.err.println("Two parameters");
    }

    public void sum(Integer i, Integer j) {
        System.err.println("Two Integers");
    }

    public void mix(Boolean b, String... arr) {
        System.err.println("test mix");
    }

    public void sum(int... numbers) {
        System.err.println(Arrays.stream(numbers).reduce(0, Integer::sum));
    }

    public void sum(int i, int j) {
        System.err.println(i + j);
    }*/

}
