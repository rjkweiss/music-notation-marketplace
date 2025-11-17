package music;

import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

import java.awt.*;

public class Accid extends Mass {
    public static int FLAT = 1, NATURAL = 2, SHARP = 3;
    public static Glyph [] GLYPHS = {Glyph.D_FLAT, Glyph.FLAT, Glyph.NATURAL, Glyph.SHARP, Glyph.D_SHARP};
    public int iGlyph;
    public Head head;
    public int left = 0; // adjust location slightly left

    public Accid(Head head, int iGlyph) {
        super("NOTE");
        this.head = head;
        this.iGlyph = iGlyph;

        // reactions
        addReaction(new Reaction("DOT") {
            @Override
            public int bid(Gesture g) {
                int x = g.vs.xM(), y = g.vs.yM();
                int xA = Accid.this.x(), yA = head.y();
                int dx = Math.abs(x - xA), dy = Math.abs(y - yA), dist = dx + dy;
                return dist > 50 ? UC.noBid : dist;
            }

            @Override
            public void act(Gesture g) {
                left += 10;
                if (left > 50) {left = 0;}
            }
        });

        addReaction(new Reaction("S-N") { // delete accid
            public int bid(Gesture g) {
                int x = g.vs.xM(), y = g.vs.yL();
                int xA = Accid.this.x(), yA = head.y();
                int dx = Math.abs(x - xA), dy = Math.abs(y - yA), dist = dx + dy;
                return dist > 50 ? UC.noBid : dist;
            }
            public void act(Gesture g) {Accid.this.deleteAccid();}
        });
    }

    public void deleteAccid(){
        head.accid = null;
        deleteMass();
    }

    public void show(Graphics g) {
        GLYPHS[iGlyph].showAt(g, head.staff.fmt.H, x(), head.y());
    }

    public int x() {
        return head.x() - UC.headAccidOffset - left;
    }
}
