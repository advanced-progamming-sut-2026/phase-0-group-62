package view;

import model.Board;
import model.Tile;
import model.Game;
import model.LawnMower;
import model.entities.plant.Plant;
import model.entities.zombie.Zombie;
import model.Sun;
import model.enums.TileType;

public class GameView extends View {
    public void showBoard(Board board) {
        showBoardState(null);
    }

    public void showBoardState(Game game) {
        if (game == null) {
            showMessage("\n--- PvZ Battlefield ---");
            showMessage("No active game session.");
            return;
        }

        showMessage("\n=========================================================================================");
        showMessage(" TICK: " + game.getTickCount() + " | SUNS: " + game.getSunCount() + " | COINS: " + game.getCoins() + " | GEMS: " + game.getDiamonds() + " | FOODS: " + game.getPlantFoodCount());
        showMessage("=========================================================================================");

        Board board = game.getBoard();
        LawnMower[] mowers = game.getLawnMowers();

        for (int r = 0; r < board.getRows(); r++) {
            StringBuilder rowStr = new StringBuilder();
            String mowerStatus = mowers[r].isUsed() ? "[X]" : "[M]";
            rowStr.append(mowerStatus).append(" ");

            for (int c = 0; c < board.getColumns(); c++) {
                Tile tile = board.getTile(r, c);
                Plant p = game.getPlantAt(c, r);
                Zombie z = getZombieAtTile(game, c, r);
                Sun s = getSunAtTile(game, c, r);

                String cellContent = ".";

                if (tile != null && tile.getType() == TileType.WATER && p == null) {
                    cellContent = "~~";
                }

                if (p != null) {
                    String freezeMark = p.isFrozen() ? "#" : "";
                    cellContent = freezeMark + p.getName().substring(0, 1).toUpperCase() + p.getHealth();
                } else if (z != null) {
                    cellContent = z.getName().substring(0, 1).toUpperCase() + z.getHealth();
                } else if (s != null) {
                    cellContent = "*";
                } else if (tile != null && tile.getType() == TileType.GRAVE) {
                    if (tile.getSunReward() > 0) {
                        cellContent = "G$";
                    } else if (tile.hasPlantFoodReward()) {
                        cellContent = "G*";
                    } else {
                        cellContent = "G";
                    }
                }

                rowStr.append(String.format("[%5s] ", cellContent));
            }
            showMessage(rowStr.toString());
        }
        showMessage("=========================================================================================\n");
    }

    private Zombie getZombieAtTile(Game game, int x, int y) {
        for (Zombie z : game.getActiveZombies()) {
            if ((int) Math.round(z.getX()) == x && z.getY() == y) {
                return z;
            }
        }
        return null;
    }

    private Sun getSunAtTile(Game game, int x, int y) {
        for (Sun s : game.getSuns()) {
            if (s.getColumn() == x && s.getRow() == y) {
                return s;
            }
        }
        return null;
    }
}