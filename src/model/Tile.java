package model;

import model.entities.plant.Plant;
import model.entities.zombie.Zombie;
import model.enums.TileType;

public class Tile {
    private final int row;
    private final int column;
    private TileType type;
    private Plant plant;
    private Zombie zombie;

    public Tile(int row, int column) {
        this.row = row;
        this.column = column;
        this.type = TileType.GRASS;
    }

    public boolean isEmpty() {
        return plant == null && zombie == null;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public Zombie getZombie() {
        return zombie;
    }

    public void setZombie(Zombie zombie) {
        this.zombie = zombie;
    }
}

