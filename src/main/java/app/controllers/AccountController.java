package app.controllers;

import app.entities.Account;
import app.entities.Customer;
import app.entities.PostalCode;
import app.entities.Seller;
import app.exceptions.AccountValidationException;
import app.exceptions.DatabaseException;
import app.persistence.AccountMapper;
import app.persistence.ConnectionPool;
import app.persistence.PostalCodeMapper;
import app.services.AccountValidator;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Objects;

public class AccountController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/login", ctx -> ctx.render("login.html"));
        app.post("/login", ctx -> login(ctx, connectionPool));
        app.get("/create-account", ctx -> ctx.render("create-account.html"));
        app.post("/create-account", ctx -> createAccount(ctx, connectionPool));
        app.get("/logout", ctx -> logout(ctx));

        app.get("/get-city", ctx -> getCityByZip(ctx, connectionPool));
    }

    public static void getCityByZip(Context ctx, ConnectionPool connectionPool) {
        int zip = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("zip")));

        try {
            PostalCode postalCode = PostalCodeMapper.getPostalCodeByZip(zip, connectionPool);

            ctx.json(postalCode);
        } catch (DatabaseException ignored) {
        }
    }

    public static void createAccount(Context ctx, ConnectionPool connectionPool) {
        try {
            String name = Objects.requireNonNull(ctx.formParam("name"));
            String address = Objects.requireNonNull(ctx.formParam("address"));
            String zip = Objects.requireNonNull(ctx.formParam("postal_code"));
            String phoneNumber = Objects.requireNonNull(ctx.formParam("phone_number"));
            String email = Objects.requireNonNull(ctx.formParam("email"));
            String password1 = Objects.requireNonNull(ctx.formParam("password1"));
            String password2 = Objects.requireNonNull(ctx.formParam("password2"));
            String city = null;

            try {
                String[] validatedFullName = AccountValidator.validateFullName(name);
                String validatedAddress = AccountValidator.validateAddress(address);
                int validatedZip = AccountValidator.validateZip(zip);

                PostalCode postalCode = PostalCodeMapper.getPostalCodeByZip(validatedZip, connectionPool);
                city = postalCode.getCity();

                String validatedPhoneNumber = AccountValidator.validatePhoneNumber(phoneNumber);
                String validatedEmail = AccountValidator.validateEmail(email);
                String validatedPassword = AccountValidator.validatePassword(password1, password2);

                Account account = new Customer(validatedEmail, validatedPassword, "CUSTOMER", validatedFullName[0], validatedFullName[1], validatedAddress, postalCode.getZip(), postalCode.getCity(), validatedPhoneNumber);

                AccountMapper.createAccount(account, connectionPool);

                ctx.sessionAttribute("currentAccount", account);
                String loginRedirect = ctx.sessionAttribute("loginRedirect");

                if (loginRedirect != null) {
                    ctx.sessionAttribute("loginRedirect", null);
                    ctx.redirect(loginRedirect);
                } else {
                    ctx.redirect("/");
                }
            } catch (DatabaseException | AccountValidationException e) {
                ctx.attribute("createAccountName", name);
                ctx.attribute("createAccountAddress", address);
                ctx.attribute("createAccountZip", zip);
                ctx.attribute("createAccountCity", city);
                ctx.attribute("createAccountPhoneNumber", phoneNumber);
                ctx.attribute("createAccountEmail", email);

                ctx.attribute("error", e.getMessage());
                ctx.render("create-account.html");
            }
        } catch (NullPointerException ignored) {
            ctx.redirect("/create-account");
        }
    }

    public static void login(Context ctx, ConnectionPool connectionPool) {
        try {
            String email = Objects.requireNonNull(ctx.formParam("email"));
            String password = Objects.requireNonNull(ctx.formParam("password"));

            try {
                String validatedEmail = AccountValidator.validateEmail(email);

                Account account = AccountMapper.login(validatedEmail, password, connectionPool);

                ctx.sessionAttribute("currentAccount", account);

                if (account instanceof Seller) {
                    ctx.redirect("/requests");
                } else {
                    String loginRedirect = ctx.sessionAttribute("loginRedirect");

                    if (loginRedirect != null) {
                        ctx.redirect(loginRedirect);
                        ctx.sessionAttribute("loginRedirect", null);
                    } else {
                        ctx.redirect("/");
                    }
                }
            } catch (DatabaseException | AccountValidationException e) {
                ctx.attribute("loginEmail", email);

                ctx.attribute("error", e.getMessage());
                ctx.render("login.html");
            }
        } catch (NullPointerException ignored) {
            ctx.redirect("/login");
        }
    }

    public static void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }
}
