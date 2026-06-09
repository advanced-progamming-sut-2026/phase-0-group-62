package controller;

import model.Model;
import view.TerminalView;
import view.menu.Menu;
import view.menu.RegisterMenu;

public class MenuController {
    private final Model model;
    private final TerminalView view;
    private Menu currentMenu;

    public MenuController(Model model, TerminalView view) {
        this.model = model;
        this.view = view;
        this.currentMenu = new RegisterMenu();
    }

    public void showCurrentMenu() {
        view.showMessage("Current menu: " + currentMenu.getName());
    }

    public Model getModel() {
        return model;
    }
}

