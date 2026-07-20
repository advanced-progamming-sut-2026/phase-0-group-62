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

    // Available zombie types (5 per stage, 10 total across all stages)
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
        this.zombieSunCount = 150 + (level - 1) * 50; // Level 1: 150, Level 2: 200, Level 3: 250
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

        // Place pre-planted plants on the left side
        Random rand = new Random();
        String[] plantTypes = {"PeaShooter", "Sunflower", "WallNut", "SnowPea", "Repeater", "Cabbagepult"};
        
        for (int row = 0; row < 5; row++) {
            // Plant 2-4 plants per row
            int numPlants = 2 + rand.nextInt(3);
            for (int i = 0; i < numPlants; i++) {
                int col = rand.nextInt(4); // Columns 0-3 on the left
                String type = plantTypes[rand.nextInt(plantTypes.length)];
                Plant p = PlantFactory.createPlant(type);
                if (p != null) {
                    p.setX(col);
                    p.setY(row);
                    game.addPlant(p);
                    game.getBoard().getTile(row, col).setPlant(p);
                }
            }
        }

        // Place SunProducerZombie in each row
        for (int row = 0; row < 5; row++) {
            Zombie spz = ZombieFactory.createZombieAtColumn("NormalZombie", row, 8);
            if (spz != null) {
                // Mark as SunProducer (using name convention)
                spz = new Zombie("SunProducerZombie", 500, 0.3, 20);
                spz.setX(8.0);
                spz.setY(row);
                game.addZombie(spz);
                game.getBoard().getTile(row, 8).setZombie(spz);
                sunProducerRows++;
            }
        }

        // Setup stage-specific zombie pool (5 zombies per stage, total 10 across all stages)
        setupStageZombiePool(level);
        game.getGameLogMessages().add("IZombie: Stage " + level + " started! Zombie suns: " + zombieSunCount);
        game.getGameLogMessages().add("Available zombies: " + String.join(", ", stageZombiePool));
    }

    private void setupStageZombiePool(int level) {
        stageZombiePool.clear();
        Random rand = new Random();
        
        // Each stage gets 5 zombies
        int startIndex = (level - 1) * 3;
        int endIndex = Math.min(startIndex + 5, ALL_ZOMBIE_TYPES.length);
        
        // Ensure we have 5 zombies
        List<String> pool = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) {
            pool.add(ALL_ZOMBIE_TYPES[i]);
        }
        // If not enough, add more from beginning
        while (pool.size() < 5) {
            pool.add(ALL_ZOMBIE_TYPES[rand.nextInt(ALL_ZOMBIE_TYPES.length)]);
        }
        
        // Ensure no duplicates in stage
        List<String> uniquePool = new ArrayList<>();
        for (String type : pool) {
            if (!uniquePool.contains(type)) {
                uniquePool.add(type);
            }
        }
        // Fill remaining with unique types from all zombies
        while (uniquePool.size() < 5) {
            String candidate = ALL_ZOMBIE_TYPES[rand.nextInt(ALL_ZOMBIE_TYPES.length)];
            if (!uniquePool.contains(candidate)) {
                uniquePool.add(candidate);
            }
        }
        stageZombiePool = uniquePool;
    }

    public boolean placeZombie(String type, int lane, Game game) {
        if (lane < 0 || lane >= 5) return false;
        
        // Check if zombie is in pool
        boolean valid = false;
        for (String z : stageZombiePool) {
            if (z.equalsIgnoreCase(type)) {
                valid = true;
                break;
            }
        }
        if (!valid) return false;

        // Calculate cost
        int cost = getZombieCost(type);
        if (zombieSunCount < cost) return false;

        // Check if this is a SunProducerZombie (can't place these)
        if (type.equalsIgnoreCase("SunProducerZombie")) return false;

        spendSun(cost);
        
        Zombie z = ZombieFactory.createZombieAtColumn(type, lane, 8);
        if (z != null) {
            z.setX(7.0); // Start at the right of the red line
            z.setY(lane);
            game.addZombie(z);
            game.getBoard().getTile(lane, 7).setZombie(z);
            game.getGameLogMessages().add("IZombie: Placed " + type + " in lane " + lane + " for " + cost + " suns.");
            return true;
        }
        return false;
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
        // First tick setup
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
                        // Check if all 3 stages completed
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

        // Check loss condition: out of suns and no active zombies
        if (zombieSunCount < 50 && game.getActiveZombies().isEmpty()) {
            game.setLost(true);
            game.stop();
            game.getGameLogMessages().add("IZombie: Out of suns and no active zombies! Game Over!");
            game.getGameLogMessages().add("The zombie ate your brain; LOSER!!!");
        }

        // Spawn plants if zombies are getting close to them
        if (game.getTickCount() % 100 == 0 && game.getActiveZombies().size() < 3) {
            for (Zombie z : game.getActiveZombies()) {
                if (z.getX() < 5 && !z.getName().equalsIgnoreCase("SunProducerZombie")) {
                    // Plants attack zombies automatically
                    Plant target = game.getPlantAt((int) z.getX() - 1, z.getY());
                    if (target != null && target.getCategory() != null && target.getCategory().equalsIgnoreCase("SHOOTER")) {
                        // Plants will shoot automatically via game loop
                    }
                }
            }
        }

        // Plant-Zombie interactions (normal game rules handle this)
    }
}
