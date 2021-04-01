package example1;


import ist.meic.pava.MultipleDispatch.UsingMultipleDispatch;
import ist.meic.pava.MultipleDispatchExtended.UsingMultipleDispatchExtended;

public class Example {
    public static void main(String[] args) {
        Device[] devices = new Device[]{new Device()};
        Shape shape = new Shape();

        for (Device device : devices) {

                    UsingMultipleDispatchExtended.invoke(device, "draw", shape);

            }
        }
    }
