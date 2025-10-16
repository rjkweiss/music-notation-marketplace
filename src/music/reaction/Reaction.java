package music.reaction;

import music.I;
import music.UC;

import java.util.*;

public abstract class Reaction implements I.React{
    private static Map byShape = new Map(); // our marketplace of all enabled shapes
    public static List initialReactions = new List(); // used by Undo to restart everything.

    public Shape shape;

    public Reaction(String shapeName){
        shape = Shape.DB.get(shapeName);
        if(shape == null){System.out.println("WTF? - Shape.DB don't know about: "+shapeName);}
    }
    public void enable(){List list = byShape.getList(shape); if(!list.contains(this)){list.add(this);}}
    public void disable(){List list = byShape.getList(shape); list.remove(this);}
    public static Reaction best(Gesture g){return byShape.getList(g.shape).loBid(g);}

    //---------------------LIST----------------------
    public static class List extends ArrayList<Reaction> {
        public void addReaction(Reaction r){add(r); r.enable();}
        public void removeReaction(Reaction r){ remove(r); r.disable();}
        // this next routine is tricky - to avoid concurrent array mods you first remove all from shape map, then clear
        public void clearAll(){for(Reaction r : this){r.disable();} this.clear();}
        public Reaction loBid(Gesture g){ // can return null - list is Empty or no one wants to bid.
            Reaction res = null; int bestSoFar = UC.noBid;
            for(Reaction r : this){
                int b = r.bid(g);
                if(b < bestSoFar){bestSoFar = b; res = r;}
            }
            return res;
        }
    }
    //---------------------MAP-----------------------
    public static class Map extends HashMap<Shape, List> {
        public List getList(Shape s){ // always succeeds
            List res = get(s);
            if(res == null){res = new List(); put(s,res);}
            return res;
        }
    }
}
