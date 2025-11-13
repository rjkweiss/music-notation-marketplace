package music;

import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

import java.awt.*;
import java.util.ArrayList;

public class Clef extends Mass implements Comparable<Clef> {
    public Glyph glyph;
    public int x;
    public Staff staff;

    public Clef(Staff staff, int x, Glyph glyph){
        super("NOTE");
        this.staff = staff;
        this.x = x;
        this.glyph = glyph;

        // reactions
        addReaction(new Reaction("S-N") { // deletes clef
            @Override
            public int bid(Gesture g) {
                int dx = Math.abs(g.vs.xL() - x), dy = Math.abs(g.vs.yL() - staff.yOfLine(4));
                if (dx + dy > 30) {return UC.noBid;}
                return dx + dy;
            }

            @Override
            public void act(Gesture g) {
                deleteClef();
            }
        });
    }

    public void show(Graphics g) {
        glyph.showAt(g, staff.fmt.H, x, staff.yOfLine(4));
    }

    @Override
    public int compareTo(Clef clef) {
        return x - clef.x;
    }

    public void deleteClef() {
        staff.clefs.remove(this);
        if (staff.clefs.isEmpty()) {staff.clefs = null;}
        deleteMass();
    }

    // ------------------------- List -------------------------- //
    public static class List extends ArrayList<Clef> {}
}
