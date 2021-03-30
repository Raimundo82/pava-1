package example;

import java.util.Arrays;

public class Screen extends Device {

    public void draw(Line l, Brush b) {
        System.err.println("draw a line where and with what?");
    }

    public void draw(Line l, Pencil p) {
        System.err.println("drawing a line on screen with pencil!");
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

    public void draw(Square s, Crayon c) {
        System.err.println("drawing a square on screen with crayon!");
    }

    public void operation(int... numbers) {

        System.err.println(Arrays.stream(numbers).reduce(0, (i, j) -> i - j));
    }
}
