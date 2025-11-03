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
