package example1;


import ist.meic.pava.MultipleDispatchExtended.UsingMultipleDispatchExtended;

public class Example {
    public static void main(String[] args) {
        Printer d = new Printer();
        Circle c = new Shape();
        Line l = new Shape();
        Open o = new Shape();
        Closed cl = new Shape();

        Circle circle = new Shape();
        new Device().draw(circle);
        UsingMultipleDispatchExtended.invoke(new Device(), "draw", circle);


    }
}
