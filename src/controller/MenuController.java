package controller;

import view.TerminalView;
import java.util.Map;

public class MenuController {

    CommandParser parser = new CommandParser();
    TerminalView view = new TerminalView();

    public CommandParser getParser() {
        return parser;
    }

    public String processRegister(Map<String, String> args) {
        Validator validator = new Validator();

        Validator.ValidationResult res = validator.validateUsername(args.get("-u"));
        if (res != Validator.ValidationResult.VALID) {
            return res.name();
        }

        res = validator.validateEmail(args.get("-e"));
        if (res != Validator.ValidationResult.VALID) {
            return res.name();
        }

        res = validator.validatePassword(args.get("-p"), args.get("-pc"));
        if (res != Validator.ValidationResult.VALID) {
            return res.name();
        }

        res = validator.validateNickname(args.get("-n"));
        if (res != Validator.ValidationResult.VALID) {
            return res.name();
        }

        return "SUCCESS";
    }
}