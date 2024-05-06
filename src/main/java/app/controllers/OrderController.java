package app.controllers;

import app.services.CarportSvg;
import app.services.Svg;
import io.javalin.http.Context;

import java.util.Locale;

public class OrderController {

    public static void viewCarportSchematic(Context ctx){
        Locale.setDefault(new Locale("US"));
        CarportSvg svg = new CarportSvg(600, 780);
        ctx.attribute("svg", svg.toString());
        ctx.render("carportSchematic.html");
    }
}
