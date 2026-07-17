package model.minigame;

public class WallnutBowling extends MiniGame {
    private int deadlineColumn;
    private int redLineX;

    public WallnutBowling() {
        super("WallnutBowling");
        this.deadlineColumn = 3;
        this.redLineX = 3;
    }

    public int getDeadlineColumn() {
        return deadlineColumn;
    }

    public void setDeadlineColumn(int deadlineColumn) {
        this.deadlineColumn = deadlineColumn;
    }

    public int getRedLineX() {
        return redLineX;
    }

    public void setRedLineX(int redLineX) {
        this.redLineX = redLineX;
    }
}