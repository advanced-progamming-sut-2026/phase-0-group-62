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

    public TravelLogMenu(MenuController controller) {
        super(controller);
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
            String action = cmd.getAction();

            if (action.equalsIgnoreCase("travel log page")) {
                String pageName = input.substring("travel log page".length()).trim().toLowerCase();
                if (pageName.isEmpty()) {
                    view.showMessage("Error: Please specify a page name (story, epic, daily, minigame).");
                    continue;
                }

                User currentUser = UserSession.getCurrentUser();
                if (currentUser == null) {
                    view.showMessage("Error: No user logged in.");
                    continue;
                }

                List<Quest> currentQuests = currentUser.getUserQuests();

                if (pageName.equals("story")) {
                    view.showMessage("=== STORY QUESTS ===");
                    displayQuests(currentQuests, Quest.QuestType.STORY);
                } else if (pageName.equals("epic")) {
                    view.showMessage("=== EPIC QUESTS ===");
                    displayQuests(currentQuests, Quest.QuestType.EPIC);
                } else if (pageName.equals("daily")) {
                    view.showMessage("=== DAILY QUESTS ===");
                    displayQuests(currentQuests, Quest.QuestType.DAILY);
                } else if (pageName.equals("minigame")) {
                    view.showMessage("=== MINI-GAMES PAGE ===");
                    view.showMessage("- Slot Machine [AVAILABLE]");
                    view.showMessage("- Zombiquarium [LOCKED]");
                    view.showMessage("- Vasebreaker [LOCKED]");
                } else {
                    view.showMessage("Error: Unknown travel log page.");
                }
            } else {
                view.showMessage("Unknown command inside Travel Log Menu.");
            }
        }
    }

    private void displayQuests(List<Quest> list, Quest.QuestType filterType) {
        List<Quest> filtered = new ArrayList<>();
        for (Quest q : list) {
            if (q.getType() == filterType) {
                filtered.add(q);
            }
        }

        if (filtered.isEmpty()) {
            view.showMessage("No quests available in this category.");
            return;
        }

        for (Quest q : filtered) {
            String statusStr = "[" + q.getStatus() + "]";
            int progress = q.getProgress();
            int target = q.getTarget();
            view.showMessage(String.format("- %s : %s | Priority: %s | Progress: %d/%d %s",
                    q.getTitle(), q.getDescription(), q.getPriority(), progress, target, statusStr));
        }
    }
}