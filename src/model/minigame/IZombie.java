package model.minigame;

import model.Game;
import model.entities.zombie.Zombie;
import java.util.ArrayList;

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

    public void updateMiniGame(Game game) {
        for (Zombie z : new ArrayList<>(game.getActiveZombies())) {
            if (z.getName().equalsIgnoreCase("SunProducerZombie")) {
                z.incrementIzombieSunTicks();
                int baseProductionInterval = Math.max(100 - (z.getIzombieSunProductionTicks() / 10), 20);
                if (game.getTickCount() % baseProductionInterval == 0) {
                    addSun(25);
                    System.out.println("IZombie: SunProducerZombie in lane " + z.getY() + " generated 25 suns. Rate increased!");
                }
            }
        }
        if (zombieSunCount < 50 && game.getActiveZombies().isEmpty()) {
            game.setLost(true);
            game.stop();
            System.out.println("IZombie: Out of suns and no active zombies left! Game Over!");
        }
    }
}