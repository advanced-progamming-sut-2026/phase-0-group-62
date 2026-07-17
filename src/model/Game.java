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

    public Game() {
        this.board = new Board(5, 9);
        this.level = new Level(1);
        this.difficulty = Difficulty.NORMAL;
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

    public Game(int rows, int columns, int levelNumber, Difficulty difficulty) {
        this();
        this.board = new Board(rows, columns);
        this.level = new Level(levelNumber);
        this.difficulty = difficulty;
        this.spawner = new Spawner(board, levelNumber * 2, difficulty);
        this.lawnMowers = new LawnMower[rows];
        for (int i = 0; i < rows; i++) {
            lawnMowers[i] = new LawnMower(i);
        }
    }

    public void start() {
        running = true;
        if (spawner != null) {
            spawner.startWave(1);
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
        System.out.println("Zombie waves started! Survive!");
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

    public void tick() {
        if (!running || won || lost) return;
        tickCount++;

        if (activeMiniGame instanceof Beghoul) {
            Beghoul bg = (Beghoul) activeMiniGame;
            if (bg.isVictoryConditionMet()) {
                won = true;
                running = false;
                for (Zombie z : new ArrayList<>(activeZombies)) {
                    board.getTile(z.getY(), (int) z.getX()).setZombie(null);
                }
                activeZombies.clear();
                System.out.println("Beghoul Objective Complete! All remaining zombies eliminated. Victory!");
                return;
            }
            if (!bg.hasAnyPossibleMoves(this)) {
                System.out.println("Beghoul: Deadlock detected! Resetting the entire garden grid.");
                bg.fillGridRandomly(this);
                while (bg.checkAndProcessMatches(this, false)) {}
            }
            if (tickCount % 80 == 0) {
                Zombie z = model.entities.zombie.factory.ZombieFactory.createZombie("NormalZombie");
                if (z != null) {
                    int r = new Random().nextInt(5);
                    z.setX(8.0);
                    z.setY(r);
                    addZombie(z);
                    board.getTile(r, 8).setZombie(z);
                }
            }
        }

        if (activeMiniGame instanceof IZombie) {
            IZombie iz = (IZombie) activeMiniGame;
            for (Zombie z : new ArrayList<>(activeZombies)) {
                if (z.getName().equalsIgnoreCase("SunProducerZombie")) {
                    z.incrementIzombieSunTicks();
                    int baseProductionInterval = Math.max(100 - (z.getIzombieSunProductionTicks() / 10), 20);
                    if (tickCount % baseProductionInterval == 0) {
                        iz.addSun(25);
                        System.out.println("IZombie: SunProducerZombie in lane " + z.getY() + " generated 25 suns. Rate increased!");
                    }
                }
            }
            if (iz.getZombieSunCount() < 50 && activeZombies.isEmpty()) {
                lost = true;
                running = false;
                System.out.println("IZombie: Out of suns and no active zombies left! Game Over!");
                return;
            }
        }

        if (activeMiniGame instanceof Vasebreaker) {
            for (int r = 0; r < board.getRows(); r++) {
                for (int c = 0; c < board.getColumns(); c++) {
                    Tile tile = board.getTile(r, c);
                    if (tile.getTemporarySeedPacket() != null) {
                        tile.setSeedPacketTimer(tile.getSeedPacketTimer() - 1);
                        if (tile.getSeedPacketTimer() <= 0) {
                            System.out.println("Vasebreaker: Temporary Seed Packet for " + tile.getTemporarySeedPacket() + " at (" + c + ", " + r + ") disappeared!");
                            tile.setTemporarySeedPacket(null);
                        }
                    }
                }
            }
            Vasebreaker vb = (Vasebreaker) activeMiniGame;
            if (vb.isVictoryConditionMet() && activeZombies.isEmpty()) {
                won = true;
                running = false;
                System.out.println("Vasebreaker: All vases broken and zombies eliminated! Victory!");
                return;
            }
        }

        if (activeMiniGame instanceof WallnutBowling) {
            if (tickCount == 1 || tickCount % 120 == 0) {
                Random rand = new Random();
                String roll = rand.nextInt(100) < 70 ? "Bowling Wallnut" : (rand.nextInt(100) < 50 ? "Explode O' Nut" : "Giant Wallnut");
                conveyorBeltPlants.add(roll);
                System.out.println("Wallnut Bowling Conveyor: Pushed " + roll + " onto the belt.");
            }

            for (Plant ball : new ArrayList<>(activePlants)) {
                if (ball.isBowlingBall()) {
                    if (tickCount % 5 == 0) {
                        board.getTile(ball.getY(), ball.getX()).setPlant(null);
                        ball.setX(ball.getX() + ball.getDx());
                        ball.setY(ball.getY() + ball.getDy());

                        if (ball.getY() < 0) {
                            ball.setY(0);
                            ball.setDy(-ball.getDy());
                        } else if (ball.getY() >= board.getRows()) {
                            ball.setY(board.getRows() - 1);
                            ball.setDy(-ball.getDy());
                        }

                        if (ball.getX() >= board.getColumns()) {
                            activePlants.remove(ball);
                            System.out.println(ball.getName() + " rolled off the board.");
                            continue;
                        }

                        board.getTile(ball.getY(), ball.getX()).setPlant(ball);

                        Zombie target = null;
                        for (Zombie z : activeZombies) {
                            if (z.getY() == ball.getY() && (int) z.getX() == ball.getX()) {
                                target = z;
                                break;
                            }
                        }

                        if (target != null) {
                            if (ball.getName().equalsIgnoreCase("Giant Wallnut")) {
                                target.takeDamage(target.getMaxHealth(), false);
                                System.out.println("Giant Wallnut crushed " + target.getName());
                            } else if (ball.getName().equalsIgnoreCase("Explode O' Nut")) {
                                System.out.println("Explode O' Nut triggered a 3x3 blast!");
                                List<Zombie> blastTargets = new ArrayList<>();
                                for (Zombie az : activeZombies) {
                                    if (Math.abs(az.getY() - ball.getY()) <= 1 && Math.abs((int) az.getX() - ball.getX()) <= 1) {
                                        blastTargets.add(az);
                                    }
                                }
                                for (Zombie bt : blastTargets) {
                                    bt.takeDamage(500, false);
                                    if (!bt.isAlive()) {
                                        activeZombies.remove(bt);
                                        board.getTile(bt.getY(), (int) bt.getX()).setZombie(null);
                                    }
                                }
                                activePlants.remove(ball);
                                board.getTile(ball.getY(), ball.getX()).setPlant(null);
                            } else {
                                target.takeDamage(200, false);
                                ball.incrementHitCount();
                                if (ball.getHitCount() == 1) {
                                    ball.setDy(new Random().nextBoolean() ? 1 : -1);
                                } else {
                                    ball.setDy(-ball.getDy());
                                }
                                System.out.println("Bowling Wallnut hit " + target.getName() + " and richocheted.");
                            }

                            if (target != null && !target.isAlive()) {
                                activeZombies.remove(target);
                                board.getTile(target.getY(), (int) target.getX()).setZombie(null);
                                zombiesKilledInLevel++;
                            }
                        }
                    }
                }
            }
        }

        SpecialLevelType specialType = level.getSpecialLevelType();
        if (activeMiniGame == null && specialType == SpecialLevelType.CONVEYOR_BELT) {
            if (tickCount == 1 || tickCount % 120 == 0) {
                String randomPlant = getRandomUnlockedPlant();
                conveyorBeltPlants.add(randomPlant);
                System.out.println("Conveyor Belt: Pushed " + randomPlant + " onto the belt. Current belt: " + conveyorBeltPlants);
            }
        }

        if (specialType == SpecialLevelType.SAVE_OUR_SEEDS) {
            for (Plant p : seedsToProtect) {
                if (!p.isAlive() || !activePlants.contains(p)) {
                    lost = true;
                    running = false;
                    System.out.println("A protected seed plant was eaten! Game Over!");
                    return;
                }
            }
        }

        if (specialType == SpecialLevelType.TIMED_WAR) {
            if (zombiesKilledInLevel >= level.getTargetZombiesToKill() || sunCount >= level.getTargetSunsToProduce()) {
                won = true;
                running = false;
                System.out.println("Timed War Objective Completed! You won!");
                return;
            }
            if (tickCount >= level.getTimeLimitTicks()) {
                lost = true;
                running = false;
                System.out.println("Time ran out! You lost the Timed War!");
                return;
            }
        }

        if (specialType == SpecialLevelType.DEAD_LINE || activeMiniGame instanceof WallnutBowling) {
            int lineCol = (activeMiniGame instanceof WallnutBowling) ? ((WallnutBowling) activeMiniGame).getDeadlineColumn() : level.getDeadlineColumn();
            for (Zombie z : activeZombies) {
                if (z.getX() <= lineCol) {
                    lost = true;
                    running = false;
                    System.out.println("A zombie crossed the deadline column " + lineCol + "! Game Over!");
                    return;
                }
            }
        }

        if (specialType == SpecialLevelType.LOVE_YOUR_PLANTS) {
            if (plantsLostCount > level.getMaxPlantsLostAllowed()) {
                lost = true;
                running = false;
                System.out.println("You lost too many plants (" + plantsLostCount + " lost, max " + level.getMaxPlantsLostAllowed() + ")! Game Over!");
                return;
            }
        }

        if (spawner != null) {
            if ((specialType == SpecialLevelType.PLANT_WHAT_YOU_GET && !zombieWavesStarted) || activeMiniGame instanceof Vasebreaker || activeMiniGame instanceof IZombie || activeMiniGame instanceof Beghoul) {
            } else {
                int oldSpawnedCount = spawner.getZombiesSpawnedInWave();
                if ((tickCount == 1 || (spawner.ticksSinceLastSpawn == 0 && oldSpawnedCount == 0)) && lastPrintedWave < spawner.getCurrentWave()) {
                    lastPrintedWave = spawner.getCurrentWave();
                    if (spawner.isFinalWave()) {
                        System.out.println("The final wave has come.");
                    } else {
                        System.out.println("Wave " + spawner.getCurrentWave() + " started.");
                    }
                }

                spawner.update();
                int newSpawnedCount = spawner.getZombiesSpawnedInWave();
                if (newSpawnedCount > oldSpawnedCount) {
                    if (currentSeason != null) {
                        currentSeason.handleWaveStart(this);
                    }

                    for (int r = 0; r < board.getRows(); r++) {
                        for (int c = 0; c < board.getColumns(); c++) {
                            Zombie z = board.getTile(r, c).getZombie();
                            if (z != null) {
                                if (currentSeason != null && "FrostbiteCaves".equalsIgnoreCase(currentSeason.getName())) {
                                    z.setImmuneToFreeze(true);
                                }
                                if (!activeZombies.contains(z)) {
                                    activeZombies.add(z);
                                    int cost = model.entities.zombie.factory.ZombieFactory.getWaveCost(z.getName());
                                    System.out.println("Zombie " + z.getName() + " spawned at wave " + spawner.getCurrentWave() + " in lane " + r + " which costed " + cost + ".");
                                }
                            }
                        }
                    }
                }
            }
        }

        handleSunDrop();

        if (currentSeason != null) {
            currentSeason.handleTick(this);
        }

        for (Plant plant : new ArrayList<>(activePlants)) {
            if (plant.isFrozen() || plant.isBowlingBall()) continue;
            plant.update();
            if (plant.getCategory() != null && plant.getCategory().equalsIgnoreCase("SUN_PRODUCER")) {
                int plantInterval = (int) (plant.getActionInterval() * 10);
                if (plantInterval > 0 && tickCount % plantInterval == 0) {
                    plant.setHasSunToCollect(true);
                    System.out.println("plant " + plant.getName() + " produced a sun at (" + plant.getX() + ", " + plant.getY() + ")");
                }
            }
        }

        for (Zombie zombie : new ArrayList<>(activeZombies)) {
            zombie.updateEffects();
            zombie.updateCooldown();

            if (activeMiniGame instanceof Zombotany) {
                if (zombie.getName().equalsIgnoreCase("PeashooterZombie") && tickCount % 15 == 0) {
                    for (int col = (int) zombie.getX(); col >= 0; col--) {
                        Plant p = getPlantAt(col, zombie.getY());
                        if (p != null && p.isAlive()) {
                            p.takeDamage(20);
                            System.out.println("Zombotany: PeashooterZombie shot a pea and hit " + p.getName() + " at (" + col + ", " + zombie.getY() + ")");
                            if (!p.isAlive()) {
                                activePlants.remove(p);
                                board.getTile(p.getY(), p.getX()).setPlant(null);
                            }
                            break;
                        }
                    }
                }
                if (zombie.getName().equalsIgnoreCase("JalapenoZombie")) {
                    zombie.incrementJalapenoTimer();
                    if (zombie.getZombotanyJalapenoTimer() >= 100) {
                        System.out.println("Zombotany: JalapenoZombie exploded and incinerated lane " + zombie.getY() + "!");
                        List<Plant> toBurn = new ArrayList<>();
                        for (Plant p : activePlants) {
                            if (p.getY() == zombie.getY()) toBurn.add(p);
                        }
                        activePlants.removeAll(toBurn);
                        for (Plant bp : toBurn) {
                            board.getTile(bp.getY(), bp.getX()).setPlant(null);
                        }
                        activeZombies.remove(zombie);
                        board.getTile(zombie.getY(), (int) zombie.getX()).setZombie(null);
                        continue;
                    }
                }
            }

            if (!zombie.hasEffect(ZombieEffect.FROZEN)) {
                Plant targetPlant = getPlantAt((int) zombie.getX(), zombie.getY());
                if (activeMiniGame instanceof Zombotany && zombie.getName().equalsIgnoreCase("SquashZombie") && targetPlant != null) {
                    System.out.println("Zombotany: SquashZombie squashed " + targetPlant.getName() + " انتحاری!");
                    activePlants.remove(targetPlant);
                    board.getTile(targetPlant.getY(), targetPlant.getX()).setPlant(null);
                    activeZombies.remove(zombie);
                    board.getTile(zombie.getY(), (int) zombie.getX()).setZombie(null);
                    continue;
                }

                if (targetPlant != null && !targetPlant.isBowlingBall()) {
                    if (tickCount % 10 == 0) {
                        targetPlant.takeDamage(zombie.getDamage());
                        if (!targetPlant.isAlive()) {
                            activePlants.remove(targetPlant);
                            board.getTile(targetPlant.getY(), targetPlant.getX()).setPlant(null);
                            System.out.println("Plant " + targetPlant.getName() + " at (" + targetPlant.getX() + ", " + targetPlant.getY() + ") is destroyed.");
                            plantsLostCount++;
                            if (activeMiniGame instanceof Beghoul) {
                                ((Beghoul) activeMiniGame).createCrater(targetPlant.getY(), targetPlant.getX());
                            }
                        }
                    }
                } else {
                    Tile currentTile = board.getTile(zombie.getY(), (int) zombie.getX());
                    if (currentSeason != null && "FrostbiteCaves".equalsIgnoreCase(currentSeason.getName()) && currentTile != null && currentTile.isSlideway() && !zombie.isDodoRider()) {
                        int targetRow = zombie.getY() + currentTile.getSlideRowOffset();
                        if (targetRow >= 0 && targetRow < board.getRows()) {
                            board.getTile(zombie.getY(), (int) zombie.getX()).setZombie(null);
                            zombie.setY(targetRow);
                            zombie.move();
                            board.getTile(zombie.getY(), (int) zombie.getX()).setZombie(zombie);
                        } else {
                            zombie.move();
                        }
                    } else {
                        zombie.move();
                    }
                }
            }

            if (zombie.getX() <= 0) {
                int row = zombie.getY();
                if (activeMiniGame instanceof IZombie) {
                    IZombie iz = (IZombie) activeMiniGame;
                    iz.eatBrain(row);
                    System.out.println("IZombie: Zombie reached the end and ate the brain in lane " + row + "!");
                    activeZombies.remove(zombie);
                    board.getTile(zombie.getY(), (int) zombie.getX()).setZombie(null);
                    if (iz.isVictoryConditionMet()) {
                        won = true;
                        running = false;
                        System.out.println("IZombie: All 5 brains eaten! You win!");
                        return;
                    }
                    continue;
                }

                if (!(activeMiniGame instanceof WallnutBowling)) {
                    LawnMower mower = lawnMowers[row];
                    if (!mower.isUsed()) {
                        mower.activate();
                        List<Zombie> toKill = new ArrayList<>();
                        for (Zombie z : activeZombies) {
                            if (z.getY() == row) {
                                toKill.add(z);
                            }
                        }
                        activeZombies.removeAll(toKill);
                        for (Zombie killed : toKill) {
                            board.getTile(killed.getY(), (int) killed.getX()).setZombie(null);
                            scoreGame.onZombieKilled(killed);
                            zombiesKilledInLevel++;
                        }
                        System.out.println("The lawn mower in the row " + row + " is triggered and killed these zombies.");
                    } else {
                        lost = true;
                        running = false;
                        System.out.println("The zombie ate your brain; LOSER !!!");
                        return;
                    }
                }
            }
        }

        for (Plant plant : activePlants) {
            if (plant.isFrozen() || plant.isBowlingBall()) continue;
            if (plant.getCategory() != null && plant.getCategory().equalsIgnoreCase("SHOOTER")) {
                if (tickCount % 15 == 0 && hasZombieInRow(plant.getY())) {
                    bullets.add(new Bullet(20, plant.getY(), plant.getX() + 1));
                }
            }
        }

        List<Bullet> bulletsToRemove = new ArrayList<>();
        for (Bullet bullet : new ArrayList<>(bullets)) {
            bullet.move();

            int row = bullet.getRow();
            int col = bullet.getColumn();
            Tile tile = board.getTile(row, col);
            if (tile != null) {
                if (bullet.getType() == Bullet.BulletType.FIRE && tile.getPlant() != null && tile.getPlant().isFrozen()) {
                    tile.getPlant().melt();
                    System.out.println("The fire bullet melted the ice on the plant at (" + col + ", " + row + ").");
                }
                if (tile.getType() == TileType.GRAVE && bullet.getType() != Bullet.BulletType.LOB) {
                    tile.setGraveHealth(tile.getGraveHealth() - bullet.getDamage());
                    bulletsToRemove.add(bullet);
                    System.out.println("The bullet hit the grave at (" + col + ", " + row + "). Grave health: " + tile.getGraveHealth());
                    if (tile.getGraveHealth() <= 0) {
                        board.removeGrave(row, col);
                        System.out.println("The grave at (" + col + ", " + row + ") is destroyed!");
                    }
                    continue;
                }
            }

            Zombie targetZombie = getFirstZombieInRowAhead(bullet.getRow(), bullet.getColumn());
            if (targetZombie != null) {
                targetZombie.takeDamage(bullet.getDamage(), false);
                bulletsToRemove.add(bullet);
                if (!targetZombie.isAlive()) {
                    activeZombies.remove(targetZombie);
                    board.getTile(targetZombie.getY(), (int) targetZombie.getX()).setZombie(null);
                    scoreGame.onZombieKilled(targetZombie);
                    zombiesKilledInLevel++;
                    System.out.println("Zombie of type " + targetZombie.getName() + " is dead at (" + (int)targetZombie.getX() + "), " + targetZombie.getY());
                }
            } else if (bullet.isOutOfBounds(board.getColumns())) {
                bulletsToRemove.add(bullet);
            }
        }
        bullets.removeAll(bulletsToRemove);

        if (spawner != null && spawner.isWaveComplete() && activeZombies.isEmpty() && !(activeMiniGame instanceof Vasebreaker) && !(activeMiniGame instanceof IZombie) && !(activeMiniGame instanceof Beghoul)) {
            won = true;
            running = false;
            System.out.println("Dear humanz , zis is not done yet; we will come back to eat your brainz , humanz .");
        }
    }

    private void handleSunDrop() {
        if (currentSeason != null && !currentSeason.allowsNaturalSunDrop()) {
            return;
        }
        SpecialLevelType specialType = level.getSpecialLevelType();
        if (specialType == SpecialLevelType.NIGHT_OPS || specialType == SpecialLevelType.PLANT_WHAT_YOU_GET || activeMiniGame != null) {
            return;
        }
        double secondsPast = tickCount / 10.0;
        double intervalSeconds = Math.max(6 + 0.05 * secondsPast, 12);
        int sunDropInterval = (int) (intervalSeconds * 10);

        if (tickCount - lastSunDropTick >= sunDropInterval) {
            lastSunDropTick = tickCount;
            Random r = new Random();
            int x = r.nextInt(board.getColumns());
            int y = r.nextInt(board.getRows());
            suns.add(new Sun(25, y, x));
            System.out.println("New sun is dropping at position (" + x + ", " + y + ")");
        }
    }

    public boolean hasZombieInRow(int row) {
        for (Zombie z : activeZombies) {
            if (z.getY() == row) return true;
        }
        return false;
    }

    private Zombie getFirstZombieInRowAhead(int row, double x) {
        Zombie closest = null;
        for (Zombie z : activeZombies) {
            if (z.getY() == row && z.getX() >= x) {
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
    public void addSun(int amount) { sunCount += amount; sunsProducedInLevel += amount; }
    public boolean spendCoins(int amount) { if (coins < amount) return false; coins -= amount; return true; }
    public void addCoins(int amount) { coins += amount; }
    public boolean spendDiamonds(int amount) { if (diamonds < amount) return false; diamonds -= amount; return true; }
    public void addDiamonds(int amount) { diamonds += amount; }
    public void addPlantFood() { plantFoodCount++; }
    public boolean usePlantFood() { if (plantFoodCount <= 0) return false; plantFoodCount--; return true; }
    public void addBullet(Bullet bullet) { bullets.add(bullet); }
    public void addSun(Sun sun) { suns.add(sun); }
    public void addZombie(Zombie zombie) { activeZombies.add(zombie); }
    public void addPlant(Plant plant) { activePlants.add(plant); scoreGame.onPlantPlaced(plant); }
    public void removePlant(Plant plant) { activePlants.remove(plant); }
    public void removeZombie(Zombie zombie) { activeZombies.remove(zombie); scoreGame.onZombieKilled(zombie); }
    public boolean isRunning() { return running; }
    public Board getBoard() { return board; }
    public Level getLevel() { return level; }
    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    public int getSunCount() { return sunCount; }
    public int getCoins() { return coins; }
    public int getDiamonds() { return diamonds; }
    public int getPlantFoodCount() { return plantFoodCount; }
    public Spawner getSpawner() { return spawner; }
    public ScoreGame getScoreGame() { return scoreGame; }
    public Greenhouse getGreenhouse() { return greenhouse; }
    public void setGreenhouse(Greenhouse greenhouse) { this.greenhouse = greenhouse; }
    public List<Bullet> getBullets() { return bullets; }
    public List<Sun> getSuns() { return suns; }
    public List<Zombie> getActiveZombies() { return activeZombies; }
    public List<Plant> getActivePlants() { return activePlants; }
    public int getTickCount() { return tickCount; }
}