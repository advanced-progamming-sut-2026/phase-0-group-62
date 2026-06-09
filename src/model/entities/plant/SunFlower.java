package model.entities.plant;

import model.enums.PlantTag;

public class SunFlower extends Plant {
    public SunFlower() {
        super("SunFlower", 50, 300, 0, PlantTag.SUN_PRODUCER);
    }

    public int produceSun() {
        return 25;
    }
}

