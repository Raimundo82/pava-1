package example1;

public class Device {


    public void draw(Line l,Open o) {
        System.err.println("draw a line on What?");
    }
    public void draw(Circle c,Closed cl) {
        System.err.println("draw a circle on what?");
    }

    public void draw(Line l,Closed cl) {
        System.err.println("draw an open shape on what?");
    }





}