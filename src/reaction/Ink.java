package reaction;

import music.I;
import music.UC;
import graphics.G;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class Ink implements I.Show, Serializable {
    public static final Buffer BUFFER = new Buffer();
    public Norm norm;
    public G.VS vs;

    public Ink() {
        norm = new Norm();
        vs = BUFFER.bbox.getNewVS();
    }

    @Override
    public void show(Graphics g) {
        g.setColor(UC.inkColor);
        norm.drawAt(g, vs);
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
        }
        // required by I.Area
        public boolean hit(int x, int y) { return true; }
        public void dn(int x, int y) { clear(); bbox.set(x, y); add(x, y); }
        public void drag(int x, int y) { add(x, y); }
        public void up(int x, int y) { add(x, y); }
    }

    // ----------------------- norm ------------------------
    public static class Norm extends G.PL implements Serializable {
        public static final int N = UC.normSampleSize, MAX = UC.normCordMax;
        public static final G.VS NCS = new G.VS(0, 0, MAX, MAX); // norm coordinate system

        public Norm() {
            super(N);
            BUFFER.subSample(this);
            G.V.T.set(BUFFER.bbox, NCS);
            transform();
        }

        public void drawAt(Graphics g, G.VS vs) {
            G.V.T.set(NCS, vs);
            for (int i = 1; i < N; i++) {
                g.drawLine(points[i - 1].tx(), points[i - 1].ty(), points[i].tx(), points[i].ty());
            }
        }
        public void blend(Norm norm, int nBlend) {
            for (int i = 0; i < N; i++) {
                points[i].blend(norm.points[i], nBlend);
            }
        }

        public int dist(Norm n) {
            int res = 0;
            for (int i = 0; i < N; i++) {
                int dx = points[i].x - n.points[i].x, dy = points[i].y - n.points[i].y;
                res += dx * dx + dy * dy;
            }
            return res;
        }
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
