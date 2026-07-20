package model;

import model.enums.TileType;
import model.entities.zombie.Zombie;
import java.util.ArrayList;
import java.util.List;

public class Board {
    private final int rows;
    private final int columns;
    private final Tile[][] tiles;

    public Board(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.tiles = new Tile[rows][columns];
        initializeTiles();
    }

    private void initializeTiles() {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                tiles[row][column] = new Tile(row, column);
            }
        }
    }

    public Tile getTile(int row, int column) {
        if (row < 0 || row >= rows || column < 0 || column >= columns) {
            return null;
        }
        return tiles[row][column];
    }

    public void setTileType(int row, int column, TileType type) {
        Tile tile = getTile(row, column);
        if (tile != null) {
            tile.setType(type);
        }
    }

    public boolean isTileWater(int row, int column) {
        Tile tile = getTile(row, column);
        return tile != null && tile.getType() == TileType.WATER;
    }

    public boolean isTileIce(int row, int column) {
        Tile tile = getTile(row, column);
        return tile != null && tile.getType() == TileType.ICE;
    }

    public boolean isTileGrave(int row, int column) {
        Tile tile = getTile(row, column);
        return tile != null && tile.getType() == TileType.GRAVE;
    }

    public boolean isTileGrass(int row, int column) {
        Tile tile = getTile(row, column);
        return tile != null && tile.getType() == TileType.GRASS;
    }

    public int getRows() { return rows; }
    public int getColumns() { return columns; }

    public boolean isInBounds(int row, int column) {
        return row >= 0 && row < rows && column >= 0 && column < columns;
    }

    public void setupGrave(int row, int col, int health, int sun, boolean pf) {
        Tile tile = getTile(row, col);
        if (tile != null) {
            tile.setType(TileType.GRAVE);
            tile.setGraveHealth(health);
            tile.setSunReward(sun);
            tile.setHasPlantFoodReward(pf);
        }
    }

    public void removeGrave(int row, int col) {
        Tile tile = getTile(row, col);
        if (tile != null) {
            tile.setType(TileType.GRASS);
            tile.setGraveHealth(0);
            tile.setSunReward(0);
            tile.setHasPlantFoodReward(false);
        }
    }

    public void setupSlideway(int row, int col, int offset) {
        Tile tile = getTile(row, col);
        if (tile != null) {
            tile.setSlideway(true);
            tile.setSlideRowOffset(offset);
        }
    }

    public void setupLowBeach(int row, int col) {
        Tile tile = getTile(row, col);
        if (tile != null) {
            tile.setLowBeach(true);
        }
    }

    public void setupNecromancy(int row, int col) {
        Tile tile = getTile(row, col);
        if (tile != null) {
            tile.setNecromancyTile(true);
        }
    }

    public boolean hasGraveInRow(int row) {
        for (int c = 0; c < columns; c++) {
            if (isTileGrave(row, c)) {
                return true;
            }
        }
        return false;
    }

    public void updateProjectilesAndCollisions(Game game) {
        List<Bullet> bulletsToRemove = new ArrayList<>();
        for (Bullet bullet : new ArrayList<>(game.getBullets())) {
            double oldBulletX = bullet.getColumn();
            bullet.move();
            double newBulletX = bullet.getColumn();
            int bRow = bullet.getRow();

            int checkTileCol = (int) Math.floor(newBulletX);
            Tile tile = getTile(bRow, checkTileCol);

            if (tile != null && tile.getType() == TileType.GRAVE && bullet.getType() != Bullet.BulletType.LOB) {
                if (oldBulletX <= checkTileCol && newBulletX >= checkTileCol) {
                    tile.setGraveHealth(tile.getGraveHealth() - bullet.getDamage());
                    bulletsToRemove.add(bullet);
                    if (tile.getGraveHealth() <= 0) {
                        game.getGameLogMessages().add("Grave destroyed at column " + checkTileCol + ", row " + bRow);
                        if (tile.getSunReward() > 0) {
                            game.addSun(tile.getSunReward());
                            game.getGameLogMessages().add("Grave destroyed! You got " + tile.getSunReward() + " suns.");
                        }
                        if (tile.hasPlantFoodReward()) {
                            game.addPlantFood();
                            game.getGameLogMessages().add("Grave destroyed! You got a Plant Food.");
                        }
                        removeGrave(bRow, checkTileCol);
                    }
                    continue;
                }
            }

            if (tile != null && tile.getPlant() != null && tile.getPlant().isFrozen()) {
                if (oldBulletX <= checkTileCol && newBulletX >= checkTileCol) {
                    if (bullet.getType() == Bullet.BulletType.FIRE) {
                        tile.getPlant().melt();
                        game.getGameLogMessages().add("Fire bullet instantly melted the ice on " + tile.getPlant().getName());
                    } else if (bullet.getType() != Bullet.BulletType.LOB) {
                        tile.getPlant().damageIce(bullet.getDamage());
                        bulletsToRemove.add(bullet);
                        if (!tile.getPlant().isFrozen()) {
                            game.getGameLogMessages().add("Ice shattered and freed " + tile.getPlant().getName());
                        }
                        continue;
                    }
                }
            }

            Zombie targetZombie = null;
            for (Zombie z : game.getActiveZombies()) {
                if (z.getY() == bRow) {
                    double zombieX = z.getX();
                    if (newBulletX >= zombieX) {
                        targetZombie = z;
                        break;
                    }
                }
            }

            if (targetZombie != null) {
                boolean graveInWay = false;
                if (bullet.getType() != Bullet.BulletType.LOB) {
                    int startCheck = (int) oldBulletX;
                    int endCheck = (int) targetZombie.getX();
                    for (int c = startCheck; c <= endCheck; c++) {
                        Tile checkTile = getTile(bRow, c);
                        if (checkTile != null && checkTile.getType() == TileType.GRAVE) {
                            graveInWay = true;
                            break;
                        }
                    }
                }

                if (!graveInWay) {
                    targetZombie.takeDamage(bullet.getDamage(), false);
                    bulletsToRemove.add(bullet);

                    if (!targetZombie.isAlive()) {
                        String deathMessage = "Zombie of type " + targetZombie.getName() + " is dead at (" + (int) Math.round(targetZombie.getX()) + ", " + targetZombie.getY() + ")";
                        game.getGameLogMessages().add(deathMessage);

                        game.getActiveZombies().remove(targetZombie);
                        for (int r = 0; r < getRows(); r++) {
                            for (int c = 0; c < getColumns(); c++) {
                                if (getTile(r, c).getZombie() == targetZombie) {
                                    getTile(r, c).setZombie(null);
                                }
                            }
                        }
                        game.getScoreGame().onZombieKilled(targetZombie, game);
                        game.incrementZombiesKilled();
                    }
                }
            } else if (bullet.isOutOfBounds(getColumns())) {
                bulletsToRemove.add(bullet);
            }
        }
        game.getBullets().removeAll(bulletsToRemove);
    }
}