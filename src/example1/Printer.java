package example1;

public class Printer extends Device {

    public void draw(Circle c, Closed cl) {
        System.err.println("draw a closed circle on printer?");
    }


    public void draw(Line l) {
        System.err.println("draw a line on printer?");
    }
    public void draw(Circle c) {
        System.err.println("draw a circle on printer?");
    }


    public void draw(Line l, Open op) {
        System.err.println("draw an open line on printer?");
    }
}
