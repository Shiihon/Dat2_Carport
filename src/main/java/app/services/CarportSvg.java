package app.services;

public class CarportSvg {

    private static final int postDistanceFromSide = 35; // The distance from the post to the side of the carport
    private static final double postDistanceFromFront = 100; // The distance from the front of the carport to the first post
    private static final double postDistanceFromBack = 30; // The distance from the back of the carport to the last post
    private static final double maxPostDistance = 330; // The max distance there can be between two posts
    private static final double postSize = 9.7; // The size of the post

    private static final double maxRafterDistance = 55; // The max distance there can be between two rafters
    private static final double rafterWidth = 4.5; // The width (slim side) of the rafter

    private static final double beamWidth = 4.5; // The width (slim side) of the beam

    private static final int frameSizeWidth = 100; // The extra frame width where other things like arrows can be drawn
    private static final int frameSizeHeight = 70; // The extra frame height where other things like arrows can be drawn

    private final int frameWidth;
    private final int frameHeight;
    private final int carportSchematicWidth;
    private final int carportSchematicHeight;
    private Svg carportSvg;

    public CarportSvg(int carportWidth, int carportLength) {
        this.frameWidth = carportLength + frameSizeWidth;
        this.frameHeight = carportWidth + frameSizeHeight;
        this.carportSchematicWidth = carportLength;
        this.carportSchematicHeight = carportWidth;

        createSchematicFrame();
        createInnerCarportSchematic();
    }

    private void createSchematicFrame() {
        carportSvg = new Svg(0, 0, String.format("0 0 %d %d", frameWidth, frameHeight), "100%");

        // Total carport width
        carportSvg.addLine(20, 0, 20, frameHeight - frameSizeHeight, "stroke-width:1px; stroke:#000000; marker-start: url(#beginArrow); marker-end: url(#endArrow);");

        carportSvg.addLine(20, 0, frameSizeWidth - 10, 0, "stroke-width:1px; stroke:#000000; stroke-dasharray: 5 5;");
        carportSvg.addLine(20, frameHeight - frameSizeHeight, frameSizeWidth - 10, frameHeight - frameSizeHeight, "stroke-width:1px; stroke:#000000; stroke-dasharray: 5 5;");

        carportSvg.addText(10, (frameHeight - frameSizeHeight) * 0.5, -90, String.format("%d cm", carportSchematicHeight));


        // Inner carport width
        carportSvg.addLine(60, postDistanceFromSide - rafterWidth * 0.5, 60, frameHeight - frameSizeHeight - postDistanceFromSide + rafterWidth * 0.5, "stroke-width:1px; stroke:#000000; marker-start: url(#beginArrow); marker-end: url(#endArrow);");

        carportSvg.addLine(60, postDistanceFromSide - rafterWidth * 0.5, frameSizeWidth - 10, postDistanceFromSide - rafterWidth * 0.5, "stroke-width:1px; stroke:#000000; stroke-dasharray: 5 5;");
        carportSvg.addLine(60, frameHeight - frameSizeHeight - postDistanceFromSide + rafterWidth * 0.5, frameSizeWidth - 10, frameHeight - frameSizeHeight - postDistanceFromSide + rafterWidth * 0.5, "stroke-width:1px; stroke:#000000; stroke-dasharray: 5 5;");

        carportSvg.addText(50, (frameHeight - frameSizeHeight) * 0.5, -90, String.format("%d cm", carportSchematicHeight - postDistanceFromSide * 2));


        // Carport length
        carportSvg.addLine(frameSizeWidth, frameHeight - 20, frameWidth, frameHeight - 20, "stroke-width:1px; stroke:#000000; marker-start: url(#beginArrow); marker-end: url(#endArrow);");

        carportSvg.addLine(frameSizeWidth, frameHeight - 20, frameSizeWidth, frameHeight - frameSizeHeight + 10, "stroke-width:1px; stroke:#000000; stroke-dasharray: 5 5;");
        carportSvg.addLine(frameWidth - 1, frameHeight - 20, frameWidth - 1, frameHeight - frameSizeHeight + 10, "stroke-width:1px; stroke:#000000; stroke-dasharray: 5 5;");

        carportSvg.addText(frameSizeWidth + (frameWidth - frameSizeWidth) * 0.5, frameHeight - 30, 0, String.format("%d cm", carportSchematicWidth));
    }

    private void createInnerCarportSchematic() {
        Svg innerSvg = new Svg(frameWidth - carportSchematicWidth, 0, String.format("%d %d", carportSchematicWidth, carportSchematicHeight), String.valueOf(carportSchematicWidth));

        innerSvg.addRectangle(0, 0, carportSchematicWidth, carportSchematicHeight, "stroke-width:2px; stroke:#000000; fill: #ffffff");

        addPosts(innerSvg);
        addBeams(innerSvg);
        addRafters(innerSvg);
        carportSvg.addSvg(innerSvg);
    }

    private void addPosts(Svg svg) {
        double totalPostDistance = carportSchematicWidth - postDistanceFromFront - postDistanceFromBack;
        int numPosts = Math.max((int) Math.ceil(totalPostDistance / maxPostDistance) + 1, 2);
        double distanceBetweenPosts = totalPostDistance / (numPosts - 1);

        for (double i = 0; i < numPosts; i++) {
            double x = postDistanceFromFront + i * distanceBetweenPosts;

            svg.addRectangle(x - postSize * 0.5, postDistanceFromSide - postSize * 0.5, postSize, postSize, "stroke-width:2px; stroke:#000000; fill: #FF5733");
            svg.addRectangle(x - postSize * 0.5, carportSchematicHeight - postDistanceFromSide - postSize * 0.5, postSize, postSize, "stroke-width:2px; stroke:#000000; fill: #FF5733");
        }
    }

    private void addBeams(Svg svg) {
        svg.addRectangle(0, postDistanceFromSide - beamWidth * 0.5, carportSchematicWidth, beamWidth, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        svg.addRectangle(0, carportSchematicHeight - postDistanceFromSide - beamWidth * 0.5, carportSchematicWidth, beamWidth, "stroke-width:1px; stroke:#000000; fill: #ffffff");
    }

    private void addRafters(Svg svg) {
        double totalRaftersDistance = carportSchematicWidth - rafterWidth;
        int numRafters = (int) Math.ceil(totalRaftersDistance / maxRafterDistance) + 1;
        double rafterDistance = totalRaftersDistance / (numRafters - 1);

        for (double i = 0; i <= numRafters; i++) {
            double x = rafterWidth * 0.5 + i * rafterDistance;

            svg.addRectangle(x - rafterWidth * 0.5, 0, rafterWidth, carportSchematicHeight, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        }

        // Steel cross
        svg.addLine(rafterDistance, postDistanceFromSide + beamWidth * 0.5, rafterDistance * (numRafters - 2), carportSchematicHeight - postDistanceFromSide - beamWidth * 0.5, "stroke-width:1px; stroke:#000000; stroke-dasharray: 5 5;");
        svg.addLine(rafterDistance + rafterWidth, postDistanceFromSide + beamWidth * 0.5, rafterDistance * (numRafters - 2) + rafterWidth, carportSchematicHeight - postDistanceFromSide - beamWidth * 0.5, "stroke-width:1px; stroke:#000000; stroke-dasharray: 5 5;");

        svg.addLine(rafterDistance, carportSchematicHeight - postDistanceFromSide - beamWidth * 0.5, rafterDistance * (numRafters - 2), postDistanceFromSide + beamWidth * 0.5, "stroke-width:1px; stroke:#000000; stroke-dasharray: 5 5;");
        svg.addLine(rafterDistance + rafterWidth, carportSchematicHeight - postDistanceFromSide - beamWidth * 0.5, rafterDistance * (numRafters - 2) + rafterWidth, postDistanceFromSide + beamWidth * 0.5, "stroke-width:1px; stroke:#000000; stroke-dasharray: 5 5;");
    }

    @Override
    public String toString() {
        return carportSvg.toString();
    }
}
