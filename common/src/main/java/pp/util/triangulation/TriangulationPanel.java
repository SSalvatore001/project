//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.triangulation;

import pp.util.Position;
import pp.util.Triangle;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.awt.BasicStroke.CAP_SQUARE;
import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON;

/**
 * A panel that shows a polygon with holes and triangles.
 */
class TriangulationPanel extends JPanel {
    private static final Color TRIANGLE_FILL_COLOR = new Color(0f, 0f, 1f, 0.1f);
    private static final Color TRIANGLE_BORDER_COLOR = Color.BLUE;
    private static final Color BACKGROUND_COLOR = new Color(250, 240, 230);
    private static final Stroke FAT_STROKE = new BasicStroke(3f, CAP_SQUARE, BasicStroke.JOIN_ROUND, 10.0f, null, 0.0f);
    private static final float POINT_SIZE = 7f;

    final List<Position> points = new ArrayList<>();
    private final List<List<Position>> holes = new ArrayList<>();
    private transient List<Triangle> triangles = Collections.emptyList();
    private final float factor;
    private final float offset;

    TriangulationPanel(float factor, float offset) {
        this.offset = offset;
        this.factor = factor;
        setPreferredSize(new Dimension(1000, 800));
        setBackground(BACKGROUND_COLOR);
    }

    void setOuter(List<Position> outer) {
        this.points.clear();
        this.points.addAll(outer);
    }

    void addHole(List<Position> hole) {
        holes.add(hole);
    }

    void setTriangles(List<Triangle> triangles) {
        this.triangles = triangles;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(new RenderingHints(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON));
        drawTriangles(g2d);
        drawPolygon(g2d, points);
        for (List<Position> pointList : holes)
            drawPolygon(g2d, pointList);
    }

    boolean isClosedPolygon() {
        return true;
    }

    private void drawTriangles(Graphics2D g2d) {
        final Stroke savedStroke = g2d.getStroke();
        final Color savedColor = g2d.getColor();
        g2d.setStroke(FAT_STROKE);
        for (Triangle t : triangles) {
            final Path2D triangle = makePath(t);
            g2d.setColor(TRIANGLE_FILL_COLOR);
            g2d.fill(triangle);
            g2d.setColor(TRIANGLE_BORDER_COLOR);
            g2d.draw(triangle);
        }
        g2d.setStroke(savedStroke);
        g2d.setColor(savedColor);
    }

    private Path2D makePath(Triangle t) {
        final Path2D path = new Path2D.Float();
        path.moveTo(viewX(t.a().getX()), viewY(t.a().getY()));
        path.lineTo(viewX(t.b().getX()), viewY(t.b().getY()));
        path.lineTo(viewX(t.c().getX()), viewY(t.c().getY()));
        path.closePath();
        return path;
    }

    private void drawPolygon(Graphics2D g2d, List<Position> pointList) {
        final Path2D polygon = new Path2D.Float();
        boolean first = true;
        for (Position p : pointList) {
            g2d.fill(new Ellipse2D.Float(viewX(p.getX()) - POINT_SIZE * 0.5f,
                                         viewY(p.getY()) - POINT_SIZE * 0.5f,
                                         POINT_SIZE,
                                         POINT_SIZE));
            if (first)
                polygon.moveTo(viewX(p.getX()), viewY(p.getY()));
            else
                polygon.lineTo(viewX(p.getX()), viewY(p.getY()));
            first = false;
        }
        if (isClosedPolygon())
            polygon.closePath();
        g2d.draw(polygon);
    }

    float viewX(float x) {
        return offset + factor * x;
    }

    float viewY(float y) {
        return offset + factor * y;
    }
}
