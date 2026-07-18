package controller.menu;

import model.Game;
import model.Tile;
import model.enums.Difficulty;
import model.entities.zombie.factory.ZombieFactory;
import model.entities.zombie.Zombie;
import model.entities.plant.Plant;
import model.season.AncientEgypt;
import model.season.BigWaveBeach;
import model.season.DarkAges;
import model.season.FrostbiteCaves;
import model.season.Season;
import util.ParsedCommand;
import view.menu.MainMenu;
import view.menu.MenuManager;
import java.util.List;

public class GameMenuController extends Controller {
    private final Game game;
    private final GameController gameController;
    private final MenuController menuController;
    private final CommandParser parser;

    public GameMenuController(MenuController menuController) {
        super(menuController);
        this.menuController = menuController;
        this.game = new Game(5, 9, 1, Difficulty.NORMAL);
        String ch = PreGameController.activeChapterName;
        Season season = null;
        if ("AncientEgypt".equals(ch)) {
            season = new AncientEgypt();
        } else if ("FrostbiteCaves".equals(ch)) {
            season = new FrostbiteCaves();
        } else if ("BigWaveBeach".equals(ch)) {
            season = new BigWaveBeach();
        } else if ("DarkAges".equals(ch)) {
            season = new DarkAges();
        }
        if (season != null) {
            this.game.setCurrentSeason(season);
        }
        this.game.start();
        this.gameController = new GameController(menuController);
        this.gameController.setGame(this.game);
        this.parser = new CommandParser();
    }

    public Game getGame() {
        return game;
    }

    public String handleGameMenuInput(String input) {
        if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("exit game")) {
            MenuManager.getInstance().setCurrentMenu(new MainMenu(menuController));
            return "EXIT_GAME";
        }

        ParsedCommand cmd = parser.parse(input);
        String action = cmd.getAction();

        if (action.equalsIgnoreCase("advance time")) {
            String ticksStr = cmd.getArg("-t");
            int ticks = 1;
            if (ticksStr != null) {
                try {
                    ticksStr = ticksStr.trim().split(" ")[0];
                    ticks = Integer.parseInt(ticksStr);
                } catch (Exception e) {
                    ticks = 1;
                }
            }
            int waveBefore = game.getSpawner() != null ? game.getSpawner().getCurrentWave() : 0;
            int sunsBefore = game.getSuns().size();
            int executed = gameController.advanceTime(ticks);
            String report = "Time advanced by " + executed + " ticks.";
            if (game.getSpawner() != null && game.getSpawner().getCurrentWave() > waveBefore) {
                report += "\n[WAVE] ---> A new Wave started! Current Wave: " + game.getSpawner().getCurrentWave();
            }
            if (game.getSuns().size() > sunsBefore && !"DarkAges".equals(PreGameController.activeChapterName)) {
                report += "\n[SUN] A natural sun fell from sky!";
            }
            List<String> logs = game.getGameLogMessages();
            for (String log : logs) {
                report += "\n" + log;
            }
            return report;
        }
        if (action.equalsIgnoreCase("show map")) {
            return "SHOW_MAP_TRIGGER";
        }
        if (action.equalsIgnoreCase("plant plant")) {
            String type = cmd.getArg("-t");
            String loc = cmd.getArg("-l");
            if (type == null || loc == null) {
                return "Usage: plant plant -t <type> -l (<x>, <y>)";
            }
            try {
                loc = loc.replace("(", "").replace(")", "");
                String[] coords = loc.split(",");
                int x = Integer.parseInt(coords[0].trim());
                int y = Integer.parseInt(coords[1].trim());
                return gameController.plantPlant(type, x, y);
            } catch (Exception e) {
                return "Invalid format! Coordinates must be inside (-l (x, y))";
            }
        }
        if (action.equalsIgnoreCase("pluck plant")) {
            String loc = cmd.getArg("-l");
            if (loc == null) {
                return "Usage: pluck plant -l (<x>, <y>)";
            }
            try {
                loc = loc.replace("(", "").replace(")", "");
                String[] coords = loc.split(",");
                int x = Integer.parseInt(coords[0].trim());
                int y = Integer.parseInt(coords[1].trim());
                return gameController.pluckPlant(x, y);
            } catch (Exception e) {
                return "Invalid format! Use pluck plant -l (<x>, <y>)";
            }
        }
        if (action.equalsIgnoreCase("feed plant")) {
            String loc = cmd.getArg("-l");
            if (loc == null) {
                return "Usage: feed plant -l (<x>, <y>)";
            }
            try {
                loc = loc.replace("(", "").replace(")", "");
                String[] coords = loc.split(",");
                int x = Integer.parseInt(coords[0].trim());
                int y = Integer.parseInt(coords[1].trim());
                return gameController.feedPlant(x, y);
            } catch (Exception e) {
                return "Invalid format! Use feed plant -l (<x>, <y>)";
            }
        }
        if (action.equalsIgnoreCase("collect sun")) {
            String loc = cmd.getArg("-l");
            if (loc == null) {
                return "Usage: collect sun -l (<x>, <y>)";
            }
            try {
                loc = loc.replace("(", "").replace(")", "");
                String[] coords = loc.split(",");
                int x = Integer.parseInt(coords[0].trim());
                int y = Integer.parseInt(coords[1].trim());
                return gameController.collectSun(x, y);
            } catch (Exception e) {
                return "Invalid coordinates format!";
            }
        }
        if (action.equalsIgnoreCase("show sun amount")) {
            return "Current sun amount: " + game.getSunCount();
        }
        if (action.equalsIgnoreCase("show plants status")) {
            if (game.getActivePlants().isEmpty()) {
                return "No plants currently active on the field.";
            }
            StringBuilder sb = new StringBuilder();
            for (Plant p : game.getActivePlants()) {
                sb.append("- ").append(p.getName()).append(" at (").append(p.getX()).append(", ").append(p.getY())
                        .append(") | HP: ").append(p.getHealth()).append("/").append(p.getMaxHealth())
                        .append(" | Can Produce Sun: ").append(p.getSunProduce() > 0).append("\n");
            }
            return sb.toString().trim();
        }
        if (action.equalsIgnoreCase("show tile status")) {
            String loc = cmd.getArg("-l");
            if (loc == null) {
                return "Usage: show tile status -l (<x>, <y>)";
            }
            try {
                loc = loc.replace("(", "").replace(")", "");
                String[] coords = loc.split(",");
                int x = Integer.parseInt(coords[0].trim());
                int y = Integer.parseInt(coords[1].trim());
                Tile tile = game.getBoard().getTile(y, x);
                if (tile != null) {
                    return "Tile (" + x + ", " + y + ") Status:\n" +
                            "- Type: " + tile.getType() + "\n" +
                            "- Plant: " + (tile.getPlant() != null ? tile.getPlant().getName() : "None") + "\n" +
                            "- Zombie: " + (tile.getZombie() != null ? tile.getZombie().getName() + " (HP: " + tile.getZombie().getHealth() + ")" : "None");
                }
                return "Error: Tile coordinates out of bounds.";
            } catch (Exception e) {
                return "Invalid format! Use show tile status -l (<x>, <y>)";
            }
        }
        if (input.toLowerCase().startsWith("release the nuke")) {
            return gameController.executeNuke();
        }
        if (action.equalsIgnoreCase("cheat remove-cooldown")) {
            return gameController.executeRemoveCooldownCheat();
        }
        if (action.equalsIgnoreCase("cheat add-plant-food")) {
            return gameController.executeAddPlantFoodCheat();
        }
        if (action.equalsIgnoreCase("cheat add")) {
            if (cmd.hasFlag("-n")) {
                try {
                    String countStr = cmd.getArg("-n");
                    int amount = Integer.parseInt(countStr.split(" ")[0]);
                    return gameController.addCheatSuns(amount);
                } catch (Exception e) {
                    return "Invalid cheat format.";
                }
            } else {
                String valueStr = cmd.getArg("VALUE");
                if (valueStr != null && valueStr.toLowerCase().contains("sun")) {
                    try {
                        int amount = Integer.parseInt(valueStr.toLowerCase().replace("suns", "").trim());
                        return gameController.addCheatSuns(amount);
                    } catch (Exception e) {
                        return "Invalid cheat format.";
                    }
                }
            }
        }
        if (action.equalsIgnoreCase("cheat spawn-zombie")) {
            String type = cmd.getArg("-t");
            String loc = cmd.getArg("-l");
            if (type == null || loc == null) {
                return "Usage: cheat spawn-zombie -t <type> -l (<x>, <y>)";
            }
            try {
                loc = loc.replace("(", "").replace(")", "");
                String[] coords = loc.split(",");
                int x = Integer.parseInt(coords[0].trim());
                int y = Integer.parseInt(coords[1].trim());
                String formattedType = type.equalsIgnoreCase("normalzombie") ? "NormalZombie" : type;
                Zombie z = ZombieFactory.createZombieAtColumn(formattedType, y, x);
                if (z != null) {
                    game.addZombie(z);
                    return "Zombie spawned via cheat.";
                }
                return "Invalid zombie type.";
            } catch (Exception e) {
                return "Invalid format! Use: cheat spawn-zombie -t <type> -l (<x>, <y>)";
            }
        }

        return "Unknown game command. Type 'exit' to return to menu.";
    }
}