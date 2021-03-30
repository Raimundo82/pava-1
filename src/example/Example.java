package example;

import ist.meic.pava.MultipleDispatch.UsingMultipleDispatch;
import ist.meic.pava.MultipleDispatchExtended.UsingMultipleDispatchExtended;

public class Example {
    public static void main(String[] args) {
        Device[] devices = new Device[]{new Screen(), new Printer()};
        Shape[] shapes = new Shape[]{new Line(), new Circle()};
        Brush[] brushes = new Brush[]{new Pencil(), new Crayon()};
        UsingMultipleDispatchExtended.invoke(devices[0], "sum", 1,2);
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