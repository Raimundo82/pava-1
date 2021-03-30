package example;

import java.util.Arrays;

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
        System.err.println("drawing a square where with what!");
    }

    public void operation(int... numbers) {

        System.err.println(Arrays.stream(numbers).reduce(0, (i, j) -> i + j));
    }

}
