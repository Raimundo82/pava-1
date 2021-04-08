package example2;

public class Printer extends Device {
    public void draw(Shape s) {
        System.err.println("draw what on printer?");
    }

    public void draw(Line l) {
        System.err.println("drawing a line on printer!");
    }

    public void draw(Circle c) {
        System.err.println("drawing a circle on printer!");
    }
}
