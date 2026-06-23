package view.menu;

import controller.MenuController;
import view.TerminalView;
import java.util.HashMap;
import java.util.Map;

public class RegisterMenu extends Menu {
    private Map<String, String> temporaryStorage = new HashMap<>();

    // سازنده: چون controller در کلاس مادر تعریف شده، از super استفاده می‌کنیم
    public RegisterMenu(MenuController controller) {
        super(controller);
    }

    @Override
    public void run() {
        handleRegister();
    }

    public void handleRegister() {
        view.showMessage("Registration started. You can enter fields individually or all at once.");

        while (true) {
            String input = view.getInput("Register");
            if (input.equalsIgnoreCase("back")) break;

            temporaryStorage.putAll(controller.getParser().getRegisterArgs(input));

            if (areAllFieldsPresent()) {
                String result = controller.processRegister(temporaryStorage);
                if (result.equals("SUCCESS")) {
                    view.showMessage("Registration successful!");
                    temporaryStorage.clear();
                    break;
                } else {
                    view.showMessage("Error: " + result);
                }
            } else {
                view.showMessage("Missing fields: " + getMissingFieldsList());
            }
        }
    }

    private boolean areAllFieldsPresent() {
        return temporaryStorage.containsKey("-u") && temporaryStorage.containsKey("-p") &&
                temporaryStorage.containsKey("-pc") && temporaryStorage.containsKey("-n") &&
                temporaryStorage.containsKey("-e");
    }

    private String getMissingFieldsList() {
        StringBuilder sb = new StringBuilder();
        if (!temporaryStorage.containsKey("-u")) sb.append("-u ");
        if (!temporaryStorage.containsKey("-p")) sb.append("-p ");
        if (!temporaryStorage.containsKey("-pc")) sb.append("-pc ");
        if (!temporaryStorage.containsKey("-n")) sb.append("-n ");
        if (!temporaryStorage.containsKey("-e")) sb.append("-e ");
        return sb.toString();
    }


}