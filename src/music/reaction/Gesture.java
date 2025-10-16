package music.reaction;

import music.I;
import music.graphics.G;

public class Gesture {
    public Shape shape;
    public G.VS vs;
    public static String recognized = "null";

    // private constructor is used for singletons -- we only need one
    // object and that one only --singleton is a pattern
    // other examples: spooler for printers
    // private constructors are also for factory methods -- cannot return null
    // return objects that have already been built or build new one if there are no
    // already built ones
    private Gesture(Shape shape, G.VS vs) {
        this.shape = shape;
        this.vs = vs;
    }

    // example of factory pattern method
    public static Gesture getNew(Ink ink) {
        // can return null
        Shape s = Shape.recognize(ink);
        return s == null ? null : new Gesture(s, ink.vs);
    }

    public static I.Area AREA = new I.Area() {
        public boolean hit(int x, int y) {return true;}
        public void dn(int x, int y) {Ink.BUFFER.dn(x, y);}
        public void drag(int x, int y) {Ink.BUFFER.drag(x, y);}
        public void up(int x, int y) {
            Ink.BUFFER.up(x, y);
            Ink ink = new Ink();
            Gesture gest = Gesture.getNew(ink); // can fail if unrecognized
            Ink.BUFFER.clear();
            recognized = gest == null ? "null" : gest.shape.name;
            if (gest != null) {
                Reaction r = Reaction.best(gest); // can fail, possibly no reaction wants it
                if (r != null) {r.act(gest);} else {recognized += " no bids";}
            }
        }
    };
}
