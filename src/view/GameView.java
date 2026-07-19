package view;

import model.Board;
import model.Tile;
import model.Game;
import model.LawnMower;
import model.entities.plant.Plant;
import model.entities.zombie.Zombie;
import model.Sun;
import model.enums.TileType;
import model.enums.SpecialLevelType;
import model.minigame.Vasebreaker;
import model.minigame.IZombie;
import model.minigame.Beghoul;
import model.minigame.WallnutBowling;

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

        printHeader(game);
        printConveyorBeltIfActive(game);
        printGrid(game);
        showMessage("=========================================================================================\n");
    }

    private void printHeader(Game game) {
        showMessage("\n=========================================================================================");
        if (game.getActiveMiniGame() instanceof IZombie) {
            IZombie iz = (IZombie) game.getActiveMiniGame();
            showMessage(" TICK: " + game.getTickCount() + " | ZOMBIE SUNS: " + iz.getZombieSunCount() + " | BRAINS EATEN: " + iz.getBrainsEaten() + "/5");
        } else {
            showMessage(" TICK: " + game.getTickCount() + " | SUNS: " + game.getSunCount() + " | COINS: " + game.getCoins() + " | GEMS: " + game.getDiamonds() + " | FOODS: " + game.getPlantFoodCount());
        }
        showMessage("=========================================================================================");
    }

    private void printConveyorBeltIfActive(Game game) {
        if (game.getLevel().getSpecialLevelType() == SpecialLevelType.CONVEYOR_BELT || game.getActiveMiniGame() instanceof WallnutBowling) {
            if (!game.getConveyorBeltPlants().isEmpty()) {
                showMessage(" [CONVEYOR BELT]: " + String.join(" | ", game.getConveyorBeltPlants()));
                showMessage("-----------------------------------------------------------------------------------------");
            }
        }
    }

    private void printGrid(Game game) {
        Board board = game.getBoard();
        LawnMower[] mowers = game.getLawnMowers();

        for (int r = 0; r < board.getRows(); r++) {
            StringBuilder rowStr = new StringBuilder();

            if (game.getActiveMiniGame() instanceof IZombie) {
                IZombie iz = (IZombie) game.getActiveMiniGame();
                rowStr.append(iz.isBrainRowEaten(r) ? "[X] " : "[B] ");
            } else {
                rowStr.append(mowers[r].isUsed() ? "[X] " : "[M] ");
            }

            for (int c = 0; c < board.getColumns(); c++) {
                Tile tile = board.getTile(r, c);
                String cellContent = getCellContent(game, tile, r, c);
                rowStr.append(String.format("[%5s] ", cellContent));
            }
            showMessage(rowStr.toString());
        }
    }

    private String getCellContent(Game game, Tile tile, int r, int c) {
        if (game.getActiveMiniGame() instanceof Vasebreaker) {
            Vasebreaker vb = (Vasebreaker) game.getActiveMiniGame();
            if (!vb.isVaseBroken(r, c)) {
                String content = vb.getVaseContent(r, c);
                if ("zombie".equalsIgnoreCase(content) || "gargantuar".equalsIgnoreCase(content)) {
                    return "V-Z?";
                } else if ("empty".equalsIgnoreCase(content)) {
                    return "V-E?";
                } else {
                    return "V-P?";
                }
            }
            if (tile.getTemporarySeedPacket() != null) {
                return "PK-" + tile.getTemporarySeedPacket().substring(0, 1).toUpperCase();
            }
        }

        if (game.getActiveMiniGame() instanceof Beghoul) {
            Beghoul bg = (Beghoul) game.getActiveMiniGame();
            if (bg.hasCrater(r, c)) {
                return "CRTR";
            }
        }

        Plant p = game.getPlantAt(c, r);
        if (p == null && tile != null && tile.getSupportPlant() != null) {
            p = tile.getSupportPlant();
        }

        Zombie z = getZombieAtTile(game, c, r);
        Sun s = getSunAtTile(game, c, r);

        if (p != null) {
            String prefix = p.isBowlingBall() ? "B-" : (p.isFrozen() ? "#" : "P");
            return prefix + p.getHealth();
        }

        if (z != null) {
            return "Z" + z.getHealth();
        }

        if (s != null) {
            return "*";
        }

        if (tile != null) {
            if (tile.getType() == TileType.WATER) return "~~";
            if (tile.getType() == TileType.ICE) return "ICE";
            if (tile.getType() == TileType.GRAVE) {
                if (tile.getSunReward() > 0) return "G$";
                if (tile.hasPlantFoodReward()) return "G*";
                return "G";
            }
        }

        if (game.getActiveMiniGame() instanceof WallnutBowling) {
            WallnutBowling wb = (WallnutBowling) game.getActiveMiniGame();
            if (c == wb.getRedLineX()) return "|";
        }

        if (game.getLevel().getSpecialLevelType() == SpecialLevelType.DEAD_LINE) {
            if (c == game.getLevel().getDeadlineColumn()) return "|";
        }

        return ".";
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