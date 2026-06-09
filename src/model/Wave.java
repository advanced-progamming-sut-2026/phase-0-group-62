package model;

public class Wave {
    private final int number;
    private int remainingZombies;

    public Wave(int number) {
        this.number = number;
        this.remainingZombies = number * 5;
    }

    public int getNumber() {
        return number;
    }

    public int getRemainingZombies() {
        return remainingZombies;
    }

    public void zombieDefeated() {
        if (remainingZombies > 0) {
            remainingZombies--;
        }
    }
}

