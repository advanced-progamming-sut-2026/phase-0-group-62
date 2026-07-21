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
    private int freezeLevel;
    private int iceHealth;
    private boolean isTransformedToSheep;

    private int dx;
    private int dy;
    private int hitCount;
    private boolean isBowlingBall;
    private int tickCounter;

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
        this.freezeLevel = 0;
        this.iceHealth = 0;
        this.isTransformedToSheep = false;
        this.dx = 0;
        this.dy = 0;
        this.hitCount = 0;
        this.isBowlingBall = false;
        this.tickCounter = 0;
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
        this.tickCounter++;
    }

    public boolean shouldShoot() {
        int intervalTicks = (int) (actionInterval * 10);
        if (intervalTicks <= 0) {
            intervalTicks = 15;
        }
        if (this.tickCounter >= intervalTicks) {
            this.tickCounter = 0;
            return true;
        }
        return false;
    }

    public String getShootBehavior() {
        if (category != null && category.equalsIgnoreCase("SHOOTER")) {
            return "Pea";
        }
        return "None";
    }

    public double getCooldown() {
        return recharge;
    }

    public double getSunProduce() {
        if (category != null && category.equalsIgnoreCase("SUN_PRODUCER")) {
            return abilityValue;
        }
        return 0.0;
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

    public boolean isTransformedToSheep() { return isTransformedToSheep; }
    public void setTransformedToSheep(boolean transformedToSheep) { isTransformedToSheep = transformedToSheep; }

    public int getFreezeLevel() {
        return freezeLevel;
    }

    public void setFreezeLevel(int freezeLevel) {
        this.freezeLevel = freezeLevel;
        if (this.freezeLevel >= 3) {
            this.freezeLevel = 3;
            this.iceHealth = 600;
        } else if (this.freezeLevel < 0) {
            this.freezeLevel = 0;
            this.iceHealth = 0;
        }
    }

    public int getIceHealth() {
        return iceHealth;
    }

    public void setIceHealth(int iceHealth) {
        this.iceHealth = iceHealth;
        if (this.iceHealth <= 0) {
            this.iceHealth = 0;
            this.freezeLevel = 0;
        }
    }

    public boolean isFrozen() {
        return freezeLevel == 3;
    }

    public void damageIce(int amount) {
        this.iceHealth -= amount;
        if (this.iceHealth <= 0) {
            this.iceHealth = 0;
            this.freezeLevel = 0;
        }
    }

    public void melt() {
        this.iceHealth = 0;
        this.freezeLevel = 0;
    }

    public boolean isAquatic() {
        if (name != null && name.equalsIgnoreCase("Lily Pad")) {
            return true;
        }
        if (tags != null) {
            for (String tag : tags) {
                if (tag.equalsIgnoreCase("aquatic") || tag.equalsIgnoreCase("water")) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getDx() { return dx; }
    public void setDx(int dx) { this.dx = dx; }
    public int getDy() { return dy; }
    public void setDy(int dy) { this.dy = dy; }
    public int getHitCount() { return hitCount; }
    public void incrementHitCount() { this.hitCount++; }
    public boolean isBowlingBall() { return isBowlingBall; }
    public void setBowlingBall(boolean bowlingBall) { this.isBowlingBall = bowlingBall; }
}