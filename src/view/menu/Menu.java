package view.menu;

import controller.MenuController;
import view.TerminalView;

public abstract class Menu {
    protected MenuController controller;
    protected TerminalView view;
    protected MenuManager manager;

    public Menu(MenuController controller) {
        this.controller = controller;
        this.view = new TerminalView();
        this.manager = MenuManager.getInstance();
    }

    public abstract void run();

    protected void printMenuHeader(String title) {
        view.showMessage("--- " + title + " ---");
    }
}