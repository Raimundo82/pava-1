package example1;


import ist.meic.pava.MultipleDispatchExtended.UsingMultipleDispatchExtended;

public class Example {
    public static void main(String[] args) {

        Shape s = new Circle();

        new Screen().draw(s, s, new Pencil());
        UsingMultipleDispatchExtended.invoke(new Screen(),"draw",s,s,new Pencil());

    }
}
