package music;

import reaction.*;

import java.awt.*;
import java.util.ArrayList;

public class Head extends Mass implements Comparable<Head> {
    public Glyph forcedGlyph = null;
    public Staff staff;
    public Stem stem = null;
    public int line;
    public Time time;
    public boolean wrongSide = false;

    public Head(Staff staff, int x, int y) {
        super("NOTE");

        this.staff = staff;
        this.time = staff.sys.getTime(x);
        line = staff.lineOfY(y); // snaps to nearest line
        time.heads.add(this);

        System.out.println("Head Constructor line: " + line);

        // add reaction
        addReaction(new Reaction("S-S") { // stem or unstem heads
            public int bid(Gesture g) {
                int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
                int w = Head.this.w(), hY = Head.this.y();
                if (y1 > hY || y2 < hY) {return UC.noBid;}
                int hL = Head.this.time.x, hR = hL + w;
                if (x < hL - w || x > hR + w) {return UC.noBid;}
                if (x < hL + w/2) {return hL - x;}
                if (x > hR - w/2) {return x - hR;}
                return UC.noBid;
            }
            public void act(Gesture g) {
                int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
                Staff staff = Head.this.staff;
                Time t = Head.this.time;
                int w = Head.this.w();
                boolean up = x > (t.x + w/2);

                if (Head.this.stem == null) {
                    t.stemHeads(staff, up, y1, y2);
                } else {
                    t.unStemHeads(y1, y2);
                }
            }
        });
    }

    public int w() {return 24 * staff.fmt.H / 10;}
    public int y() {return staff.yOfLine(line);}
    public int x() {
        int res = time.x;
        if (wrongSide) {res += (stem != null && stem.isUp) ? w() : -w();}
        return res;
    }
    public Glyph normalGlyph() {
        if (stem == null) {return Glyph.HEAD_Q;}
        if (stem.nFlags == -1) {return Glyph.HEAD_HALF;}
        if (stem.nFlags == -2) {return Glyph.HEAD_W;}
        return Glyph.HEAD_Q;
    }
    public void delete() {time.heads.remove(this);} // stub


    public void unStem() {
        if (stem != null) {
            stem.heads.remove(this);
            if (stem.heads.size() == 0) {stem.deleteStem();}
            stem = null;
            wrongSide = false;
        }
    }
    public void joinStem(Stem s) {
        if (stem != null) {unStem();}
        s.heads.add(this);
        stem = s;
    }

    public void show(Graphics g) {
        g.setColor(wrongSide ? Color.GREEN : Color.BLUE);
        if (stem != null && stem.heads.size() != 0 && this == stem.firstHead()) {g.setColor(Color.RED);}
        int H = staff.fmt.H;
        (forcedGlyph != null ? forcedGlyph : normalGlyph()).showAt(g, H, x(), y());
    }

    @Override
    public int compareTo(Head h) {
        return (staff.iStaff != h.staff.iStaff) ? staff.iStaff - h.staff.iStaff : line - h.line;
    }

    // --------------------------------- List ---------------------- //
    public static class List extends ArrayList<Head> {}
}
