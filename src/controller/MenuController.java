package controller;

import model.User;
import model.enums.Gender;
import util.FileManager;
import util.HashUtil;
import view.TerminalView;

import java.util.List;
import java.util.Map;

public class MenuController {

    private final CommandParser parser = new CommandParser();
    private final TerminalView view = new TerminalView();

    // متدی که ورودی را از کاربر (یا از منو) دریافت می‌کند
    public String processRegister(Map<String, String> args) {
        Validator validator = new Validator();
        Validator.ValidationResult res;

        // چک کردن یوزرنیم
        res = validator.validateUsername(args.get("-u"));
        if (res != Validator.ValidationResult.VALID) {
            view.showUsernameError(res);
            return res.name();
        }

        if (FileManager.isUsernameExists(args.get("-u"))) {
            view.showUsernameExistsError();
            return "USERNAME_EXISTS";
        }

        // چک کردن ایمیل
        res = validator.validateEmail(args.get("-e"));
        if (res != Validator.ValidationResult.VALID) {
            view.showEmailError(res);
            return res.name();
        }

        // چک کردن پسورد
        res = validator.validatePassword(args.get("-p"), args.get("-pc"));
        if (res != Validator.ValidationResult.VALID) {
            view.showPasswordError(res);
            return res.name();
        }

        // چک کردن نیک‌نیم
        res = validator.validateNickname(args.get("-n"));
        if (res != Validator.ValidationResult.VALID) {
            view.showInvalidDisplayNameError();
            return res.name();
        }
        // چک جنسیت
        res = validator.validateGender(args.get("-g"));
        if (res != Validator.ValidationResult.VALID) {
            view.showInvalidGenderError();
            return res.name();
        }

        String hashedPassword = HashUtil.sha256(args.get("-p"));
        User newUser = new User(
                args.get("-u"),
                hashedPassword,
                args.get("-n"),
                args.get("-e"),
                Gender.valueOf(args.get("-g").toUpperCase())
        );

        List<User> users = FileManager.loadUsers();

        users.add(newUser);

        FileManager.saveUsers(users);

        return "SUCCESS";
    }
}