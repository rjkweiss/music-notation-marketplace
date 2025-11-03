package music;

import graphics.*;
import reaction.*;
import reaction.Shape;

import java.awt.*;
import java.awt.event.*;

public class MusicEd extends WinApp {
    static {new Layer("BACK"); new Layer("NOTE"); new Layer("FORE");}
    public static boolean training = false;
    public static I.Area currArea = Gesture.AREA;
    public static Page PAGE;

    public MusicEd() {
        super("Music Editor", UC.mainWindowWidth, UC.mainWindowHeight);
        // initial reactions
        Reaction.initialReactions.addReaction(new Reaction("W-W") {
            @Override
            public int bid(Gesture g) {
                return 0;
            }

            @Override
            public void act(Gesture g) {
                int y = g.vs.yM();
                MusicEd.PAGE = new Page(y);
                this.disable();
            }
        });
    }

    public void paintComponent(Graphics g) {
        G.fillback(g);
        Layer.ALL.show(g);
        g.setColor(Color.BLUE);
        Ink.BUFFER.show(g);
        g.drawString(Gesture.recognized, 900, 30);
        if (PAGE != null) {
            Glyph.CLEF_G.showAt(g, 8, 100, PAGE.margins.top + 4 * 8);
            Glyph.HEAD_Q.showAt(g, 8, 200, PAGE.margins.top + 4 * 8);
        }
    }

    public void mousePressed(MouseEvent me) {
        currArea.dn(me.getX(), me.getY());
        repaint();
    }
    public void mouseDragged(MouseEvent me) {
        currArea.drag(me.getX(), me.getY());
        repaint();
    }
    public void mouseReleased(MouseEvent me) {
        currArea.up(me.getX(), me.getY());
        trainButton(me);
        repaint();
    }

    // key typed reactions
    public void keyTyped(KeyEvent ke) {
        if (training) {
            Shape.TRAINER.keyTyped(ke);
            repaint();
        }
    }

    public void trainButton(MouseEvent me) {
        if (me.getX() > UC.mainWindowWidth - 40 && me.getY() < 40) {
            training = !training;
            currArea = training ? Shape.TRAINER : Gesture.AREA;
        }
    }

    public static void main(String[] args) {
        PANEL = new MusicEd();
        WinApp.launch();
    }
}
