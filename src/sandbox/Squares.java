package sandbox;

import music.I;
import music.UC;
import graphics.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Squares extends WinApp {
    public G.VS theVS = new G.VS(100, 100, 200, 300);
    public static Square.List squares = new Square.List();
    public static Square lastSquare;
    public static Color color = G.rndColor();
    private boolean dragging = false;
    private static G.V mouseDelta = new G.V(0, 0);
    public static I.Area currArea;

    public Squares() {
        super("Squares", UC.mainWindowWidth, UC.mainWindowHeight);
    }

    public void paintComponent(Graphics g) {
        theVS.fill(g,  color);
        squares.draw(g);
    }

    public void mousePressed(MouseEvent me) {
        int x = me.getX(), y = me.getY();
        currArea = squares.hit(x, y);
        currArea.dn(x, y);
        repaint();
    }

    public void mouseDragged(MouseEvent me) {
        int x = me.getX(), y = me.getY();
        currArea.drag(x, y);
        repaint();
    }

    // --------------------- Square ---------------
    public static class Square extends G.VS implements I.Area {
        public Color c = G.rndColor();
        public static Square BACKGROUND = new Square(){
            public void dn(int x, int y) {lastSquare = new Square(x, y);squares.add(lastSquare);}
            public void drag(int x, int y) {lastSquare.resize(x, y);}
            public void up(int x, int y) {}
        };
        public Square() {super(0, 0, 3000, 3000); c= Color.white;}
        public Square(int x, int y) {super(x, y, 100, 100);}


        public void resize(int x, int y) {
            if (x > loc.x && y > loc.y) {size.set(x - loc.x, y - loc.y);}
        }

        public void moveTo(int x, int y) {loc.set(x,y);}

        @Override
        public void dn(int x, int y) {mouseDelta.set(loc.x - x, loc.y - y);}

        @Override
        public void drag(int x, int y) {
            loc.set(mouseDelta.x + x, mouseDelta.y + y);
        }

        @Override
        public void up(int x, int y) {

        }

        // ----------------- List ----------------
        public static class List extends ArrayList<Square> {
            public List() {super(); add(BACKGROUND);}
            public void draw(Graphics g) {for (Square s : this) {s.fill(g, s.c);}}
            public Square hit(int x, int y) {
                Square res = null;
                for (Square s : this) {if (s.hit(x,y)) {res = s;}}
                return res;
            }
        }
    }

    public static void main(String[] args) {
        PANEL = new Squares();
        WinApp.launch();
    }
}
