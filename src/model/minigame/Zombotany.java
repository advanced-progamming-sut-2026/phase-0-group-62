package model.minigame;

public class Zombotany extends MiniGame {
    private int stageDifficulty;

    public Zombotany() {
        super("Zombotany");
        this.stageDifficulty = 1;
    }

    public int getStageDifficulty() {
        return stageDifficulty;
    }

    public void setStageDifficulty(int stageDifficulty) {
        this.stageDifficulty = stageDifficulty;
    }
}