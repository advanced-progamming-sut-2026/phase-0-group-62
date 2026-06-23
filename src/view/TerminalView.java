package view;

import controller.Validator;

public class TerminalView extends View {


    public void showEmptyFieldMessage(String elemt){
        showMessage("you should enter" + elemt);
    }
    //userneme
    public void showUsernameAlreadyExistsError() {
        showMessage("Username is already taken.");
    }
    public void showUsernameError(Validator.ValidationResult result) {
        String message = switch (result) {
            case INVALID_FORMAT -> "Username can only contain letters, numbers, and hyphens.";
            case INVALID_LENGTH -> "Username must be between 3 and 15 characters.";
            default -> "Invalid username.";

        };
        showMessage(message);
    }
    //password
    public void showPasswordError(Validator.ValidationResult result) {
        String message = switch (result) {
            case INVALID_LENGTH -> "Password must be at least 8 characters long.";
            case PASSWORD_MISMATCH -> "Password and confirmation do not match.";
            case WEAK_PASSWORD_NO_UPPER -> "Password must contain at least one uppercase letter.";
            case WEAK_PASSWORD_NO_LOWER -> "Password must contain at least one lowercase letter.";
            case WEAK_PASSWORD_NO_DIGIT -> "Password must contain at least one digit.";
            case WEAK_PASSWORD_NO_SPECIAL -> "Password must contain at least one special character.";
            default -> "Invalid password.";
        };
        showMessage(message);
    }


    public void showInvalidDisplayNameError(){
        showMessage("Nickname must be between 3 and 30 characters.");
    }

    public void showInvalidGenderError(){
        showMessage("Gender must be male or female.");
    }
    //email
    public void showEmailError(Validator.ValidationResult result) {
        String message = switch (result) {
            case INVALID_EMAIL_FORMAT -> "Invalid email format. Please check for @, dots, and valid domain.";
            default -> "Invalid email.";
        };
        showMessage(message);
    }



}

