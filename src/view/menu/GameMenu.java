package view.menu;

import controller.menu.CommandParser;
import controller.menu.MenuController;
import controller.menu.GameController;
import model.Game;
import model.Tile;
import model.enums.Difficulty;
import model.entities.plant.Plant;
import model.entities.zombie.Zombie;
import util.ParsedCommand;
import view.GameView;

public class GameMenu extends Menu {
    private final Game game;
    private final GameView gameView = new GameView();
    private final GameController gameController;

    public GameMenu(MenuController controller) {
        super(controller);
        this.game = new Game(5, 9, 1, Difficulty.NORMAL);
        this.game.start();
        this.gameController = new GameController(controller);
        this.gameController.setGame(this.game);
    }

    @Override
    public void runMenu() {
        CommandParser parser = new CommandParser();
        while (true) {
            gameView.showBoardState(game);

            if (game.isWon()) {
                view.showMessage("Congratulations! You won the game!");
                manager.setCurrentMenu(new MainMenu(controller));
                break;
            }
            if (game.isLost()) {
                view.showMessage("Game Over! The zombies ate your brains!");
                manager.setCurrentMenu(new MainMenu(controller));
                break;
            }

            String input = view.getInput("game play");
            ParsedCommand cmd = parser.parse(input);

            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("exit game")) {
                view.showMessage("Returning to main menu...");
                manager.setCurrentMenu(new MainMenu(controller));
                break;
            }

            if (cmd.getAction().equalsIgnoreCase("advance time")) {
                String ticksStr = cmd.getArg("-t");
                int ticks = 1;
                if (ticksStr != null) {
                    try {
                        ticks = Integer.parseInt(ticksStr.split(" ")[0]);
                    } catch (Exception e) {
                        ticks = 1;
                    }
                }
                gameController.advanceTime(ticks);
                view.showMessage("Time advanced by " + ticks + " ticks.");
            }
            else if (cmd.getAction().equalsIgnoreCase("show map")) {
                gameView.showBoardState(game);
            }
            else if (cmd.getAction().equalsIgnoreCase("plant plant")) {
                String type = cmd.getArg("-t");
                String loc = cmd.getArg("-l");
                if (type == null || loc == null) {
                    view.showMessage("Usage: plant plant -t <type> -l (<x>, <y>)");
                    continue;
                }
                try {
                    loc = loc.replace("(", "").replace(")", "");
                    String[] coords = loc.split(",");
                    int x = Integer.parseInt(coords[0].trim());
                    int y = Integer.parseInt(coords[1].trim());

                    String result = gameController.plantPlant(type, x, y);
                    view.showMessage(result);
                } catch (Exception e) {
                    view.showMessage("Invalid format! Coordinates must be inside (-l (x, y))");
                }
            }
            else if (cmd.getAction().equalsIgnoreCase("pluck plant")) {
                String loc = cmd.getArg("-l");
                if (loc == null) {
                    view.showMessage("Usage: pluck plant -l (<x>, <y>)");
                    continue;
                }
                try {
                    loc = loc.replace("(", "").replace(")", "");
                    String[] coords = loc.split(",");
                    int x = Integer.parseInt(coords[0].trim());
                    int y = Integer.parseInt(coords[1].trim());

                    String result = gameController.pluckPlant(x, y);
                    view.showMessage(result);
                } catch (Exception e) {
                    view.showMessage("Invalid format! Use pluck plant -l (<x>, <y>)");
                }
            }
            else if (cmd.getAction().equalsIgnoreCase("feed plant")) {
                String loc = cmd.getArg("-l");
                if (loc == null) {
                    view.showMessage("Usage: feed plant -l (<x>, <y>)");
                    continue;
                }
                try {
                    loc = loc.replace("(", "").replace(")", "");
                    String[] coords = loc.split(",");
                    int x = Integer.parseInt(coords[0].trim());
                    int y = Integer.parseInt(coords[1].trim());

                    String result = gameController.feedPlant(x, y);
                    view.showMessage(result);
                } catch (Exception e) {
                    view.showMessage("Invalid format! Use feed plant -l (<x>, <y>)");
                }
            }
            else if (cmd.getAction().equalsIgnoreCase("collect sun")) {
                String loc = cmd.getArg("-l");
                if (loc == null) {
                    view.showMessage("Usage: collect sun -l (<x>, <y>)");
                    continue;
                }
                try {
                    loc = loc.replace("(", "").replace(")", "");
                    String[] coords = loc.split(",");
                    int x = Integer.parseInt(coords[0].trim());
                    int y = Integer.parseInt(coords[1].trim());

                    String result = gameController.collectSun(x, y);
                    view.showMessage(result);
                } catch (Exception e) {
                    view.showMessage("Invalid coordinates format!");
                }
            }
            else if (cmd.getAction().equalsIgnoreCase("show sun amount")) {
                view.showMessage("Current sun amount: " + game.getSunCount());
            }
            else if (cmd.getAction().equalsIgnoreCase("show plants status")) {
                if (game.getActivePlants().isEmpty()) {
                    view.showMessage("No plants currently active on the field.");
                } else {
                    for (Plant p : game.getActivePlants()) {
                        view.showMessage("- " + p.getName() + " at (" + p.getX() + ", " + p.getY() + ") | HP: " + p.getHealth() + "/" + p.getMaxHealth() + " | Can Produce Sun: " + (p.getSunProduce() > 0));
                    }
                }
            }
            else if (cmd.getAction().equalsIgnoreCase("show tile status")) {
                String loc = cmd.getArg("-l");
                if (loc == null) {
                    view.showMessage("Usage: show tile status -l (<x>, <y>)");
                    continue;
                }
                try {
                    loc = loc.replace("(", "").replace(")", "");
                    String[] coords = loc.split(",");
                    int x = Integer.parseInt(coords[0].trim());
                    int y = Integer.parseInt(coords[1].trim());
                    Tile tile = game.getBoard().getTile(y, x);
                    if (tile != null) {
                        view.showMessage("Tile (" + x + ", " + y + ") Status:");
                        view.showMessage("- Type: " + tile.getType());
                        view.showMessage("- Plant: " + (tile.getPlant() != null ? tile.getPlant().getName() : "None"));
                        view.showMessage("- Zombie: " + (tile.getZombie() != null ? tile.getZombie().getName() + " (HP: " + tile.getZombie().getHealth() + ")" : "None"));
                    } else {
                        view.showMessage("Error: Tile coordinates out of bounds.");
                    }
                } catch (Exception e) {
                    view.showMessage("Invalid format! Use show tile status -l (<x>, <y>)");
                }
            }
            else if (cmd.getAction().equalsIgnoreCase("release the nuke")) {
                String result = gameController.executeNuke();
                view.showMessage(result);
            }
            else if (cmd.getAction().equalsIgnoreCase("cheat remove-cooldown")) {
                String result = gameController.executeRemoveCooldownCheat();
                view.showMessage(result);
            }
            else if (cmd.getAction().equalsIgnoreCase("cheat add-plant-food")) {
                String result = gameController.executeAddPlantFoodCheat();
                view.showMessage(result);
            }
            else if (cmd.getAction().equalsIgnoreCase("cheat add")) {
                if (cmd.hasFlag("-n")) {
                    try {
                        String countStr = cmd.getArg("-n");
                        int amount = Integer.parseInt(countStr.split(" ")[0]);
                        String result = gameController.addCheatSuns(amount);
                        view.showMessage(result);
                    } catch (Exception e) {
                        view.showMessage("Invalid cheat format.");
                    }
                } else {
                    String valueStr = cmd.getArg("VALUE");
                    if (valueStr != null && valueStr.toLowerCase().contains("sun")) {
                        try {
                            int amount = Integer.parseInt(valueStr.toLowerCase().replace("suns", "").trim());
                            String result = gameController.addCheatSuns(amount);
                            view.showMessage(result);
                        } catch (Exception e) {
                            view.showMessage("Invalid cheat format.");
                        }
                    }
                }
            }
            else {
                view.showMessage("Unknown game command. Type 'exit' to return to menu.");
            }
        }
    }
}