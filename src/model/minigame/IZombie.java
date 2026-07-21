package model.minigame;

import model.Game;
import model.Tile;
import model.entities.zombie.Zombie;
import model.entities.plant.Plant;
import model.entities.plant.factory.PlantFactory;
import model.entities.zombie.factory.ZombieFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IZombie extends MiniGame {
    private int zombieSunCount;
    private int brainsEaten;
    private boolean[] brainRowEaten;
    private int stageLevel;
    private int maxStageLevel;
    private List<String> availableZombies;
    private List<String> stageZombiePool;
    private int sunProducerRows;

    private static final String[] ALL_ZOMBIE_TYPES = {
            "NormalZombie", "ConeZombie", "BucketZombie", "FootballZombie",
            "NewspaperZombie", "TurquoiseZombie", "ProspectorZombie",
            "PianistZombie", "BarrelRollerZombie", "AllStarZombie"
    };

    public IZombie() {
        super("IZombie");
        this.zombieSunCount = 150;
        this.brainsEaten = 0;
        this.brainRowEaten = new boolean[5];
        this.stageLevel = 1;
        this.maxStageLevel = 3;
        this.availableZombies = new ArrayList<>();
        this.stageZombiePool = new ArrayList<>();
        this.sunProducerRows = 0;
    }

    public void setupStage(Game game, int level) {
        this.stageLevel = level;
        this.brainsEaten = 0;
        this.brainRowEaten = new boolean[5];
        this.zombieSunCount = 150 + (level - 1) * 50;
        this.sunProducerRows = 0;

        // Clear existing plants and zombies
        for (Plant p : new ArrayList<>(game.getActivePlants())) {
            game.getBoard().getTile(p.getY(), p.getX()).setPlant(null);
            game.removePlant(p);
        }
        for (Zombie z : new ArrayList<>(game.getActiveZombies())) {
            game.getBoard().getTile(z.getY(), (int) z.getX()).setZombie(null);
            game.removeZombie(z);
        }

        // --- PLACE DEFENDING PLANTS ON THE LEFT SIDE ---
        Random rand = new Random();
        String[] plantTypes = {"PeaShooter", "Sunflower", "WallNut", "SnowPea", "Repeater", "Cabbagepult"};

        for (int row = 0; row < 5; row++) {
            int numPlants = 2 + rand.nextInt(3);
            for (int i = 0; i < numPlants; i++) {
                int col = rand.nextInt(4);
                String type = plantTypes[rand.nextInt(plantTypes.length)];
                Plant p = PlantFactory.createPlant(type);

                // FALLBACK: If PlantFactory returns null, create a plant manually
                if (p == null) {
                    // Create a fallback plant with proper constructor
                    int hp = 100;
                    int damage = 20;
                    if (type.equalsIgnoreCase("WallNut")) hp = 400;
                    if (type.equalsIgnoreCase("TallNut")) hp = 800;
                    if (type.equalsIgnoreCase("Repeater")) damage = 40;

                    p = new Plant(rand.nextInt(1000) + 200, type, "SHOOTER", null, 50, hp, damage, 2.0, 0, null, 0, null, 0);
                    p.initHealth();
                }

                if (p != null) {
                    p.setX(col);
                    p.setY(row);
                    game.addPlant(p);
                    game.getBoard().getTile(row, col).setPlant(p);
                }
            }
        }
    }

    private void setupStageZombiePool(int level) {
        stageZombiePool = new ArrayList<>();
        Random rand = new Random();

        String[][] stagePools = {
                {"NormalZombie", "ConeZombie", "NormalZombie", "NormalZombie", "ConeZombie"},
                {"NormalZombie", "ConeZombie", "BucketZombie", "NormalZombie", "ConeZombie"},
                {"NormalZombie", "ConeZombie", "BucketZombie", "FootballZombie", "NewspaperZombie"}
        };

        String[] pool = stagePools[Math.min(level - 1, stagePools.length - 1)];
        for (String type : pool) {
            stageZombiePool.add(type);
        }
    }

    public boolean placeZombie(String type, int lane, Game game) {
        if (lane < 0 || lane >= 5) return false;

        boolean valid = false;
        for (String z : stageZombiePool) {
            if (z.equalsIgnoreCase(type)) {
                valid = true;
                break;
            }
        }
        if (!valid) return false;

        int cost = getZombieCost(type);
        if (zombieSunCount < cost) return false;

        if (type.equalsIgnoreCase("SunProducerZombie")) return false;

        spendSun(cost);

        Zombie z = ZombieFactory.createZombieAtColumn(type, lane, 7);
        if (z == null) {
            z = new Zombie(type, 200, 0.5, 20);
            z.setX(7.0);
            z.setY(lane);
        } else {
            z.setX(7.0);
            z.setY(lane);
        }
        game.addZombie(z);
        game.getBoard().getTile(lane, 7).setZombie(z);
        game.getGameLogMessages().add("IZombie: Placed " + type + " in lane " + lane + " for " + cost + " suns.");
        return true;
    }

    private int getZombieCost(String type) {
        switch (type.toLowerCase()) {
            case "normalzombie": return 50;
            case "conezombie": return 75;
            case "bucketzombie": return 125;
            case "footballzombie": return 150;
            case "newspaperzombie": return 100;
            case "turquoisezombie": return 120;
            case "prospectorzombie": return 110;
            case "pianistzombie": return 130;
            case "barrelrollerzombie": return 140;
            case "allstarzombie": return 160;
            default: return 50;
        }
    }

    public int getZombieSunCount() { return zombieSunCount; }
    public void setZombieSunCount(int zombieSunCount) { this.zombieSunCount = zombieSunCount; }
    public void spendSun(int amount) { this.zombieSunCount -= amount; }
    public void addSun(int amount) { this.zombieSunCount += amount; }
    public int getBrainsEaten() { return brainsEaten; }
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
    public boolean isVictoryConditionMet() { return brainsEaten >= 5; }
    public int getStageLevel() { return stageLevel; }
    public List<String> getStageZombiePool() { return new ArrayList<>(stageZombiePool); }

    public void updateMiniGame(Game game) {
        if (game.getTickCount() == 1) {
            setupStage(game, stageLevel);
        }

        // SunProducerZombies generate suns
        for (Zombie z : new ArrayList<>(game.getActiveZombies())) {
            if (z.getName().equalsIgnoreCase("SunProducerZombie")) {
                z.incrementIzombieSunTicks();
                int baseProductionInterval = Math.max(100 - (z.getIzombieSunProductionTicks() / 10), 20);
                if (game.getTickCount() % baseProductionInterval == 0) {
                    addSun(25);
                    game.getGameLogMessages().add("IZombie: SunProducerZombie in lane " + z.getY() + " generated 25 suns.");
                }
            }
        }

        // Check for brain eating - zombies reaching column 0
        for (Zombie z : new ArrayList<>(game.getActiveZombies())) {
            if (z.getX() <= 0 && !z.getName().equalsIgnoreCase("SunProducerZombie")) {
                int row = z.getY();
                if (!isBrainRowEaten(row)) {
                    eatBrain(row);
                    game.getActiveZombies().remove(z);
                    game.getBoard().getTile(row, 0).setZombie(null);
                    game.getGameLogMessages().add("IZombie: Brain eaten in lane " + row + "! (" + brainsEaten + "/5)");

                    if (isVictoryConditionMet()) {
                        if (stageLevel < maxStageLevel) {
                            completeLevel(stageLevel, brainsEaten);
                            stageLevel++;
                            game.getGameLogMessages().add("IZombie: Stage " + (stageLevel - 1) + " complete! Moving to Stage " + stageLevel);
                            setupStage(game, stageLevel);
                            return;
                        } else {
                            game.setWon(true);
                            game.stop();
                            game.getGameLogMessages().add("IZombie: All stages complete! Victory!");
                            game.getGameLogMessages().add("Dear humanz, zis is not done yet; we will come back to eat your brainz, humanz.");
                            return;
                        }
                    }
                }
            }
        }

        // Check loss condition
        if (zombieSunCount < 50 && game.getActiveZombies().isEmpty()) {
            game.setLost(true);
            game.stop();
            game.getGameLogMessages().add("IZombie: Out of suns and no active zombies! Game Over!");
            game.getGameLogMessages().add("The zombie ate your brain; LOSER!!!");
        }
    }
}
