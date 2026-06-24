package view.menu;

import controller.MenuController;
import view.TerminalView;

public abstract class Menu {
    protected MenuController controller;
    protected TerminalView view;


    public Menu(MenuController controller) {
        this.controller = controller;
        this.view = new TerminalView();
    }

    public abstract void run();

    protected void printMenuHeader(String title) {
        view.showMessage("--- " + title + " ---");
    }
}