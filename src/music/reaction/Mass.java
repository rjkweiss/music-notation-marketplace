package music.reaction;

import music.I;

import java.awt.*;

public abstract class Mass extends Reaction.List implements I.Show {
    public Layer layer;

    public Mass(String layerName){
        this.layer = Layer.byName.get(layerName);
        if (layer != null){
            layer.add(this);
        } else {
            System.out.println("Bad layer name " + layerName);
        }
    }

    public void deleteMass() {
        // clears all reactions from this list and reactions by shape
        clearAll();
        layer.remove(this);
    }

    public void show(Graphics g) {}

    // fix bug that shows up when removing masses as I.Show from layers
    private static int M = 1;
    private int hash = M++;
    @Override
    public int hashCode() {return hash;}
    @Override
    public boolean equals(Object obj) {return this == obj;}
}
