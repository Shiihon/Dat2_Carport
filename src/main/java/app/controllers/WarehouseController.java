package app.controllers;

import app.entities.Material;
import app.persistence.ConnectionPool;
import app.persistence.MaterialMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class WarehouseController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/warehouse", ctx -> viewWarehouse(ctx, connectionPool));
    }

    public static void viewWarehouse(Context ctx, ConnectionPool connectionPool) {

        List<Material> materialList = MaterialMapper.getAllMaterials(connectionPool);
        ctx.attribute("materialList", materialList);
        ctx.render("warehouse.html");
    }

    public static void addNewMaterial(Context ctx, ConnectionPool connectionPool) {

    }
}
