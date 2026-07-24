package model;

import model.enums.Difficulty;
import model.entities.zombie.Spawner;
import model.entities.zombie.Zombie;
import model.entities.zombie.ZombieEffect;
import model.entities.plant.Plant;
import model.score.ScoreGame;
import model.greenhouse.Greenhouse;
import model.enums.TileType;
import model.enums.SpecialLevelType;
import model.minigame.MiniGame;
import model.minigame.Vasebreaker;
import model.minigame.WallnutBowling;
import model.minigame.IZombie;
import model.minigame.Zombotany;
import model.minigame.Beghoul;
import model.season.Season;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
    private Board board;
    private Level level;
    private Difficulty difficulty;
    private int difficultyLevel;
    private int sunCount;
    private boolean running;
    private int coins;
    private int diamonds;
    private int plantFoodCount;
    private Spawner spawner;
    private ScoreGame scoreGame;
    private Greenhouse greenhouse;
    private List<Bullet> bullets;
    private List<Sun> suns;
    private List<Zombie> activeZombies;
    private List<Plant> activePlants;
    private int tickCount;
    private LawnMower[] lawnMowers;
    private boolean won;
    private boolean lost;
    private int lastSunDropTick;
    private int lastPrintedWave;
    private Season currentSeason;
    private List<String> conveyorBeltPlants;
    private int lastConveyorSpawnTick;
    private List<Plant> seedsToProtect;
    private int zombiesKilledInLevel;
    private int sunsProducedInLevel;
    private int plantsLostCount;
    private boolean zombieWavesStarted;
    private MiniGame activeMiniGame;
    private List<String> gameLogMessages = new ArrayList<>();

    public Game() {
        this.board = new Board(5, 9);
        this.level = new Level(1);
        this.difficulty = Difficulty.NORMAL;
        this.difficultyLevel = 3;
        this.sunCount = 50;
        this.coins = 0;
        this.diamonds = 0;
        this.plantFoodCount = 0;
        this.bullets = new ArrayList<>();
        this.suns = new ArrayList<>();
        this.activeZombies = new ArrayList<>();
        this.activePlants = new ArrayList<>();
        this.scoreGame = new ScoreGame();
        this.tickCount = 0;
        this.lawnMowers = new LawnMower[5];
        for (int i = 0; i < 5; i++) {
            lawnMowers[i] = new LawnMower(i);
        }
        this.won = false;
        this.lost = false;
        this.lastSunDropTick = 0;
        this.lastPrintedWave = 0;
        this.currentSeason = new Season("Normal", 10);
        this.conveyorBeltPlants = new ArrayList<>();
        this.lastConveyorSpawnTick = 0;
        this.seedsToProtect = new ArrayList<>();
        this.zombiesKilledInLevel = 0;
        this.sunsProducedInLevel = 0;
        this.plantsLostCount = 0;
        this.zombieWavesStarted = true;
        this.activeMiniGame = null;
    }

    public Game(int rows, int columns, int levelNumber, int difficultyLevel) {
        this();
        this.board = new Board(rows, columns);
        this.level = new Level(levelNumber);
        this.difficultyLevel = difficultyLevel;
        if (difficultyLevel <= 2) {
            this.difficulty = Difficulty.EASY;
        } else if (difficultyLevel >= 4) {
            this.difficulty = Difficulty.HARD;
        } else {
            this.difficulty = Difficulty.NORMAL;
        }
        this.spawner = new Spawner(board, levelNumber * 2, this.difficulty);
        this.lawnMowers = new LawnMower[rows];
        for (int i = 0; i < rows; i++) {
            lawnMowers[i] = new LawnMower(i);
        }
    }

    public Game(int rows, int columns, int levelNumber, Difficulty difficulty) {
        this(rows, columns, levelNumber, difficulty == Difficulty.EASY ? 1 : (difficulty == Difficulty.HARD ? 5 : 3));
    }

    public void start() {
        running = true;
        if (spawner != null) {
            spawner.startWave(1);
            gameLogMessages.add("Wave " + spawner.getCurrentWave() + " started.");
        }
    }

    public void stop() {
        running = false;
    }

    public Season getCurrentSeason() {
        return currentSeason;
    }

    public void setCurrentSeason(Season currentSeason) {
        this.currentSeason = currentSeason;
        if (this.spawner != null) {
            this.spawner.setCurrentSeason(currentSeason);
        }
        if (currentSeason != null) {
            currentSeason.setupEnvironment(this);
        }
    }

    public void setupSpecialLevelFeatures() {
        if (level == null) return;
        SpecialLevelType type = level.getSpecialLevelType();
        if (type == SpecialLevelType.SAVE_OUR_SEEDS) {
            for (int[] pos : level.getSeedProtectionPositions()) {
                Plant p = model.entities.plant.factory.PlantFactory.createPlant("PeaShooter");
                if (p != null) {
                    p.setX(pos[1]);
                    p.setY(pos[0]);
                    addPlant(p);
                    board.getTile(pos[0], pos[1]).setPlant(p);
                    seedsToProtect.add(p);
                }
            }
        } else if (type == SpecialLevelType.PLANT_WHAT_YOU_GET) {
            this.sunCount = level.getInitialSunAmount();
            this.zombieWavesStarted = false;
        }
    }

    private String getRandomUnlockedPlant() {
        List<String> unlocked = new ArrayList<>();
        if (model.UserSession.isLoggedIn() && model.UserSession.getCurrentUser() != null) {
            unlocked = model.UserSession.getCurrentUser().getUnlockedPlants();
        }
        if (unlocked == null || unlocked.isEmpty()) {
            unlocked = new ArrayList<>();
            unlocked.add("PeaShooter");
        }
        return unlocked.get(new Random().nextInt(unlocked.size()));
    }

    public void startZombieWaves() {
        this.zombieWavesStarted = true;
    }

    public boolean isZombieWavesStarted() {
        return zombieWavesStarted;
    }

    public void setZombieWavesStarted(boolean zombieWavesStarted) {
        this.zombieWavesStarted = zombieWavesStarted;
    }

    public List<String> getConveyorBeltPlants() {
        return conveyorBeltPlants;
    }

    public List<Plant> getSeedsToProtect() {
        return seedsToProtect;
    }

    public int getZombiesKilledInLevel() {
        return zombiesKilledInLevel;
    }

    public int getPlantsLostCount() {
        return plantsLostCount;
    }

    public void setLost(boolean lost) {
        this.lost = lost;
    }

    public void setWon(boolean won) {
        this.won = won;
    }

    public void setSunCount(int sunCount) {
        this.sunCount = sunCount;
    }

    public MiniGame getActiveMiniGame() {
        return activeMiniGame;
    }

    public void setActiveMiniGame(MiniGame activeMiniGame) {
        this.activeMiniGame = activeMiniGame;
    }

    public List<String> getGameLogMessages() {
        List<String> logCopy = new ArrayList<>(gameLogMessages);
        gameLogMessages.clear();
        return logCopy;
    }

    public void incrementZombiesKilled() {
        this.zombiesKilledInLevel++;
    }

    public void incrementPlantsLost() {
        this.plantsLostCount++;
    }

    public void tick() {
        if (!running || won || lost) return;
        tickCount++;

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getColumns(); c++) {
                board.getTile(r, c).setZombie(null);
            }
        }

        for (Zombie zombie : activeZombies) {
            int zX = (int) Math.round(zombie.getX());
            int zY = zombie.getY();
            if (zX >= 0 && zX < board.getColumns() && zY >= 0 && zY < board.getRows()) {
                board.getTile(zY, zX).setZombie(zombie);
            }
        }

        if (activeMiniGame instanceof Beghoul) {
            ((Beghoul) activeMiniGame).updateMiniGame(this);
            if (won || lost) return;
        } else if (activeMiniGame instanceof IZombie) {
            ((IZombie) activeMiniGame).updateMiniGame(this);
            if (won || lost) return;
        } else if (activeMiniGame instanceof Vasebreaker) {
            ((Vasebreaker) activeMiniGame).updateMiniGame(this);
            if (won || lost) return;
        } else if (activeMiniGame instanceof WallnutBowling) {
            ((WallnutBowling) activeMiniGame).updateMiniGame(this);
            if (won || lost) return;
        } else if (activeMiniGame instanceof Zombotany) {
            ((Zombotany) activeMiniGame).updateMiniGame(this);
            if (won || lost) return;
        }

        SpecialLevelType specialType = level.getSpecialLevelType();
        if (activeMiniGame == null && specialType == SpecialLevelType.CONVEYOR_BELT) {
            if (tickCount == 1 || tickCount % 120 == 0) {
                String randomPlant = getRandomUnlockedPlant();
                conveyorBeltPlants.add(randomPlant);
            }
        }

        if (specialType == SpecialLevelType.SAVE_OUR_SEEDS) {
            for (Plant p : seedsToProtect) {
                if (!p.isAlive() || !activePlants.contains(p)) {
                    lost = true;
                    running = false;
                    gameLogMessages.add("The zombie ate your brain; LOSER!!!");
                    return;
                }
            }
        }

        if (specialType == SpecialLevelType.TIMED_WAR) {
            if (zombiesKilledInLevel >= level.getTargetZombiesToKill() || sunCount >= level.getTargetSunsToProduce()) {
                won = true;
                running = false;
                gameLogMessages.add("Dear humanz, zis is not done yet; we will come back to eat your brainz, humanz.");
                if (model.UserSession.isLoggedIn() && model.UserSession.getCurrentUser() != null) {
                    model.UserSession.getCurrentUser().addNews("Congratulations! New levels and mini-games are now unlocked.");
                    util.FileManager.updateUser(model.UserSession.getCurrentUser());
                }
                return;
            }
            if (tickCount >= level.getTimeLimitTicks()) {
                lost = true;
                running = false;
                gameLogMessages.add("The zombie ate your brain; LOSER!!!");
                return;
            }
        }

        if (specialType == SpecialLevelType.DEAD_LINE) {
            int lineCol = level.getDeadlineColumn();
            for (Zombie z : activeZombies) {
                if (z.getX() <= lineCol) {
                    lost = true;
                    running = false;
                    gameLogMessages.add("The zombie ate your brain; LOSER!!!");
                    return;
                }
            }
        }

        if (specialType == SpecialLevelType.LOVE_YOUR_PLANTS) {
            if (plantsLostCount > level.getMaxPlantsLostAllowed()) {
                lost = true;
                running = false;
                gameLogMessages.add("The zombie ate your brain; LOSER!!!");
                return;
            }
        }

        updatePlantsAndAbilities();
        List<Zombie> zombiesToRemove = new ArrayList<>();
        List<Zombie> zombiesToAdd = new ArrayList<>();

        for (Zombie zombie : new ArrayList<>(activeZombies)) {
            zombie.updateEffects();
            zombie.updateCooldown();

            if (!zombie.isAlive()) {
                zombiesToRemove.add(zombie);
                if (zombie.getName().equalsIgnoreCase("ZombieCrystalSkull")) {
                    if (zombie.getStolenSunCount() > 0) {
                        addSun(zombie.getStolenSunCount());
                        gameLogMessages.add("Turquoise Zombie died! Returned " + zombie.getStolenSunCount() + " stolen suns.");
                    }
                } else if (zombie.getStolenSuns() > 0) {
                    int returnSun = zombie.getStolenSuns();
                    addSun(returnSun);
                    gameLogMessages.add("Ra Zombie died! Returned " + returnSun + " stolen suns.");
                }
                if (zombie.getName().equalsIgnoreCase("ZombieWizard")) {
                    for (Plant p : activePlants) {
                        if (p.isTransformedToSheep()) {
                            p.setTransformedToSheep(false);
                        }
                    }
                }
                String deathMessage = "Zombie of type " + zombie.getName() + " is dead at (" + (int) Math.round(zombie.getX()) + ", " + zombie.getY() + ")";
                gameLogMessages.add(deathMessage);

                processZombieDeathDrops(zombie);

                scoreGame.onZombieKilled(zombie, this);
                zombiesKilledInLevel++;
                continue;
            }

            // --- بررسی برخورد فوری زامبی با Iceberg Lettuce و مین‌های مسلح (Potato Mine / Primal Potato Mine) ---
            double zombieXCoord = zombie.getX();
            int zombieYCoord = zombie.getY();
            int zombieCurrentCol = (int) Math.round(zombieXCoord);

            for (Plant plant : new ArrayList<>(activePlants)) {
                if (plant.getY() == zombieYCoord && Math.abs(plant.getX() - zombieXCoord) <= 0.8) {
                    String pName = plant.getName();
                    if (pName.equalsIgnoreCase("Iceberg Lettuce")) {
                        zombie.applyFrozen(5.0);
                        activePlants.remove(plant);
                        board.getTile(plant.getY(), plant.getX()).setPlant(null);
                        gameLogMessages.add("Iceberg Lettuce froze zombie " + zombie.getName() + "!");
                        break;
                    } else if ((pName.equalsIgnoreCase("Potato Mine") || pName.equalsIgnoreCase("Primal Potato Mine")) && plant.isArmed()) {
                        int rad = pName.contains("Primal") ? 1 : 0;
                        for (Zombie az : new ArrayList<>(activeZombies)) {
                            if (Math.abs(az.getY() - plant.getY()) <= rad && Math.abs(az.getX() - plant.getX()) <= (rad + 0.8)) {
                                az.takeDamage(plant.getDamage() > 0 ? plant.getDamage() : 1800, true);
                            }
                        }
                        activePlants.remove(plant);
                        board.getTile(plant.getY(), plant.getX()).setPlant(null);
                        gameLogMessages.add(pName + " detonated on zombie contact!");
                        break;
                    }
                }
            }

            processSpecialZombieAbilities(zombie, zombiesToAdd);

            if (zombie.isHypnotized()) {
                for (Zombie enemy : activeZombies) {
                    if (!enemy.isHypnotized() && enemy.getY() == zombie.getY() && Math.abs(enemy.getX() - zombie.getX()) <= 0.8) {
                        enemy.takeDamage(zombie.getDamage(), false);
                        gameLogMessages.add("Hypnotized zombie attacked " + enemy.getName() + "!");
                        break;
                    }
                }
            } else if (!zombie.hasEffect(ZombieEffect.FROZEN)) {
                double zombieX = zombie.getX();
                int zombieY = zombie.getY();

                if (zombie.isDodoRider() && !zombie.isJumping() && zombie.getJumpCooldown() <= 0) {
                    int nextTileX = (int) Math.floor(zombieX) - 1;
                    if (nextTileX >= 0 && nextTileX < board.getColumns()) {
                        Tile nextTile = board.getTile(zombieY, nextTileX);
                        Plant plantAtNext = getPlantAt(nextTileX, zombieY);

                        boolean shouldJump = false;
                        if (nextTile != null && nextTile.isSlideway()) {
                            shouldJump = true;
                        }
                        if (plantAtNext != null && plantAtNext.getName().equalsIgnoreCase("WallNut") &&
                                !plantAtNext.getName().equalsIgnoreCase("TallNut")) {
                            shouldJump = true;
                        }
                        if (plantAtNext != null && (plantAtNext.getName().equalsIgnoreCase("Chomper") ||
                                plantAtNext.getName().equalsIgnoreCase("Squash") ||
                                plantAtNext.getName().equalsIgnoreCase("PotatoMine") ||
                                plantAtNext.getName().equalsIgnoreCase("PrimalPotatoMine"))) {
                            shouldJump = true;
                        }

                        if (shouldJump) {
                            int jumpDistance = 2;
                            double targetX = nextTileX - jumpDistance;
                            if (targetX < 0) targetX = 0;
                            zombie.startJump(targetX, 10);
                            gameLogMessages.add("Dodo Rider jumped over obstacle at (" + nextTileX + ", " + zombieY + ")!");
                        }
                    }
                }

                int targetPlantX = (int) Math.floor(zombieX);
                if (zombieX - targetPlantX == 0.0) {
                    targetPlantX = targetPlantX - 1;
                }

                Plant targetPlant = getPlantAt(targetPlantX, zombieY);

                if (targetPlant != null && !targetPlant.isBowlingBall() && zombieX - targetPlant.getX() <= 1.05) {
                    if (zombie.isBarrelRoller() && !zombie.isBarrelDestroyed()) {
                        activePlants.remove(targetPlant);
                        board.getTile(targetPlant.getY(), targetPlant.getX()).setPlant(null);
                        plantsLostCount++;
                        gameLogMessages.add("Barrel Roller crushed " + targetPlant.getName() + " at (" + targetPlant.getX() + ", " + targetPlant.getY() + ")!");
                        continue;
                    }

                    if (zombie.isTroglobite() && !zombie.isIceBlockDestroyed()) {
                        activePlants.remove(targetPlant);
                        board.getTile(targetPlant.getY(), targetPlant.getX()).setPlant(null);
                        plantsLostCount++;
                        gameLogMessages.add("Troglobite crushed " + targetPlant.getName() + " with ice block at (" + targetPlant.getX() + ", " + targetPlant.getY() + ")!");
                        continue;
                    }

                    if (zombie.getName().equalsIgnoreCase("ZombieBeachSnorkel") && zombie.isUnderwater()) {
                        zombie.setUnderwater(false);
                        zombie.setHasSurfaced(true);
                        gameLogMessages.add("Snorkel Zombie surfaced at (" + targetPlant.getX() + ", " + targetPlant.getY() + ")!");
                    }

                    if (zombie.getName().equalsIgnoreCase("ZombieExplorer") && zombie.isTorchLit()) {
                        activePlants.remove(targetPlant);
                        board.getTile(targetPlant.getY(), targetPlant.getX()).setPlant(null);
                        plantsLostCount++;
                        gameLogMessages.add("Explorer Zombie burned plant " + targetPlant.getName() + " at (" + targetPlant.getX() + ", " + targetPlant.getY() + ")!");
                    } else if (zombie.getName().equalsIgnoreCase("ZombieModernAllStar") && zombie.isCharging()) {
                        targetPlant.takeDamage(1500);
                        zombie.setCharging(false);
                        gameLogMessages.add("All-Star Zombie tackled plant " + targetPlant.getName() + "!");
                        checkPlantDeath(targetPlant);
                    } else if (zombie.getName().equalsIgnoreCase("ZombieWizard")) {
                        // Handled in processSpecialZombieAbilities
                    } else {
                        if (tickCount % 10 == 0) {
                            targetPlant.takeDamage(zombie.getDamage());
                            scoreGame.onDamageTaken(zombie.getDamage());

                            if (targetPlant.getName().equalsIgnoreCase("Sun Bean")) {
                                addSun(5);
                                gameLogMessages.add("Sun Bean produced 5 suns from zombie bite!");
                            } else if (targetPlant.getName().equalsIgnoreCase("Hypno-shroom")) {
                                zombie.setHypnotized(true);
                                targetPlant.takeDamage(99999);
                                gameLogMessages.add("Zombie ate Hypno-shroom and turned to fight for player!");
                            } else if (targetPlant.getName().equalsIgnoreCase("Endurian")) {
                                zombie.takeDamage(20, false);
                                gameLogMessages.add("Endurian reflected 20 damage back to zombie!");
                            } else if (targetPlant.getName().equalsIgnoreCase("Garlic")) {
                                int newY = zombie.getY() + (new Random().nextBoolean() ? 1 : -1);
                                if (newY >= 0 && newY < board.getRows()) {
                                    zombie.setY(newY);
                                    gameLogMessages.add("Garlic redirected zombie to lane " + newY + "!");
                                }
                            }

                            checkPlantDeath(targetPlant);
                        }
                    }
                } else {
                    if (zombie.getName().equalsIgnoreCase("ZombieBeachSnorkel") && zombie.isUnderwater()) {
                        zombie.move();
                        continue;
                    }

                    int nextTileX = (int) Math.floor(zombieX);
                    if (nextTileX >= 0 && nextTileX < board.getColumns() && zombieY >= 0 && zombieY < board.getRows()) {
                        Tile currentTile = board.getTile(zombieY, nextTileX);
                        if (currentSeason != null && "FrostbiteCaves".equalsIgnoreCase(currentSeason.getName()) && currentTile != null && currentTile.isSlideway() && !zombie.isDodoRider()) {
                            int targetRow = zombie.getY() + currentTile.getSlideRowOffset();
                            if (targetRow >= 0 && targetRow < board.getRows()) {
                                zombie.setY(targetRow);
                                zombie.move();
                            } else {
                                zombie.move();
                            }
                        } else {
                            zombie.move();
                        }
                    } else {
                        zombie.move();
                    }
                }
            }

            if (!zombie.isHypnotized() && zombie.getX() <= 0) {
                int row = zombie.getY();
                if (activeMiniGame instanceof IZombie) {
                    IZombie iz = (IZombie) activeMiniGame;
                    iz.eatBrain(row);
                    zombiesToRemove.add(zombie);
                    if (iz.isVictoryConditionMet()) {
                        won = true;
                        running = false;
                        gameLogMessages.add("Dear humanz, zis is not done yet; we will come back to eat your brainz, humanz.");
                        return;
                    }
                    continue;
                }

                if (!(activeMiniGame instanceof WallnutBowling)) {
                    LawnMower mower = lawnMowers[row];
                    if (!mower.isUsed()) {
                        List<Zombie> toKill = new ArrayList<>();
                        for (Zombie z : activeZombies) {
                            if (z.getY() == row) {
                                toKill.add(z);
                            }
                        }
                        if (!toKill.isEmpty()) {
                            mower.activate();
                            gameLogMessages.add("The lawn mower in the row " + row + " is triggered and killed these zombies:");
                            zombiesToRemove.addAll(toKill);
                            for (Zombie killed : toKill) {
                                scoreGame.onZombieKilled(killed, this);
                                zombiesKilledInLevel++;
                                gameLogMessages.add("Zombie of type " + killed.getName() + " is dead at (" + (int)Math.round(killed.getX()) + ", " + killed.getY() + ")");
                            }
                        }
                    } else {
                        if (!zombiesToRemove.contains(zombie)) {
                            lost = true;
                            running = false;
                            scoreGame.onComboBreak();
                            gameLogMessages.add("The zombie ate your brain; LOSER!!!");
                            return;
                        }
                    }
                }
            }
        }
        activeZombies.removeAll(zombiesToRemove);
        activeZombies.addAll(zombiesToAdd);

        for (Zombie z : activeZombies) {
            if (z.getName().equalsIgnoreCase("ZombieBeachSnorkel") && z.isUnderwater()) {
                if (z.getHealth() < z.getMaxHealth()) {
                    z.setHealth(z.getHealth() + 1);
                }
            }
        }

        if (lost || won || !running) return;
        if (spawner != null) {
            if ((specialType == SpecialLevelType.PLANT_WHAT_YOU_GET && !zombieWavesStarted) || activeMiniGame instanceof Vasebreaker || activeMiniGame instanceof IZombie || activeMiniGame instanceof Beghoul) {
            } else {
                Zombie newlySpawned = spawner.update();
                if (newlySpawned != null) {
                    if (currentSeason != null && "AncientEgypt".equalsIgnoreCase(currentSeason.getName()) && spawner.isFinalWave()) {
                        int currentWave = spawner.getCurrentWave();
                        int totalWaves = spawner.getTotalWaves();
                        int defaultColumn = board.getColumns() - 1;
                        int modifiedCol = currentSeason.modifySpawnColumn(currentWave, totalWaves, defaultColumn, spawner.getZombiesSpawnedInWave(), board, newlySpawned.getY());
                        newlySpawned.setX(modifiedCol);
                    }
                    activeZombies.add(newlySpawned);
                    int cost = newlySpawned.getWaveCost();
                    gameLogMessages.add("Zombie " + newlySpawned.getName() + " spawned at wave " + spawner.getCurrentWave() + " in lane " + newlySpawned.getY() + " which costed " + cost + ".");
                }
            }
        }
        if (lost || won || !running) return;
        handleSunDrop();
        if (currentSeason != null) {
            currentSeason.handleTick(this);
            if (currentSeason.getName().equalsIgnoreCase("BigWaveBeach")) {
                for (Plant p : new ArrayList<>(activePlants)) {
                    Tile t = board.getTile(p.getY(), p.getX());
                    if (t != null && t.getType() == TileType.WATER) {
                        boolean isAquatic = p.isAquatic();
                        boolean hasLilyPad = (t.getSupportPlant() != null && t.getSupportPlant().getName().equalsIgnoreCase("Lily Pad"));
                        if (!isAquatic && !hasLilyPad) {
                            activePlants.remove(p);
                            t.setPlant(null);
                            gameLogMessages.add("Plant " + p.getName() + " drowned in the rising tide!");
                        }
                    }
                }
            }
        }
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getColumns(); c++) {
                board.getTile(r, c).setZombie(null);
            }
        }
        for (Zombie zombie : activeZombies) {
            int zX = (int) Math.round(zombie.getX());
            int zY = zombie.getY();
            if (zX >= 0 && zX < board.getColumns() && zY >= 0 && zY < board.getRows()) {
                board.getTile(zY, zX).setZombie(zombie);
            }
        }
        board.updateProjectilesAndCollisions(this);
        if (spawner != null && !(activeMiniGame instanceof Vasebreaker) && !(activeMiniGame instanceof IZombie) && !(activeMiniGame instanceof Beghoul)) {
            if (spawner.isWaveComplete() && activeZombies.isEmpty()) {
                if (spawner.getCurrentWave() < spawner.getTotalWaves()) {
                    int nextWave = spawner.getCurrentWave() + 1;
                    if (currentSeason != null) { currentSeason.handleWaveStart(this); }
                    spawner.startWave(nextWave);
                    if (spawner.isFinalWave()) {
                        gameLogMessages.add("The final wave has come.");
                    } else {
                        gameLogMessages.add("Wave " + nextWave + " started.");
                    }
                } else {
                    won = true;
                    running = false;
                    scoreGame.onWaveCompleted(spawner.getCurrentWave());
                    gameLogMessages.add("Dear humanz, zis is not done yet; we will come back to eat your brainz, humanz.");
                    if (model.UserSession.isLoggedIn() && model.UserSession.getCurrentUser() != null) {
                        model.UserSession.getCurrentUser().addNews("Congratulations! New levels and mini-games are now unlocked.");
                        util.FileManager.updateUser(model.UserSession.getCurrentUser());
                    }
                }
            } else if (!spawner.isWaveComplete()) {
                double healthSum = 0;
                double maxHealthSum = 0;
                for (Zombie z : activeZombies) {
                    healthSum += z.getHealth();
                    maxHealthSum += z.getMaxHealth();
                }
                int scheduledCount = spawner.getZombiesInWave();
                int remainingToSpawn = scheduledCount - spawner.getZombiesSpawnedInWave();
                double remainingSpawnHealth = remainingToSpawn * 200.0;
                double totalWaveHealth = (scheduledCount * 200.0) + maxHealthSum;
                double currentWaveHealth = healthSum + remainingSpawnHealth;
                if (totalWaveHealth > 0 && (currentWaveHealth / totalWaveHealth) <= 0.25) {
                    if (spawner.getCurrentWave() < spawner.getTotalWaves()) {
                        int nextWave = spawner.getCurrentWave() + 1;
                        if (currentSeason != null) { currentSeason.handleWaveStart(this); }
                        spawner.startWave(nextWave);
                        if (spawner.isFinalWave()) {
                            gameLogMessages.add("The final wave has come.");
                        } else {
                            gameLogMessages.add("Wave " + nextWave + " started.");
                        }
                    }
                }
            }
        }
    }

    private void checkPlantDeath(Plant plant) {
        if (plant != null && !plant.isAlive()) {
            activePlants.remove(plant);
            board.getTile(plant.getY(), plant.getX()).setPlant(null);
            plantsLostCount++;
            gameLogMessages.add("Plant " + plant.getName() + " at (" + plant.getX() + ", " + plant.getY() + ") is destroyed.");

            if (plant.getName().equalsIgnoreCase("Explode-o-nut")) {
                for (Zombie z : new ArrayList<>(activeZombies)) {
                    if (Math.abs(z.getY() - plant.getY()) <= 1 && Math.abs(z.getX() - plant.getX()) <= 1.5) {
                        z.takeDamage(1800, true);
                    }
                }
                gameLogMessages.add("Explode-o-nut exploded on death in a 3x3 area!");
            }

            if (activeMiniGame instanceof Beghoul) {
                ((Beghoul) activeMiniGame).createCrater(plant.getY(), plant.getX());
            }
        }
    }
    private void updatePlantsAndAbilities() {
        List<Plant> plantsToRemove = new ArrayList<>();
        for (Plant plant : new ArrayList<>(activePlants)) {
            if (plant.isFrozen() || plant.isBowlingBall() || plant.isTransformedToSheep()) {
                continue;
            }
            plant.update();
            String name = plant.getName();

            // --- اصلاح قطعی انجماد Iceberg Lettuce وقتی زامبی می‌رسد روی خودش ---
            if (name.equalsIgnoreCase("Iceberg Lettuce")) {
                for (Zombie z : new ArrayList<>(activeZombies)) {
                    // بررسی اینکه زامبی در همان سطر باشد و به مختصات ستون گیاه رسیده باشد (فاصله کمتر از 0.6)
                    if (!z.isHypnotized() && z.getY() == plant.getY() && Math.abs(z.getX() - plant.getX()) <= 0.6) {
                        z.applyFrozen(5.0); // انجماد زامبی
                        plantsToRemove.add(plant); // نابودی کاهو
                        gameLogMessages.add("Iceberg Lettuce froze zombie " + z.getName() + " at column " + plant.getX() + "!");
                        break;
                    }
                }
            }

            if (name.equalsIgnoreCase("Sweet Potato")) {
                for (Zombie z : activeZombies) {
                    if (Math.abs(z.getY() - plant.getY()) == 1 && Math.abs(z.getX() - plant.getX()) <= 3) {
                        z.setY(plant.getY());
                        gameLogMessages.add("Sweet Potato attracted zombie from adjacent lane to lane " + plant.getY() + "!");
                    }
                }
            } else if (name.equalsIgnoreCase("Imitater")) {
                Plant copyPlant = model.entities.plant.factory.PlantFactory.createPlant("Peashooter");
                if (copyPlant != null) {
                    copyPlant.setX(plant.getX());
                    copyPlant.setY(plant.getY());
                    board.getTile(plant.getY(), plant.getX()).setPlant(copyPlant);
                    activePlants.add(copyPlant);
                    plantsToRemove.add(plant);
                    gameLogMessages.add("Imitater copied plant at (" + plant.getX() + ", " + plant.getY() + ")!");
                }
            }

            if ("SUN_PRODUCER".equalsIgnoreCase(plant.getCategory()) || name.equalsIgnoreCase("Enlighten-mint")) {
                if (name.equalsIgnoreCase("Primal Sunflower")) {
                    if (!plant.isHasSunToCollect()) {
                        plant.setHasSunToCollect(true);
                    }
                } else if (name.equalsIgnoreCase("Gold Bloom")) {
                    addSun((int) plant.getAbilityValue());
                    plantsToRemove.add(plant);
                    gameLogMessages.add("Gold Bloom burst and produced " + (int) plant.getAbilityValue() + " suns!");
                } else if (name.equalsIgnoreCase("Enlighten-mint")) {
                    executeMintLogic(plant, plantsToRemove);
                } else {
                    int intervalTicks = (int) (plant.getActionInterval() * 10);
                    if (intervalTicks > 0 && plant.shouldShoot()) {
                        if (!plant.isHasSunToCollect()) {
                            plant.setHasSunToCollect(true);
                            gameLogMessages.add("Plant " + name + " produced sun at (" + plant.getX() + ", " + plant.getY() + ")");
                        }
                    }
                }
            }

            boolean isAttacker = "SHOOTER".equalsIgnoreCase(plant.getCategory()) ||
                    "STRIKE_THROUGH".equalsIgnoreCase(plant.getCategory()) ||
                    "HOMING".equalsIgnoreCase(plant.getCategory()) ||
                    "LOBBER".equalsIgnoreCase(plant.getCategory());

            if (isAttacker && plant.shouldShoot()) {
                boolean targetInRow = hasZombieInRow(plant.getY()) || board.hasGraveInRow(plant.getY());
                if (name.equalsIgnoreCase("Threepeater")) {
                    int py = plant.getY();
                    targetInRow = hasZombieInRow(py) || board.hasGraveInRow(py)
                            || (py > 0 && (hasZombieInRow(py - 1) || board.hasGraveInRow(py - 1)))
                            || (py < board.getRows() - 1 && (hasZombieInRow(py + 1) || board.hasGraveInRow(py + 1)));
                }

                if (targetInRow || "HOMING".equalsIgnoreCase(plant.getCategory()) || name.equalsIgnoreCase("Starfruit") || name.equalsIgnoreCase("Laser Bean")) {
                    spawnBulletsForPlant(plant);
                }
            }

            if ("MELEE".equalsIgnoreCase(plant.getCategory()) && plant.shouldShoot()) {
                executeMeleeAttack(plant);
            }

            if ("EXPLOSIVE".equalsIgnoreCase(plant.getCategory())) {
                executeExplosiveLogic(plant, plantsToRemove);
            }

            if (("MODIFIER".equalsIgnoreCase(plant.getCategory()) || "HOMING".equalsIgnoreCase(plant.getCategory())) && plant.shouldShoot()) {
                executeUtilityLogic(plant, plantsToRemove);
            }

            if (name.toLowerCase().contains("mint")) {
                executeMintLogic(plant, plantsToRemove);
            }
        }
        for (Plant p : plantsToRemove) {
            removePlant(p);
            Tile t = board.getTile(p.getY(), p.getX());
            if (t != null && t.getPlant() == p) {
                t.setPlant(null);
            }
        }
    }

    private void spawnBulletsForPlant(Plant plant) {
        String name = plant.getName();
        int px = plant.getX();
        int py = plant.getY();
        int dmg = plant.getDamage() > 0 ? plant.getDamage() : 20;

        if (name.equalsIgnoreCase("Threepeater")) {
            // شلیک همزمان به ۳ لاین موازی جلوی خود (بدون شلیک به عقب)
            bullets.add(new Bullet(dmg, py, px + 1, Bullet.BulletType.NORMAL, false, false, 0));
            if (py > 0) bullets.add(new Bullet(dmg, py - 1, px + 1, Bullet.BulletType.NORMAL, false, false, 0));
            if (py < board.getRows() - 1) bullets.add(new Bullet(dmg, py + 1, px + 1, Bullet.BulletType.NORMAL, false, false, 0));
        } else if (name.equalsIgnoreCase("Split Pea")) {
            // ۱ تیر به جلو (دمیج ۲۰) و ۲ تیر به عقب (هرکدام دمیج ۲۰)
            bullets.add(new Bullet(20, py, px + 1, Bullet.BulletType.NORMAL, false, false, 0));
            bullets.add(new Bullet(20, py, Math.max(0, px - 1), Bullet.BulletType.NORMAL, false, false, 0));
            bullets.add(new Bullet(20, py, Math.max(0, px - 1), Bullet.BulletType.NORMAL, false, false, 0));
        } else if (name.equalsIgnoreCase("Snow Pea")) {
            bullets.add(new Bullet(20, py, px + 1, Bullet.BulletType.ICE, false, false, 0));
        } else if (name.equalsIgnoreCase("Cactus")) {
            // شلیک خار با قابلیت نفوذ از حداکثر ۳ زامبی اول
            Bullet cactusBullet = new Bullet(30, py, px + 1, Bullet.BulletType.STRIKE_THROUGH, true, false, 0);
            cactusBullet.setMaxPierceTargets(3);
            bullets.add(cactusBullet);
        } else if (name.equalsIgnoreCase("Goo Peashooter")) {
            // شلیک تیر سمی با نادیده گرفتن زره زامبی
            bullets.add(new Bullet(20, py, px + 1, Bullet.BulletType.POISON, false, false, 0));
        } else if (name.equalsIgnoreCase("Bowling Bulb")) {
            // شلیک ۳ نوع پیاز به ترتیب دمیج ۴۰، ۱۲۰ و ۱۸۰
            int bulbDmg = 40;
            if (plant.getHitCount() % 3 == 1) bulbDmg = 120;
            else if (plant.getHitCount() % 3 == 2) bulbDmg = 180;
            plant.incrementHitCount();
            bullets.add(new Bullet(bulbDmg, py, px + 1, Bullet.BulletType.NORMAL, true, true, 1));
        } else if (name.equalsIgnoreCase("Peashooter") || name.equalsIgnoreCase("Pea Pod")) {
            int heads = plant.getPeaPodHeads();
            for (int i = 0; i < heads; i++) {
                bullets.add(new Bullet(dmg, py, px + 1, Bullet.BulletType.NORMAL, false, false, 0));
            }
        } else if (name.equalsIgnoreCase("Repeater")) {
            bullets.add(new Bullet(dmg, py, px + 1, Bullet.BulletType.NORMAL, false, false, 0));
            bullets.add(new Bullet(dmg, py, px + 1, Bullet.BulletType.NORMAL, false, false, 0));
        } else {
            bullets.add(new Bullet(dmg, py, px + 1, Bullet.BulletType.NORMAL, false, false, 0));
        }
    }

    public String applyPlantFood(Plant plant) {
        if (plant == null) return "";

        plant.heal(plant.getMaxHealth());
        String name = plant.getName();

        if (name.equalsIgnoreCase("Sunflower")) {
            addSun(150);
            gameLogMessages.add("Sunflower used Plant Food and instantly generated 150 suns!");
            return "Plant Food Effect: Produced 150 suns!";
        } else if (name.equalsIgnoreCase("Twin Sunflower")) {
            addSun(250);
            gameLogMessages.add("Twin Sunflower used Plant Food and instantly generated 250 suns!");
            return "Plant Food Effect: Produced 250 suns!";
        } else if (name.equalsIgnoreCase("Sun-shroom")) {
            plant.setPlantStage(3);
            addSun(225);
            gameLogMessages.add("Sun-shroom instantly grew to max stage and produced 225 suns!");
            return "Plant Food Effect: Grew to max size and produced 225 suns!";
        } else if (name.equalsIgnoreCase("Primal Sunflower")) {
            addSun(225);
            gameLogMessages.add("Primal Sunflower used Plant Food and produced 225 suns!");
            return "Plant Food Effect: Produced 225 suns!";
        } else if (name.equalsIgnoreCase("Peashooter")) {
            for (int i = 0; i < 60; i++) {
                bullets.add(new Bullet(20, plant.getY(), plant.getX() + 1, Bullet.BulletType.NORMAL, false, false, 0));
            }
            gameLogMessages.add("Peashooter unleashed a massive pea barrage!");
            return "Plant Food Effect: Unleashed Gatling barrage!";
        } else if (name.equalsIgnoreCase("Repeater")) {
            for (int i = 0; i < 60; i++) {
                bullets.add(new Bullet(20, plant.getY(), plant.getX() + 1, Bullet.BulletType.NORMAL, false, false, 0));
            }
            bullets.add(new Bullet(400, plant.getY(), plant.getX() + 1, Bullet.BulletType.NORMAL, false, false, 0));
            gameLogMessages.add("Repeater fired a giant pea with 20x damage!");
            return "Plant Food Effect: Unleashed heavy barrage & giant pea!";
        } else if (name.equalsIgnoreCase("Threepeater")) {
            for (int r = 0; r < board.getRows(); r++) {
                for (int i = 0; i < 30; i++) {
                    bullets.add(new Bullet(20, r, plant.getX() + 1, Bullet.BulletType.NORMAL, false, false, 0));
                }
            }
            gameLogMessages.add("Threepeater spread pea barrage across all lanes!");
            return "Plant Food Effect: Unleashed fan barrage across all lanes!";
        } else if (name.equalsIgnoreCase("Snow Pea")) {
            for (Zombie z : activeZombies) {
                if (z.getY() == plant.getY()) {
                    z.applyFrozen(5.0);
                }
            }
            for (int i = 0; i < 60; i++) {
                bullets.add(new Bullet(20, plant.getY(), plant.getX() + 1, Bullet.BulletType.ICE, false, false, 0));
            }
            gameLogMessages.add("Snow Pea froze its lane and shot ice barrage!");
            return "Plant Food Effect: Froze lane and shot ice barrage!";
        } else if (name.equalsIgnoreCase("Fire Peashooter")) {
            for (Zombie z : activeZombies) {
                if (z.getY() == plant.getY()) {
                    z.takeDamage(1800, true);
                }
            }
            gameLogMessages.add("Fire Peashooter incinerated the entire lane with fire!");
            return "Plant Food Effect: Incinerated the entire lane!";
        } else if (name.equalsIgnoreCase("Citron")) {
            for (Zombie z : activeZombies) {
                if (z.getY() == plant.getY()) {
                    z.takeDamage(5000, true);
                }
            }
            gameLogMessages.add("Citron fired a massive plasma ball wiping the lane!");
            return "Plant Food Effect: Fired massive plasma ball!";
        } else if (name.equalsIgnoreCase("Cabbage-pult") || name.equalsIgnoreCase("Kernel-pult") || name.equalsIgnoreCase("Melon-pult") || name.equalsIgnoreCase("Winter Melon")) {
            for (Zombie z : activeZombies) {
                int dmg = name.toLowerCase().contains("melon") ? 200 : 100;
                z.takeDamage(dmg, false);
                if (name.equalsIgnoreCase("Kernel-pult")) z.applyFrozen(3.0);
            }
            gameLogMessages.add(name + " launched heavy artillery at all zombies on field!");
            return "Plant Food Effect: Launched heavy artillery!";
        } else if (name.equalsIgnoreCase("Potato Mine") || name.equalsIgnoreCase("Primal Potato Mine")) {
            plant.setArmed(true);
            Random r = new Random();
            for (int k = 0; r.nextInt(2) == 0 && k < 2; k++) {
                int rx = r.nextInt(board.getColumns());
                int ry = r.nextInt(board.getRows());
                Plant clone = model.entities.plant.factory.PlantFactory.createPlant(name);
                if (clone != null && board.getTile(ry, rx).isEmpty()) {
                    clone.setX(rx);
                    clone.setY(ry);
                    clone.setArmed(true);
                    addPlant(clone);
                    board.getTile(ry, rx).setPlant(clone);
                }
            }
            gameLogMessages.add(name + " armed instantly and spawned clone mines!");
            return "Plant Food Effect: Armed instantly and spawned clones!";
        } else if (name.equalsIgnoreCase("Iceberg Lettuce")) {
            for (Zombie z : activeZombies) {
                z.applyFrozen(5.0);
            }
            gameLogMessages.add("Iceberg Lettuce froze all zombies on screen!");
            return "Plant Food Effect: Froze all zombies on screen!";
        } else if (name.equalsIgnoreCase("Wall-nut")) {
            plant.applyPlantFoodArmor(4000);
            gameLogMessages.add("Wall-nut gained 4000 armor!");
            return "Plant Food Effect: Gained 4000 armor!";
        } else if (name.equalsIgnoreCase("Tall-nut")) {
            plant.applyPlantFoodArmor(8000);
            gameLogMessages.add("Tall-nut gained 8000 heavy armor!");
            return "Plant Food Effect: Gained 8000 heavy armor!";
        } else if (name.equalsIgnoreCase("Endurian") || name.equalsIgnoreCase("Explode-o-nut") || name.equalsIgnoreCase("Pumpkin") || name.equalsIgnoreCase("Sun Bean")) {
            plant.applyPlantFoodArmor(4000);
            gameLogMessages.add(name + " gained metal Plant Food armor!");
            return "Plant Food Effect: Gained metal armor!";
        } else if (name.equalsIgnoreCase("Torchwood")) {
            plant.setBlueFlame(true);
            gameLogMessages.add("Torchwood ignited blue flame (3x damage to passing peas)!");
            return "Plant Food Effect: Ignited blue flame!";
        } else if (name.equalsIgnoreCase("Hypno-shroom")) {
            for (Zombie z : activeZombies) {
                if (z.getY() == plant.getY() && Math.abs(z.getX() - plant.getX()) <= 1.0) {
                    z.setHypnotized(true);
                    gameLogMessages.add("Hypno-shroom hypnotized adjacent zombie into Gargantuar ally!");
                    break;
                }
            }
            return "Plant Food Effect: Hypnotized zombie!";
        } else if (name.equalsIgnoreCase("Bonk Choy") || name.equalsIgnoreCase("Wasabi Whip")) {
            for (Zombie z : activeZombies) {
                if (Math.abs(z.getY() - plant.getY()) <= 1 && Math.abs(z.getX() - plant.getX()) <= 1.5) {
                    z.takeDamage(1500, false);
                }
            }
            gameLogMessages.add(name + " unleashed 3x3 rapid flurry attacks!");
            return "Plant Food Effect: Unleashed rapid flurry attacks!";
        } else if (name.equalsIgnoreCase("Chomper")) {
            int swallowed = 0;
            for (Zombie z : new ArrayList<>(activeZombies)) {
                if (swallowed < 3 && z.getY() == plant.getY() && z.getX() >= plant.getX()) {
                    z.takeDamage(99999, true);
                    swallowed++;
                }
            }
            gameLogMessages.add("Chomper instantly swallowed " + swallowed + " zombies!");
            return "Plant Food Effect: Instantly swallowed zombies!";
        }
        return "Plant Food applied!";
    }

    private void executeMeleeAttack(Plant plant) {
        String name = plant.getName();
        int px = plant.getX();
        int py = plant.getY();
        if (name.equalsIgnoreCase("Chomper")) {
            if (!plant.isDigesting()) {
                Zombie target = getFirstZombieInRowAhead(py, px);
                if (target != null && target.getX() - px <= 1.2) {
                    target.takeDamage(99999, true);
                    plant.startDigestion(400);
                    gameLogMessages.add("Chomper swallowed a zombie at (" + px + ", " + py + ")!");
                }
            }
        } else if (name.equalsIgnoreCase("Bonk Choy") || name.equalsIgnoreCase("Wasabi Whip")) {
            for (Zombie z : activeZombies) {
                if (z.getY() == py && Math.abs(z.getX() - px) <= 1.1) {
                    z.takeDamage(plant.getDamage(), false);
                }
            }
        } else if (name.equalsIgnoreCase("Phat Beet") || name.equalsIgnoreCase("Kiwibeast")) {
            int radius = plant.getPlantStage();
            for (Zombie z : activeZombies) {
                if (Math.abs(z.getY() - py) <= radius && Math.abs(z.getX() - px) <= radius) {
                    z.takeDamage(plant.getDamage(), false);
                }
            }
        }
    }

    private void executeExplosiveLogic(Plant plant, List<Plant> toRemove) {
        String name = plant.getName();
        int px = plant.getX();
        int py = plant.getY();

        if (name.equalsIgnoreCase("Doom-shroom")) {
            for (Zombie z : new ArrayList<>(activeZombies)) {
                z.takeDamage(1800, true);
            }
            toRemove.add(plant);
            Tile t = board.getTile(py, px);
            if (t != null) {
                t.setCrater(true);
            }
            gameLogMessages.add("Doom-shroom exploded and created an unplantable crater at (" + px + ", " + py + ")!");
        } else if (name.equalsIgnoreCase("Ice-shroom")) {
            for (Zombie z : activeZombies) {
                z.applyFrozen(5.0);
            }
            toRemove.add(plant);
            gameLogMessages.add("Ice-shroom froze all zombies on map!");
        } else if (name.equalsIgnoreCase("Hot Potato")) {
            Tile tile = board.getTile(py, px);
            if (tile != null && tile.getPlant() != null && tile.getPlant().isFrozen()) {
                tile.getPlant().melt();
                gameLogMessages.add("Hot Potato instantly melted ice on plant at (" + px + ", " + py + ")!");
            }
            for (Zombie z : new ArrayList<>(activeZombies)) {
                if (z.getY() == py && (int) Math.round(z.getX()) == px) {
                    z.removeEffect(ZombieEffect.FROZEN);
                    z.removeEffect(ZombieEffect.CHILLED);
                    z.setFrozenIceHealth(0);
                    gameLogMessages.add("Hot Potato instantly melted ice on zombie " + z.getName() + " at (" + px + ", " + py + ")!");
                }
            }
            toRemove.add(plant);
        } else if (name.equalsIgnoreCase("Grave Buster")) {
            Tile tile = board.getTile(py, px);
            if (tile != null && tile.getType() == TileType.GRAVE) {
                if (tile.getSunReward() > 0) addSun(tile.getSunReward());
                if (tile.hasPlantFoodReward()) addPlantFood();
                board.removeGrave(py, px);
                toRemove.add(plant);
                gameLogMessages.add("Grave Buster completely removed the grave at (" + px + ", " + py + ")!");
            }
        } else if (name.equalsIgnoreCase("Cherry Bomb") || name.equalsIgnoreCase("Grapeshot")) {
            for (Zombie z : new ArrayList<>(activeZombies)) {
                if (Math.abs(z.getY() - py) <= 1 && Math.abs(z.getX() - px) <= 1.5) {
                    z.takeDamage(1800, true);
                }
            }
            toRemove.add(plant);
            gameLogMessages.add(name + " exploded in a 3x3 area!");
        } else if (name.equalsIgnoreCase("Jalapeno")) {
            for (Zombie z : new ArrayList<>(activeZombies)) {
                if (z.getY() == py) {
                    z.takeDamage(1800, true);
                }
            }
            toRemove.add(plant);
            gameLogMessages.add("Jalapeno incinerated row " + py + "!");
        } else if (name.equalsIgnoreCase("Tangle Kelp")) {
            Tile t = board.getTile(py, px);
            if (t != null && t.getType() == TileType.WATER) {
                Zombie target = getFirstZombieInRowAhead(py, px - 0.5);
                if (target != null && Math.abs(target.getX() - px) <= 0.8) {
                    target.takeDamage(99999, true);
                    toRemove.add(plant);
                    gameLogMessages.add("Tangle Kelp pulled zombie underwater at (" + px + ", " + py + ")!");
                }
            }
        } else if (name.equalsIgnoreCase("Potato Mine") || name.equalsIgnoreCase("Primal Potato Mine")) {
            if (plant.isArmed()) {
                for (Zombie z : new ArrayList<>(activeZombies)) {

                    if (!z.isHypnotized() && z.getY() == py && Math.abs((int) Math.round(z.getX()) - px) <= 1) {
                        int radius = name.contains("Primal") ? 1 : 0;
                        for (Zombie az : new ArrayList<>(activeZombies)) {
                            if (Math.abs(az.getY() - py) <= radius && Math.abs((int) Math.round(az.getX()) - px) <= (radius + 1)) {
                                az.takeDamage(1800, true);
                            }
                        }
                        toRemove.add(plant);
                        gameLogMessages.add(name + " detonated!");
                        break;
                    }
                }
            }
        } else if (name.equalsIgnoreCase("Iceberg Lettuce")) {
            for (Zombie z : new ArrayList<>(activeZombies)) {
                if (!z.isHypnotized() && z.getY() == py && Math.abs((int) Math.round(z.getX()) - px) <= 1) {
                    z.applyFrozen(5.0);
                    toRemove.add(plant);
                    gameLogMessages.add("Iceberg Lettuce froze zombie " + z.getName() + "!");
                    break;
                }
            }
        }
    }

    private void executeUtilityLogic(Plant plant, List<Plant> toRemove) {
        String name = plant.getName();
        int px = plant.getX();
        int py = plant.getY();
        if (name.equalsIgnoreCase("Magnet-shroom") && plant.getMagnetCooldownTicks() <= 0) {
            for (Zombie z : activeZombies) {
                if (z.getArmorHealth() > 0 && ("BUCKET".equalsIgnoreCase(z.getArmorType()) || "CONE".equalsIgnoreCase(z.getArmorType()) || "KNIGHT".equalsIgnoreCase(z.getArmorType()))) {
                    z.setArmorHealth(0);
                    z.setArmorType("none");
                    plant.startMagnetCooldown(150);
                    gameLogMessages.add("Magnet-shroom removed armor from zombie at lane " + z.getY() + "!");
                    break;
                }
            }
        } else if (name.equalsIgnoreCase("Caulipower") || name.equalsIgnoreCase("Electric Blueberry")) {
            if (!activeZombies.isEmpty()) {
                Zombie target = activeZombies.get(new Random().nextInt(activeZombies.size()));
                if (target != null) {
                    if (name.equalsIgnoreCase("Caulipower")) {
                        target.setHypnotized(true);
                        gameLogMessages.add("Caulipower hypnotized zombie " + target.getName() + "!");
                    } else {
                        target.takeDamage(5000, true);
                        gameLogMessages.add("Electric Blueberry zapped zombie " + target.getName() + "!");
                    }
                }
            }
        }
    }

    private void executeMintLogic(Plant plant, List<Plant> toRemove) {
        String name = plant.getName().trim();
        String familyCategory = "";

        if (name.equalsIgnoreCase("Appease-mint")) familyCategory = "SHOOTER";
        else if (name.equalsIgnoreCase("Arma-mint")) familyCategory = "LOBBER";
        else if (name.equalsIgnoreCase("Bombard-mint")) familyCategory = "EXPLOSIVE";
        else if (name.equalsIgnoreCase("Enforce-mint")) familyCategory = "MELEE";
        else if (name.equalsIgnoreCase("Reinforce-mint")) familyCategory = "WALL_NUT";
        else if (name.equalsIgnoreCase("Enchant-mint")) familyCategory = "MODIFIER";
        else if (name.equalsIgnoreCase("Pierce-mint")) familyCategory = "STRIKE_THROUGH";
        else if (name.equalsIgnoreCase("catTail-mint")) familyCategory = "HOMING";
        else if (name.equalsIgnoreCase("Enlighten-mint")) familyCategory = "SUN_PRODUCER";

        if (!familyCategory.isEmpty()) {
            triggerMintBoost(familyCategory);
            toRemove.add(plant);
            gameLogMessages.add(name + " activated Plant Food boost for all " + familyCategory + " family plants!");
        }
    }

    private void triggerMintBoost(String category) {
        String normalizedTarget = category.replace("-", "").replace("_", "").toLowerCase();
        for (Plant p : new ArrayList<>(activePlants)) {
            String pCat = p.getCategory();
            if (pCat != null) {
                String normalizedCat = pCat.replace("-", "").replace("_", "").toLowerCase();
                if (normalizedCat.equals(normalizedTarget)) {
                    if (normalizedTarget.equals("wallnut")) {
                        p.applyPlantFoodArmor(4000);
                        gameLogMessages.add("Reinforce-mint boosted " + p.getName() + " with 4000 armor!");
                    } else {
                        p.heal(p.getMaxHealth());
                        applyPlantFood(p);
                        gameLogMessages.add("Boosted family plant: " + p.getName());
                    }
                }
            }
        }
    }

    private void processZombieDeathDrops(Zombie zombie) {
        Random r = new Random();
        if (zombie.isGlowing()) {
            if (getPlantFoodCount() < 3) {
                addPlantFood();
                gameLogMessages.add("The glowing zombie dropped a plant food; you have " + getPlantFoodCount() + " plant foods now.");
            }
        }
        if (r.nextInt(100) < 10) {
            int dropType = r.nextInt(3);
            if (dropType == 0) {
                addCoins(50);
                gameLogMessages.add("A zombie dropped a coin; you have " + getCoins() + " coins now.");
            } else if (dropType == 1) {
                addDiamonds(1);
                gameLogMessages.add("A zombie dropped a diamond; you have " + getDiamonds() + " diamonds now.");
            } else {
                if (getGreenhouse() != null) {
                    getGreenhouse().addPot(new model.greenhouse.Pot(0, 0));
                }
                int potCount = getGreenhouse() != null ? getGreenhouse().getUnlockedPotCount() : 1;
                gameLogMessages.add("A zombie dropped a pot; you have " + potCount + " pots now.");
            }
        }
    }

    private void processSpecialZombieAbilities(Zombie zombie, List<Zombie> zombiesToAdd) {
        String name = zombie.getName();

        if (name.equalsIgnoreCase("ZombieGargantuar") && !zombie.isHasThrownImp()) {
            if (zombie.getHealth() <= zombie.getMaxHealth() / 2) {
                zombie.setHasThrownImp(true);
                Zombie imp = model.entities.zombie.factory.ZombieFactory.createZombie("ZombieImp", difficultyLevel);
                if (imp != null) {
                    imp.setY(zombie.getY());
                    imp.setX(2.0);
                    zombiesToAdd.add(imp);
                    gameLogMessages.add("Gargantuar threw an Imp to column 2!");
                }
            }
        }

        if (name.equalsIgnoreCase("ZombieRa")) {
            zombie.incrementRaStealTimer();
            if (zombie.getRaStealTimer() >= 20) {
                zombie.resetRaStealTimer();
                if (!suns.isEmpty()) {
                    Sun targetSun = suns.remove(0);
                    zombie.setStolenSuns(zombie.getStolenSuns() + targetSun.getValue());
                    gameLogMessages.add("Ra Zombie absorbed a sun from position (" + targetSun.getColumn() + ", " + targetSun.getRow() + ")!");
                }
            }
        }

        if (name.equalsIgnoreCase("ZombieCrystalSkull")) {
            zombie.incrementTurquoiseLaserTimer();
            if (zombie.getTurquoiseLaserTimer() >= 30) {
                zombie.resetTurquoiseLaserTimer();
                if (!suns.isEmpty()) {
                    Sun targetSun = suns.remove(0);
                    int sunValue = targetSun.getValue();
                    zombie.setStolenSuns(zombie.getStolenSuns() + sunValue);
                    gameLogMessages.add("Turquoise Zombie stole " + sunValue + " suns from position (" + targetSun.getColumn() + ", " + targetSun.getRow() + ")!");
                }
            }
        }

        if (name.equalsIgnoreCase("ZombieTombRaiser")) {
            zombie.incrementTombraiserTimer();
            if (zombie.getTombraiserTimer() >= 100) {
                zombie.resetTombraiserTimer();
                Random r = new Random();
                int rx = r.nextInt(board.getColumns());
                int ry = r.nextInt(board.getRows());
                Tile tile = board.getTile(ry, rx);
                if (tile != null && tile.isEmpty() && tile.getType() == TileType.GRASS) {
                    board.setupGrave(ry, rx, 700, 0, false);
                    gameLogMessages.add("Tombraiser Zombie created a grave at (" + rx + ", " + ry + ")");
                }
            }
        }

        if (name.equalsIgnoreCase("ZombieIceAgeHunter")) {
            if (tickCount % 30 == 0) {
                Plant p = getFirstPlantInRow(zombie.getY());
                if (p != null) {
                    p.setFreezeLevel(p.getFreezeLevel() + 1);
                    gameLogMessages.add("Hunter Zombie threw a snowball at plant " + p.getName() + "!");
                }
            }
        }

        if (name.equalsIgnoreCase("ZombieBeachFisherman")) {
            zombie.incrementFishermanTimer();
            if (zombie.getFishermanTimer() >= 25) {
                zombie.resetFishermanTimer();
                Plant target = getFirstPlantInRow(zombie.getY());
                if (target != null) {
                    if (target.getX() + 1 == (int) Math.round(zombie.getX())) {
                        activePlants.remove(target);
                        board.getTile(target.getY(), target.getX()).setPlant(null);
                        gameLogMessages.add("Fisherman Zombie hooked and destroyed plant " + target.getName() + "!");
                    } else if (target.getX() + 1 < board.getColumns()) {
                        board.getTile(target.getY(), target.getX()).setPlant(null);
                        target.setX(target.getX() + 1);
                        board.getTile(target.getY(), target.getX()).setPlant(target);
                        gameLogMessages.add("Fisherman Zombie pulled plant " + target.getName() + " to column " + target.getX());
                    }
                }
            }
        }

        if (name.equalsIgnoreCase("ZombieBeachOctopus")) {
            zombie.incrementOctopusTimer();
            if (zombie.getOctopusTimer() >= 40) {
                zombie.resetOctopusTimer();
                Plant p = getFirstPlantInRow(zombie.getY());
                if (p != null && !p.isFrozen()) {
                    p.setFreezeLevel(3);
                    gameLogMessages.add("Octopus Zombie threw an octopus on plant " + p.getName() + "!");
                }
            }
        }

        if (name.equalsIgnoreCase("ZombieDarkKing")) {
            zombie.incrementKingTimer();
            if (zombie.getKingTimer() >= 25) {
                zombie.resetKingTimer();
                for (Zombie neighbor : activeZombies) {
                    if (Math.abs(neighbor.getY() - zombie.getY()) <= 1 && Math.abs((int) neighbor.getX() - (int) zombie.getX()) <= 2) {
                        if (neighbor.getName().equalsIgnoreCase("ZombieDefault") || neighbor.getName().equalsIgnoreCase("NormalZombie")) {
                            neighbor.setArmorHealth(1600);
                            neighbor.setArmorType("KNIGHT");
                            gameLogMessages.add("King Zombie knighted a zombie at lane " + neighbor.getY() + "!");
                            break;
                        }
                    }
                }
            }
        }

        if (name.equalsIgnoreCase("ZombieWizard")) {
            zombie.incrementWizardTimer();
            if (zombie.getWizardTimer() >= 60) {
                zombie.resetWizardTimer();
                List<Plant> candidates = new ArrayList<>();
                for (Plant p : activePlants) {
                    if (p.getY() == zombie.getY() && p.getX() < zombie.getX()) {
                        candidates.add(p);
                    }
                }
                candidates.sort((a, b) -> Double.compare(b.getX(), a.getX()));

                Plant target = null;
                if (!candidates.isEmpty()) {
                    target = candidates.get(0);
                } else if (!activePlants.isEmpty()) {
                    target = activePlants.get(new Random().nextInt(activePlants.size()));
                }

                if (target != null && !target.isTransformedToSheep()) {
                    target.setTransformedToSheep(true);
                    gameLogMessages.add("Wizard Zombie transformed " + target.getName() + " at (" + target.getX() + ", " + target.getY() + ") into a sheep!");
                }
            }
        }

        if (name.equalsIgnoreCase("ZombieProspector") && zombie.getDynamiteTimer() > 0) {
            if (zombie.getDynamiteTimer() <= 1.0) {
                zombie.setDynamiteTimer(0.0);
                zombie.setX(1.0);
                zombie.setAngry(true);
                gameLogMessages.add("Prospector Zombie landed at column 1 after dynamite explosion!");
            }
        }

        if (name.equalsIgnoreCase("ZombiePiano")) {
            zombie.incrementPianoPlayTimer();
            if (zombie.getPianoPlayTimer() >= 30) {
                zombie.resetPianoPlayTimer();
                Random r = new Random();
                for (Zombie z : activeZombies) {
                    if (!z.isBoss() && r.nextBoolean()) {
                        int newY = z.getY() + (r.nextBoolean() ? 1 : -1);
                        if (newY >= 0 && newY < board.getRows()) {
                            z.setY(newY);
                        }
                    }
                }
                gameLogMessages.add("Piano Zombie played music! Zombies swapped lanes!");
            }
        }
    }

    private Plant getFirstPlantInRow(int row) {
        Plant closest = null;
        for (Plant p : activePlants) {
            if (p.getY() == row) {
                if (closest == null || p.getX() > closest.getX()) {
                    closest = p;
                }
            }
        }
        return closest;
    }

    public List<String> getRawLogMessagesDirectly() {
        List<String> currentMessages = new ArrayList<>(gameLogMessages);
        gameLogMessages.clear();
        return currentMessages;
    }

    private void handleSunDrop() {
        if (currentSeason != null && !currentSeason.allowsNaturalSunDrop()) {
            return;
        }
        SpecialLevelType specialType = level.getSpecialLevelType();
        if (specialType == SpecialLevelType.NIGHT_OPS || specialType == SpecialLevelType.PLANT_WHAT_YOU_GET || activeMiniGame != null) {
            return;
        }
        double t = tickCount / 10.0;
        double formulaInterval = Math.max(6 + 0.05 * t, 12);
        double scaleIncrease = difficultyLevel / 3.0;
        int sunDropInterval = (int) (formulaInterval * 10 * scaleIncrease);

        if (tickCount - lastSunDropTick >= sunDropInterval) {
            lastSunDropTick = tickCount;
            Random r = new Random();
            int x = r.nextInt(board.getColumns());
            int y = r.nextInt(board.getRows());
            int chance = r.nextInt(100);
            String sunType = "Normal";
            if (chance < 5) {
                sunType = "Radioactive";
                suns.add(new Sun(50, y, x));
            } else if (chance < 20) {
                sunType = "Special";
                suns.add(new Sun(100, y, x));
            } else {
                suns.add(new Sun(25, y, x));
            }
            gameLogMessages.add("New " + sunType + " sun is dropping at position (" + x + ", " + y + ")");
            gameLogMessages.add("Sun reached the ground at position (" + x + ", " + y + ")");
        }
    }

    public boolean hasZombieInRow(int row) {
        for (Zombie z : activeZombies) {
            if (!z.isHypnotized() && z.getY() == row) return true;
        }
        return false;
    }

    public Zombie getFirstZombieInRowAhead(int row, double x) {
        Zombie closest = null;
        for (Zombie z : activeZombies) {
            if (!z.isHypnotized() && z.getY() == row && z.getX() >= x) {
                if (closest == null || z.getX() < closest.getX()) {
                    closest = z;
                }
            }
        }
        return closest;
    }

    public Plant getPlantAt(int x, int y) {
        for (Plant p : activePlants) {
            if (p.getX() == x && p.getY() == y) return p;
        }
        return null;
    }

    public boolean isWon() { return won; }
    public boolean isLost() { return lost; }
    public LawnMower[] getLawnMowers() { return lawnMowers; }
    public boolean spendSun(int amount) {
        if (sunCount < amount) return false;
        sunCount -= amount;
        return true;
    }
    public void addSun(int amount) {
        sunCount += amount;
        sunsProducedInLevel += amount;
        scoreGame.onSunCollected(amount);
    }
    public boolean spendCoins(int amount) {
        if (coins < amount) return false;
        coins -= amount;
        return true;
    }
    public void addCoins(int amount) {
        coins += amount;
        scoreGame.onCoinEarned(amount);
    }
    public boolean spendDiamonds(int amount) {
        if (diamonds < amount) return false;
        diamonds -= amount;
        return true;
    }
    public void addDiamonds(int amount) {
        diamonds += amount;
        scoreGame.onDiamondEarned(amount);
    }
    public void addPlantFood() { plantFoodCount++; }
    public boolean usePlantFood() {
        if (plantFoodCount <= 0) return false;
        plantFoodCount--;
        return true;
    }
    public void addBullet(Bullet bullet) { bullets.add(bullet); }
    public void addSun(Sun sun) { suns.add(sun); }
    public void addZombie(Zombie zombie) { activeZombies.add(zombie); }
    public void addPlant(Plant plant) {
        activePlants.add(plant);
        scoreGame.onPlantPlaced(plant);
    }
    public void removePlant(Plant plant) { activePlants.remove(plant); }
    public void removeZombie(Zombie zombie) {
        activeZombies.remove(zombie);
        scoreGame.onZombieKilled(zombie, this);
    }
    public boolean isRunning() { return running; }
    public Board getBoard() { return board; }
    public Level getLevel() { return level; }
    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    public int getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(int difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    public int getSunCount() { return sunCount; }
    public int getCoins() { return coins; }
    public int getDiamonds() { return diamonds; }
    public int getPlantFoodCount() { return plantFoodCount; }
    public Spawner getSpawner() { return spawner; }
    public void setSpawner(Spawner spawner) { this.spawner = spawner; }
    public model.score.ScoreGame getScoreGame() { return scoreGame; }
    public Greenhouse getGreenhouse() { return greenhouse; }
    public void setGreenhouse(Greenhouse greenhouse) { this.greenhouse = greenhouse; }
    public List<Bullet> getBullets() { return bullets; }
    public List<Sun> getSuns() { return suns; }
    public List<Zombie> getActiveZombies() { return activeZombies; }
    public List<Plant> getActivePlants() { return activePlants; }
    public int getTickCount() { return tickCount; }
}