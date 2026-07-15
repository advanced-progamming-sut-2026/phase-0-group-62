package view.menu;

import controller.menu.CommandParser;
import controller.menu.MenuController;
import model.User;
import model.leaderboard.Leaderboard;
import util.ParsedCommand;
import util.FileManager;

import java.util.List;

public class LeaderboardMenu extends Menu {
    public LeaderboardMenu(MenuController controller) {
        super(controller);
    }

    @Override
    public void runMenu() {
        CommandParser parser = new CommandParser();
        while (true) {
            String input = view.getInput("LeaderboardMenu");
            if (input.equalsIgnoreCase("back")) {
                manager.setCurrentMenu(new PlayMenu(controller));
                break;
            }

            ParsedCommand cmd = parser.parse(input);
            if (cmd.getAction().equalsIgnoreCase("menu leaderboard")) {
                List<User> users = FileManager.loadUsers();
                Leaderboard leaderboard = new Leaderboard();
                for (User u : users) {
                    leaderboard.addUser(u);
                }

                String mode = cmd.getArg("-s");
                if (mode == null) {
                    leaderboard.sortUsers(Leaderboard.SortType.BY_SCORE);
                } else if (mode.equalsIgnoreCase("level")) {
                    leaderboard.sortUsers(Leaderboard.SortType.BY_LEVEL);
                } else if (mode.equalsIgnoreCase("minigame")) {
                    leaderboard.sortUsers(Leaderboard.SortType.BY_MINI_GAMES);
                } else if (mode.equalsIgnoreCase("quest")) {
                    leaderboard.sortUsers(Leaderboard.SortType.BY_QUESTS);
                } else {
                    leaderboard.sortUsers(Leaderboard.SortType.BY_SCORE);
                }

                List<User> topTen = leaderboard.getTopUsers(10);
                view.showMessage("=== LEADERBOARD ===");
                int rank = 1;
                for (User u : topTen) {
                    view.showMessage(rank + ". " + u.getUsername() + " | Score: " + u.getScore());
                    rank++;
                }
            } else {
                view.showMessage("Unknown command in Leaderboard Menu.");
            }
        }
    }
}