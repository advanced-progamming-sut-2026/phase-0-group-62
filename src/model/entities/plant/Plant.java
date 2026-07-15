package model.entities.plant;

import java.util.List;

public class Plant {
    private int id;
    private String name;
    private String category;
    private List<String> tags;
    private int cost;
    private int baseHp;
    private int damage;
    private double actionInterval;
    private double recharge;
    private String abilityType;
    private double abilityValue;
    private String plantFoodType;
    private double plantFoodValue;
    private int health;
    private int maxHealth;
    private boolean isBoosted;
    private boolean hasSunToCollect;
    private int x;
    private int y;

    public Plant(int id, String name, String category, List<String> tags, int cost, int baseHp, int damage, double actionInterval, double recharge, String abilityType, double abilityValue, String plantFoodType, double plantFoodValue) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.tags = tags;
        this.cost = cost;
        this.baseHp = baseHp;
        this.damage = damage;
        this.actionInterval = actionInterval;
        this.recharge = recharge;
        this.abilityType = abilityType;
        this.abilityValue = abilityValue;
        this.plantFoodType = plantFoodType;
        this.plantFoodValue = plantFoodValue;
        this.health = baseHp;
        this.maxHealth = baseHp;
        this.isBoosted = false;
        this.hasSunToCollect = false;
    }

    public void initHealth() {
        this.health = this.baseHp;
        this.maxHealth = this.baseHp;
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

    public void update() {
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public List<String> getTags() { return tags; }
    public int getCost() { return cost; }
    public int getBaseHp() { return baseHp; }
    public int getDamage() { return damage; }
    public double getActionInterval() { return actionInterval; }
    public double getRecharge() { return recharge; }
    public String getAbilityType() { return abilityType; }
    public double getAbilityValue() { return abilityValue; }
    public String getPlantFoodType() { return plantFoodType; }
    public double getPlantFoodValue() { return plantFoodValue; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public boolean isBoosted() { return isBoosted; }
    public void setBoosted(boolean boosted) { isBoosted = boosted; }
    public boolean isHasSunToCollect() { return hasSunToCollect; }
    public void setHasSunToCollect(boolean hasSunToCollect) { this.hasSunToCollect = hasSunToCollect; }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
}