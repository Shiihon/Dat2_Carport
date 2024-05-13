package app.controllers;

import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Objects;


public class SalesController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/viewOrderDetails", ctx -> viewOrderDetails(ctx, connectionPool));
    }

    public static void approveReview(Context ctx, ConnectionPool connectionPool) {

    }

    public static void updatePrice(Context ctx, ConnectionPool connectionPool) {
        // lav den!
    }

    public static void viewCostumersOrders(Context ctx, ConnectionPool connectionPool) {

    }

    public static void viewCarportSchematic(Context ctx) {

    }

    public static void viewOrderDetails(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        int orderId = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("orderId")));

        try {
            Order order = OrderMapper.getOrderById(orderId, connectionPool);
            ctx.attribute("order", order);
            ctx.render("orderdetails.html");

        } catch (DatabaseException e) {
            throw new DatabaseException("Failed to recieve order details");
        }
    }
}
