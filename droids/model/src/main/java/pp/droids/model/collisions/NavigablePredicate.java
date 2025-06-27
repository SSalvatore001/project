//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.collisions;

import pp.droids.model.item.Item;
import pp.droids.model.item.Polygon;

/**
 * A predicate that is true for all items that can collide with droids etc.
 * if they are close enough.
 */
public class NavigablePredicate extends CollisionPredicate {
    public NavigablePredicate(Item item) {
        super(item);
    }

    @Override
    public Boolean visit(Polygon poly) {
        return Boolean.TRUE;
    }
}
