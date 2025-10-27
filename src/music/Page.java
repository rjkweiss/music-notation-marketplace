package music;

import graphics.*;
import reaction.*;

import java.awt.*;

public class Page extends Mass {
    public Margins margins = new Margins();
    public int sysGap; // size of space between sys on page. Set by adding 2nd sys
    public int maxH = 0;
    public G.HC pageTop;
    public Sys.List sysList = new Sys.List();

    public Page(int y) {
        super("BACK");
        margins.top = y;
        pageTop = new G.HC(G.HC.ZERO, y);
        G.HC sysTop = new G.HC(pageTop, 0);
        sysList.add(new Sys(this, sysTop));
        updateMaxH();

        // reactions go here
        addReaction(new Reaction("W-W") {
            @Override
            public int bid(Gesture g) {
                if (sysList.size() != 1) {return UC.noBid;}

                Sys sys = sysList.get(0);
                int y = g.vs.yM();
                if (y < sys.yBot() + UC.minStaffGap) {return UC.noBid;}

                return 1000;
            }

            @Override
            public void act(Gesture g) {
                sysList.get(0).addNewStaff(g.vs.yM());
            }
        });

        addReaction(new Reaction("W-E") { // add new sys to page
            @Override
            public int bid(Gesture g) {
                Sys lastSys = sysList.get(sysList.size() - 1);
                int y = g.vs.yM();
                if (y < lastSys.yBot() + UC.minSysGap) {return UC.noBid;}
                return 1000;
            }

            @Override
            public void act(Gesture g) {
                addNewSys(g.vs.yM());
            }
        });
    }

    public void addNewSys(int y) { // called by page reactions
        int nSys = sysList.size(), sysHeight = sysList.get(0).height();
        if (nSys == 1) {
            sysGap = y - sysHeight - pageTop.v();
        }
        G.HC sysTop = new G.HC(pageTop, nSys * (sysHeight + sysGap));
        sysList.add(new Sys(this, sysTop));
    }
    public void updateMaxH() {
        Sys sys = sysList.get(0);
        int newH = sys.staffs.get(sys.staffs.size() - 1).fmt.H;
        if (maxH < newH) {maxH = newH;}
    }

    public void show(Graphics g) {
        g.setColor(Color.BLACK);
    }

    // ---------------------- Margins ----------------- //
    public static class Margins {
        private static int MM = 50;
        public int top = MM, left = MM, bot = UC.mainWindowHeight - MM, right = UC.mainWindowWidth - MM;
    }
}
