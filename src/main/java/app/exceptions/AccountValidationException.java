package app.exceptions;

public class AccountValidationException extends Exception {

    public AccountValidationException(String userMessage) {
        super(userMessage);
    }
}