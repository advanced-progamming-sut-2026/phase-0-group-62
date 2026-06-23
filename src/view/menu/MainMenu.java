package view.menu;

import controller.MenuController;

public class MainMenu extends Menu {

    public MainMenu(MenuController controller) {
        super(controller);
    }

    @Override
    public void run() {
        // متد خالی برای رفع ارور کامپایل
        view.showMessage("Main Menu is under construction.");
    }
}