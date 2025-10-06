package music.reaction;

import music.I;
import music.UC;
import music.graphics.G;

import java.awt.*;
import java.io.*;
import java.util.*;

public class Shape implements Serializable {
    public static Shape.Database DB = Shape.Database.load();
    public static Shape DOT = DB.get("DOT");
    public static Collection<Shape> LIST = DB.values();
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
        public static Database load() {
            Database res  = new Database();
            // initialize db with DOT character
            res.put("DOT", new Shape("DOT"));
            // good practice -- always open/write to files within try block
            try {
                System.out.println("Attempting to load database...");
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
                res = (Database) ois.readObject();
                // print the db shape's keys
                System.out.println("Loaded database successfully - Found: " + res.keySet());
                ois.close();
            } catch (Exception e) {
                System.out.println("Failed to load database!");
                System.out.println(e);
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
            private static int M = 10, W = 60;
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
        }
    }
}
