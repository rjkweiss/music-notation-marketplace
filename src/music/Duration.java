package music;

import reaction.Mass;

import java.awt.*;

public abstract class Duration extends Mass {
    public int nFlags = 0, nDots = 0;

    public Duration() {super("NOTE");}

    public void incFlags() {if (nFlags < 4) {nFlags++;}}
    public void decFlags() {if (nFlags > -2) {nFlags--;}}
    public void cycleDot() {nDots++; if (nDots > 3) {nDots = 0;}}

    public abstract void show(Graphics g);
}
