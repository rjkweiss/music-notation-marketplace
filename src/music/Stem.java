package music;

import java.awt.*;
import java.util.Collections;

public class Stem extends Duration {
    public Staff staff;
    public Head.List heads = new Head.List();
    public boolean isUp = true;

    public Stem(Staff staff, boolean up) {
        super();
        this.staff = staff;
        this.isUp = up;

        // reactions
    }

    public Head firstHead() {return heads.get(isUp ? heads.size() - 1 : 0);}
    public Head lastHead() {return heads.get(isUp ? 0 : heads.size() - 1);}
    public int yFirstHead() {
        Head h = firstHead();
        return h.staff.yOfLine(h.line);
    }
    public int yBeamEnd() {
        Head h = lastHead();
        int line = h.line;
        line += isUp ? -7 : 7;
        int flagInc = nFlags > 2 ? 2 * (nFlags - 2) : 0;
        line += isUp ? -flagInc : flagInc;
        if ((isUp && line > 4) || (!isUp && line < 4)) {line = 4;}
        return h.staff.yOfLine(line);
    }
    public int x() {
        Head h = firstHead();
        return h.time.x + (isUp ? h.w() : 0);
    }
    public void deleteStem() {deleteMass();} // only call if list of heads is empty
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
        }
    }

}
