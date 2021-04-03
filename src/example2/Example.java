package example2;

import ist.meic.pava.MultipleDispatchExtended.UsingMultipleDispatchExtended;

public class Example {
    public static void main(String[] args) {
        Device[] devices = new Device[]{new Device(), new Screen()};
        Shape c = new Shape();

        for (Device device : devices) {
            UsingMultipleDispatchExtended.invoke(device, "draw", c);

        }
    }
}

