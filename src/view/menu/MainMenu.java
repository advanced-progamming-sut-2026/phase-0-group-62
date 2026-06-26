package view.menu;

import controller.CommandParser;
import controller.MenuController;
import util.ParsedCommand;

public class MainMenu extends Menu {

    public MainMenu(MenuController controller) {
        super(controller);
    }

    @Override
    public void run() {
        CommandParser parser = new CommandParser();
        MenuController ctrl = (MenuController) this.controller;
        while (true) {
            String input = view.getInput("main menu");
            ParsedCommand cmd = parser.parse(input);

            if (cmd.getAction().equals("menu logout")) {
                String result = ctrl.processLogout(cmd);
                view.showLogoutResult(result);

                MenuManager.getInstance().setCurrentMenu(new LoginMenu(ctrl));
                break;
            }
        }
    }
}