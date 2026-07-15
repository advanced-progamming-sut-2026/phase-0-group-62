package model;

import model.enums.Difficulty;
import model.entities.zombie.Spawner;
import model.entities.zombie.Zombie;
import model.entities.zombie.ZombieEffect;
import model.entities.plant.Plant;
import model.score.ScoreGame;
import model.greenhouse.Greenhouse;
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

    public void tick() {
        if (!running || won || lost) return;
        tickCount++;

        if (spawner != null) {
            int oldSpawnedCount = spawner.getZombiesSpawnedInWave();
            if (tickCount == 1 || (spawner.ticksSinceLastSpawn == 0 && oldSpawnedCount == 0)) {
                if (spawner.isFinalWave()) {
                    System.out.println("The final wave has come.");
                } else {
                    System.out.println("Wave " + spawner.getCurrentWave() + " started.");
                }
            }

            spawner.update();
            int newSpawnedCount = spawner.getZombiesSpawnedInWave();
            if (newSpawnedCount > oldSpawnedCount) {
                for (int r = 0; r < board.getRows(); r++) {
                    for (int c = 0; c < board.getColumns(); c++) {
                        Zombie z = board.getTile(r, c).getZombie();
                        if (z != null && !activeZombies.contains(z)) {
                            activeZombies.add(z);
                            int cost = model.entities.zombie.factory.ZombieFactory.getWaveCost(z.getName());
                            System.out.println("Zombie " + z.getName() + " spawned at wave " + spawner.getCurrentWave() + " in lane " + r + " which costed " + cost + ".");
                        }
                    }
                }
            }
        }

        handleSunDrop();

        for (Plant plant : new ArrayList<>(activePlants)) {
            plant.update();
            if (tickCount % 100 == 0 && plant.getSunProduce() > 0) {
                plant.setHasSunToCollect(true);
                System.out.println("plant " + plant.getName() + " produced a sun at (" + plant.getX() + ", " + plant.getY() + ")");
            }
        }

        for (Zombie zombie : new ArrayList<>(activeZombies)) {
            zombie.updateEffects();
            zombie.updateCooldown();

            if (!zombie.hasEffect(ZombieEffect.FROZEN)) {
                Plant targetPlant = getPlantAt((int) zombie.getX(), zombie.getY());
                if (targetPlant != null) {
                    if (tickCount % 10 == 0) {
                        targetPlant.takeDamage(zombie.getDamage());
                        if (!targetPlant.isAlive()) {
                            activePlants.remove(targetPlant);
                            board.getTile(targetPlant.getY(), targetPlant.getX()).setPlant(null);
                            System.out.println("Plant " + targetPlant.getName() + " at (" + targetPlant.getX() + ", " + targetPlant.getY() + ") is destroyed.");
                        }
                    }
                } else {
                    zombie.move();
                }
            }

            if (zombie.getX() <= 0) {
                int row = zombie.getY();
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

        for (Plant plant : activePlants) {
            if (plant.getCategory() != null && plant.getCategory().equalsIgnoreCase("SHOOTER")) {
                if (tickCount % 15 == 0 && hasZombieInRow(plant.getY())) {
                    bullets.add(new Bullet(20, plant.getY(), plant.getX() + 1));
                }
            }
        }

        List<Bullet> bulletsToRemove = new ArrayList<>();
        for (Bullet bullet : new ArrayList<>(bullets)) {
            bullet.move();
            Zombie targetZombie = getFirstZombieInRowAhead(bullet.getRow(), bullet.getColumn());
            if (targetZombie != null) {
                targetZombie.takeDamage(bullet.getDamage(), false);
                bulletsToRemove.add(bullet);
                if (!targetZombie.isAlive()) {
                    activeZombies.remove(targetZombie);
                    board.getTile(targetZombie.getY(), (int) targetZombie.getX()).setZombie(null);
                    scoreGame.onZombieKilled(targetZombie);
                    System.out.println("Zombie of type " + targetZombie.getName() + " is dead at (" + (int)targetZombie.getX() + "), " + targetZombie.getY());

                    Random r = new Random();
                    if (targetZombie.isGlowing()) {
                        if (r.nextInt(100) < 5) {
                            if (plantFoodCount < 3) {
                                plantFoodCount++;
                                System.out.println("The glowing zombie dropeed a plant food; you have " + plantFoodCount + " plant foods now.");
                            }
                        }
                    }
                    if (r.nextInt(100) < 10) {
                        int dropType = r.nextInt(3);
                        if (dropType == 0) {
                            coins += 50;
                            System.out.println("A zombie dropeed a coin; you have " + coins + " coins now.");
                        } else if (dropType == 1) {
                            diamonds += 1;
                            System.out.println("A zombie dropeed a diamond; you have " + diamonds + " diamonds now.");
                        } else {
                            int pots = (greenhouse != null) ? greenhouse.getUnlockedPotCount() : 1;
                            System.out.println("A zombie dropeed a pot; you have " + pots + " pots now.");
                        }
                    }
                }
            } else if (bullet.isOutOfBounds(board.getColumns())) {
                bulletsToRemove.add(bullet);
            }
        }
        bullets.removeAll(bulletsToRemove);

        if (spawner != null && spawner.isWaveComplete() && activeZombies.isEmpty()) {
            won = true;
            running = false;
            System.out.println("Dear humanz , zis is not done yet; we will come back to eat your brainz , humanz .");
        }
    }

    private void handleSunDrop() {
        double secondsPast = tickCount / 10.0;
        double intervalSeconds = Math.max(6 + 0.05 * secondsPast, 12);
        int intervalTicks = (int) (intervalSeconds * 10);

        if (tickCount - lastSunDropTick >= intervalTicks) {
            lastSunDropTick = tickCount;
            Random r = new Random();
            int x = r.nextInt(board.getColumns());
            int y = r.nextInt(board.getRows());

            int chance = r.nextInt(100);
            String sunType = "NORMAL";
            int value = 25;

            if (chance < 80) {
                sunType = "NORMAL";
                value = 25;
            } else if (chance < 95) {
                sunType = "SPECIAL";
                value = 100;
            } else {
                sunType = "RADIOACTIVE";
                value = 25;
            }

            System.out.println("New " + sunType + " sun is dropping at position (" + x + ", " + y + ")");
            suns.add(new Sun(value, y, x));
            System.out.println("Sun reached the ground at position (" + x + ", " + y + ")");
        }
    }

    private boolean hasZombieInRow(int row) {
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

    public boolean isWon() {
        return won;
    }

    public boolean isLost() {
        return lost;
    }

    public LawnMower[] getLawnMowers() {
        return lawnMowers;
    }

    public boolean spendSun(int amount) {
        if (sunCount < amount) {
            return false;
        }
        sunCount -= amount;
        return true;
    }

    public void addSun(int amount) {
        sunCount += amount;
    }

    public boolean spendCoins(int amount) {
        if (coins < amount) return false;
        coins -= amount;
        return true;
    }

    public void addCoins(int amount) {
        coins += amount;
    }

    public boolean spendDiamonds(int amount) {
        if (diamonds < amount) return false;
        diamonds -= amount;
        return true;
    }

    public void addDiamonds(int amount) {
        diamonds += amount;
    }

    public void addPlantFood() {
        plantFoodCount++;
    }

    public boolean usePlantFood() {
        if (plantFoodCount <= 0) return false;
        plantFoodCount--;
        return true;
    }

    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    public void addSun(Sun sun) {
        suns.add(sun);
    }

    public void addZombie(Zombie zombie) {
        activeZombies.add(zombie);
    }

    public void addPlant(Plant plant) {
        activePlants.add(plant);
        scoreGame.onPlantPlaced(plant);
    }

    public void removePlant(Plant plant) {
        activePlants.remove(plant);
    }

    public void removeZombie(Zombie zombie) {
        activeZombies.remove(zombie);
        scoreGame.onZombieKilled(zombie);
    }

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