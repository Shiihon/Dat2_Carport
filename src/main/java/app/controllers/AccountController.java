package app.controllers;

import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;


public class AccountController {

    public void addRoutes(Javalin app, ConnectionPool connectionPool) {

    }

    public void createAccount(Context ctx, ConnectionPool connectionPool) {

    }

    public void login(Context ctx, ConnectionPool connectionPool) {

        //KODE TIL US3
        String redirectPath = ctx.sessionAttribute("loginRedirect");
        if (redirectPath != null) {
            ctx.redirect(redirectPath);
            //fjerner redirect stien efter den er kørt en gang
            ctx.sessionAttribute("loginRedirect", null);
        } else {
            ctx.redirect("/frontpage");
        }

    }

    public void logout(Context ctx, ConnectionPool connectionPool) {

    }
}
