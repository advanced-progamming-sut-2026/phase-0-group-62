package model.entities.plant;

import model.enums.PlantTag;

public class Plant {
    private String name;
    private int cost;
    private int health;
    private int damage;
    private PlantTag tag;

    public Plant(String name, int cost, int health, int damage, PlantTag tag) {
        this.name = name;
        this.cost = cost;
        this.health = health;
        this.damage = damage;
        this.tag = tag;
    }

    public void takeDamage(int amount) {
        health -= amount;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public int getHealth() {
        return health;
    }

    public int getDamage() {
        return damage;
    }

    public PlantTag getTag() {
        return tag;
    }
}

