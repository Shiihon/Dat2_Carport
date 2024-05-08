package app.services;

public class Svg {

    private static final String SVG_TEMPLATE = """
            <svg version="1.1" x="%d" y="%d"
            viewBox="%s" width="%s" preserveAspectRatio="xMinYMin">""";

    private static final String SVG_ARROW_DEFS = """
                <defs>
                    <marker id="beginArrow" markerWidth="12" markerHeight="12" refX="0" refY="6" orient="auto">
                        <path d="M0,6 L12,0 L12,12 L0,6" style="fill: #000000;" />
                    </marker>
                    <marker id="endArrow" markerWidth="12" markerHeight="12" refX="12" refY="6" orient="auto">
                        <path d="M0,0 L12,6 L0,12 L0,0 " style="fill: #000000;" />
                    </marker>
                </defs>
            """;

    private static final String SVG_RECT_TEMPLATE = "<rect x=\"%.2f\" y=\"%.2f\" height=\"%.2f\" width=\"%.2f\" style=\"%s\" />";

    private static final String SVG_LINE_TEMPLATE = "<line x1=\"%.2f\" y1=\"%.2f\" x2=\"%.2f\" y2=\"%.2f\" style=\"%s\" />";

    private static final String SVG_TEXT_TEMPLATE = "<text style=\"text-anchor: middle\" transform=\"translate(%.2f, %.2f) rotate(%.2f)\">%s</text>";


    private final StringBuilder svg = new StringBuilder();

    public Svg(int x, int y, String viewBox, String width) {

        svg.append(String.format(SVG_TEMPLATE, x, y, viewBox, width));
        svg.append(SVG_ARROW_DEFS);
    }

    public void addRectangle(double x, double y, double height, double width, String style){

        svg.append(String.format(SVG_RECT_TEMPLATE, x, y, height, width, style));

    }

    public void addLine(double x1, double y1, double x2, double y2, String style) {

        svg.append(String.format(SVG_LINE_TEMPLATE, x1, y1, x2, y2, style));

    }

    public void addText(double x, double y, double rotation, String text) {

        svg.append(String.format(SVG_TEXT_TEMPLATE, x, y, rotation, text));

    }

    public void addSvg(Svg innerSvg) {
        svg.append(innerSvg.toString());
    }

    @Override
    public String toString() {
        return svg.append("</svg>").toString();
    }
}
