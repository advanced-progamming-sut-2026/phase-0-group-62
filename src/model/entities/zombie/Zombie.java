package model.entities.zombie;

public class Zombie {
    private String name;
    private int health;
    private int maxHealth;
    private double speed;
    private int damage;
    private double x;
    private int y;
    private int armorHealth;
    private int maxArmorHealth;
    private String armorType;
    private boolean isGlowing;
    private boolean isHypnotized;
    private double chilledDuration;
    private double frozenDuration;
    private int stolenSuns;
    private boolean isAngry;
    private boolean isTorchLit;
    private double dynamiteTimer;

    public Zombie(String name, int health, double speed, int damage) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.speed = speed;
        this.damage = damage;
        this.x = 8.0;
        this.y = 0;
        this.armorHealth = 0;
        this.maxArmorHealth = 0;
        this.armorType = "none";
        this.isGlowing = false;
        this.isHypnotized = false;
        this.chilledDuration = 0.0;
        this.frozenDuration = 0.0;
        this.stolenSuns = 0;
        this.isAngry = false;
        this.isTorchLit = false;
        this.dynamiteTimer = 0.0;
    }

    public void takeDamage(int amount, boolean bypassArmor) {
        if (bypassArmor || armorHealth <= 0) {
            health -= amount;
        } else {
            armorHealth -= amount;
            if (armorHealth < 0) {
                health += armorHealth;
                armorHealth = 0;
            }
        }
        if (health < 0) {
            health = 0;
        }
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void applyChilled(double duration) {
        this.chilledDuration = Math.max(this.chilledDuration, duration);
    }

    public void applyFrozen(double duration) {
        this.frozenDuration = Math.max(this.frozenDuration, duration);
    }

    public void updateEffects(double deltaSeconds) {
        if (chilledDuration > 0) {
            chilledDuration -= deltaSeconds;
            if (chilledDuration < 0) chilledDuration = 0;
        }
        if (frozenDuration > 0) {
            frozenDuration -= deltaSeconds;
            if (frozenDuration < 0) frozenDuration = 0;
        }
        if (dynamiteTimer > 0) {
            dynamiteTimer -= deltaSeconds;
            if (dynamiteTimer < 0) dynamiteTimer = 0;
        }
    }

    public double getEffectiveSpeed() {
        if (frozenDuration > 0) {
            return 0.0;
        }
        double currentSpeed = speed;
        if (chilledDuration > 0) {
            currentSpeed /= 2.0;
        }
        if (isAngry) {
            currentSpeed *= 2.0;
        }
        return currentSpeed;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public double getSpeed() {
        return speed;
    }

    public int getDamage() {
        return damage;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getArmorHealth() {
        return armorHealth;
    }

    public void setArmorHealth(int armorHealth) {
        this.armorHealth = armorHealth;
        this.maxArmorHealth = armorHealth;
    }

    public int getMaxArmorHealth() {
        return maxArmorHealth;
    }

    public String getArmorType() {
        return armorType;
    }

    public void setArmorType(String armorType) {
        this.armorType = armorType;
    }

    public boolean isGlowing() {
        return isGlowing;
    }

    public void setGlowing(boolean glowing) {
        isGlowing = glowing;
    }

    public boolean isHypnotized() {
        return isHypnotized;
    }

    public void setHypnotized(boolean hypnotized) {
        isHypnotized = hypnotized;
    }

    public double getChilledDuration() {
        return chilledDuration;
    }

    public double getFrozenDuration() {
        return frozenDuration;
    }

    public int getStolenSuns() {
        return stolenSuns;
    }

    public void setStolenSuns(int stolenSuns) {
        this.stolenSuns = stolenSuns;
    }

    public boolean isAngry() {
        return isAngry;
    }

    public void setAngry(boolean angry) {
        isAngry = angry;
    }

    public boolean isTorchLit() {
        return isTorchLit;
    }

    public void setTorchLit(boolean torchLit) {
        isTorchLit = torchLit;
    }

    public double getDynamiteTimer() {
        return dynamiteTimer;
    }

    public void setDynamiteTimer(double dynamiteTimer) {
        this.dynamiteTimer = dynamiteTimer;
    }
}