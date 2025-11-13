package music;

import graphics.*;
import reaction.*;

import java.awt.*;
import java.util.ArrayList;

public class Sys extends Mass {
    public Page page;
    public int iSys;
    public Staff.List staffs;
    public Time.List times;
    public Stem.List stems = new Stem.List();

    public Sys(Page page, G.HC sysTop) {
        super("BACK");
        this.page = page;
        iSys = page.sysList.size();
        staffs = new Staff.List(sysTop);
        times = new Time.List(this);

        if (iSys == 0) {
            staffs.add(new Staff(this, 0, new G.HC(sysTop, 0)));
        } else { // other systems are clones of first system
            Sys oldSys = page.sysList.get(0);

            for (Staff oldStaff : oldSys.staffs) {
                Staff ns = oldStaff.copy(this);
                this.staffs.add(ns);
            }
        }

        // reactions
        addReaction(new Reaction("E-E") { // beam stems
            @Override
            public int bid(Gesture g) {
                int x1 = g.vs.xL(),  y1 = g.vs.yL(), x2 = g.vs.xH(), y2 = g.vs.yH();
                if (stems.fastReject((y1 + y2) / 2)) {return UC.noBid;}
                ArrayList<Stem> temp = stems.allIntersectors(x1, y1, x2, y2);
                if (temp.size() < 2) {return UC.noBid;}
                Beam beam = temp.get(0).beam; // check if all crossed stems are owned by same beam (including null)

                for (Stem s: temp) {
                    if (s.beam != beam) {return UC.noBid;}
                }

                if (beam == null && temp.size() != 2) {return UC.noBid;}
                if (beam == null && (temp.get(0).nFlags != 0 || temp.get(1).nFlags != 0)) {return UC.noBid;}
                return 50; // either create new beam or flag a set of beams
            }

            @Override
            public void act(Gesture g) {
                int x1 = g.vs.xL(),  y1 = g.vs.yL(), x2 = g.vs.xH(), y2 = g.vs.yH();
                ArrayList<Stem> temp = stems.allIntersectors(x1, y1, x2, y2);
                Beam beam = temp.get(0).beam;
                if (beam == null) {
                    new Beam(temp.get(0), temp.get(1));
                } else {
                    for (Stem s: temp) {s.incFlags();}
                }
            }
        });
    }

    // accessor function that allows us to get sys times
    public Time getTime(int x) {return times.getTime(x);}

    public void addNewStaff(int y) {
        int off = y - staffs.sysTop();
        G.HC staffTop = new G.HC(staffs.sysTop, off);
        staffs.add(new Staff(this, staffs.size(), staffTop));
        page.updateMaxH();
    }

    public void show(Graphics g) {
        int x = page.margins.left;
        g.drawLine(x, yTop(), x, yBot());
    }

    public int yTop() {return staffs.sysTop();}
    public int yBot() {return staffs.get(staffs.size() - 1).yBot();}
    public int height() {return yBot() - yTop();}


    // -------------------------------- List ---------------------- //
    public static class List extends ArrayList<Sys> {}
}
