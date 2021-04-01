package example1;

public class Screen extends Device {
    public void draw(Shape s) {
        System.err.println("draw what on screen?");
    }

    public void draw(Line l) {
        System.err.println("drawing a line on screen!");
    }

    public void draw(Circle c) {
        System.err.println("drawing a circle on screen!");
    }
}