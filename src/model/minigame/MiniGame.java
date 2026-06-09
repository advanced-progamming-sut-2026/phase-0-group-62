package model.minigame;

public class MiniGame {
    private String name;
    private boolean completed;

    public MiniGame(String name) {
        this.name = name;
    }

    public void complete() {
        completed = true;
    }

    public String getName() {
        return name;
    }

    public boolean isCompleted() {
        return completed;
    }
}

