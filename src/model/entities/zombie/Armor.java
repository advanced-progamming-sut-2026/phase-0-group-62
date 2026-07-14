package model.entities.zombie;

public class Armor {
    private ArmorType type;
    private int health;
    private int maxHealth;
    private boolean destroyed;
    private int resistance; // Percentage of damage resisted (0-100)

    public Armor(ArmorType type, int health) {
        this(type, health, 0);
    }

    public Armor(ArmorType type, int health, int resistance) {
        this.type = type;
        this.health = health;
        this.maxHealth = health;
        this.resistance = resistance;
        this.destroyed = false;
    }

    public void takeDamage(int amount) {
        if (destroyed) return;

        // Apply resistance
        int actualDamage = (int) (amount * (1 - resistance / 100.0));
        health -= actualDamage;

        if (health <= 0) {
            health = 0;
            destroyed = true;
        }
    }

    public void onDestroyed(Zombie zombie) {
        // Special effects when armor is destroyed
        switch (type) {
            case NEWSPAPER:
                zombie.setEnraged(true);
                break;
            case DYNAMITE:
                // Explodes after 10 seconds
                break;
            case UMBRELLA:
                // No longer blocks lobbed attacks
                break;
            default:
                break;
        }
    }

    public boolean isDestroyed() { return destroyed; }
    public ArmorType getType() { return type; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getResistance() { return resistance; }
    public void setResistance(int resistance) { this.resistance = resistance; }

    @Override
    public String toString() {
        return type.toString() + ": " + health + "/" + maxHealth;
    }

    public static Armor createCone() {
        return new Armor(ArmorType.CONE, 370);
    }

    public static Armor createBucket() {
        return new Armor(ArmorType.BUCKET, 1100);
    }

    public static Armor createHelmet() {
        return new Armor(ArmorType.HELMET, 800);
    }

    public static Armor createShoulderArmor() {
        return new Armor(ArmorType.SHOULDER_ARMOR, 800);
    }

    public static Armor createBlock() {
        return new Armor(ArmorType.BLOCK, 2200);
    }

    public static Armor createNewspaper() {
        return new Armor(ArmorType.NEWSPAPER, 200);
    }

    public static Armor createBox() {
        return new Armor(ArmorType.BOX, 500, 50); // 50% resistance
    }

    public static Armor createTorch() {
        return new Armor(ArmorType.TORCH, 300);
    }
}
