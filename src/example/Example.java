package example;

import ist.meic.pava.MultipleDispatch.UsingMultipleDispatch;
import ist.meic.pava.MultipleDispatchExtended.UsingMultipleDispatchExtended;

public class Example {
    public static void main(String[] args) {
        Device[] devices = new Device[]{new Screen(), /*new Tablet(),*/ new Printer()/*, new Hologram()*/};
        Shape[] shapes = new Shape[]{new Line(), new Circle()/*, new Square()*/};
        Brush[] brushes = new Brush[]{new Pencil(), new Crayon()};


        for (Device ds : devices) {
            for (Shape sh : shapes) {
                for (Brush br : brushes) {
                    ds.draw(sh, br);
                    UsingMultipleDispatch.invoke(ds, "draw", sh, br);
                    UsingMultipleDispatchExtended.invoke(ds, "draw", sh, br);
                }
            }
        }
    }
}

