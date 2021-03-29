package example;

import static ist.meic.pava.MultipleDispatch.UsingMultipleDispatch.invoke;

public class Example {
    public static void main(String[] args) {
        Device[] devices = new Device[]{new Screen(), new Printer()};
        Shape[] shapes = new Shape[]{new Line(), new Circle()};
        Brush[] brushes = new Brush[]{new Pencil(), new Crayon()};
        for (Device device : devices) {
            for (Shape shape : shapes) {
                for (Brush brush : brushes) {
                    invoke(device, "draw", shape, brush);
                }
            }
        }
    }
}