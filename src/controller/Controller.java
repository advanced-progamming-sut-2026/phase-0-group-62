package controller;

import model.Model;
import view.TerminalView;

public class Controller {
    private final Model model;
    private final TerminalView view;
    private final MenuController menuController;

    public Controller(Model model, TerminalView view) {
        this.model = model;
        this.view = view;
        this.menuController = new MenuController(model, view);
    }

    public void start() {
        view.showWelcome();
        menuController.showCurrentMenu();
    }
}

