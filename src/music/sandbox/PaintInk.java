package music.sandbox;

import music.UC;
import music.graphics.G;
import music.graphics.WinApp;
import music.reaction.Ink;
import music.reaction.Shape;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.MouseEvent;

// test app for the ink class
public class PaintInk extends WinApp {
    public static Ink.List inkList = new Ink.List();
    public static Shape.Prototype.List pList = new Shape.Prototype.List();
    public PaintInk() {super("Paint Ink", UC.mainWindowWidth, UC.mainWindowHeight);}

    @Override
    public void paintComponent(Graphics g) {
        G.fillback(g);
        g.setColor(UC.inkColor);
        inkList.show(g);
        Ink.BUFFER.show(g);
        if (inkList.size() > 1) {
            int last = inkList.size() - 1;
            int dist = inkList.get(last).norm.dist(inkList.get(last - 1).norm);
            g.setColor(dist > UC.noMatchDist ? Color.RED : Color.BLACK);
            g.drawString("dist: " + dist, 600, 60);
        }
        pList.show(g);
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
        Ink ink = new Ink();
        inkList.add(ink);
        Shape.Prototype proto;
        if (pList.bestDist(ink.norm) < UC.noMatchDist) {
            proto = Shape.Prototype.List.bestMatch;
            proto.blend(ink.norm);
        } else {
            proto = new Shape.Prototype();
            pList.add(proto);
        }
        ink.norm = proto;
        repaint();
    }


    public static void main(String[] args) {
        PANEL  =  new PaintInk();
        WinApp.launch();
    }
}
