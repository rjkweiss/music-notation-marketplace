package music;

import java.awt.*;

// --- universal constants throughout project
public class UC {
    public static final int mainWindowWidth = 1000;
    public static final int mainWindowHeight = 700;
    public static final int inkBufferMax = 500;
    public static final int normSampleSize = 25;
    public static final int normCordMax = 1000;
    public static final int noMatchDist = 500_000;
    public static Color inkColor = Color.BLACK;
    public static int dotThreshold = 5;
    public static String shapeDatabaseFilename = "shapeDB.dat";
    public static int noBid = 10_000;
    public static int minStaffGap = 40;
    public static int minSysGap = 40;
    public static final int barToMarginSnap = 20;
}
