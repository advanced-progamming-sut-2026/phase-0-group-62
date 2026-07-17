package model.entities.plant;

import model.Bullet;
import model.entities.zombie.Zombie;

import java.util.ArrayList;
import java.util.List;

public class ShootBehavior {
    private ShootType shootType;
    private int damage;
    private int range;
    private boolean pierce;
    private boolean explosive;
    private boolean homing;
    private int splashRadius;
    private int cooldown;
    private int currentCooldown;

    public enum ShootType {
        DIRECT,         // Straight line (Peashooter)
        LOB,            // Lobbed over obstacles (Cabbage-pult)
        ICE,            // Slows zombies (Snow Pea)
        FIRE,           // Double damage, melts ice (Fire Peashooter)
        POISON,         // Damages over time, ignores armor (Goo Peashooter)
        LASER,          // Pierces through zombies (Repeater)
        CATAPULT,       // Lobbed with area damage (Melon-pult)
        BURST,          // Multiple shots (Threepeater)
        HOMING,         // Tracks zombies (Homing Thistle)
        MINT,           // Boosts all plants of a family
        MELEE,          // Close range physical (Bonk Choy)
        INSTANT,        // Instant kill (Cherry Bomb)
        TRAP,           // Mines (Potato Mine)
        SUPPORT,        // Support abilities (Sunflower)
        DEFENSIVE,      // Defensive (Wall-nut)
        STRIKE_THROUGH, // Hits all zombies in lane (Laser Bean)
        MAGIC,          // Special effects (Witch Hazel)
        ELECTRIC        // Chain lightning (Lightning Reed)
    }

    public ShootBehavior(ShootType shootType, int damage, int range, boolean pierce,
                         boolean explosive, boolean homing, int splashRadius, int cooldown) {
        this.shootType = shootType;
        this.damage = damage;
        this.range = range;
        this.pierce = pierce;
        this.explosive = explosive;
        this.homing = homing;
        this.splashRadius = splashRadius;
        this.cooldown = cooldown;
        this.currentCooldown = 0;
    }

    public ShootBehavior(ShootType shootType, int damage, int range, int cooldown) {
        this(shootType, damage, range, false, false, false, 0, cooldown);
    }

    public ShootBehavior(ShootType shootType, int damage, int range, boolean pierce, int cooldown) {
        this(shootType, damage, range, pierce, false, false, 0, cooldown);
    }

    public boolean canShoot() {
        return currentCooldown <= 0;
    }

    public void updateCooldown() {
        if (currentCooldown > 0) {
            currentCooldown--;
        }
    }

    public void resetCooldown() {
        currentCooldown = cooldown;
    }

    public Bullet createBullet(int row, int column) {
        // Determine bullet type based on shoot type
        Bullet.BulletType bulletType = switch (shootType) {
            case ICE -> Bullet.BulletType.ICE;
            case FIRE -> Bullet.BulletType.FIRE;
            case POISON -> Bullet.BulletType.POISON;
            case LASER -> Bullet.BulletType.LASER;
            case LOB, CATAPULT -> Bullet.BulletType.LOB;
            case HOMING -> Bullet.BulletType.HOMING;
            case STRIKE_THROUGH -> Bullet.BulletType.STRIKE_THROUGH;
            case ELECTRIC -> Bullet.BulletType.ELECTRIC;
            default -> Bullet.BulletType.NORMAL;
        };

        return new Bullet(damage, row, column, bulletType, pierce, explosive, splashRadius);
    }

    public List<Bullet> createBurstBullets(int row, int column, int count) {
        List<Bullet> bullets = new ArrayList<>();
        if (shootType == ShootType.BURST) {
            for (int i = 0; i < count; i++) {
                Bullet bullet = createBullet(row, column);
                bullet.setTargetRow(row + (i - count/2)); // Spread across rows
                bullets.add(bullet);
            }
        } else {
            bullets.add(createBullet(row, column));
        }
        return bullets;
    }

    public ShootType getShootType() { return shootType; }
    public int getDamage() { return damage; }
    public int getRange() { return range; }
    public boolean isPierce() { return pierce; }
    public boolean isExplosive() { return explosive; }
    public boolean isHoming() { return homing; }
    public int getSplashRadius() { return splashRadius; }
    public int getCooldown() { return cooldown; }
    public int getCurrentCooldown() { return currentCooldown; }
    public void setCurrentCooldown(int currentCooldown) { this.currentCooldown = currentCooldown; }

    public static class Builder {
        private ShootType shootType;
        private int damage = 20;
        private int range = 9;
        private boolean pierce = false;
        private boolean explosive = false;
        private boolean homing = false;
        private int splashRadius = 0;
        private int cooldown = 30;

        public Builder(ShootType shootType) {
            this.shootType = shootType;
        }

        public Builder damage(int damage) { this.damage = damage; return this; }
        public Builder range(int range) { this.range = range; return this; }
        public Builder pierce(boolean pierce) { this.pierce = pierce; return this; }
        public Builder explosive(boolean explosive) { this.explosive = explosive; return this; }
        public Builder homing(boolean homing) { this.homing = homing; return this; }
        public Builder splashRadius(int splashRadius) { this.splashRadius = splashRadius; return this; }
        public Builder cooldown(int cooldown) { this.cooldown = cooldown; return this; }

        public ShootBehavior build() {
            return new ShootBehavior(shootType, damage, range, pierce, explosive, homing, splashRadius, cooldown);
        }
    }
}