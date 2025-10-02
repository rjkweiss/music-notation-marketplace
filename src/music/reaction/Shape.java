package music.reaction;

import music.I;
import music.UC;
import music.graphics.G;

import java.awt.*;
import java.util.ArrayList;

public class Shape {
    public Prototype.List prototypes = new Prototype.List();
    public String name;

    public Shape(String name) {this.name = name;}

    // ---------------------- Prototype ---------------------
    public static class Prototype extends Ink.Norm {
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
