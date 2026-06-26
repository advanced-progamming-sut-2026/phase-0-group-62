package controller;

import model.SecurityQuestions;
import model.Settings;
import model.User;
import model.UserSession;
import model.enums.Gender;
import util.FileManager;
import util.HashUtil;
import util.ParsedCommand;
import view.TerminalView;

import java.util.List;

import static util.FileManager.checkPassword;
import static util.FileManager.isUsernameExists;

public class MenuController {
    private final TerminalView view = new TerminalView();
    private String currentForgetPasswordUsername;

    public String processRegister(ParsedCommand cmd) {
        Validator validator = new Validator();
        Validator.ValidationResult res;
        boolean hasError = false;

        res = validator.validateUsername(cmd.getArg("-u"));
        if (res != Validator.ValidationResult.VALID) {
            view.showUsernameError(res);
            hasError = true;
        }

        if (isUsernameExists(cmd.getArg("-u"))) {
            view.showUsernameExistsError();
            hasError = true;
        }

        res = validator.validateEmail(cmd.getArg("-e"));
        if (res != Validator.ValidationResult.VALID) {
            view.showEmailError(res);
            hasError = true;
        }

        String passwordArg = cmd.getArg("-p");
        String password = null;
        String passwordConfirm = null;

        if (passwordArg != null && passwordArg.contains(" ")) {
            String[] passwords = passwordArg.split(" ");
            password = passwords[0];
            passwordConfirm = passwords[1];
        } else if (passwordArg != null) {
            password = passwordArg;
        }

        res = validator.validatePassword(password, passwordConfirm);
        if (res != Validator.ValidationResult.VALID) {
            view.showPasswordError(res);
            hasError = true;
        }

        res = validator.validateNickname(cmd.getArg("-n"));
        if (res != Validator.ValidationResult.VALID) {
            view.showInvalidDisplayNameError();
            hasError = true;
        }

        res = validator.validateGender(cmd.getArg("-g"));
        if (res != Validator.ValidationResult.VALID) {
            view.showInvalidGenderError();
            hasError = true;
        }

        if (hasError) return "invalid";

        if (cmd.getArg("-q") != null && cmd.getArg("-a") != null && cmd.getArg("-c") != null) {
            if (!cmd.getArg("-a").equals(cmd.getArg("-c"))) {
                view.showMessage("Security answer confirmation does not match!");
                return "invalid";
            }


            User newUser = new User(
                    cmd.getArg("-u"),
                    HashUtil.sha256(password),
                    cmd.getArg("-n"),
                    cmd.getArg("-e"),
                    Gender.valueOf(cmd.getArg("-g").toUpperCase()),
                    cmd.getArg("-q"),
                    HashUtil.sha256(cmd.getArg("-a"))
            );

            List<User> users = FileManager.loadUsers();
            users.add(newUser);
            FileManager.saveUsers(users);

            return "SUCCESS";
        }

        List<String> questions = SecurityQuestions.getAll();
        view.showChoseQuestion(questions);
        return "VALID_STEP_1";
    }
    public String processLogin(ParsedCommand cmd) {
        if (!cmd.hasFlag("-u") || !cmd.hasFlag("-p")) {
            return "Invalid command format. Username and password are required.";
        }

        String username = cmd.getArg("-u");
        String password = cmd.getArg("-p");
        boolean stayLoggedIn = cmd.hasFlag("-stay-logged-in");
        boolean usernameIsUniq = !isUsernameExists(username);

        if (usernameIsUniq) {
            return "Username doesn't exist!";
        }

        boolean passwordIsTrue = checkPassword(username, HashUtil.sha256(password));

        if (!passwordIsTrue) {
            return "Password incorrect!";
        }

        User user = FileManager.getUser(username);
        UserSession.setCurrentUser(user);

        if (stayLoggedIn) {
            Settings settings = FileManager.loadSettings();
            settings.setAutoLoginUsername(user.getUsername());
            FileManager.saveSettings(settings);
        }

        return "Login successful!";
    }
    public String processForgetPassword(ParsedCommand cmd) {
        if (cmd.getAction().equals("forget password")) {
            if (!cmd.hasFlag("-u") || !cmd.hasFlag("-e")) {
                return "Invalid command format. Username and email are required.";
            }

            String username = cmd.getArg("-u");
            String email = cmd.getArg("-e");
            User user = FileManager.getUser(username);

            if (user == null) {
                return "Username doesn't exist!";
            }

            if (!user.getEmail().equalsIgnoreCase(email)) {
                return "Username and email doesn't match!";
            }

            currentForgetPasswordUsername = username;

            String questionNum = user.getSecurityQuestion();
            int questionIndex = Integer.parseInt(questionNum);
            String questionText = SecurityQuestions.getQuestionByIndex(questionIndex - 1);

            view.showSecurityQuestion(questionText);
            return "SUCCESS_username and email check";
        }

        if (cmd.getAction().equals("answer")) {
            if (!cmd.hasFlag("-a")) {
                return "Invalid command format. Answer is required.";
            }

            if (currentForgetPasswordUsername == null) {
                return "Please enter forget password command first!";
            }

            User user = FileManager.getUser(currentForgetPasswordUsername);
            if (user == null) {
                return "Username doesn't exist!";
            }

            String answer = user.getSecurityAnswer();
            String inputAnswer = cmd.getArg("-a");

            if (answer.equals(HashUtil.sha256(inputAnswer))) {
                return "SUCCESS_answer get";
            } else {
                return "Answer is incorrect!";
            }
        }

        if (cmd.getAction().equals("new password")) {
            if (!cmd.hasFlag("-p") || !cmd.hasFlag("-c")) {
                return "Invalid command format. Password and confirmation are required.";
            }

            if (currentForgetPasswordUsername == null) {
                return "Please verify your identity first!";
            }

            User user = FileManager.getUser(currentForgetPasswordUsername);
            if (user == null) {
                return "Username doesn't exist!";
            }

            Validator validator = new Validator();
            Validator.ValidationResult res = validator.validatePassword(cmd.getArg("-p"), cmd.getArg("-c"));
            if (res != Validator.ValidationResult.VALID) {
                view.showPasswordError(res);
                return "invalid password";
            }

            String newPassword = cmd.getArg("-p");
            String hashedPassword = HashUtil.sha256(newPassword);

            if (hashedPassword.equals(user.getPassword())) {
                return "Please enter a new password which is different from your current password.";
            }

            user.setPassword(hashedPassword);
            FileManager.updateUser(user);

            currentForgetPasswordUsername = null;
            return "SUCCESS_password changed";
        }

        return "invalid action";
    }
    public String processLogout(ParsedCommand cmd) {
        String username = model.UserSession.getCurrentUser().getUsername();

        model.UserSession.clear();

        model.Settings settings = FileManager.loadSettings();
        settings.setAutoLoginUsername(null);
        FileManager.saveSettings(settings);

        return "User " + username + " logged out successfully!";
    }
}


