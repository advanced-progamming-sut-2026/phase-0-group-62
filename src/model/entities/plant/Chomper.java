package model.entities.plant;

import model.enums.PlantTag;

public class Chomper extends Plant {
    public Chomper() {
        super("Chomper", 150, 300, 1000, PlantTag.INSTANT_ATTACK);
    }
}

