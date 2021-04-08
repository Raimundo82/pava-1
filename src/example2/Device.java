package example2;

public class Device {

    public void draw(Shape s) {
        System.err.println("draw what where?");
    }

    public void draw(Line l) {
        System.err.println("draw a line where?");
    }

    public void draw(Circle c) {
        System.err.println("draw a circle where?");
    }

}
