package app.controllers;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.AccountMapper;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.services.CarportSvg;
import app.services.OrderBillGenerator;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class OrderController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/carportSchematic", ctx -> OrderController.viewCarportSchematic(ctx));
        app.post("/continuerequest", ctx -> continueRequest(ctx, connectionPool));
        app.post("/sendrequest", ctx -> sendOrderRequest(ctx, connectionPool));
        app.get("/order-overview", ctx -> orderOverview(ctx));
        app.get("/request-confirmation", ctx -> ctx.render("request-confirmation.html"));
        app.get("backtofrontpage", ctx -> ctx.render("index.html"));
        app.get("/myorders", ctx -> viewMyOrders(ctx, connectionPool));
        app.post("/payOrder", ctx -> payOrder(ctx, connectionPool));
        app.get("/viewInvoice", ctx -> viewInvoice(ctx, connectionPool));
    }

    private static void continueRequest(Context ctx, ConnectionPool connectionPool) {
        try {
            int width = Integer.parseInt(Objects.requireNonNull(ctx.formParam("width-option")));
            int length = Integer.parseInt(Objects.requireNonNull(ctx.formParam("length-option")));

            Customer customer = ctx.sessionAttribute("currentAccount");

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
        } catch (NullPointerException ignored) {
            ctx.redirect("/");
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
        Customer customer = ctx.sessionAttribute("currentAccount");

        try {
            OrderMapper.setOrderStatus(orderId, Order.OrderStatus.PAID, connectionPool);

            Invoice newInvoice = new Invoice(orderId, customer.getId(), LocalDate.now());
            OrderMapper.createOrderInvoice(newInvoice, connectionPool);

            ctx.redirect("myorders");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("myorders.html");
        }
    }

    public static void viewInvoice(Context ctx, ConnectionPool connectionPool) {

        int orderId = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("orderId")));

        try {
            Order order = OrderMapper.getOrderById(orderId, connectionPool);
            Invoice invoice = OrderMapper.getOrderInvoice(orderId, connectionPool);
            ctx.attribute("order", order);
            ctx.attribute("invoice", invoice);
            Account customer = AccountMapper.getAccountById(order.getAccountId(), connectionPool);
            ctx.attribute("customer", customer);
            ctx.render("invoice.html");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("invoice.html");
        }
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

    public static void orderOverview(Context ctx) {
        int width = ctx.sessionAttribute("width");
        int length = ctx.sessionAttribute("length");

        Locale.setDefault(Locale.US);
        CarportSvg svg = new CarportSvg(width, length);
        ctx.attribute("svg", svg.toString());
        ctx.render("order-overview.html");
    }
}