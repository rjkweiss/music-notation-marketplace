package music.graphics;

import java.awt.*;
import java.util.Random;

public class G {
    public static Random RND = new Random();

    public static int rnd(int max) {return RND.nextInt(max);}
    public static void fillback(Graphics g) {g.setColor(Color.white); g.fillRect(0, 0, 5000, 5000);}
    public static Color rndColor() {
        return new Color(rnd(256), rnd(256), rnd(256));
    }

    // ----------------------------- V ----------------------------------------
    public static class V {
        public static Transform T = new Transform();
        public int x, y;

        public V(int x, int y) { this.x = x; this.y = y; }

        public V(V v) {
            set(v.x, v.y);
        }

        public void set(int x, int y) { this.x = x; this.y = y; }
        public void set(V v) { set(v.x, v.y); }
        public void add(V v) { this.x += v.x; this.y += v.y; }
        public void blend(V v, int k) {set((k * x + v.x) / (k + 1), (k * y + v.y) / (k + 1));}
        public void setT(V v) {set(v.tx(), v.ty());}
        public int tx() { return x * T.n/T.d + T.dx;}
        public int ty() { return y * T.n/T.d + T.dy;}

        // ------------------------- Transform -----------------------------
        public static class Transform {
            public int dx, dy, n, d; // isomorphic scale = n / d
            // (x', y') = (x * n/d + dx, y * n/d + dy)
            public void setScale(int oW, int oH, int nW, int nH) {
                n = (nW > nH) ? nW : nH;
                d = (oW > oH) ? oW : oH;
            }
            public int setOff(int oX, int oW, int nX, int nW) {return (-oX - oW/2) * n / d + nX + nW/2;}
            public void set(VS oVS, VS nVS) {
                setScale(oVS.size.x,  oVS.size.y, nVS.size.x, nVS.size.y);
                dx = setOff(oVS.loc.x, oVS.size.x, nVS.loc.x, nVS.size.x);
                dy = setOff(oVS.loc.y,  oVS.size.y, nVS.loc.y, nVS.size.y);
            }
            public void set(BBox from, VS to) {
                setScale(from.h.size(), from.v.size(), to.size.x, to.size.y);
                dx = setOff(from.h.lo, from.h.size(), to.loc.x, to.size.x);
                dy = setOff(from.v.lo, from.v.size(), to.loc.y, to.size.y);
            }
        }
    }

    // ----------------------------- VS ----------------------------------------
    public static class VS {
        public V loc, size;

        public VS(int x, int y, int w, int h) { loc = new V(x, y); size = new V(w, h); }
        public void fill(Graphics g, Color c) {
            g.setColor(c);
            g.fillRect(loc.x, loc.y, size.x, size.y);
        }
        public boolean hit(int x, int y) {
            return loc.x <= x && loc.y <= y && x <= (loc.x + size.x) && y <= (loc.y + size.y);
        }
    }

    // ----------------------------- LoHi ----------------------------------------
    public static class LoHi {
        public int lo, hi;
        public LoHi(int lo, int hi) {this.lo = lo; this.hi = hi;}
        public void set(int v) {lo = v; hi = v;}
        public void add(int v) {if (v < lo) {lo = v;} if (v > hi) {hi = v;}}
        public int size() {return (hi - lo) == 0 ? 1 : hi - lo;}
    }

    // ----------------------------- BBox ----------------------------------------
    public static class BBox {
        public LoHi h, v; // h - horizontal bound, v - vertical bound
        public BBox() {h  = new LoHi(0, 0); v = new LoHi(0, 0);}
        public void set(int x, int y) {h.set(x); v.set(y);}
        public void add(int x, int y) {h.add(x); v.add(y);}
        public void add(V v) {add(v.x, v.y);}
        public VS getNewVS(){return new VS(h.lo, v.lo, h.hi - h.lo, v.hi - v.lo);}
        public void draw(Graphics g) {g.drawRect(h.lo, v.lo, h.hi - h.lo, v.hi - v.lo);}
    }

    // ----------------------------- PL ----------------------------------------
    public static class PL {
        public V[] points;
        public PL(int count) {
            points = new V[count];
            for (int i = 0; i < count; i++) { points[i] = new V(0, 0); }
        }
        public int size() { return points.length; }
        public void transform() {
            for (int i = 0; i < points.length; i++) {points[i].setT(points[i]);}
        }
        public void drawN(Graphics g, int n) {
            for (int i = 1; i < n; i++) {
                g.drawLine(points[i - 1].x, points[i - 1].y, points[i].x, points[i].y);
            }
            drawNDots(g, n);
        }
        public void drawNDots(Graphics g, int n) {
            g.setColor(Color.RED);
            for (int i = 0; i < n; i++) {
                g.drawOval(points[i].x - 2, points[i].y - 2, 4, 4);
            }
        }
        public void draw(Graphics g) { drawN(g, size()); }
    }
}
