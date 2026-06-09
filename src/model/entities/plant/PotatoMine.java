package model.entities.plant;

import model.enums.PlantTag;

public class PotatoMine extends Plant {
    private boolean armed;

    public PotatoMine() {
        super("PotatoMine", 25, 300, 1800, PlantTag.EXPLOSIVE);
    }

    public void arm() {
        armed = true;
    }

    public boolean isArmed() {
        return armed;
    }
}

