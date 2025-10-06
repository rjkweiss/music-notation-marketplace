package music.reaction;

import music.UC;
import music.graphics.*;

import java.awt.*;
import java.awt.event.*;

public class ShapeTrainer extends WinApp {
    public static String UNKNOWN = " <- Unknown shape"; // tracks names of unknow
    public static String ILLEGAL = " <- Name not legal";
    public static String KNOWN = " <- Known shape";
    public static String currState = ILLEGAL;
    public static Shape.Prototype.List pList = new Shape.Prototype.List();
    public String currName = "";

    public ShapeTrainer() {
        super("Shape Trainer", UC.mainWindowWidth, UC.mainWindowHeight);
    }
    public void setState() {
        currState = !Shape.DB.isLegal(currName) ? ILLEGAL : UNKNOWN;
        if (Shape.DB.isKnown(currName)) {
            currState = KNOWN;
            pList = Shape.DB.get(currName).prototypes;
        } else {
            pList = null;
        }
    }
    public void paintComponent(Graphics g) {
        G.fillback(g);
        g.setColor(Color.BLACK);
        g.drawString(currName, 600, 30);
        g.drawString(currState, 700, 30);
        g.setColor(Color.RED);
        Ink.BUFFER.show(g);
        if (pList != null) {pList.show(g);}
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
        if (currState != ILLEGAL) {
            Ink ink = new Ink();
            Shape.Prototype proto;
            // if pList is empty, create a new shape
            if (pList == null) {
                Shape s = new Shape (currName);
                Shape.DB.put(currName, s);
                pList = s.prototypes;
            }
            // we found a match, so we blend
            if (pList.bestDist(ink.norm) < UC.noMatchDist) {
                proto = Shape.Prototype.List.bestMatch;
                proto.blend(ink.norm);
            } else {
                proto = new Shape.Prototype();
                pList.add(proto);
            }
            // need in case the state has changed from unknown to known
            setState();
        }
        repaint();
    }

    public void keyTyped(KeyEvent ke) {
        char c = ke.getKeyChar();
        System.out.println("Typed: " + c);
        currName = (c == ' ') || (c == 0x0d) || (c == 0x0a) ? "" : currName + c;
        setState();
        // save the state
        if ((c == 0x0d) || (c == 0x0a)) {Shape.Database.save();}
        repaint();
    }
    public static void main(String[] args) {
        PANEL = new ShapeTrainer();
        WinApp.launch();
    }
}
