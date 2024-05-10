package app.controllers;

import app.entities.Account;
import app.entities.Customer;
import app.exceptions.DatabaseException;
import app.persistence.AccountMapper;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;


public class AccountController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/login", ctx -> ctx.render("login.html"));
        app.post("/login", ctx -> login(ctx, connectionPool));
        app.get("/create-account", ctx -> ctx.render("create-account.html"));
        app.post("/create-account", ctx -> createAccount(ctx, connectionPool));
        app.get("/logout", ctx -> logout(ctx));
    }

    public static void createAccount(Context ctx, ConnectionPool connectionPool) {
        String[] name = ctx.formParam("name").split(" ");
        String address = ctx.formParam("address");
        int zip = Integer.parseInt(ctx.formParam("postal_code"));
        String phoneNumber = ctx.formParam("phone_number");
        String email = ctx.formParam("email");
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");

        if (!password1.equals(password2)) {
            ctx.attribute("createAccountName", String.join(" ", name));
            ctx.attribute("createAccountAddress", address);
            ctx.attribute("createAccountZip", zip);
            ctx.attribute("createAccountPhoneNumber", phoneNumber);
            ctx.attribute("createAccountEmail", email);

            ctx.attribute("error", "Passwords don't match");
            ctx.render("create-account.html");
            return;
        }

        try {
            Account account = new Customer(email, password1, "CUSTOMER", name[0], name[1], address, zip, "City", phoneNumber);

            AccountMapper.createAccount(account, connectionPool);

            ctx.sessionAttribute("currentAccount", account);
            String loginRedirect = ctx.sessionAttribute("loginRedirect");

            if (loginRedirect != null) {
                ctx.sessionAttribute("loginRedirect", null);
                ctx.redirect(loginRedirect);
            } else {
                ctx.redirect("/");
            }
        } catch (DatabaseException e) {
            ctx.attribute("createAccountName", String.join(" ", name));
            ctx.attribute("createAccountAddress", address);
            ctx.attribute("createAccountZip", zip);
            ctx.attribute("createAccountPhoneNumber", phoneNumber);
            ctx.attribute("createAccountEmail", email);

            ctx.attribute("error", e.getMessage());
            ctx.render("create-account.html");
        }
    }

    public static void login(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        try {
            Account account = AccountMapper.login(email, password, connectionPool);

            ctx.sessionAttribute("currentAccount", account);
            String loginRedirect = ctx.sessionAttribute("loginRedirect");

            if (loginRedirect != null) {
                ctx.redirect(loginRedirect);
                ctx.sessionAttribute("loginRedirect", null);
            } else {
                ctx.redirect("/");
            }
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("login.html");
        }
    }

    public static void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }
}
