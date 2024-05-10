package app.controllers;

import app.persistence.ConnectionPool;
import app.services.CarportSvg;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.Locale;

public class OrderController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/carportSchematic", ctx -> OrderController.viewCarportSchematic(ctx));
    }

    public void sendOrderRequest(Context ctx, ConnectionPool connectionPool) {

    }

    public void viewMyOrders(Context ctx, ConnectionPool connectionPool) {

    }

    public void payOrder(Context ctx, ConnectionPool connectionPool) {

    }

    public void viewInvoice(Context ctx, ConnectionPool connectionPool) {

    }

    public void cancelOrder(Context ctx, ConnectionPool connectionPool) {

    }

    public static void viewCarportSchematic(Context ctx){



        Locale.setDefault(new Locale("US"));
        CarportSvg svg = new CarportSvg(600, 780);
        ctx.attribute("svg", svg.toString());
        ctx.render("carportSchematic.html");
    }
}
