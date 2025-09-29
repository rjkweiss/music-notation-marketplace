package music.reaction;

import music.I;
import music.UC;
import music.graphics.G;

import java.awt.*;
import java.util.ArrayList;

public class Ink extends G.PL implements I.Show {
    public static final Buffer BUFFER = new Buffer();
    public static final int K = 10;
    public static G.VS TEMP = new G.VS(100, 100, 100, 100);

    public Ink() {
        super(K);
        BUFFER.subSample(this);
        G.V.T.set(BUFFER.bbox, TEMP);
        transform();
        G.V.T.set(TEMP, BUFFER.bbox.getNewVS());
        transform();
    }

    @Override
    public void show(Graphics g) {
        g.setColor(UC.inkColor);
        draw(g);
    }
    // ------------------------ Buffer ------------------------
    public static class Buffer extends G.PL implements I.Show, I.Area {
        public static final int MAX = UC.inkBufferMax;
        public int n;
        public G.BBox bbox  = new G.BBox();

        private Buffer() { super(MAX); }

        public void add(int x, int y) {
            if (n < MAX) {
                // points[n++].set(x,y) --- similar to the next two lines of code
                points[n].set(x, y);
                n++;
                bbox.add(x, y);
            }
        }
        public void clear() {
            n = 0;
        }
        public void subSample(G.PL pl) {
            int k = pl.size();
            for (int i = 0; i < k; i++) {pl.points[i].set(this.points[i * (n - 1)/(k - 1)]);}
        }
        // required by I.Show
        public void show(Graphics g) {
            // you do not need to use "this" keyword when calling parent functions -- works just fine with
            // just calling function name itself
            this.drawN(g, n);
            bbox.draw(g);
        }
        // required by I.Area
        public boolean hit(int x, int y) { return true; }
        public void dn(int x, int y) { clear(); bbox.set(x, y); add(x, y); }
        public void drag(int x, int y) { add(x, y); }
        public void up(int x, int y) { add(x, y); }
    }

    // ------------------------ list ------------------------
    public static class List extends ArrayList<Ink> implements I.Show {
        @Override
        public void show(Graphics g) {
            //int x = this.get(0).points[0].x;
            g.setColor(UC.inkColor);
            //g.drawString("list size: " + size() + " " + x, 500, 100);
            for (Ink ink : this) {
                ink.show(g);
            }
        }
    }
}
