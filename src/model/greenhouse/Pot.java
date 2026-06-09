package model.greenhouse;

import model.entities.plant.Plant;

public class Pot {
    private Plant plant;

    public boolean isEmpty() {
        return plant == null;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }
}

