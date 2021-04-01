package example;

import ist.meic.pava.MultipleDispatch.UsingMultipleDispatch;
import ist.meic.pava.MultipleDispatchExtended.UsingMultipleDispatchExtended;

public class Example {
    public static void main(String[] args) {
        Device[] devices = new Device[]{new Screen(), new Tablet(), new Printer(), new Hologram()};
        Shape[] shapes = new Shape[]{new Line(), new Circle(), new Square()};
        Brush[] brushes = new Brush[]{new Pencil(), new Crayon()};
        Device d = new Device();
        UsingMultipleDispatchExtended.invoke(d, "print", "b", "c");
        UsingMultipleDispatchExtended.invoke(d, "print", "b", "c", "a", "v");
        UsingMultipleDispatchExtended.invoke(d, "sum",2,4);
        UsingMultipleDispatchExtended.invoke(d, "sum",2,4,5);
        UsingMultipleDispatchExtended.invoke(d, "mix", true, "a","b");
        for (Device device : devices) {
            for (Shape shape : shapes) {
                for (Brush brush : brushes) {
                    UsingMultipleDispatch.invoke(device, "draw", shape, brush);
                    UsingMultipleDispatchExtended.invoke(device, "draw", shape, brush);
                }
            }
        }
    }
}
