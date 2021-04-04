package example;

import ist.meic.pava.MultipleDispatch.UsingMultipleDispatch;
import ist.meic.pava.MultipleDispatchExtended.UsingMultipleDispatchExtended;

public class Example {
    public static void main(String[] args) {
        Device[] devices = new Device[]{new Screen(), /*new Tablet(),*/ new Printer()/*, new Hologram()*/};
        Shape[] shapes = new Shape[]{new Line(), new Circle()/*, new Square()*/};
        Brush[] brushes = new Brush[]{new Pencil(), new Crayon()};

        Shape circle = new Circle();
        Brush pencil = new Pencil();
        Device screen = new Screen();
        screen.draw(circle,circle,pencil);
        UsingMultipleDispatch.invoke(screen,"draw",circle,circle,pencil);


/*        UsingMultipleDispatchExtended.invoke(d, "draw", new Shape(), new Shape(), new Shape());
        UsingMultipleDispatchExtended.invoke(s, "draw", new Circle(), new Shape(), new Circle());
        UsingMultipleDispatchExtended.invoke(s, "draw", new Line(), new Circle(), new Square());
        UsingMultipleDispatchExtended.invoke(s, "draw", new Circle(), new Circle(), new Circle());
        UsingMultipleDispatchExtended.invoke(s, "print", "b", "c");
        UsingMultipleDispatchExtended.invoke(s, "print", "b", "c", "a", "v");
        UsingMultipleDispatchExtended.invoke(s, "sum", 2, 4);
        UsingMultipleDispatchExtended.invoke(s, "sum", 2, 4, 5);
        UsingMultipleDispatchExtended.invoke(s, "mix", true, "a", "b");*/

      //  UsingMultipleDispatchExtended.invoke(new Device(), "draw", sha);
/*        for (Device ds : devices) {
            for (Shape sh : shapes) {
                for (Brush br : brushes) {
                    UsingMultipleDispatch.invoke(ds, "draw", sh, br);
                    UsingMultipleDispatchExtended.invoke(ds, "draw", sh, br);
                    UsingMultipleDispatchExtended.invoke(ds, "draw", sh, br);
                }
            }
        }*/
    }
}

