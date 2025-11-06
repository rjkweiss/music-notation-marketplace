package music;

import reaction.Gesture;
import reaction.Reaction;

import java.awt.*;

public class Rest extends Duration {
    public Staff staff;
    public Time time;
    public int line = 4; // center line

    private static Glyph[] glyphs = {Glyph.REST_W, Glyph.REST_H, Glyph.REST_Q, Glyph.REST_1F, Glyph.REST_2F, Glyph.REST_3F, Glyph.REST_4F};

    public Rest(Staff staff, Time time) {
        this.staff = staff;
        this.time = time;

        // reactions go here
        addReaction(new Reaction("E-E") { // adds flag to rest
            public int bid(Gesture g) {
                int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH(), x = Rest.this.time.x;
                if (x1 > x || x2 < x) {return UC.noBid;}
                return Math.abs(y - Rest.this.staff.yLine(4));
            }

            public void act(Gesture g) {
                Rest.this.incFlags();
            }
        });

        addReaction(new Reaction("W-W") { // removes flag
            public int bid(Gesture g) {
                int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH(), x = Rest.this.time.x;
                if (x1 > x || x2 < x) {return UC.noBid;}
                return Math.abs(y - Rest.this.staff.yLine(4));
            }

            public void act(Gesture g) {
                Rest.this.decFlags();
            }
        });

        addReaction(new Reaction("DOT") { // add augmentation dot
            public int bid(Gesture g) {
                int xR = Rest.this.time.x, yR = Rest.this.y();
                int x = g.vs.xM(), y = g.vs.yM();
                if (x < xR || x > xR + 40 || y < yR - 40 || y > yR + 40) {return UC.noBid;}
                return Math.abs(x - xR) + Math.abs(y - yR);
            }
            public void act(Gesture g) {Rest.this.cycleDot();}
        });
    }

    public int y() {return staff.yLine(line);}

    public void show(Graphics g) {
        int H = staff.fmt.H, y = y();
        Glyph glyph = glyphs[nFlags + 2];
        glyph.showAt(g, H, time.x, y);
        int off = UC.augDotOffset, sp = UC.augDotSpacing;
        g.setColor(Color.RED);

        for (int i = 0; i < nDots; i++) {
            g.fillOval(time.x + off + i * sp, y-3 * H/2, H * 2/3, H * 2/3);

        }
    }
}
