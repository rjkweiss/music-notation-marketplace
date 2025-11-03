package music;

import graphics.*;
import reaction.*;

import java.awt.*;
import java.util.ArrayList;

public class Staff extends Mass {
    public Sys sys;
    public int iStaff; // this is an index
    public G.HC staffTop;
    public Staff.Fmt fmt = new Fmt(5, 8);

    public Staff(Sys sys, int iStaff, G.HC staffTop) {
        super("BACK");
        this.sys = sys;
        this.iStaff = iStaff;
        this.staffTop = staffTop;

        // reactions
        addReaction(new Reaction("S-S") { // creates a bar
            public int bid(Gesture g) {
                Page PAGE = sys.page;
                int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
                int left = PAGE.margins.left, right = PAGE.margins.right;
                if (x < left || x > right + UC.barToMarginSnap) {return UC.noBid;}
                int d = Math.abs(y1 - Staff.this.yTop()) +  Math.abs(y2 - Staff.this.yBot());
                int bias = UC.barToMarginSnap; // the maximum cycleBar bid must outbid create bar
                return (d < 30) ? d + bias : UC.noBid;
            }

            public void act(Gesture g) {
                new Bar(Staff.this.sys, g.vs.xM());
            }
        });

        addReaction(new Reaction("S-S") { // toggle barContinues
            public int bid(Gesture g) {
                if (Staff.this.sys.iSys != 0) {return UC.noBid;} // only change bar continues for system
                int y1 = g.vs.yL(), y2 = g.vs.yH();
                if (iStaff == sys.staffs.size() - 1) {return UC.noBid;}
                if (Math.abs(y1 - yBot()) > 20) {return UC.noBid;}
                Staff nextStaff = sys.staffs.get(iStaff + 1);
                if (Math.abs(y2 - nextStaff.yTop()) > 20) {return UC.noBid;}
                return 10;
            }
            public void act(Gesture g) {fmt.toggleBarContinues();}
        });

        addReaction(new Reaction("SW-SW") { // add note to staff
            public int bid(Gesture g) {
                Page.Margins pageMargins = sys.page.margins;
                int x = g.vs.xM(), y = g.vs.yM();
                if (x < pageMargins.left || x > pageMargins.right) {return UC.noBid;}
                int H = Staff.this.fmt.H, top = Staff.this.yTop() - H, bot = Staff.this.yBot() + H;
                if (y < top || y > bot) {return UC.noBid;}
                return 10;
            }

            public void act(Gesture g) {new Head(Staff.this,  g.vs.xM(), g.vs.yM());}
        });

        addReaction(new Reaction("W-S") { // adds Q REST
            public int bid(Gesture g) {
                int x = g.vs.xL(), y = g.vs.yM();
                Page.Margins pageMargins = sys.page.margins;
                if(x < pageMargins.left || x > pageMargins.right) {return UC.noBid;}
                int H = fmt.H, top = yTop() - H, bot = yBot() + H;

                if (y < top || y > bot) {return UC.noBid;}
                return 10;
            }

            public void act(Gesture g) {
                Time t = Staff.this.sys.getTime(g.vs.xL());
                new Rest(Staff.this, t);
            }
        });

        addReaction(new Reaction("E-S") { // adds 8th REST
            public int bid(Gesture g) {
                int x = g.vs.xL(), y = g.vs.yM();
                Page.Margins pageMargins = sys.page.margins;
                if(x < pageMargins.left || x > pageMargins.right) {return UC.noBid;}
                int H = fmt.H, top = yTop() - H, bot = yBot() + H;

                if (y < top || y > bot) {return UC.noBid;}
                return 10;
            }

            public void act(Gesture g) {
                Time t = Staff.this.sys.getTime(g.vs.xL());
                new Rest(Staff.this, t).nFlags = 1;
            }
        });
    }

    public int yTop() {return staffTop.v();}
    public int yOfLine(int line) {return yTop() + (line * fmt.H);}
    public int yBot() {return yOfLine(2 * (fmt.nLines - 1));}
    public Staff copy(Sys newSys) {
        G.HC hc = new G.HC(newSys.staffs.sysTop, staffTop.dv);
        return new Staff(newSys, iStaff, hc);
    }

    public void show(Graphics g) {
        Page.Margins m = sys.page.margins;
        int x1 = m.left, x2 = m.right, y = yTop(), h = fmt.H * 2;
        for (int i = 0; i < fmt.nLines; i++) {
            g.drawLine(x1, y + (i * h), x2, y + (i * h));
        }
    }

    public int yLine(int n) {return yTop() + (n * fmt.H);}
    public int lineOfY(int y) {
        int H = fmt.H;
        int bias = 100; // because integer truncation rounds towards 0
        int top = yTop() - H * bias;
        return (y - top + H/2) / H - bias;
    }

    // ----------------------------- FMT --------------------- //
    public static class Fmt {
        public int nLines, H;
        public boolean barContinues = false;

        public Fmt(int nLines, int H) {
            this.nLines = nLines;
            this.H = H;
        }
        public void toggleBarContinues() {barContinues = !barContinues;}
    }

    // ----------------------------- List ------------------------ //
    public static class List extends ArrayList<Staff> {
        public G.HC sysTop;

        public List(G.HC sysTop) {
            this.sysTop = sysTop;
        }

        public int sysTop() {return sysTop.v();}

    }

}
