package model.minigame;

import model.Game;
import model.Tile;
import model.entities.plant.Plant;
import model.entities.plant.factory.PlantFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Beghoul extends MiniGame {
    private int matchesFormed;
    private int targetMatches;
    private boolean[][] craters;

    public Beghoul() {
        super("Beghoul");
        this.matchesFormed = 0;
        this.targetMatches = 10;
        this.craters = new boolean[5][9];
    }

    public int getMatchesFormed() {
        return matchesFormed;
    }

    public void addMatch() {
        this.matchesFormed++;
    }

    public int getTargetMatches() {
        return targetMatches;
    }

    public void setTargetMatches(int targetMatches) {
        this.targetMatches = targetMatches;
    }

    public boolean hasCrater(int r, int c) {
        if (r >= 0 && r < 5 && c >= 0 && c < 9) return craters[r][c];
        return false;
    }

    public void createCrater(int r, int c) {
        if (r >= 0 && r < 5 && c >= 0 && c < 9) {
            craters[r][c] = true;
        }
    }

    public void resetGridCraters() {
        this.craters = new boolean[5][9];
    }

    public boolean isVictoryConditionMet() {
        return matchesFormed >= targetMatches;
    }

    public void fillGridRandomly(Game game) {
        String[] types = {"PeaShooter", "Sunflower", "WallNut", "SnowPea", "Repeater"};
        Random rand = new Random();
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 9; c++) {
                if (!hasCrater(r, c)) {
                    Tile tile = game.getBoard().getTile(r, c);
                    if (tile.getPlant() != null) {
                        game.removePlant(tile.getPlant());
                    }
                    String type = types[rand.nextInt(types.length)];
                    Plant p = PlantFactory.createPlant(type);
                    if (p == null) {
                        p = new Plant(rand.nextInt(1000) + 200, type, "BEGHOULD", null, 0, 300, 20, 2.0, 0, null, 0, null, 0);
                    }
                    p.setX(c);
                    p.setY(r);
                    game.addPlant(p);
                    tile.setPlant(p);
                }
            }
        }
    }

    public boolean checkAndProcessMatches(Game game, boolean isCascade) {
        boolean[][] toRemove = new boolean[5][9];
        boolean foundMatch = false;

        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 7; c++) {
                Tile t1 = game.getBoard().getTile(r, c);
                Tile t2 = game.getBoard().getTile(r, c + 1);
                Tile t3 = game.getBoard().getTile(r, c + 2);
                if (t1.getPlant() != null && t2.getPlant() != null && t3.getPlant() != null) {
                    String name1 = t1.getPlant().getName();
                    String name2 = t2.getPlant().getName();
                    String name3 = t3.getPlant().getName();
                    if (name1.equalsIgnoreCase(name2) && name2.equalsIgnoreCase(name3)) {
                        toRemove[r][c] = true;
                        toRemove[r][c + 1] = true;
                        toRemove[r][c + 2] = true;
                        foundMatch = true;
                    }
                }
            }
        }

        for (int c = 0; c < 9; c++) {
            for (int r = 0; r < 3; r++) {
                Tile t1 = game.getBoard().getTile(r, c);
                Tile t2 = game.getBoard().getTile(r + 1, c);
                Tile t3 = game.getBoard().getTile(r + 2, c);
                if (t1.getPlant() != null && t2.getPlant() != null && t3.getPlant() != null) {
                    String name1 = t1.getPlant().getName();
                    String name2 = t2.getPlant().getName();
                    String name3 = t3.getPlant().getName();
                    if (name1.equalsIgnoreCase(name2) && name2.equalsIgnoreCase(name3)) {
                        toRemove[r][c] = true;
                        toRemove[r + 1][c] = true;
                        toRemove[r + 2][c] = true;
                        foundMatch = true;
                    }
                }
            }
        }

        if (!foundMatch) return false;

        int matchSizeCount = 0;
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 9; c++) {
                if (toRemove[r][c]) {
                    Tile tile = game.getBoard().getTile(r, c);
                    if (tile.getPlant() != null) {
                        game.removePlant(tile.getPlant());
                        tile.setPlant(null);
                        matchSizeCount++;
                    }
                }
            }
        }

        addMatch();
        int baseSunReward = 50;
        if (matchSizeCount == 4) baseSunReward = 100;
        else if (matchSizeCount >= 5) baseSunReward = 150;

        if (isCascade) {
            baseSunReward += 50;
        }

        game.addSun(baseSunReward);
        System.out.println("Beghoul Match formed! Suns rewarded: " + baseSunReward);

        applyGravityAndRefill(game);
        return true;
    }

    private void applyGravityAndRefill(Game game) {
        String[] types = {"PeaShooter", "Sunflower", "WallNut", "SnowPea", "Repeater"};
        Random rand = new Random();

        for (int c = 0; c < 9; c++) {
            for (int r = 4; r >= 0; r--) {
                if (!hasCrater(r, c) && game.getBoard().getTile(r, c).getPlant() == null) {
                    int nextRow = r - 1;
                    while (nextRow >= 0 && (hasCrater(nextRow, c) || game.getBoard().getTile(nextRow, c).getPlant() == null)) {
                        nextRow--;
                    }
                    if (nextRow >= 0) {
                        Plant p = game.getBoard().getTile(nextRow, c).getPlant();
                        game.getBoard().getTile(nextRow, c).setPlant(null);
                        p.setY(r);
                        game.getBoard().getTile(r, c).setPlant(p);
                        r++;
                    }
                }
            }
        }

        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 9; c++) {
                if (!hasCrater(r, c) && game.getBoard().getTile(r, c).getPlant() == null) {
                    String type = types[rand.nextInt(types.length)];
                    Plant p = PlantFactory.createPlant(type);
                    if (p == null) {
                        p = new Plant(rand.nextInt(1000) + 200, type, "BEGHOULD", null, 0, 300, 20, 2.0, 0, null, 0, null, 0);
                    }
                    p.setX(c);
                    p.setY(r);
                    game.addPlant(p);
                    game.getBoard().getTile(r, c).setPlant(p);
                }
            }
        }
    }

    public boolean hasAnyPossibleMoves(Game game) {
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 9; c++) {
                if (c < 8 && !hasCrater(r, c) && !hasCrater(r, c + 1)) {
                    if (testSwapCheck(game, r, c, r, c + 1)) return true;
                }
                if (r < 4 && !hasCrater(r, c) && !hasCrater(r + 1, c)) {
                    if (testSwapCheck(game, r, c, r + 1, c)) return true;
                }
            }
        }
        return false;
    }

    private boolean testSwapCheck(Game game, int r1, int c1, int r2, int c2) {
        Tile t1 = game.getBoard().getTile(r1, c1);
        Tile t2 = game.getBoard().getTile(r2, c2);
        Plant p1 = t1.getPlant();
        Plant p2 = t2.getPlant();
        if (p1 == null || p2 == null) return false;

        t1.setPlant(p2);
        t2.setPlant(p1);

        boolean hasMatch = false;
        outerLoop:
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 9; c++) {
                if (c < 7) {
                    Plant pA = game.getBoard().getTile(r, c).getPlant();
                    Plant pB = game.getBoard().getTile(r, c + 1).getPlant();
                    Plant pC = game.getBoard().getTile(r, c + 2).getPlant();
                    if (pA != null && pB != null && pC != null && pA.getName().equalsIgnoreCase(pB.getName()) && pB.getName().equalsIgnoreCase(pC.getName())) {
                        hasMatch = true;
                        break outerLoop;
                    }
                }
                if (r < 3) {
                    Plant pA = game.getBoard().getTile(r, c).getPlant();
                    Plant pB = game.getBoard().getTile(r + 1, c).getPlant();
                    Plant pC = game.getBoard().getTile(r + 2, c).getPlant();
                    if (pA != null && pB != null && pC != null && pA.getName().equalsIgnoreCase(pB.getName()) && pB.getName().equalsIgnoreCase(pC.getName())) {
                        hasMatch = true;
                        break outerLoop;
                    }
                }
            }
        }

        t1.setPlant(p1);
        t2.setPlant(p2);
        return hasMatch;
    }
}