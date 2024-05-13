package app.controllers;

import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class SalesController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/requests", ctx -> viewRequests(ctx, connectionPool));
    }

    public static void viewRequests(Context ctx, ConnectionPool connectionPool) {
        try {
            List<Order> requests = OrderMapper.getAllOrdersByStatus(Order.OrderStatus.WAITING_FOR_REVIEW, connectionPool);

            ctx.attribute("requests", requests);
            ctx.render("customer-requests.html");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
        }
    }

    public void approveReview(Context ctx, ConnectionPool connectionPool) {

    }

    public void updatePrice(Context ctx, ConnectionPool connectionPool) {

    }

    public void viewCostumersOrders(Context ctx, ConnectionPool connectionPool) {

    }

    public void viewCarportSchematic(Context ctx) {

    }
}
