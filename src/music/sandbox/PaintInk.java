package music.sandbox;

import music.UC;
import music.graphics.G;
import music.graphics.WinApp;
import music.reaction.Ink;

import java.awt.*;
import java.awt.event.MouseEvent;

// test app for the ink class
public class PaintInk extends WinApp {
    public static Ink.List inkList = new Ink.List();
    public PaintInk() {super("Paint Ink", UC.mainWindowWidth, UC.mainWindowHeight);}

    @Override
    public void paintComponent(Graphics g) {
        G.fillback(g);
//        g.setColor(Color.BLUE);
//        g.fillRect(100, 100, 100, 100);
        g.setColor(UC.inkColor);
        inkList.show(g);
        Ink.BUFFER.show(g);
    }

    public void mousePressed(MouseEvent me) {
        Ink.BUFFER.dn(me.getX(), me.getY());
        repaint();
    }
    public void mouseDragged(MouseEvent me) {
        Ink.BUFFER.drag(me.getX(), me.getY());
        repaint();
    }
    public void mouseReleased(MouseEvent me) {
        Ink.BUFFER.up(me.getX(), me.getY());
        inkList.add(new Ink());
        repaint();
    }


    public static void main(String[] args) {
        PANEL  =  new PaintInk();
        WinApp.launch();
    }
}
