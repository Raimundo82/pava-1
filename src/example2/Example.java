package example2;

import ist.meic.pava.MultipleDispatch.UsingMultipleDispatch;
import ist.meic.pava.MultipleDispatchExtended.UsingMultipleDispatchExtended;

public class Example {
    public static void main(String[] args) {
        Device[] devices = new Device[]{new Screen(), new Printer()};
        Shape[] shapes = new Shape[]{new Line(), new Circle()};

        for (Device device : devices) {
            for (Shape shape : shapes) {
                device.draw(shape);
                UsingMultipleDispatch.invoke(device, "draw", shape);
                UsingMultipleDispatchExtended.invoke(device, "draw", shape);

            }
        }
    }
}

