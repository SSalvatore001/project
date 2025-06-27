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
 * checks for overlaps of that item.
 */
public class OverlapVisitor extends AbstractOverlapVisitor<Visitor<Boolean>> {
    private final Position pos;

    public OverlapVisitor(Position pos) {
        this.pos = pos;
    }

    @Override
    Visitor<Boolean> handle(CircularItem item) {
        return new CircularItemOverlapVisitor(item, pos);
    }

    @Override
    Visitor<Boolean> handle(PolygonItem poly) {
        return new PolygonOverlapVisitor(poly, pos);
    }
}
