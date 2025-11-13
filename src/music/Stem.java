package music;

import reaction.Gesture;
import reaction.Reaction;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class Stem extends Duration implements Comparable<Stem> {
    public Staff staff;
    public Head.List heads = new Head.List();
    public Beam beam;
    public boolean isUp = true;

    public Stem(Staff staff, Head.List heads, boolean up) {
        super();
        this.staff = staff;
        this.isUp = up;
        for (Head h : heads) {h.unStem(); h.stem = this;}
        this.heads = heads;
        staff.sys.stems.addStem(this);
        setWrongSides();

        // reactions
        addReaction(new Reaction("E-E") { // increment flags on stem
            public int bid(Gesture g) {
                int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH();
                int xs = Stem.this.x();
                if (x1 > xs || x2 < xs) {return UC.noBid;}
                int y1 = Stem.this.yLow(), y2 = Stem.this.yHigh();
                if (y < y1 || y > y2) {return UC.noBid;}
                return Math.abs(y - (y1 + y2) / 2) + 60; // bias so sys 'E-E' can now bid
            }
            public void act(Gesture g) {Stem.this.incFlags();}
        });

        addReaction(new Reaction("W-W") { // decrement flags on stem
            public int bid(Gesture g) {
                int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH();
                int xs = Stem.this.x();
                if (x1 > xs || x2 < xs) {return UC.noBid;}
                int y1 = Stem.this.yLow(), y2 = Stem.this.yHigh();
                if (y < y1 || y > y2) {return UC.noBid;}
                return Math.abs(y - (y1 + y2) / 2);
            }
            public void act(Gesture g) {
                Stem.this.decFlags();
                if (nFlags == 0 && beam != null) {beam.deleteBeam();}
            }
        });
    }

    public static Stem getStems(Staff staff, Time time, int y1, int y2, boolean up) {
        Head.List heads = new Head.List();
        for (Head h: time.heads) {
            int yH = h.y();
            if (yH > y1 && yH < y2) {heads.add(h);}
        }
        if (heads.size() == 0) {return null;}
        Beam beam = internalStem(staff.sys, time.x, y1, y2); // possibly internal stem in beam group
        Stem res = new Stem(staff, heads, up);
        if (beam != null) {beam.addStem(res); res.nFlags = 1;}
        return res;
    }

    public static Beam internalStem(Sys sys, int x, int y1, int y2) {
        for (Stem s: sys.stems) {
            if (s.beam != null && s.x() < x && s.yLow() < y2 && s.yHigh() > y1) {
                int bX = s.beam.first().x(), bY = s.beam.first().yBeamEnd();
                int eX = s.beam.last().x(), eY = s.beam.last().yBeamEnd();
                if (Beam.verticalLineCrossesSegment(x, y1, y2, bX, bY, eX, eY)) {return s.beam;}
            }
        }
        return null;
    }

    public Head firstHead() {return heads.get(isUp ? heads.size() - 1 : 0);}
    public Head lastHead() {return heads.get(isUp ? 0 : heads.size() - 1);}
    public int yFirstHead() {
        if (heads.size() == 0) {return 200;} // guard empty stems
        Head h = firstHead();
        return h.staff.yOfLine(h.line);
    }
    public int yLow() {return isUp ? yBeamEnd() : yFirstHead();}
    public int yHigh() {return isUp ? yFirstHead() : yBeamEnd();}
    public boolean isInternalStem() {
        if (beam == null) {return false;}
        if (this == beam.first() || this == beam.last()) {return false;}
        return true;
    }
    public int yBeamEnd() {
        if (heads.size() == 0) {return 100;} // guard empty stems

        if (isInternalStem()) {beam.setMasterBeam(); return Beam.yOfX(x());}

        Head h = lastHead();
        int line = h.line;
        line += isUp ? -7 : 7;
        int flagInc = nFlags > 2 ? 2 * (nFlags - 2) : 0;
        line += isUp ? -flagInc : flagInc;
        if ((isUp && line > 4) || (!isUp && line < 4)) {line = 4;}
        return h.staff.yOfLine(line);
    }
    public int x() {
        if (heads.isEmpty()) {return 100;} // guard empty stems
        Head h = firstHead();
        return h.time.x + (isUp ? h.w() : 0);
    }
    public void deleteStem() { // only call if list of heads is empty
        if (!heads.isEmpty()) {System.out.println("Deleting stem with heads on it");}
        staff.sys.stems.remove(this);
        if (beam != null) {beam.removeStem(this);}
        deleteMass();
    }
    public void setWrongSides() {
        Collections.sort(heads);
        int i, last, next;
        if (isUp) {i = heads.size() - 1; last = 0; next = -1;} else {i = 0; last = heads.size() - 1; next = 1;}
        Head pH = heads.get(i);
        pH.wrongSide = false;

        while (i != last) {
            i += next;
            Head nH = heads.get(i);
            nH.wrongSide = (pH.staff == nH.staff && Math.abs(nH.line - pH.line) <= 1 && !pH.wrongSide);
            pH = nH;
        }
    }

    public void show(Graphics g) {
        if (nFlags >= -1 && heads.size() > 0) {
            int x = x(), h = staff.fmt.H, yH = yFirstHead(), yB = yBeamEnd();
            g.drawLine(x, yH, x, yB);

            if (nFlags > 0 && beam == null) {
                if (nFlags == 1) {(isUp ? Glyph.FLAG1D : Glyph.FLAG1U).showAt(g, h, x, yB);}
                if (nFlags == 2) {(isUp ? Glyph.FLAG2D : Glyph.FLAG2U).showAt(g, h, x, yB);}
                if (nFlags == 3) {(isUp ? Glyph.FLAG3D : Glyph.FLAG3U).showAt(g, h, x, yB);}
                if (nFlags == 4) {(isUp ? Glyph.FLAG4D : Glyph.FLAG4U).showAt(g, h, x, yB);}
            }
        }
    }

    @Override
    public int compareTo(Stem s) {return x() - s.x();}

    // ------------------------------- List ---------------------- //
    public static class List extends ArrayList<Stem> {
        public int yMin = Integer.MAX_VALUE, yMax = Integer.MIN_VALUE;

        public void addStem(Stem s) {
            add(s);
            if (s.yLow() < yMin) {yMin = s.yLow();}
            if (s.yHigh() > yMax) {yMax = s.yHigh();}
        }

        public boolean fastReject(int y) {
            return y > yMax || y < yMin;
        }

        public void sort() {
            Collections.sort(this);
        }

        public ArrayList<Stem> allIntersectors(int x1, int y1, int x2, int y2) {
            ArrayList<Stem> res = new ArrayList<>();
            for (Stem s : this) {
                if (Beam.verticalLineCrossesSegment(s.x(), s.yLow(), s.yHigh(), x1, y1, x2, y2)) {res.add(s);}
            }
            return res;
        }
    }
}
