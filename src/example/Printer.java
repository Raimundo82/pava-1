package example;

import java.util.Arrays;

public class Printer extends Device {

    public void draw(Line l, Brush b) {
        System.err.println("drawing a line on printer with what?");
    }

    public void draw(Square s, Brush b) {
        System.err.println("drawing a square on screen with what?");
    }

    public void draw(Circle c, Pencil p) {
        System.err.println("drawing a circle on printer with pencil!");
    }

    public void draw(Circle c, Crayon r) {
        System.err.println("drawing a circle on printer with crayon!");
    }



    public void operation(int... numbers) {

        System.err.println(Arrays.stream(numbers).reduce(1, (i, j) -> i * j));
    }
}