package example2;

public class Screen extends Device  {


    public void draw(Line l) {
        System.err.println(("draw line on Screen?"));
    }

    public void draw(Circle c) {
        System.err.println(("draw circle on Screen?"));
    }
    public void draw(Shape s) {
        System.err.println(("draw what on Screen?"));
    }
}
