package controller.menu;

import model.User;
import model.UserSession;
import model.entities.plant.Plant;
import model.entities.plant.loader.PlantLoader;
import util.FileManager;
import util.ParsedCommand;
import java.util.ArrayList;
import java.util.List;

public class PreGameController {
    public static String activeChapterName = null;
    private final List<String> selectedPlants = new ArrayList<>();
    private final List<String> boostedPlants = new ArrayList<>();
    private final int maxSlots = 8;

    public String processCommand(ParsedCommand cmd, String action) {
        User currentUser = UserSession.getCurrentUser();
        if (currentUser == null) {
            return "Error: No user is logged in.";
        }

        if (action.equalsIgnoreCase("menu enter chapter")) {
            String chapter = cmd.getArg("-c");
            if (chapter == null) {
                return "Error: Please specify a chapter using -c <chaptername>";
            }
            if (chapter.equalsIgnoreCase("AncientEgypt") || chapter.equalsIgnoreCase("Ancient Egypt") || chapter.equalsIgnoreCase("Egypt")) {
                activeChapterName = "AncientEgypt";
                return "Successfully entered chapter: AncientEgypt";
            } else if (chapter.equalsIgnoreCase("FrostbiteCaves") || chapter.equalsIgnoreCase("Frostbite Caves")) {
                activeChapterName = "FrostbiteCaves";
                return "Successfully entered chapter: FrostbiteCaves";
            } else if (chapter.equalsIgnoreCase("BigWaveBeach") || chapter.equalsIgnoreCase("Big Wave Beach")) {
                activeChapterName = "BigWaveBeach";
                return "Successfully entered chapter: BigWaveBeach";
            } else if (chapter.equalsIgnoreCase("DarkAges") || chapter.equalsIgnoreCase("Dark Ages")) {
                activeChapterName = "DarkAges";
                return "Successfully entered chapter: DarkAges";
            } else {
                return "Error: Unknown chapter name.";
            }
        }

        if (action.equalsIgnoreCase("show all plants")) {
            List<Plant> allPlants = PlantLoader.loadPlants();
            StringBuilder sb = new StringBuilder("All game plants:\n");
            for (Plant p : allPlants) {
                sb.append("- ").append(p.getName()).append("\n");
            }
            return sb.toString().trim();
        }

        if (action.equalsIgnoreCase("show available plants")) {
            List<String> unlocked = currentUser.getUnlockedPlants();
            if (unlocked.isEmpty()) {
                return "You have no unlocked plants.";
            }
            StringBuilder sb = new StringBuilder("Your available plants:\n");
            for (String plant : unlocked) {
                int level = currentUser.getPlantLevels().getOrDefault(plant, 1);
                sb.append("- ").append(plant).append(" (Level ").append(level).append(")");
                if (boostedPlants.contains(plant)) {
                    sb.append(" [BOOSTED]");
                }
                sb.append("\n");
            }
            return sb.toString().trim();
        }

        if (action.equalsIgnoreCase("add plant")) {
            String plantName = cmd.getArg("-t");
            if (plantName == null) return "Invalid format. Use: add plant -t <type>";

            boolean existsInGame = PlantLoader.loadPlants().stream()
                    .anyMatch(p -> p.getName().equalsIgnoreCase(plantName));
            if (!existsInGame) {
                return "Error: Plant type does not exist in the game.";
            }

            String exactPlantName = findExactPlantName(currentUser.getUnlockedPlants(), plantName);
            if (exactPlantName == null) {
                return "Error: This plant is locked! Purchase it from the collection menu.";
            }

            if (selectedPlants.contains(exactPlantName)) {
                return "Error: " + exactPlantName + " is already selected.";
            }

            if (selectedPlants.size() >= maxSlots) {
                return "Error: Your selection slots are full (Max " + maxSlots + ").";
            }

            selectedPlants.add(exactPlantName);
            return "Plant " + exactPlantName + " added. (" + selectedPlants.size() + "/" + maxSlots + ")";
        }

        if (action.equalsIgnoreCase("remove plant")) {
            String plantName = cmd.getArg("-t");
            if (plantName == null) return "Invalid format. Use: remove plant -t <type>";

            String exactPlantName = findExactPlantName(selectedPlants, plantName);
            if (exactPlantName == null) {
                return "Error: This plant is not in your selected list.";
            }

            selectedPlants.remove(exactPlantName);
            boostedPlants.remove(exactPlantName);
            return "Plant " + exactPlantName + " removed. (" + selectedPlants.size() + "/" + maxSlots + ")";
        }

        if (action.equalsIgnoreCase("boost plant")) {
            String plantName = cmd.getArg("-t");
            if (plantName == null) return "Invalid format. Use: boost plant -t <type>";

            String exactPlantName = findExactPlantName(currentUser.getUnlockedPlants(), plantName);
            if (exactPlantName == null) {
                return "Error: You can only boost plants you own.";
            }

            if (boostedPlants.contains(exactPlantName)) {
                return "Error: This plant is already boosted for this game.";
            }

            if (currentUser.getGems() < 2) {
                return "Error: Insufficient gems! Boost costs 2 gems. You have: " + currentUser.getGems();
            }

            currentUser.setGems(currentUser.getGems() - 2);
            FileManager.updateUser(currentUser);
            UserSession.setCurrentUser(currentUser);

            boostedPlants.add(exactPlantName);
            return "Plant " + exactPlantName + " boosted successfully! 2 gems deducted.";
        }

        if (action.equalsIgnoreCase("start game")) {
            if (activeChapterName == null) {
                return "Error: You must select a chapter first using 'menu enter chapter -c <chaptername>' before starting the game.";
            }
            if (selectedPlants.isEmpty()) {
                return "Error: You must select at least one plant to start the game.";
            }
            return "START_GAME_CONFIRMED";
        }

        return "invalid command";
    }

    private String findExactPlantName(List<String> list, String searchName) {
        for (String s : list) {
            if (s.equalsIgnoreCase(searchName)) {
                return s;
            }
        }
        return null;
    }

    public List<String> getSelectedPlants() { return selectedPlants; }
    public List<String> getBoostedPlants() { return boostedPlants; }
}