package model.minigame;

public class IZombie extends MiniGame {
    private int zombieSunCount;
    private int brainsEaten;
    private boolean[] brainRowEaten;

    public IZombie() {
        super("IZombie");
        this.zombieSunCount = 150;
        this.brainsEaten = 0;
        this.brainRowEaten = new boolean[5];
    }

    public int getZombieSunCount() {
        return zombieSunCount;
    }

    public void setZombieSunCount(int zombieSunCount) {
        this.zombieSunCount = zombieSunCount;
    }

    public void spendSun(int amount) {
        this.zombieSunCount -= amount;
    }

    public void addSun(int amount) {
        this.zombieSunCount += amount;
    }

    public int getBrainsEaten() {
        return brainsEaten;
    }

    public boolean isBrainRowEaten(int row) {
        if (row >= 0 && row < 5) return brainRowEaten[row];
        return true;
    }

    public void eatBrain(int row) {
        if (row >= 0 && row < 5 && !brainRowEaten[row]) {
            brainRowEaten[row] = true;
            brainsEaten++;
        }
    }

    public boolean isVictoryConditionMet() {
        return brainsEaten >= 5;
    }
}