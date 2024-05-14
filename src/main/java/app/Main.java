package app;

import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.AccountController;
import app.controllers.OrderController;
import app.controllers.SalesController;
import app.controllers.WarehouseController;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Main {

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "cupcake_development";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    public static void main(String[] args) {
        // Initializing Javalin and Jetty webserver

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.jetty.modifyServletContextHandler(handler -> handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);

        // Routing

        app.get("/", ctx -> ctx.render("index.html"));
        AccountController.addRoutes(app, connectionPool);
        OrderController.addRoutes(app, connectionPool);
        SalesController.addRoutes(app, connectionPool);
        WarehouseController.addRoutes(app, connectionPool);
        SalesController.addRoutes(app, connectionPool);
    }
}