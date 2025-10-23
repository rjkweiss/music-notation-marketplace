package music.reaction;

import music.I;
import music.graphics.G;

import java.util.ArrayList;

public class Gesture {
    public Shape shape;
    public G.VS vs;
    public static String recognized = "null";
    private static List UNDO = new List();

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

    private void redoGesture() {
        Reaction r = Reaction.best(this);
        if (r != null) {r.act(this);}
    }

    private void doGesture() {
        Reaction r = Reaction.best(this);
        if (r != null) {
            UNDO.add(this);
            r.act(this);
        } else {
            recognized += " no bids";
        }
    }

    public static void undo() {
        if (UNDO.size() > 0) {
            UNDO.remove(UNDO.size() - 1); // remove last element
            Layer.nuke(); // eliminate all the masses in the layer
            Reaction.nuke(); // clear the byShape map and reload initial reactions
            UNDO.redo();
        }
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
                if (gest.shape.name.equals("N-N")) {
                    undo();
                } else {
                    gest.doGesture();
                }
            }
        }
    };

    // -------------------------- List ---------------------- //
    public static class List extends ArrayList<Gesture> {
        private void redo() {
            for (Gesture g : this) {g.redoGesture();}
        }
    }
}
