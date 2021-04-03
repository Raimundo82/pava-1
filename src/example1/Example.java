package example1;


import ist.meic.pava.MultipleDispatchExtended.UsingMultipleDispatchExtended;

import java.util.Arrays;

public class Example {
    public static void main(String[] args) {
        Printer d = new Printer();
        Circle c = new Shape();
        Open o = new Shape();
        Closed cl = new Shape();

        Shape shape = new Shape();
        UsingMultipleDispatchExtended.invoke(new Device(), "draw", shape,shape);

    }
}
