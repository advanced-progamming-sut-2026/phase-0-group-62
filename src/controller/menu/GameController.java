package controller.menu;

import model.Game;
import model.entities.plant.factory.PlantFactory;
import model.entities.plant.Plant;
import model.entities.zombie.Zombie;
import model.Sun;
import java.util.Random;

public class GameController extends Controller {
    private Game game;
    private boolean cooldownCheatActive = false;

    public GameController(MenuController controller) {
        super(controller);
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String plantPlant(String type, int x, int y) {
        if (game == null) {
            return "Error: No active game session.";
        }
        if (x < 0 || x >= game.getBoard().getColumns() || y < 0 || y >= game.getBoard().getRows()) {
            return "Error: Coordinates out of bounds!";
        }
        Plant check = game.getPlantAt(x, y);
        if (check != null) {
            return "Error: There is already a plant here!";
        }
        Plant newPlant = PlantFactory.createPlant(type);
        if (newPlant == null) {
            return "Error: Plant type not found!";
        }
        if (game.getSunCount() < newPlant.getCost()) {
            return "Error: Not enough suns! Required: " + newPlant.getCost();
        }

        game.spendSun(newPlant.getCost());
        newPlant.setX(x);
        newPlant.setY(y);
        game.addPlant(newPlant);
        game.getBoard().getTile(y, x).setPlant(newPlant);
        return "Successfully planted " + type + " at (" + x + ", " + y + ")";
    }

    public String pluckPlant(int x, int y) {
        if (game == null) {
            return "Error: No active game session.";
        }
        Plant target = game.getPlantAt(x, y);
        if (target == null) {
            return "Error: There is no plant at this location to pluck.";
        }
        game.removePlant(target);
        game.getBoard().getTile(y, x).setPlant(null);
        return "Successfully plucked plant at (" + x + ", " + y + ")";
    }

    public String feedPlant(int x, int y) {
        if (game == null) {
            return "Error: No active game session.";
        }
        Plant target = game.getPlantAt(x, y);
        if (target == null) {
            return "Error: There is no plant here to feed.";
        }
        if (game.getPlantFoodCount() <= 0) {
            return "Error: You do not have any plant food left.";
        }
        if (game.usePlantFood()) {
            target.heal(target.getMaxHealth());
            return "Successfully fed plant at (" + x + ", " + y + "). HP fully restored!";
        }
        return "Error: Could not use plant food.";
    }

    public String collectSun(int x, int y) {
        if (game == null) {
            return "Error: No active game session.";
        }
        Sun targetSun = null;
        for (Sun s : game.getSuns()) {
            if (s.getColumn() == x && s.getRow() == y) {
                targetSun = s;
                break;
            }
        }
        Plant targetPlant = game.getPlantAt(x, y);
        if (targetSun != null) {
            game.addSun(targetSun.getValue());
            game.getSuns().remove(targetSun);
            return "Collected sun at (" + x + ", " + y + "). Total: " + game.getSunCount();
        } else if (targetPlant != null && targetPlant.isHasSunToCollect()) {
            game.addSun(25);
            targetPlant.setHasSunToCollect(false);
            return "Collected sun from " + targetPlant.getName() + " at (" + x + ", " + y + "). Total: " + game.getSunCount();
        }
        return "Error: No sun available to collect at this location.";
    }

    public String processZombieDeathDrops(Zombie zombie) {
        StringBuilder message = new StringBuilder();
        Random r = new Random();

        if (zombie.isGlowing()) {
            if (r.nextInt(100) < 5) {
                if (game.getPlantFoodCount() < 3) {
                    game.addPlantFood();
                    message.append("The glowing zombie dropeed a plant food; you have ")
                            .append(game.getPlantFoodCount())
                            .append(" plant foods now.\n");
                }
            }
        }

        if (r.nextInt(100) < 10) {
            int dropType = r.nextInt(3);
            if (dropType == 0) {
                game.addCoins(50);
                message.append("A zombie dropeed a coin; you have ")
                        .append(game.getCoins())
                        .append(" coins now.");
            } else if (dropType == 1) {
                game.addDiamonds(1);
                message.append("A zombie dropeed a diamond; you have ")
                        .append(game.getDiamonds())
                        .append(" diamonds now.");
            } else {
                message.append("A zombie dropeed a pot; you have ")
                        .append(game.getGreenhouse() != null ? game.getGreenhouse().getUnlockedPotCount() : 1)
                        .append(" pots now.");
            }
        }
        return message.toString().trim();
    }

    public String executeNuke() {
        if (game == null) {
            return "Error: No active game session.";
        }
        int count = game.getActiveZombies().size();
        for (Zombie z : game.getActiveZombies()) {
            game.getBoard().getTile(z.getY(), (int) z.getX()).setZombie(null);
        }
        game.getActiveZombies().clear();
        return "Nuke released! " + count + " zombies wiped off the map.";
    }

    public String executeRemoveCooldownCheat() {
        cooldownCheatActive = true;
        return "Cheat activated: Cooldown limits removed for all plants.";
    }

    public String executeAddPlantFoodCheat() {
        if (game == null) {
            return "Error: No active game session.";
        }
        game.addPlantFood();
        return "Cheat activated: Added 1 plant food. Total: " + game.getPlantFoodCount();
    }

    public String addCheatSuns(int amount) {
        if (game == null) {
            return "Error: No active game session.";
        }
        game.addSun(amount);
        return "Cheat activated: Added " + amount + " suns.";
    }

    public void advanceTime(int ticks) {
        if (game == null) return;
        for (int i = 0; i < ticks; i++) {
            game.tick();
        }
    }

    public boolean isCooldownCheatActive() {
        return cooldownCheatActive;
    }
}