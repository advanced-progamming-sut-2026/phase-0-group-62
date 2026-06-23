package view.menu;

import controller.MenuController;

public class SettingsMenu extends Menu {

    public SettingsMenu(MenuController controller) {
        super(controller);
    }

    @Override
    public void run() {
        view.showMessage("Settings Menu is under construction.");
    }
}