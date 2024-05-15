package app.controllers;

import app.entities.Customer;
import app.entities.Order;
import app.entities.OrderBillItem;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.services.CarportSvg;
import app.services.OrderBillGenerator;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public class OrderController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/carportSchematic", ctx -> OrderController.viewCarportSchematic(ctx));
        app.post("/continuerequest", ctx -> continueRequest(ctx, connectionPool));
        app.post("/sendrequest", ctx -> sendOrderRequest(ctx, connectionPool));
        app.get("/order-overview", ctx -> ctx.render("order-overview.html"));
        app.get("/request-confirmation", ctx -> ctx.render("request-confirmation.html"));

    }

    private static void continueRequest(Context ctx, ConnectionPool connectionPool) {

        Customer customer = ctx.sessionAttribute("currentAccount");

        int width = Integer.parseInt(ctx.formParam("width-option"));
        int length = Integer.parseInt(ctx.formParam("length-option"));

        System.out.println("Width: " + width + " Length: " + length);

        //gemmer attributer i nuværende session
        ctx.sessionAttribute("width", width);
        ctx.sessionAttribute("length", length);

        if (customer == null) {
            //gemmer destinationen til når brugeren er logget ind
            ctx.sessionAttribute("loginRedirect", "/order-overview");
            ctx.redirect("/login");
        } else {
            ctx.redirect("/order-overview");
        }
    }

    public static void sendOrderRequest(Context ctx, ConnectionPool connectionPool) {

        try {
            int width = ctx.sessionAttribute("width");
            int length = ctx.sessionAttribute("length");

            String title = String.format("Carport bredde: %d cm & Carport længde: %d cm", width, length);

            List<OrderBillItem> orderBillItemList = OrderBillGenerator.generateOrderBill(width, length, connectionPool);

            Order newOrder = new Order(title, width, length, Order.OrderStatus.WAITING_FOR_REVIEW, 0, orderBillItemList, LocalDateTime.now());

            Customer customer = ctx.sessionAttribute("currentAccount");
            OrderMapper.createOrder(newOrder, customer.getId(), connectionPool);

            ctx.redirect("/request-confirmation");

        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
        }
    }

    public void viewMyOrders(Context ctx, ConnectionPool connectionPool) {

    }

    public void payOrder(Context ctx, ConnectionPool connectionPool) {

    }

    public void viewInvoice(Context ctx, ConnectionPool connectionPool) {

    }

    public void cancelOrder(Context ctx, ConnectionPool connectionPool) {

    }

    public static void viewCarportSchematic(Context ctx) {
        int width = Integer.parseInt(ctx.queryParam("width"));
        int length = Integer.parseInt(ctx.queryParam("length"));

        Locale.setDefault(Locale.US);
        CarportSvg svg = new CarportSvg(width, length);
        ctx.attribute("svg", svg.toString());
        ctx.render("carportSchematic.html");
    }
}