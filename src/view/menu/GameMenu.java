package view.menu;

import controller.MenuController;

public class GameMenu extends Menu {

    public GameMenu(MenuController controller) {
        super(controller);
    }

    @Override
    public void run() {
        // فعلاً برای رفع ارور کامپایل، این متد خالی است
        view.showMessage("Login Menu is under construction.");
    }
}

