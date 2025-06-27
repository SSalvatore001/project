//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.collisions;

import pp.droids.model.item.CircularItem;
import pp.droids.model.item.PolygonItem;
import pp.droids.model.item.Visitor;
import pp.util.Position;

/**
 * A visitor class that creates a specific visitor for each item. The former visitor
 * checks for overlaps when that item is moved on a straight line.
 */
public class MoveOverlapVisitor extends AbstractOverlapVisitor<Visitor<Boolean>> {
    private final Position from;
    private final Position to;

    /**
     * Creates a visitor for items moved on a straight line between the specified positions.
     *
     * @param from start of movement
     * @param to   end of movement
     */
    public MoveOverlapVisitor(Position from, Position to) {
        this.from = from;
        this.to = to;
    }

    @Override
    Visitor<Boolean> handle(CircularItem item) {
        return new MoveCircularItemVisitor(item, from, to);
    }

    @Override
    Visitor<Boolean> handle(PolygonItem polygon) {
        throw new UnsupportedOperationException("cannot move a polygon");
    }
}
