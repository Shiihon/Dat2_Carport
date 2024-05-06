package app.services;

public class CarportSvg {
    private int width;
    private int height;
    private Svg carportSvg;

    public CarportSvg(int width, int height) {
        this.width = width;
        this.height = height;

        this.carportSvg = new Svg(0, 0, "0 0 855 690", "");
        carportSvg.addLine(62, 45, 62, 579, "stroke-width:1px; stroke:#000000; marker-start: url(#beginArrow); marker-end: url(#endArrow);" );
        carportSvg.addLine(22, 10, 22, 610, "stroke-width:1px; stroke:#000000; marker-start: url(#beginArrow); marker-end: url(#endArrow);" );
        carportSvg.addLine(75, 650, 855, 650, "stroke-width:1px; stroke:#000000; marker-start: url(#beginArrow); marker-end: url(#endArrow);" );

        carportSvg.addLine(855, 660, 855, 610, "stroke-width:1px; stroke:#000000; stroke-dasharray: 5 5;" );
        carportSvg.addLine(75, 660, 75, 610, "stroke-width:1px; stroke:#000000; stroke-dasharray: 5 5;" );

        carportSvg.addLine(17, 10, 80, 10, "stroke-width:1px; stroke:#000000; stroke-dasharray: 5 5;" );
        carportSvg.addLine(17, 610, 80, 610, "stroke-width:1px; stroke:#000000; stroke-dasharray: 5 5;" );

        carportSvg.addLine(60, 45, 75, 45, "stroke-width:1px; stroke:#000000; stroke-dasharray: 5 5;" );
        carportSvg.addLine(60, 579, 75, 579, "stroke-width:1px; stroke:#000000; stroke-dasharray: 5 5;" );

        carportSvg.addText(12, 305, -90, "600 cm");
        carportSvg.addText(52, 305, -90, "530 cm");
        carportSvg.addText(470, 670, 0, "780 cm");


        Svg innerSvg = new Svg(75, 10, "0 0 780 600", "780");
        innerSvg.addRectangle(0, 0, 600, 780, "stroke-width:2px; stroke:#000000; fill: #ffffff");
        innerSvg.addLine(55, 35, 600, 569.5, "stroke-width:2px; stroke:#000000; stroke-dasharray: 5 5;" );
        innerSvg.addLine(55, 569.5, 600, 35, "stroke-width:2px; stroke:#000000; stroke-dasharray: 5 5;" );

        addBeams(innerSvg);
        addRafters(innerSvg);
        addPosts(innerSvg);
        carportSvg.addSvg(innerSvg);

    }

    private void addBeams(Svg svg) {
        svg.addRectangle(0, 35, 4.5, 780, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        svg.addRectangle(0, 565, 4.5, 780, "stroke-width:1px; stroke:#000000; fill: #ffffff");
    }

    private void addRafters(Svg svg){

        for (double i = 0; i < 800; i += 55.712) {

            svg.addRectangle(i, 0, 600, 4.5, "stroke-width:1px; stroke:#000000; fill: #ffffff");

        }
    }

    private void addPosts(Svg svg) {
        for (double i = 110; i < 800; i+= 310) {

            svg.addRectangle(i, 32, 9.7, 10, "stroke-width:2px; stroke:#000000; fill: #FF5733");
            svg.addRectangle(i, 562, 9.7, 10, "stroke-width:2px; stroke:#000000; fill: #FF5733");
        }
    }

    @Override
    public String toString() {
        return carportSvg.toString();
    }
}
