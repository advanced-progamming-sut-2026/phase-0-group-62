package model.entities.plant;

import model.enums.PlantTag;

public class Repeater extends Plant {
    public Repeater() {
        super("Repeater", 200, 300, 20, PlantTag.SHOOTER);
    }

    public int getShotsPerAttack() {
        return 2;
    }
}

