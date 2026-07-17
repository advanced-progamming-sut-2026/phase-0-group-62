package model.minigame;

public class Vasebreaker extends MiniGame {
    private String[][] vaseContents;
    private boolean[][] vaseBroken;
    private int totalVases;
    private int brokenVasesCount;

    public Vasebreaker() {
        super("Vasebreaker");
        this.vaseContents = new String[5][9];
        this.vaseBroken = new boolean[5][9];
        this.totalVases = 0;
        this.brokenVasesCount = 0;
    }

    public void setupVaseGrid(int rows, int cols) {
        this.vaseContents = new String[rows][cols];
        this.vaseBroken = new boolean[rows][cols];
    }

    public String getVaseContent(int r, int c) {
        if (r >= 0 && r < vaseContents.length && c >= 0 && c < vaseContents[0].length) {
            return vaseContents[r][c];
        }
        return null;
    }

    public void setVaseContent(int r, int c, String content) {
        if (r >= 0 && r < vaseContents.length && c >= 0 && c < vaseContents[0].length) {
            vaseContents[r][c] = content;
            totalVases++;
        }
    }

    public boolean isVaseBroken(int r, int c) {
        if (r >= 0 && r < vaseBroken.length && c >= 0 && c < vaseBroken[0].length) {
            return vaseBroken[r][c];
        }
        return true;
    }

    public void breakVase(int r, int c) {
        if (r >= 0 && r < vaseBroken.length && c >= 0 && c < vaseBroken[0].length && !vaseBroken[r][c]) {
            vaseBroken[r][c] = true;
            brokenVasesCount++;
        }
    }

    public int getTotalVases() { return totalVases; }
    public int getBrokenVasesCount() { return brokenVasesCount; }
    public boolean isVictoryConditionMet() { return brokenVasesCount >= totalVases; }
}