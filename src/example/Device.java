package example;

public class Device {
    public void draw(Shape s, Brush b) {
        System.err.println("draw what where and with what?");
    }

    public void draw(Line l, Brush b) {
        System.err.println("draw a line where and with what?");
    }

    public void draw(Circle c, Brush b) {
        System.err.println("draw a circle where and with what?");
    }

    public void draw(Square s, Brush b) {
        System.err.println("draw a square where and with what?");
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

    public void sum(int i, int j) {
        System.err.println(i + j);
    }
}
