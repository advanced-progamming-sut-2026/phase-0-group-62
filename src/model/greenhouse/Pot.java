package model.greenhouse;

import model.entities.plant.Plant;

public class Pot {
    private final int row;
    private final int column;
    private Plant plant;
    private boolean locked;
    private long plantedTime;
    private long growthTime;
    private boolean readyToHarvest;
    private boolean isGrowing;

    // Growth times in milliseconds
    private static final long MARIGOLD_GROWTH_TIME = 2 * 60 * 60 * 1000; // 2 hours
    private static final long RANDOM_PLANT_GROWTH_TIME = 8 * 60 * 60 * 1000; // 8 hours

    public Pot(int row, int column) {
        this.row = row;
        this.column = column;
        this.locked = row > 0; // First row unlocked by default
        this.plant = null;
        this.readyToHarvest = false;
        this.isGrowing = false;
        this.plantedTime = 0;
        this.growthTime = 0;
    }

    public boolean isEmpty() {
        return plant == null && !isGrowing;
    }

    public void plant(Plant plant) {
        this.plant = plant;
        this.plantedTime = System.currentTimeMillis();
        this.isGrowing = true;
        this.readyToHarvest = false;
        
        // Set growth time based on plant type
        if (plant.getName().equalsIgnoreCase("Marigold") || 
            plant.getName().equalsIgnoreCase("marigold")) {
            this.growthTime = MARIGOLD_GROWTH_TIME;
        } else {
            this.growthTime = RANDOM_PLANT_GROWTH_TIME;
        }
    }

    public void update() {
        if (!isGrowing || readyToHarvest) return;
        
        long elapsed = System.currentTimeMillis() - plantedTime;
        if (elapsed >= growthTime) {
            readyToHarvest = true;
            isGrowing = false;
        }
    }

    public void harvest() {
        readyToHarvest = false;
        isGrowing = false;
        plant = null;
        plantedTime = 0;
        growthTime = 0;
    }

    public void accelerateGrowth(long amount) {
        if (!isGrowing || readyToHarvest) return;
        
        long newPlantedTime = plantedTime + amount;
        long elapsed = System.currentTimeMillis() - newPlantedTime;
        if (elapsed >= growthTime) {
            readyToHarvest = true;
            isGrowing = false;
        } else {
            plantedTime = newPlantedTime;
        }
    }

    public double getGrowthProgress() {
        if (!isGrowing || readyToHarvest) return 1.0;
        if (plantedTime == 0) return 0.0;
        
        long elapsed = System.currentTimeMillis() - plantedTime;
        return Math.min(1.0, (double) elapsed / growthTime);
    }

    public long getRemainingTime() {
        if (!isGrowing || readyToHarvest) return 0;
        long elapsed = System.currentTimeMillis() - plantedTime;
        return Math.max(0, growthTime - elapsed);
    }

    public Plant getPlant() { return plant; }
    public void setPlant(Plant plant) { this.plant = plant; }
    public int getRow() { return row; }
    public int getColumn() { return column; }
    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }
    public boolean isReadyToHarvest() { return readyToHarvest; }
    public boolean isGrowing() { return isGrowing; }
    public long getPlantedTime() { return plantedTime; }
    public long getGrowthTime() { return growthTime; }
}
