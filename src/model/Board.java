package model;

import model.enums.TileType;

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
}