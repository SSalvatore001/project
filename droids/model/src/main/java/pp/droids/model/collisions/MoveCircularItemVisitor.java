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
import pp.util.Segment;
import pp.util.SegmentLike;

import static pp.util.FloatMath.sqr;

/**
 * A visitor class that creates a specific visitor for a moving circular item. The former visitor
 * checks for overlaps when that item is moved on a straight line.
 */
class MoveCircularItemVisitor extends AbstractOverlapVisitor<Boolean> {
    private final CircularItem item;
    private final Position from;
    private final Position to;

    /**
     * Creates a visitor for circular items moved on a straight line between the specified positions.
     *
     * @param item visiting item
     * @param from start position for movement
     * @param to   end position for movement
     */
    public MoveCircularItemVisitor(CircularItem item, Position from, Position to) {
        this.item = item;
        this.from = from;
        this.to = to;
    }

    @Override
    Boolean handle(CircularItem other) {
        return item != other &&
               !item.isDestroyed() &&
               !other.isDestroyed() &&
               SegmentLike.distance(from, to, other) <= item.getRadius() + other.getRadius();
    }

    @Override
    Boolean handle(PolygonItem poly) {
        if (item.isDestroyed() || poly.isDestroyed())
            return Boolean.FALSE;
        final Segment seg = new Segment(from, to);
        final float dist2 = sqr(item.getRadius());
        return poly.getAllSegments().stream()
                   .anyMatch(cur -> seg.minDistanceSquared(cur) <= dist2);
    }
}
