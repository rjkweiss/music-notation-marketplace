package music;

import reaction.Mass;

import java.awt.*;

public class Beam extends Mass {
    public Stem.List stems = new Stem.List();
    public static Polygon poly;

    static {
        int [] foo = {0, 0, 0, 0};
        poly = new Polygon(foo, foo, 4);
    }

    public Beam(Stem f, Stem l) {
        super("NOTE");
        addStem(f);
        addStem(l);
    }



    public void show(Graphics g) {g.setColor(Color.BLACK); drawBeamGroup(g);}

    public void drawBeamGroup(Graphics g) {
        setMasterBeam();
        Stem firstStem = first();
        int H = firstStem.staff.fmt.H, sH = firstStem.isUp ? H : -H;
        int nPrev = 0, nCurr = firstStem.nFlags, nNext = stems.get(1).nFlags;
        int pX;
        int cX = firstStem.x();
        int bX = cX + 3 * H; // forward leaning beam on first stem from cx, bx
        if (nCurr > nNext) {drawBeamStack(g, nNext, nCurr, cX, bX, sH);} // beamlets on first stem
        for (int curr = 1; curr < stems.size(); curr++) {
            Stem sCurr = stems.get(curr);
            pX = cX;
            cX = sCurr.x();
            nPrev = nCurr;
            nCurr = nNext;
            nNext = (curr < stems.size() - 1) ? stems.get(curr + 1).nFlags : 0;
            int nBack = Math.min(nPrev, nCurr);
            drawBeamStack(g, 0, nBack, pX, cX, sH); // draw beams back to previous stem
            if (nCurr > nPrev && nCurr > nNext) {
                if (nPrev < nNext) {
                    bX = cX + 3 * H;
                    drawBeamStack(g, nNext, nCurr, cX, bX, sH);
                } else {
                    bX = cX - 3 * H;
                    drawBeamStack(g, nPrev, nCurr, bX, cX, sH);
                }
            }
        }
    }

    public static void setPoly(int x1, int y1, int x2, int y2, int h) {
        int [] a = poly.xpoints;
        a[0] = x1; a[1] = x2; a[2] = x2; a[3] = x1;
        a = poly.ypoints;
        a[0] = y1; a[1] = y2; a[2] = y2 + h; a[3] = y1 + h;
    }

    public static void drawBeamStack(Graphics g, int n1, int n2, int x1, int x2, int h) {
        int y1 = yOfX(x1), y2 = yOfX(x2);
        for (int i = n1; i < n2; i++) {
            setPoly(x1, y1 + i * 2 * h, x2, y2 + i * 2 * h, h);
            g.fillPolygon(poly);
        }
    }

    public void addStem(Stem s) {
        if (s.beam == null) {
            stems.addStem(s);
            s.beam = this;
            s.nFlags = 1;
            stems.sort();
        }
    }
    public Stem first() {return stems.get(0);}
    public Stem last() {return stems.get(stems.size() - 1);}
    public void deleteBeam() { // stems still exist, stems & dots still exist; just remove beams from stems
        for (Stem s : stems) {s.beam = null;}
        deleteMass();
    }

    // Math helper methods
    public static int yOfX(int x, int x1, int y1, int x2, int y2) {
        int dy =  y2 - y1, dx = x2 - x1;
        return (x - x1) * dy / dx + y1;
    }
    public static int mx1, my1, mx2, my2; // coordinates for master beam
    public static int yOfX(int x) {
        int dy = my2 - my1, dx = mx2 - mx1;
        return (x - mx1) * dy / dx + my1;
    }
    public static boolean verticalLineCrossesSegment(int x, int y1, int y2, int bX, int bY, int eX, int eY) {
        if (x < bX || x > eX) {return false;}
        int y = yOfX(x, bX, bY, eX, eY);
        if (y1 < y2) {return y1 < y && y < y2;} else {return y2 < y && y < y1;}
    }
    public static void setMasterBeam(int x1, int y1, int x2, int y2) {
        mx1 = x1;
        my1 = y1;
        mx2 = x2;
        my2 = y2;
    }
    public void setMasterBeam() {
        mx1 = first().x();
        my1 = first().yBeamEnd();
        mx2 = last().x();
        my2 = last().yBeamEnd();
    }

    public void removeStem(Stem stem) {
        if (stem == first() || stem == last()) {
            deleteBeam();
        } else {
            stems.remove(stem);
            stems.sort();
        }
    }
}
