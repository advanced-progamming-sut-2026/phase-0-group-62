package controller;

public class CommandParser {
    public String[] parse(String command) {
        if (command == null || command.isBlank()) {
            return new String[0];
        }
        return command.trim().split("\\s+");
    }
}

