package example1;

public class Screen extends Device {

    public void draw(Circle s1, Shape s2, Brush b) {
        System.err.println("drawing on screen one circle and what with what?");
    }
    public void draw(Shape s1, Circle c, Pencil p) {
        System.err.println("drawing on screen what and one circle with Pencil?");;
    }

}
