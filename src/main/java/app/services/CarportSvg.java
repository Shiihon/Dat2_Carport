package app.services;

public class CarportSvg {

    private static final int frameSize = 100; // The extra frame size where other things like arrows can be drawn

    private static final int postDistanceFromSide = 35; // The distance from the post to the side of the carport
    private static final int maxPostDistance = 200; // The max distance there can be between two posts
    private static final int postDistanceBuffer = 110; // Small buffer distance to ensure a post won't be placed right on the edge
    private static final int postSize = 15; // The size of the post

    private static final int maxRafterDistance = 55; // The max distance there can be between two rafters
    private static final int rafterWidth = 5; // The width (slim side) of the rafter

    private static final int beamWidth = 5; // The width (slim side) of the beam

    private final int frameWidth;
    private final int frameHeight;
    private final int carportWidth;
    private final int carportLength;
    private Svg carportSvg;

    public CarportSvg(int carportWidth, int carportLength) {
        this.frameWidth = carportLength + frameSize;
        this.frameHeight = carportWidth + frameSize * 2;
        this.carportWidth = carportLength;
        this.carportLength = carportWidth;

        createSchematicFrame();
        createInnerCarportSchematic();
    }

    private void createSchematicFrame() {
        carportSvg = new Svg(0, 0, String.format("0 0 %d %d", frameWidth, frameHeight), "100%");

        //carportSvg.addRectangle(0, 0, frameWidth, frameHeight, "stroke-width:2px; stroke:#000000; fill: #ffffff");

        // Total carport width
        carportSvg.addLine(20, frameSize, 20, frameHeight - frameSize, "stroke-width:1px; stroke:#000000; marker-start: url(#beginArrow); marker-end: url(#endArrow);");
        carportSvg.addLine(20, frameSize, 80, frameSize, "stroke-width:1px; stroke:#000000; stroke-dasharray: 5 5;");
        carportSvg.addLine(20, frameHeight - frameSize, 80, frameHeight - frameSize, "stroke-width:1px; stroke:#000000; stroke-dasharray: 5 5;");
        carportSvg.addText(10, frameHeight * 0.5, -90, String.format("%d cm", carportLength));

        // Inner carport width
        carportSvg.addLine(60, frameSize + postDistanceFromSide - rafterWidth * 0.5, 60, frameHeight - frameSize - postDistanceFromSide + rafterWidth * 0.5, "stroke-width:1px; stroke:#000000; marker-start: url(#beginArrow); marker-end: url(#endArrow);");
        carportSvg.addLine(60, frameSize + postDistanceFromSide - rafterWidth * 0.5, frameSize, frameSize + postDistanceFromSide - rafterWidth * 0.5, "stroke-width:1px; stroke:#000000; stroke-dasharray: 5 5;");
        carportSvg.addLine(60, frameHeight - frameSize - postDistanceFromSide + rafterWidth * 0.5, frameSize, frameHeight - frameSize - postDistanceFromSide + rafterWidth * 0.5, "stroke-width:1px; stroke:#000000; stroke-dasharray: 5 5;");
        carportSvg.addText(50, frameHeight * 0.5, -90, String.format("%d cm", carportLength - postDistanceFromSide * 2));

        // Carport length
        carportSvg.addLine(frameSize, frameHeight - frameSize * 0.5, frameWidth, frameHeight - frameSize * 0.5, "stroke-width:1px; stroke:#000000; marker-start: url(#beginArrow); marker-end: url(#endArrow);");
        carportSvg.addLine(frameSize, frameHeight - frameSize * 0.5, frameSize, frameHeight - frameSize, "stroke-width:1px; stroke:#000000; stroke-dasharray: 5 5;");
        carportSvg.addLine(frameWidth - 1, frameHeight - frameSize * 0.5, frameWidth - 1, frameHeight - frameSize, "stroke-width:1px; stroke:#000000; stroke-dasharray: 5 5;");
        carportSvg.addText(frameSize + (frameWidth - frameSize) * 0.5, frameHeight - frameSize * 0.5 + 20, 0, String.format("%d cm", carportWidth));
    }

    private void createInnerCarportSchematic() {
        Svg innerSvg = new Svg(frameWidth - carportWidth, (frameHeight - carportLength) / 2, String.format("%d %d", carportWidth, carportLength), String.valueOf(carportWidth));

        innerSvg.addRectangle(0, 0, carportWidth, carportLength, "stroke-width:2px; stroke:#000000; fill: #ffffff");

        addPosts(innerSvg);
        addBeams(innerSvg);
        addRafters(innerSvg);
        carportSvg.addSvg(innerSvg);
    }

    private void addPosts(Svg svg) {
        int numPosts = Math.max((carportWidth - postDistanceBuffer) / maxPostDistance, 2);
        double postDistance = (double) carportWidth / numPosts;

        for (double i = 0; i < numPosts; i++) {
            double x = i * postDistance + postDistance * 0.5;

            svg.addRectangle(x - postSize * 0.5, postDistanceFromSide - postSize * 0.5, postSize, postSize, "stroke-width:2px; stroke:#000000; fill: #FF5733");
            svg.addRectangle(x - postSize * 0.5, carportLength - postDistanceFromSide - postSize * 0.5, postSize, postSize, "stroke-width:2px; stroke:#000000; fill: #FF5733");
        }
    }

    private void addBeams(Svg svg) {
        svg.addRectangle(0, postDistanceFromSide - beamWidth * 0.5, carportWidth, beamWidth, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        svg.addRectangle(0, carportLength - postDistanceFromSide - beamWidth * 0.5, carportWidth, beamWidth, "stroke-width:1px; stroke:#000000; fill: #ffffff");
    }

    private void addRafters(Svg svg) {
        int numRafters = (int) Math.floor((carportWidth - rafterWidth * 1.5) / maxRafterDistance);
        double rafterDistance = (carportWidth - rafterWidth * 1.5) / numRafters;

        for (double i = 0; i <= numRafters; i++) {
            double x = rafterWidth * 1.5 * 0.5 + i * rafterDistance;

            svg.addRectangle(x - rafterWidth * 0.5, 0, rafterWidth, carportLength, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        }

        // Steel cross
        svg.addLine(rafterDistance, postDistanceFromSide + beamWidth * 0.5, rafterDistance * (numRafters - 1), carportLength - postDistanceFromSide - beamWidth * 0.5, "stroke-width:1px; stroke:#000000; stroke-dasharray: 5 5;");
        svg.addLine(rafterDistance + rafterWidth, postDistanceFromSide + beamWidth * 0.5, rafterDistance * (numRafters - 1) + rafterWidth, carportLength - postDistanceFromSide - beamWidth * 0.5, "stroke-width:1px; stroke:#000000; stroke-dasharray: 5 5;");

        svg.addLine(rafterDistance, carportLength - postDistanceFromSide - beamWidth * 0.5, rafterDistance * (numRafters - 1), postDistanceFromSide + beamWidth * 0.5, "stroke-width:1px; stroke:#000000; stroke-dasharray: 5 5;");
        svg.addLine(rafterDistance + rafterWidth, carportLength - postDistanceFromSide - beamWidth * 0.5, rafterDistance * (numRafters - 1) + rafterWidth, postDistanceFromSide + beamWidth * 0.5, "stroke-width:1px; stroke:#000000; stroke-dasharray: 5 5;");
    }

    @Override
    public String toString() {
        return carportSvg.toString();
    }
}
