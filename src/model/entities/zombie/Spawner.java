package model.entities.zombie;

import model.Board;
import model.Tile;
import model.entities.zombie.factory.ZombieFactory;
import model.enums.Difficulty;

import java.util.*;

public class Spawner {
    private Board board;
    private int currentWave;
    private int totalWaves;
    private int remainingZombies;
    private int zombiesPerWave;
    private Difficulty difficulty;
    private Map<Integer, List<String>> waveSchedule;
    private boolean finalWaveStarted;
    private int zombiesSpawnedInWave;
    public int ticksSinceLastSpawn;
    private int spawnInterval;

    public Spawner(Board board, int totalWaves, Difficulty difficulty) {
        this.board = board;
        this.totalWaves = totalWaves;
        this.difficulty = difficulty;
        this.currentWave = 0;
        this.remainingZombies = 0;
        this.zombiesSpawnedInWave = 0;
        this.ticksSinceLastSpawn = 0;
        this.finalWaveStarted = false;
        this.waveSchedule = new HashMap<>();
        this.spawnInterval = getSpawnInterval();
        initializeWaves();
    }

    private void initializeWaves() {
        for (int wave = 1; wave <= totalWaves; wave++) {
            int waveCost = calculateWaveCost(wave);
            List<String> zombieTypes = generateZombieTypes(waveCost, wave);
            waveSchedule.put(wave, zombieTypes);
        }
    }

    private int calculateWaveCost(int wave) {
        int baseCost = 50;
        int waveMultiplier = (int) Math.pow(1.25, wave - 1);
        int cost = (int) (baseCost * waveMultiplier);
        switch (difficulty) {
            case EASY:
                cost = (int) (cost * 0.7);
                break;
            case HARD:
                cost = (int) (cost * 1.4);
                break;
            default:
                break;
        }

        if (wave == totalWaves) {
            cost *= 2;
        }

        return Math.max(cost, 10);
    }

    private List<String> generateZombieTypes(int waveCost, int wave) {
        List<String> types = new ArrayList<>();
        int remainingCost = waveCost;

        List<String> availableZombies = getAvailableZombiesForWave(wave);
        while (remainingCost > 0) {
            String type = availableZombies.get(new Random().nextInt(availableZombies.size()));
            int cost = ZombieFactory.getWaveCost(type);
            if (cost <= remainingCost) {
                types.add(type);
                remainingCost -= cost;
            } else {
                if (remainingCost >= 10) {
                    types.add("NormalZombie");
                    remainingCost -= 10;
                } else {
                    break;
                }
            }
        }

        return types;
    }

    private List<String> getAvailableZombiesForWave(int wave) {
        List<String> available = new ArrayList<>();
        available.add("NormalZombie");

        if (wave >= 2) {
            available.add("ConeZombie");
        }
        if (wave >= 3) {
            available.add("BucketZombie");
        }
        if (wave >= 4) {
            available.add("FastZombie");
        }

        return available;
    }

    private int getSpawnInterval() {
        switch (difficulty) {
            case EASY:
                return 120;
            case HARD:
                return 40;
            default:
                return 80;
        }
    }

    public void startWave(int waveNumber) {
        currentWave = waveNumber;
        List<String> zombieTypes = waveSchedule.get(waveNumber);
        if (zombieTypes == null) return;

        remainingZombies = zombieTypes.size();
        zombiesSpawnedInWave = 0;
        finalWaveStarted = (waveNumber == totalWaves);
        ticksSinceLastSpawn = 0;
    }

    public Zombie spawnNextZombie() {
        List<String> zombieTypes = waveSchedule.get(currentWave);
        if (zombieTypes == null || zombiesSpawnedInWave >= zombieTypes.size()) {
            return null;
        }

        String type = zombieTypes.get(zombiesSpawnedInWave);
        int lane = new Random().nextInt(board.getRows());
        if (currentWave == totalWaves) {
            lane = zombiesSpawnedInWave % board.getRows();
        }

        int column = board.getColumns() - 1;
        Zombie zombie = ZombieFactory.createZombieAtColumn(type, lane, column);
        if (zombie != null) {
            if (new Random().nextInt(100) < 5) {
                zombie.setGlowing(true);
            }

            Tile tile = board.getTile(lane, column);
            if (tile != null) {
                tile.setZombie(zombie);
            }

            zombiesSpawnedInWave++;
            remainingZombies--;
        }

        return zombie;
    }

    public void update() {
        ticksSinceLastSpawn++;
        if (ticksSinceLastSpawn >= spawnInterval && remainingZombies > 0) {
            spawnNextZombie();
            ticksSinceLastSpawn = 0;
        }
    }

    public boolean isWaveComplete() {
        return remainingZombies <= 0 && zombiesSpawnedInWave >=
                waveSchedule.getOrDefault(currentWave, Collections.emptyList()).size();
    }

    public boolean isFinalWave() {
        return currentWave == totalWaves;
    }

    public boolean isFinalWaveStarted() {
        return finalWaveStarted;
    }

    public int getCurrentWave() { return currentWave; }
    public int getTotalWaves() { return totalWaves; }
    public int getRemainingZombies() { return remainingZombies; }
    public int getZombiesSpawnedInWave() { return zombiesSpawnedInWave; }

    public List<String> getCurrentWaveZombies() {
        return waveSchedule.getOrDefault(currentWave, Collections.emptyList());
    }

    public int getZombiesInWave() {
        return waveSchedule.getOrDefault(currentWave, Collections.emptyList()).size();
    }

    public void setSpawnInterval(int spawnInterval) { this.spawnInterval = spawnInterval; }
}