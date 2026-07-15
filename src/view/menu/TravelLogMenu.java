package view.menu;

import controller.menu.CommandParser;
import controller.menu.MenuController;
import model.User;
import model.UserSession;
import model.quest.Quest;
import util.ParsedCommand;

import java.util.ArrayList;
import java.util.List;

public class TravelLogMenu extends Menu {
    private List<Quest> quests;

    public TravelLogMenu(MenuController controller) {
        super(controller);
        this.quests = new ArrayList<>();
        this.quests.add(new Quest("First Blood", "Kill 10 zombies in battlefield", Quest.QuestType.STORY));
        this.quests.add(new Quest("Sun Collector", "Collect 500 suns total", Quest.QuestType.DAILY));
    }

    @Override
    public void runMenu() {
        CommandParser parser = new CommandParser();
        while (true) {
            String input = view.getInput("TravelLogMenu");
            if (input.equalsIgnoreCase("back")) {
                manager.setCurrentMenu(new PlayMenu(controller));
                break;
            }

            ParsedCommand cmd = parser.parse(input);
            if (cmd.getAction().equalsIgnoreCase("travel log page")) {
                view.showMessage("=== TRAVEL LOG QUESTS ===");
                for (Quest q : quests) {
                    view.showMessage("- [" + q.getType() + "] " + q.getTitle() + " : " + q.getDescription() + " | Status: " + q.getStatus());
                }
            } else {
                view.showMessage("Unknown command inside Travel Log Menu.");
            }
        }
    }
}