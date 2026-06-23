package view.menu;

import controller.MenuController;

public class NewsMenu extends Menu {

    public NewsMenu(MenuController controller) {
        super(controller);
    }

    @Override
    public void run() {
        view.showMessage("News Menu is under construction.");
    }
}