package view.menu;

import controller.MenuController;
import controller.CommandParser;
import java.util.Map;

public class RegisterMenu extends Menu {

    public RegisterMenu(MenuController controller) {
        super(controller);
    }

    @Override
    public void run() {
        // کست کردن برای دسترسی به کنترلر خاص
        MenuController ctrl = (MenuController) this.controller;
        CommandParser parser = new CommandParser(); // پارسر برای تبدیل دستور به Map


        while (true) {
            String input = view.getInput("Register");

            if (input.equalsIgnoreCase("back")) {
                break;
            }


            Map<String, String> args = parser.getRegisterArgs(input);


            String result = ctrl.processRegister(args);

            if (result.equals("SUCCESS")) {
                view.showMessage("Registration successful!");
                break;
            } else {
                view.showMessage("Please try again or type 'back' to return.");
            }
        }
    }
}