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

import static pp.util.FloatMath.sqr;

/**
 * A visitor class that creates a specific visitor for a circular item.
 */
class CircularItemOverlapVisitor extends AbstractOverlapVisitor<Boolean> {
    private final CircularItem item;
    private final float dx;
    private final float dy;

    /**
     * Creates a visitor for items.
     *
     * @param item visiting item
     * @param pos  Position to check overlaps
     */
    public CircularItemOverlapVisitor(CircularItem item, Position pos) {
        this.item = item;
        dx = item.getX() - pos.getX();
        dy = item.getY() - pos.getY();
    }

    @Override
    Boolean handle(CircularItem other) {
        return item != other &&
               !item.isDestroyed() &&
               !other.isDestroyed() &&
               item.distanceSquaredTo(other.getX() + dx,
                                      other.getY() + dy) <= sqr(item.getRadius() + other.getRadius());
    }

    @Override
    Boolean handle(PolygonItem polygon) {
        return PolygonOverlapVisitor.overlap(polygon, item, -dx, -dy);
    }
}
