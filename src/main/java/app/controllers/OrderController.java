package app.controllers;

import app.entities.Customer;
import app.entities.Order;
import app.persistence.ConnectionPool;
import app.services.CarportSvg;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.time.LocalDateTime;
import java.util.Locale;

public class OrderController {

    public void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("continuerequest", ctx -> continueRequest(ctx, connectionPool));

    }

    private void continueRequest(Context ctx, ConnectionPool connectionPool) {

        Customer customer = ctx.sessionAttribute("currentUser");

        String carportWidth = ctx.formParam("width-option");
        String carportLength = ctx.formParam("length-option");

        //gemmer attributer i nuværende session
        ctx.sessionAttribute("carportWidth", carportWidth);
        ctx.sessionAttribute("carportLength", carportLength);

        if (customer == null) {
            //gemmer destinationen til når brugeren er logget ind
            ctx.sessionAttribute("loginRedirect", "/order-overview");
            ctx.redirect("/login.html");
        } else {
            ctx.redirect("/order-overview");
        }
    }

    public void sendOrderRequest(Context ctx, ConnectionPool connectionPool) {
        String width = ctx.formParam("width");
        String height = ctx.formParam("height");

        String title = "Carport bredde: " + width + "& Carport længde: " + height;

        Order newOrder = new Order(-1,title, Order.OrderStatus.WAITING_FOR_REVIEW,null,null, LocalDateTime.now());



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
