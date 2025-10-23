package reaction;

import music.*;
import graphics.G;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class Shape implements Serializable {
    public static Shape.Database DB = Shape.Database.load();
    public static Shape DOT = DB.get("DOT");
    public static Collection<Shape> LIST = DB.values();
    public static Trainer TRAINER = new Trainer();
    public Prototype.List prototypes = new Prototype.List();
    public String name;


    public Shape(String name) {this.name = name;}

    public static Shape recognize(Ink ink) {
        // handle dots
        if (ink.vs.size.x < UC.dotThreshold && ink.vs.size.y < UC.dotThreshold) {return DOT;}

        // matching for the closest shape to what we have
        Shape bestMatch = null; // recognize failed, so we return null
        int bestSoFar = UC.noMatchDist;
        for (Shape shape : LIST) {
            int d = shape.prototypes.bestDist(ink.norm);
            if (d < bestSoFar) {
                bestMatch = shape;
                bestSoFar = d;
            }
        }
        return bestMatch;
    }

    // ---------------------- Database ---------------------
    // kind of a BST that stores information alphabetical
    public static class Database extends TreeMap<String, Shape> {
        private static String filename = UC.shapeDatabaseFilename;

        private Database () {
            super();
            put("DOT", new Shape("DOT"));
        }

        private Shape forceGet(String name){ // always returns Shape..
            if(!DB.containsKey(name)){DB.put(name, new Shape(name));} //..adds new if necessary
            return DB.get(name);
        }
        public void train(String name, Ink.Norm norm){if(isLegal(name)){forceGet(name).prototypes.train(norm);}}

        public static Database load(){
            Database res;
            try{
                System.out.println("attempting DB load..");
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
                res = (Shape.Database) ois.readObject();
                System.out.println("Successful load - found" + res.keySet());
                ois.close();
            } catch(Exception e) {
                System.out.println("Load failed.");
                System.out.println(e);
                res = new Database();
            }
            return res;
        }

        // handles serialization
        public static void save() {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
                oos.writeObject(DB);
                System.out.println("Saved database successfully: - " + filename);
                oos.close();
            } catch (Exception e) {
                System.out.println("Failed to save database!");
                System.out.println(e);
            }
        }
        // check states
        public boolean isKnown(String name) {return containsKey(name);}
        public boolean isUnknown(String name) {return !containsKey(name);}
        public boolean isLegal(String name) {return !name.equals("") && !name.equals("DOT");}
    }

    // ---------------------- Prototype ---------------------
    public static class Prototype extends Ink.Norm implements Serializable {
        public int nBlend = 1;
        public void blend(Ink.Norm norm) {blend(norm, nBlend); nBlend++;}

        // ------------------ List --------------------
        public static class List extends ArrayList<Prototype> implements I.Show {
            public static Prototype bestMatch; // sets a side effect bestDist
            private static int M = 10, W = 60, showBoxHeight = M + W;
            private static G.VS showBox = new G.VS(M, M, W, W);

            public int bestDist(Ink.Norm norm) {
                bestMatch = null;
                int bestSoFar = UC.noMatchDist;
                for (Prototype p : this) {
                    int d = p.dist(norm);
                    if (d < bestSoFar) {bestSoFar = d; bestMatch = p;}
                }
                return bestSoFar;
            }

            public void show(Graphics g) {
                g.setColor(Color.ORANGE);
                for (int i = 0; i < size(); i++) {
                    Prototype p = get(i);
                    int x = M + i * (M + W);
                    showBox.loc.set(x, M);
                    p.drawAt(g, showBox);
                    g.drawString("" + p.nBlend, x, 20);
                }
            }

            public void train(Ink.Norm norm){
                if(bestDist(norm) < UC.noMatchDist){ // we found a match so blend
                    bestMatch.blend(norm);
                }else{
                    add(new Shape.Prototype()); // didn't match so add a new one (from Ink.BUFFER)
                }
            }
        }
    }

    // ---------------------- Trainer --------------- //
    public static class Trainer implements I.Show, I.Area{

        private Trainer(){}; // Singleton

        public static String UNKNOWN = " <- this name is currently Unknown.";
        public static String ILLEGAL = " <-this name is NOT a legal Shape name.";
        public static String KNOWN   = " <-this is a known shape.";

        public static String curName = "";
        public static String curState = ILLEGAL;

        public static Shape.Prototype.List pList = new Shape.Prototype.List();

        public void setState(){
            curState = !Shape.DB.isLegal(curName) ? ILLEGAL : UNKNOWN;
            if(curState == UNKNOWN){
                if(Shape.DB.isKnown(curName)){
                    curState = KNOWN;
                    pList = Shape.DB.get(curName).prototypes;
                }else{ // it really is UNKNOWN
                    pList = null;
                }
            }
        }

        // I.Show functions
        public void show(Graphics g){
            G.fillback(g);
            g.setColor(Color.BLACK);
            g.drawString(curName, 600,30);
            g.drawString(curState, 700,30);
            g.setColor(Color.RED);
            Ink.BUFFER.show(g);
            if(pList != null){pList.show(g);}
        }

        // I.Area functions
        public boolean hit(int x, int y){return true;}
        public void dn(int x, int y){Ink.BUFFER.dn(x,y);}
        public void drag(int x, int y){Ink.BUFFER.drag(x,y);}
        public void up(int x, int y){
            if (removePrototype(x,y)) {return;}

            Ink.BUFFER.up(x,y);
            Ink ink = new Ink();
            Shape.DB.train(curName, ink.norm); // safe because legal name test is done in Database
            setState(); // possibly convert previously UNKNOWN to KNOWN
        }
        private boolean removePrototype(int x, int y) {
            int H = Prototype.List.showBoxHeight;
            if (y < H) { //if stroke ended in show box, don't train, just delete
                int iBox = x / H; // compute box number
                Prototype.List pList = Trainer.pList;
                if (pList != null && iBox < pList.size()) {
                    pList.remove(iBox);
                }
                Ink.BUFFER.clear();
                return true; // tell up() we were in show box areas
            }
            return false;
        }

        public void keyTyped(KeyEvent ke) {
            char c = ke.getKeyChar();
            System.out.println("Typed: " + c); // debug
            if(c == 0x0D || c == 0x0A){Shape.DB.save();}
            curName = (c == ' ' || c == 0x0D || c == 0x0A)? "": curName + c; // x0D & x0A are ascii CR & LF
            setState();
        }
    }
}
