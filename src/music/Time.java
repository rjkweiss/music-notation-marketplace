package music;

import java.util.ArrayList;

public class Time {
    public int x;

    private Time(Sys sys, int x) {
        this.x = x;
        sys.times.add(this);

    }

    // ------------------------------- List -------------------- //
    public static class List extends ArrayList<Time> {
        public Sys sys;

        public List(Sys sys) {this.sys = sys;}

        public Time getTime(int x) {
            if (size() == 0) {return new Time(sys, x);}
            Time t = getClosestTime(x);
            return Math.abs(x - t.x) < UC.snapTime ? t : new Time(sys, x);
        }

        private Time getClosestTime(int x) {
            Time res =  get(0);
            int bestSoFar = Math.abs(x - res.x);

            for (Time t : this) {
                int dist =  Math.abs(x - t.x);
                if (dist < bestSoFar) {res = t; bestSoFar = dist;}
            }
            return res;
        }
    }
}
