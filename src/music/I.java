package music;

import reaction.Gesture;

import java.awt.*;

public interface I {

    public interface Hit {public boolean hit(int x, int y);}
    public interface Draw {public void draw(Graphics g);}
    public interface Area extends Hit {
        public void dn(int x, int y);
        public void drag(int x, int y);
        public void up(int x, int y);
    }
    public interface Show { public void show(Graphics g);}
    public interface Act{public void act(Gesture g);} // what you do if you react to some gesture
    public interface React extends Act{public int bid(Gesture g);}
}
