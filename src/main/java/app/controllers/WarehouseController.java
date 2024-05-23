package app.controllers;

import app.entities.Material;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.MaterialMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class WarehouseController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/warehouse", ctx -> viewWarehouse(ctx, connectionPool));
        app.get("/registerNewMaterial", ctx -> ctx.render("registerNewMaterial.html"));
        app.post("/addNewMaterial", ctx -> addNewMaterial(ctx, connectionPool));
    }

    public static void viewWarehouse(Context ctx, ConnectionPool connectionPool) throws DatabaseException {

        List<Material> materialList = MaterialMapper.getAllMaterials(connectionPool);
        ctx.attribute("materialList", materialList);
        ctx.render("warehouse.html");
    }

    public static void addNewMaterial(Context ctx, ConnectionPool connectionPool) {
        String description = ctx.formParam("description");
        String unit = ctx.formParam("unit");
        String materials = ctx.formParam("material");
        int length = Integer.parseInt(ctx.formParam("length"));
        int price = Integer.parseInt(ctx.formParam("price"));

        try {
            Material material = new Material(description, unit, price, length, Material.MaterialType.valueOf(materials));
            MaterialMapper.createMaterial(material, connectionPool);
            ctx.redirect("/warehouse");

        } catch (DatabaseException e) {
            ctx.attribute("error", "Kunne ikke registrerer materialet");
            ctx.render("/addNewMaterial");
        }
    }
}
