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
import java.util.Objects;

public class OrderController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/carportSchematic", ctx -> OrderController.viewCarportSchematic(ctx));
        app.post("/continuerequest", ctx -> continueRequest(ctx, connectionPool));
        app.post("/sendrequest", ctx -> sendOrderRequest(ctx, connectionPool));
        app.get("/order-overview", ctx -> ctx.render("order-overview.html"));
        app.get("/request-confirmation", ctx -> ctx.render("request-confirmation.html"));
        app.get("backtofrontpage", ctx -> ctx.render("index.html"));
        app.get("/myorders", ctx -> viewMyOrders(ctx, connectionPool));
        app.post("/payOrder", ctx -> payOrder(ctx, connectionPool));
    }

    private static void continueRequest(Context ctx, ConnectionPool connectionPool) {

        Customer customer = ctx.sessionAttribute("currentAccount");

        int width = Integer.parseInt(ctx.formParam("width-option"));
        int length = Integer.parseInt(ctx.formParam("length-option"));

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
            Customer customer = ctx.sessionAttribute("currentAccount");
            int width = ctx.sessionAttribute("width");
            int length = ctx.sessionAttribute("length");

            String title = String.format("Carport bredde: %d cm & Carport længde: %d cm", width, length);

            List<OrderBillItem> orderBillItemList = OrderBillGenerator.generateOrderBill(width, length, connectionPool);
            double orderBillPrice = calculateOrderBillPrice(orderBillItemList);

            Order newOrder = new Order(customer.getId(), title, width, length, Order.OrderStatus.WAITING_FOR_REVIEW, orderBillPrice, orderBillItemList, LocalDateTime.now());

            OrderMapper.createOrder(newOrder, connectionPool);

            ctx.redirect("/request-confirmation");

        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("order-overview.html");
        }
    }

    private static double calculateOrderBillPrice(List<OrderBillItem> orderBillItemList) {
        double totalPrice = 0;

        for (OrderBillItem orderBillItem : orderBillItemList) {
            totalPrice += orderBillItem.getItemPrice();
        }

        return totalPrice;
    }

    public static void viewMyOrders(Context ctx, ConnectionPool connectionPool) {

        try {
            Customer customer = ctx.sessionAttribute("currentAccount");

            if (customer == null) {
                //gemmer destinationen til når brugeren er logget ind
                ctx.sessionAttribute("loginRedirect", "/myorders");
                ctx.redirect("/login");
            } else {
                int customerId = customer.getId();
                List<Order> myorders = OrderMapper.getAllCustomerOrders(customerId, connectionPool);
                ctx.attribute("myorders", myorders);
                ctx.render("myorders.html");
            }
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("order-overview.html");
        }
    }

    public static void payOrder(Context ctx, ConnectionPool connectionPool) {
        int orderId = Integer.parseInt(Objects.requireNonNull(ctx.formParam("orderId")));

        try {
           OrderMapper.setOrderStatus(orderId, Order.OrderStatus.PAID, connectionPool);

            ctx.redirect("myorders");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("myorders.html");
        }
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