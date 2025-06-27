//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.collisions;

import pp.droids.model.item.CircularItem;
import pp.droids.model.item.PolygonItem;
import pp.util.Position;

/**
 * A visitor class that creates a specific visitor for polygon items.
 */
class PolygonOverlapVisitor extends AbstractOverlapVisitor<Boolean> {
    private final PolygonItem polygon;
    private final float dx;
    private final float dy;

    /**
     * Checks whether the specified polygon and the specified circular item overlap
     * if the latter is translated by (dx,dy)
     *
     * @param polygon a polygon
     * @param other   a circular item
     * @param dx      x-component of the translation of other
     * @param dy      y-component of the translation of other
     * @return true if there is an overlap
     */
    static boolean overlap(PolygonItem polygon, CircularItem other, float dx, float dy) {
        if (polygon.isDestroyed() || other.isDestroyed()) return false;
        final float x = other.getX() + dx;
        final float y = other.getY() + dy;
        final float radius = other.getRadius();
        return polygon.getAllSegments().stream()
                      .anyMatch(cur -> cur.distanceTo(x, y) <= radius);
    }

    /**
     * Checks whether the specified polygon and the Position are overlapping
     *
     * @param polygon a polygon
     * @param pos     a position
     */
    public PolygonOverlapVisitor(PolygonItem polygon, Position pos) {
        this.polygon = polygon;
        dx = polygon.getX() - pos.getX();
        dy = polygon.getY() - pos.getY();
    }

    @Override
    Boolean handle(CircularItem other) {
        return overlap(polygon, other, dx, dy);
    }

    @Override
    Boolean handle(PolygonItem polygon) {
        return Boolean.FALSE;
    }
}
