package model.entities.plant;

import model.enums.PlantTag;
import java.util.List;

public class Plant {
    private String name;
    private int cost;
    private int health;
    private int maxHealth;
    private int damage;
    private List<PlantTag> tags;
    private String shootBehavior;
    private double cooldown;
    private int sunProduce;
    private int produceInterval;
    private int level;
    private boolean isBoosted;
    private boolean hasSunToCollect;
    private int x;
    private int y;

    public Plant(String name, int cost, int health, int damage, List<PlantTag> tags, String shootBehavior, double cooldown, int sunProduce, int produceInterval) {
        this.name = name;
        this.cost = cost;
        this.health = health;
        this.maxHealth = health;
        this.damage = damage;
        this.tags = tags;
        this.shootBehavior = shootBehavior;
        this.cooldown = cooldown;
        this.sunProduce = sunProduce;
        this.produceInterval = produceInterval;
        this.level = 1;
        this.isBoosted = false;
        this.hasSunToCollect = false;
    }

    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) {
            health = 0;
        }
    }

    public void heal(int amount) {
        health += amount;
        if (health > maxHealth) {
            health = maxHealth;
        }
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void upgrade(int healthIncrease, int damageIncrease) {
        this.level++;
        this.maxHealth += healthIncrease;
        this.health = this.maxHealth;
        this.damage += damageIncrease;
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

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getDamage() {
        return damage;
    }

    public List<PlantTag> getTags() {
        return tags;
    }

    public String getShootBehavior() {
        return shootBehavior;
    }

    public double getCooldown() {
        return cooldown;
    }

    public int getSunProduce() {
        return sunProduce;
    }

    public int getProduceInterval() {
        return produceInterval;
    }

    public int getLevel() {
        return level;
    }

    public boolean isBoosted() {
        return isBoosted;
    }

    public void setBoosted(boolean boosted) {
        isBoosted = boosted;
    }

    public boolean isHasSunToCollect() {
        return hasSunToCollect;
    }

    public void setHasSunToCollect(boolean hasSunToCollect) {
        this.hasSunToCollect = hasSunToCollect;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}