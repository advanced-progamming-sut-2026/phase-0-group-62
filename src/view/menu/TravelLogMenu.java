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
    private final List<Quest> quests;

    public TravelLogMenu(MenuController controller) {
        super(controller);
        this.quests = new ArrayList<>();

        Quest story1 = new Quest("First Blood", "Kill 10 zombies in battlefield", Quest.QuestType.STORY);
        story1.setTarget(10);

        Quest epic1 = new Quest("Epic Defender", "Survive difficult waves", Quest.QuestType.EPIC);
        epic1.setTarget(5);
        epic1.setRewardDiamonds(5);

        Quest daily1 = new Quest("Sun Collector", "Collect 500 suns total", Quest.QuestType.DAILY);
        daily1.setTarget(500);
        daily1.setRewardCoins(200);

        this.quests.add(story1);
        this.quests.add(epic1);
        this.quests.add(daily1);
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

                if (pageName.equals("story")) {
                    view.showMessage("=== STORY QUESTS (PRIORITY: CRITICAL) ===");
                    displayQuests(Quest.QuestType.STORY);
                } else if (pageName.equals("epic")) {
                    view.showMessage("=== EPIC QUESTS (PRIORITY: HIGH) ===");
                    displayQuests(Quest.QuestType.EPIC);
                } else if (pageName.equals("daily")) {
                    view.showMessage("=== DAILY QUESTS (PRIORITY: MEDIUM/LOW) ===");
                    displayQuests(Quest.QuestType.DAILY);
                } else if (pageName.equals("minigame")) {
                    view.showMessage("=== MINI-GAMES PAGE ===");
                    view.showMessage("- Slot Machine [AVAILABLE]");
                    view.showMessage("- Zombiquarium [LOCKED]");
                    view.showMessage("- Vasebreaker [LOCKED]");
                } else {
                    view.showMessage("Error: Unknown travel log page. Available options: story, epic, daily, minigame.");
                }
            } else {
                view.showMessage("Unknown command inside Travel Log Menu. Available commands: 'travel log page <page_name>', 'back'.");
            }
        }
    }

    private void displayQuests(Quest.QuestType filterType) {
        List<Quest> filtered = new ArrayList<>();
        for (Quest q : this.quests) {
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
            view.showMessage(String.format("- %s : %s | Progress: %d/%d %s",
                    q.getTitle(), q.getDescription(), progress, target, statusStr));
        }
    }
}