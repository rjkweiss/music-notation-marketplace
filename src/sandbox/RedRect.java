package sandbox;

import graphics.G;
import graphics.WinApp;

import java.awt.*;
import java.awt.event.MouseEvent;

public class RedRect extends WinApp {
    public int clicks = 0;

    public RedRect() {
        super("Red Rectangle", 1000, 700);
    }

    public void paintComponent(Graphics g) {
        g.setColor(G.rndColor());
        g.fillOval(100, 100, 100, 200);
        g.drawLine(100, 600, 600, 100);
        g.drawString("Red Rectangle " + clicks, 300, 100);
    }

    public void mousePressed(MouseEvent me) {
        clicks++;
        repaint();
    }

    public static void main(String[] args) {
        PANEL = new RedRect();
        WinApp.launch();
    }
}
