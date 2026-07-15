package view.menu;

import controller.menu.CommandParser;
import controller.menu.MenuController;
import model.User;
import model.UserSession;
import model.greenhouse.Greenhouse;
import model.greenhouse.Pot;
import model.entities.plant.factory.PlantFactory;
import model.entities.plant.Plant;
import util.ParsedCommand;
import util.FileManager;

public class GreenhouseMenu extends Menu {
    private Greenhouse greenhouse;

    public GreenhouseMenu(MenuController controller) {
        super(controller);
        this.greenhouse = new Greenhouse();
    }

    @Override
    public void runMenu() {
        CommandParser parser = new CommandParser();
        while (true) {
            String input = view.getInput("GreenhouseMenu");
            if (input.equalsIgnoreCase("back")) {
                manager.setCurrentMenu(new PlayMenu(controller));
                break;
            }

            ParsedCommand cmd = parser.parse(input);
            User currentUser = UserSession.getCurrentUser();

            if (cmd.getAction().equalsIgnoreCase("show greenhouse")) {
                view.showMessage("=== YOUR GREENHOUSE ===");
                greenhouse.updateAllPots();
                int index = 0;
                for (Pot pot : greenhouse.getPots()) {
                    String status = pot.isLocked() ? "[LOCKED]" : pot.isEmpty() ? "[EMPTY]" : pot.isReadyToHarvest() ? "[READY]" : "[GROWING: " + (int)(pot.getGrowthProgress() * 100) + "%]";
                    view.showMessage("Pot " + index + " (Row " + pot.getRow() + ", Col " + pot.getColumn() + "): " + status);
                    index++;
                }
            }
            else if (cmd.getAction().equalsIgnoreCase("grow")) {
                String plantType = cmd.getArg("-t");
                String idStr = cmd.getArg("-i");
                if (plantType == null || idStr == null) {
                    view.showMessage("Usage: grow -t <plant_type> -i <pot_index>");
                    continue;
                }
                try {
                    int potIndex = Integer.parseInt(idStr);
                    int r = potIndex / 5;
                    int c = potIndex % 5;
                    Pot pot = greenhouse.getPot(r, c);

                    if (pot == null || pot.isLocked()) {
                        view.showMessage("Error: This pot is locked or invalid.");
                        continue;
                    }
                    if (!pot.isEmpty()) {
                        view.showMessage("Error: This pot is already occupied.");
                        continue;
                    }

                    Plant p = PlantFactory.createPlant(plantType);
                    if (p == null) {
                        view.showMessage("Error: Invalid plant type.");
                        continue;
                    }

                    pot.plant(p);
                    view.showMessage("Successfully planted " + plantType + " in pot " + potIndex);
                } catch (Exception e) {
                    view.showMessage("Invalid format.");
                }
            }
            else if (cmd.getAction().equalsIgnoreCase("collect")) {
                String idStr = cmd.getArg("-i");
                if (idStr == null) {
                    view.showMessage("Usage: collect -i <pot_index>");
                    continue;
                }
                try {
                    int potIndex = Integer.parseInt(idStr);
                    int r = potIndex / 5;
                    int c = potIndex % 5;
                    Pot pot = greenhouse.getPot(r, c);

                    if (pot == null || !pot.isReadyToHarvest()) {
                        view.showMessage("Error: Plant is not ready to harvest.");
                        continue;
                    }

                    if (pot.getPlant().getName().equalsIgnoreCase("Marigold")) {
                        currentUser.setCoins(currentUser.getCoins() + 500);
                        view.showMessage("Harvested Marigold! Received 500 coins.");
                    } else {
                        view.showMessage("Harvested " + pot.getPlant().getName() + "! Permanent boost activated.");
                    }
                    pot.harvest();
                    FileManager.updateUser(currentUser);
                } catch (Exception e) {
                    view.showMessage("Invalid format.");
                }
            }
            else {
                view.showMessage("Unknown command in Greenhouse Menu.");
            }
        }
    }
}