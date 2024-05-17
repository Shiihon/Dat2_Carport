package app.services;

import app.exceptions.AccountValidationException;

import java.util.regex.Pattern;

public class AccountValidator {

    // Email pattern to check for a @ in a string
    private static final Pattern emailPattern = Pattern.compile("^(.+)@(\\S+)$");

    /**
     * Validate a string and check that it is not null or empty, and it contains
     * both a firstname and lastname separated by a space
     *
     * @param fullName The full name containing the firstname and lastname separated by space
     * @return A string array containing the firstname on index 0 and the lastname on index 1
     * @throws AccountValidationException If the provided full name violates any of the validation checks
     */
    public static String[] validateFullName(String fullName) throws AccountValidationException {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new AccountValidationException("Full name is empty");
        }

        String[] nameParts = fullName.split(" ");

        if (nameParts.length < 2) {
            throw new AccountValidationException("Full name must contain both firstname and lastname");
        }

        return nameParts;
    }

    /**
     * Validate an input address to ensure that it is not null or empty
     *
     * @param address The address to validate
     * @return The validated address
     * @throws AccountValidationException If the provided address violates any of the validation checks
     */
    public static String validateAddress(String address) throws AccountValidationException {
        if (address == null || address.trim().isEmpty()) {
            throw new AccountValidationException("Address is empty");
        }

        return address;
    }

    /**
     * Validate a zipcode string to ensure it is not null or empty and that it is an integer
     *
     * @param zipStr The zipcode string to be validated
     * @return The validated zip code
     * @throws AccountValidationException If the provided zipcode string violates any of the validation checks
     */
    public static int validateZip(String zipStr) throws AccountValidationException {
        if (zipStr == null || zipStr.trim().isEmpty()) {
            throw new AccountValidationException("Zip is empty");
        }

        try {
            return Integer.parseInt(zipStr);
        } catch (NumberFormatException e) {
            throw new AccountValidationException("Invalid zip code");
        }
    }

    /**
     * Validate a phone number to ensure it is not null or empty
     *
     * @param phoneNumber The phone number to be validated
     * @return The validated phone number
     * @throws AccountValidationException If the provided phone number violates any of the validation checks
     */
    public static String validatePhoneNumber(String phoneNumber) throws AccountValidationException {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new AccountValidationException("Phone number is empty");
        }

        return phoneNumber;
    }

    /**
     * Validate an email string to ensure it is not null or empty and that it contains the @ character
     *
     * @param email The email to be validated
     * @return The validated email
     * @throws AccountValidationException If the provided email violates any of the validation checks
     */
    public static String validateEmail(String email) throws AccountValidationException {
        if (email == null || email.trim().isEmpty()) {
            throw new AccountValidationException("Email is empty");
        }
        if (!emailPattern.matcher(email).matches()) {
            throw new AccountValidationException("Invalid email");
        }

        return email;
    }

    /**
     * Checks that the first password is not null or empty, and it is a certain minimum length. Then check
     * that the first password matches the second
     *
     * @param password1 The first password
     * @param password2 The second password
     * @return The validated password
     * @throws AccountValidationException If password1 and or password2 violates any of the validation checks
     */
    public static String validatePassword(String password1, String password2) throws AccountValidationException {
        if (password1 == null || password1.trim().isEmpty()) {
            throw new AccountValidationException("Password is empty");
        }
        if (password1.length() < 4) {
            throw new AccountValidationException("Password must be at least 4 characters");
        }
        if (password1.length() > 50) {
            throw new AccountValidationException("Password can't be more than 50 characters");
        }
        if (!password1.equals(password2)) {
            throw new AccountValidationException("Passwords do not match");
        }

        return password1;
    }
}
