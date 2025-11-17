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
    public Accid accid = null;

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
                    Stem.getStems(staff, t, y1, y2, up);
                } else {
                    t.unStemHeads(y1, y2);
                }
            }
        });

        addReaction(new Reaction("DOT") { // cycle aug dots
            public int bid(Gesture g) {
                int xH = x(), yH = y(), h = staff.fmt.H, w = w();
                int x = g.vs.xM(), y = g.vs.yM();

                if (x < xH || x > xH + 2 * w || y < yH - h || y > yH + h) {return UC.noBid;}
                return Math.abs(xH + w - x) + Math.abs(yH - y);
            }

            public void act(Gesture g) {
                if (Head.this.stem != null) {Head.this.stem.cycleDot();}
            }
        });

        addReaction(new Reaction("NE-SE") { // up arrow create sharp / raise
            public int bid(Gesture g) {
                int x = g.vs.xM(), y = g.vs.yL(); // center top is hotspot
                int hX = Head.this.x() + Head.this.w() / 2, hY = Head.this.y();
                int dx = Math.abs(x - hX), dy = Math.abs(y - hY), diff = dx + dy;
                return diff < 50 ? diff : UC.noBid;
            }

            public void act(Gesture g) {Head.this.accidUp();}
        });

        addReaction(new Reaction("SE-NE") { // down arrow create flat / lower
            public int bid(Gesture g) {
                int x = g.vs.xM(), y = g.vs.yH(); // center bot is hotspot
                int hX = Head.this.x() + Head.this.w() / 2, hY = Head.this.y();
                int dx = Math.abs(x - hX), dy = Math.abs(y - hY), diff = dx + dy;
                return diff < 50 ? diff : UC.noBid;
            }

            public void act(Gesture g) {Head.this.accidDn();}
        });

        addReaction(new Reaction("S-N") { // delete head
            public int bid(Gesture g) {
                int x = g.vs.xM(), y = g.vs.yL();
                int hX = Head.this.x() + Head.this.w() / 2, hY = Head.this.y();
                int dx = Math.abs(x - hX), dy = Math.abs(y - hY), diff = dx + dy;
                return diff < 50 ? diff : UC.noBid;
            }
            public void act(Gesture g) {Head.this.deleteHead();}
        });
    }

    public int w() {return 24 * staff.fmt.H / 10;}
    public int y() {return staff.yOfLine(line);}
    public int x() {
        int res = time.x;
        if (wrongSide) {res += (stem != null && stem.isUp) ? w() : -w();}
        return res;
    }
    public void accidUp(){
        if (accid == null) {accid = new Accid(this, Accid.SHARP); return;}
        if (accid.iGlyph < 4) {accid.iGlyph++;}
    }
    public void accidDn(){
        if (accid == null) {accid = new Accid(this, Accid.FLAT); return;}
        if (accid.iGlyph > 0) {accid.iGlyph--;}
    }
    public Glyph normalGlyph() {
        if (stem == null) {return Glyph.HEAD_Q;}
        if (stem.nFlags == -1) {return Glyph.HEAD_HALF;}
        if (stem.nFlags == -2) {return Glyph.HEAD_W;}
        return Glyph.HEAD_Q;
    }
    public void deleteHead() {
        if (accid != null) {accid.deleteAccid();}
        time.heads.remove(this);
        unStem();
        deleteMass();
    }
    public void unStem() {
        if (stem != null) {
            stem.heads.remove(this);
            if (stem.heads.isEmpty()) {stem.deleteStem();}
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
        g.setColor( stem == null ? Color.RED : Color.BLACK);
        int H = staff.fmt.H;
        (forcedGlyph != null ? forcedGlyph : normalGlyph()).showAt(g, H, x(), y());
        if (stem != null) {
            int off = UC.augDotOffset, sp = UC.augDotSpacing;
            for (int i = 0; i < stem.nDots; i++) {
                g.fillOval(time.x + off + i * sp, y() - 3 * H/2, H * 2/3, H * 2/3);
            }
        }
    }
    @Override
    public int compareTo(Head h) {
        return (staff.iStaff != h.staff.iStaff) ? staff.iStaff - h.staff.iStaff : line - h.line;
    }

    // --------------------------------- List ---------------------- //
    public static class List extends ArrayList<Head> {}
}
