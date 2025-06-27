//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.triangulation;

import pp.util.FloatPoint;
import pp.util.Position;
import pp.util.SegmentLike;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.util.Collections;

import static pp.util.Util.getLast;

/**
 * A demo application for trying out polygon triangulation.
 */
public class TriangulationApp extends TriangulationPanel implements MouseListener, MouseMotionListener {
    private static final float DIST_SQ = 9f;
    private static final float DIST = 3f;
    private boolean polygonClosed;
    private int mouseX = -10;
    private int mouseY = -10;
    private int draggedPoint = -1;
    private String hint;

    private TriangulationApp() {
        super(1f, 0f);
        addMouseMotionListener(this);
        addMouseListener(this);
    }

    void reset() {
        points.clear();
        polygonClosed = false;
        draggedPoint = -1;
        deleteTriangles();
        repaint();
    }

    private void deleteTriangles() {
        setTriangles(Collections.emptyList());
    }

    void triangulate() {
        if (!polygonClosed) return;
        try {
            setTriangles(new Polygon(points).triangulate());
            repaint();
        }
        catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2d = (Graphics2D) g;
        if (!points.isEmpty() && !polygonClosed) {
            final Position first = points.get(0);
            final Position last = getLast(points);
            g2d.draw(new Line2D.Float(last.getX(), last.getY(), mouseX, mouseY));
            if (points.size() > 2 && first.distanceSquaredTo(mouseX, mouseY) < DIST_SQ)
                g2d.drawString("Closing polygon", mouseX + 5, mouseY + 5);  //NON-NLS
        }
        else if (hint != null)
            g2d.drawString(hint, mouseX + 5, mouseY - 5);  //NON-NLS
    }

    @Override
    boolean isClosedPolygon() {
        return polygonClosed;
    }

    /**
     * Returns the index of the point that is close to mouse pointer coordinates,
     * or -1 if mouse coordinates are not close to any point.
     */
    private int findPoint() {
        for (Position p : points)
            if (p.distanceSquaredTo(mouseX, mouseY) < DIST_SQ)
                return points.indexOf(p);
        return -1;
    }

    /**
     * Returns the index in points where to add a new point at mouse pointer coordinates that are
     * close to the corresponding segment of the polygon, or -1 if mouse coordinates
     * are not close to any segment.
     */
    private int findSegment() {
        if (points.isEmpty() || !polygonClosed)
            return -1;
        Position cur = getLast(points);
        int index = 0;
        for (Position next : points) {
            if (SegmentLike.distance(cur.getX(), cur.getY(), next.getX(), next.getY(), mouseX, mouseY) <= DIST)
                return index;
            cur = next;
            index++;
        }
        return -1;
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        mouseX = event.getX();
        mouseY = event.getY();
        hint = null;
        if (findPoint() >= 0) {
            hint = "Drag point or delete it (right mouse button)"; //NON-NLS
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        else if (findSegment() >= 0) {
            hint = "Create point"; //NON-NLS
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }
        else
            setCursor(Cursor.getDefaultCursor());
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (draggedPoint >= 0) {
            points.set(draggedPoint, new FloatPoint(event.getX(), event.getY()));
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        draggedPoint = -1;
        setCursor(Cursor.getDefaultCursor());
    }

    @Override
    public void mousePressed(MouseEvent event) {
        mouseX = event.getX();
        mouseY = event.getY();
        switch (event.getButton()) {
            case MouseEvent.BUTTON1 -> leftButton();
            case MouseEvent.BUTTON3 -> rightButton();
            default -> { /* do nothing */ }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) { /* ignored */ }

    @Override
    public void mouseEntered(MouseEvent e) { /* ignored */ }

    @Override
    public void mouseExited(MouseEvent e) { /* ignored */ }

    private void leftButton() {
        hint = null;
        if (polygonClosed) {
            draggedPoint = findPoint();
            if (draggedPoint < 0) {
                final int index = findSegment();
                if (index >= 0)
                    points.add(index, new FloatPoint(mouseX, mouseY));
            }
        }
        else if (points.size() > 2 && points.get(0).distanceSquaredTo(mouseX, mouseY) < DIST_SQ)
            polygonClosed = true;
        else
            points.add(new FloatPoint(mouseX, mouseY));
        repaint();
    }

    private void rightButton() {
        hint = null;
        final int index = findPoint();
        if (index >= 0) {
            points.remove(index);
            if (points.size() >= 3)
                deleteTriangles();
            else
                reset();
            repaint();
        }
    }

    public static void main(String[] args) {
        final TriangulationApp triPanel = new TriangulationApp();
        final JFrame frame = new JFrame("Triangulation"); //NON-NLS
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(triPanel, BorderLayout.CENTER);
        frame.getContentPane().add(buttonPanel(triPanel), BorderLayout.NORTH);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private static JPanel buttonPanel(TriangulationApp triPanel) {
        final JPanel buttonPanel = new JPanel(new FlowLayout());
        final JButton calc = new JButton("Triangulate"); //NON-NLS
        calc.addActionListener(e -> triPanel.triangulate());
        final JButton reset = new JButton("Reset"); //NON-NLS
        reset.addActionListener(e -> triPanel.reset());
        buttonPanel.add(calc);
        buttonPanel.add(reset);
        return buttonPanel;
    }
}
