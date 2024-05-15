package app.controllers;

import app.entities.Account;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.AccountMapper;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Objects;


public class SalesController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/viewOrderDetails", ctx -> viewOrderDetails(ctx, connectionPool));
        app.post("updatePrice", ctx -> updatePrice(ctx, connectionPool));
    }

    public static void approveReview(Context ctx, ConnectionPool connectionPool) {

    }

    public static void updatePrice(Context ctx, ConnectionPool connectionPool) {
        int orderId = Integer.parseInt(Objects.requireNonNull(ctx.formParam("orderId")));
        double newPrice = Double.parseDouble(ctx.formParam("price"));

        try {
            OrderMapper.setOrderPrice(orderId, newPrice, connectionPool);
            ctx.sessionAttribute("message", "Prisen er nu opdateret");
            ctx.redirect(String.format("/viewOrderDetails?orderId=%d", orderId));

        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("viewOrderDetails.html");
        }
    }

    public static void viewCostumersOrders(Context ctx, ConnectionPool connectionPool) {

    }

    public static void viewCarportSchematic(Context ctx) {

    }

    public static void viewOrderDetails(Context ctx, ConnectionPool connectionPool) {
        int orderId = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("orderId")));

        try {
            Order order = OrderMapper.getOrderById(orderId, connectionPool);
            ctx.attribute("order", order);
            Account customer = AccountMapper.getAccountById(order.getAccountId(), connectionPool);
            ctx.attribute("customer", customer);
            ctx.render("viewOrderDetails.html");
            ctx.sessionAttribute("message", null);

        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("viewOrderDetails.html");
        }
    }
}
