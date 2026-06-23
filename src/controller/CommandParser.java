package controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandParser {

    private final List<String> validCommands = Arrays.asList(
            "register", "login", "forget password", "menu logout", "show menu"
    );

    public String getAction(String input) {
        String trimmedInput = input.trim().toLowerCase();

        for (String cmd : validCommands) {
            if (trimmedInput.startsWith(cmd)) {
                return cmd;
            }
        }
        return "unknown";
    }

    public Map<String, String> getRegisterArgs(String input) {
        Map<String, String> args = new HashMap<>();
        String[] parts = input.trim().split("\\s+");

        for (int i = 1; i < parts.length; i++) {
            if (parts[i].equals("-p") && i + 2 < parts.length) {
                args.put("-p", parts[i + 1]);
                args.put("-pc", parts[i + 2]);
                i += 2;
            } else if (parts[i].startsWith("-") && i + 1 < parts.length) {
                args.put(parts[i], parts[i + 1]);
                i++;
            }
        }
        return args;
    }
}