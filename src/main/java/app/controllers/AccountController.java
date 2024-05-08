package app.controllers;

import app.entities.Account;
import app.exceptions.DatabaseException;
import app.persistence.AccountMapper;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;


public class AccountController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/login", ctx -> ctx.render("login.html"));
        app.post("/login", ctx -> login(ctx, connectionPool));
    }

    public void createAccount(Context ctx, ConnectionPool connectionPool) {

    }

    public static void login(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        try {
            Account account = AccountMapper.login(email, password, connectionPool);

            ctx.sessionAttribute("currentAccount", account);
            ctx.redirect("/");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("login.html");
        }
    }

    public void logout(Context ctx, ConnectionPool connectionPool) {

    }
}
