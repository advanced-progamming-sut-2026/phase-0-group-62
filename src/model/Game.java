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
    }

    public Game(int rows, int columns, int levelNumber, Difficulty difficulty) {
        this();
        this.board = new Board(rows, columns);
        this.level = new Level(levelNumber);
        this.difficulty = difficulty;
        this.spawner = new Spawner(board, levelNumber * 2, difficulty);
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
        if (!running) return;
        tickCount++;

        // Update spawner
        if (spawner != null) {
            spawner.update();
        }

        // Update plants
        for (Plant plant : activePlants) {
            plant.update();
        }

        // Update zombies
        for (Zombie zombie : activeZombies) {
            zombie.updateEffects();
            zombie.updateCooldown();
            if (!zombie.hasEffect(ZombieEffect.FROZEN)) {
                zombie.move();
            }
        }

        // Update bullets
        List<Bullet> toRemove = new ArrayList<>();
        for (Bullet bullet : bullets) {
            bullet.move();
            if (bullet.isOutOfBounds(board.getColumns())) {
                toRemove.add(bullet);
            }
        }
        bullets.removeAll(toRemove);
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
