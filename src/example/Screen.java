package example;

public class Screen extends Device {



    public void draw(Line l, Brush b) {
        System.err.println("drawing a line where and with what?");
    }

    public void draw(Line l, Pencil p) {
        System.err.println("drawing a line where and with pencil?");
    }


    public void draw(Line l, Crayon c) {
        System.err.println("drawing a line on screen with crayon!");
    }

    public void draw(Circle c, Brush b) {
        System.err.println("drawing a circle on screen with what?");
    }

    public void draw(Circle c, Pencil p) {
        System.err.println("drawing a circle on screen with pencil!");
    }

    public void draw(Circle s1, Shape s2, Brush b) {
        System.err.println("drawing on screen one circle and what two what with what?");
    }

    public void draw(Shape s1, Circle s2, Pencil b) {
        System.err.println("drawing on screen what and one circle with pencil?");
    }

/*
    public void draw(Shape c1, Shape s, Shape c2) {
        System.err.println("draw three what on screen?");
    }

    public void draw(Circle... circles) {
        System.err.println("draw circles on screen!");
    }


    public void draw(Shape... shapes) {
        System.err.println("draw what on screen?");
    }*/


}
